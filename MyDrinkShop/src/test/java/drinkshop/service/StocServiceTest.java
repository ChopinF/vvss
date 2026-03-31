package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("WhiteBoxTesting")
class StocServiceTest {

    private StocService stocService;
    private Repository<Integer, Stoc> stocRepo;

    @BeforeEach
    void setUp() {
        stocRepo = new AbstractRepository<Integer, Stoc>() {
            @Override
            protected Integer getId(Stoc entity) {
                return entity.getId();
            }
        };
        stocService = new StocService(stocRepo);
    }

    // ==========================================
    // WBT TESTS (6 Test Cases din Tabelul de Acoperire F02)
    // ==========================================

    @Test
    @DisplayName("F02_TC01: Rețetă cu stoc insuficient (areSuficient = false). Aruncă excepție.")
    void testConsuma_TC01_StocInsuficient() {
        // Input: Rețetă necesită 10 unități de Cafea, dar stocul este gol
        List<IngredientReteta> ingrediente = List.of(new IngredientReteta("Cafea", 10.0));
        Reteta reteta = new Reteta(1, ingrediente);

        // Assert: Trebuie să arunce IllegalStateException (Node 1 -> Node 2 -> Exit)
        Exception exception = assertThrows(IllegalStateException.class, () -> stocService.consuma(reteta));
        assertEquals("Stoc insuficient pentru rețeta.", exception.getMessage());
    }

    @Test
    @DisplayName("F02_TC02: Rețetă goală (0 ingrediente), areSuficient = true. Metoda se încheie.")
    void testConsuma_TC02_RetetaGoala() {
        // Input: Rețetă fără niciun ingredient
        Reteta reteta = new Reteta(2, new ArrayList<>());

        // Assert: Nu trebuie să arunce nicio excepție (Node 1 -> Node 3 -> Node 9)
        assertDoesNotThrow(() -> stocService.consuma(reteta));
    }

    @Test
    @DisplayName("F02_TC03: 1 ingredient, 0 elemente găsite de stocRepo. Bucla interioară se sare.")
    void testConsuma_TC03_FaraStocGasit() {
        // Input: Ingredient cu cantitate 0 (pentru ca areSuficient să fie true), stoc gol
        List<IngredientReteta> ingrediente = List.of(new IngredientReteta("Lapte", 0.0));
        Reteta reteta = new Reteta(3, ingrediente);

        // Assert: Acoperă Node 1, 3, 4, 5(F), 9
        assertDoesNotThrow(() -> stocService.consuma(reteta));
    }

    @Test
    @DisplayName("F02_TC04: 1 ingredient (necesar = 0), 1 stoc existent. Se face break la ramas <= 0.")
    void testConsuma_TC04_NecesarZero_Break() {
        // Setăm stocul în Repo
        Stoc stocLapte = new Stoc(1, "Lapte", 10, 2);
        stocRepo.save(stocLapte);

        // Input: Rețetă care necesită 0 lapte
        List<IngredientReteta> ingrediente = List.of(new IngredientReteta("Lapte", 0.0));
        Reteta reteta = new Reteta(4, ingrediente);

        stocService.consuma(reteta);

        // Assert: Stocul a rămas neatins (Node 1, 3, 4, 5, 6, 7, 9)
        Stoc stocActualizat = stocRepo.findAll().stream().findFirst().orElseThrow();
        assertEquals(10.0, stocActualizat.getCantitate());
    }

    @Test
    @DisplayName("F02_TC05: 1 ingredient (cantitate=5), stocRepo are 1 stoc cu cantitatea 10. Stocul scade.")
    void testConsuma_TC05_StocScazut() {
        // Setăm stocul în Repo (10 unități)
        Stoc stocZahar = new Stoc(2, "Zahar", 10, 2);
        stocRepo.save(stocZahar);

        // Input: Rețetă necesită 5 unități
        List<IngredientReteta> ingrediente = List.of(new IngredientReteta("Zahar", 5.0));
        Reteta reteta = new Reteta(5, ingrediente);

        stocService.consuma(reteta);

        // Assert: Stocul trebuie să scadă la 5 (Node 1, 3, 4, 5, 6, 8, 9)
        Stoc stocActualizat = stocRepo.findAll().stream().findFirst().orElseThrow();
        assertEquals(5.0, stocActualizat.getCantitate());
    }

    @Test
    @DisplayName("F02_TC06: 2 ingrediente, stocRepo găsește stoc pentru ambele. (Loop x2)")
    void testConsuma_TC06_DouaIngrediente() {
        // Setăm stocul pentru 2 ingrediente
        Stoc stocCafea = new Stoc(3, "Cafea", 20, 5);
        Stoc stocApa = new Stoc(4, "Apa", 50, 10);
        stocRepo.save(stocCafea);
        stocRepo.save(stocApa);

        // Input: Rețeta cere ambele ingrediente
        List<IngredientReteta> ingrediente = List.of(
                new IngredientReteta("Cafea", 15.0),
                new IngredientReteta("Apa", 10.0)
        );
        Reteta reteta = new Reteta(6, ingrediente);

        stocService.consuma(reteta);

        // Assert: Ambele stocuri trebuie să fie scăzute corect
        List<Stoc> stocuri = (List<Stoc>) stocRepo.findAll();
        for (Stoc s : stocuri) {
            if (s.getIngredient().equals("Cafea")) {
                assertEquals(5.0, s.getCantitate());
            } else if (s.getIngredient().equals("Apa")) {
                assertEquals(40.0, s.getCantitate());
            }
        }
    }
}