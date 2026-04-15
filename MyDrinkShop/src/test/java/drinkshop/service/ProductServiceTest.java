package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

// Adnotarea 1: @Tag
@Tag("BlackBoxTesting")
class ProductServiceTest {

    private ProductService productService;
    private Repository<Integer, Product> testRepo;

    @BeforeEach
    void setUp() {
        testRepo = new AbstractRepository<Integer, Product>() {
            @Override
            protected Integer getId(Product entity) {
                return entity.getId();
            }
        };
        productService = new ProductService(testRepo, new ProductValidator());
    }

    // ==========================================
    // ECP TESTS (5 Test Cases din Fisierul Excel)
    // ==========================================

    // Adnotarea 2: @DisplayName
    @Test
    @DisplayName("TC01_ECP_Valid: Adaugare produs cu valori valide (Pret: 15.0, Nume: Cappuccino)")
    void testAddProduct_ECP_Valid() {
        Product p = new Product(100, "Cappuccino", 15.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);
        productService.addProduct(p);
        assertEquals(1, testRepo.findAll().size());
    }

    // Adnotarea 3 & 4: @ParameterizedTest si @CsvSource
    @ParameterizedTest(name = "{0}: Pret = {1}")
    @CsvSource({
            "TC02_ECP_Invalid, -5.0",
            "TC03_ECP_Invalid, 15000.0"
    })
    @DisplayName("ECP Invalid Price: Testam valori din clasele invalide de pret")
    void testAddProduct_ECP_InvalidPrice(String testId, double invalidPrice) {
        Product p = new Product(101, "Latte", invalidPrice, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);
        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }

    @Test
    @DisplayName("TC04_ECP_Invalid: Numele este un string gol (Clasa invalida I1_nume)")
    void testAddProduct_ECP_InvalidName_Empty() {
        Product p = new Product(103, "", 12.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);
        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }

    @Test
    @DisplayName("TC05_ECP_Invalid: Numele contine 256 caractere (Clasa invalida I2_nume)")
    void testAddProduct_ECP_InvalidName_TooLong() {
        String longName = "A".repeat(256);
        Product p = new Product(104, longName, 12.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);
        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }


    // ==========================================
    // BVA TESTS (7 Test Cases din Fisierul Excel)
    // ==========================================

    @ParameterizedTest(name = "{0}: Pret = {1}")
    @CsvSource({
            "TC01_BVA_Valid, 0.01",
            "TC02_BVA_Valid, 10000.0"
    })
    @DisplayName("BVA Valid Price: Valorile pe limitele valide pentru pret")
    void testAddProduct_BVA_ValidPrice(String testId, double validPrice) {
        Product p = new Product(105, "Cafea Limita", validPrice, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        productService.addProduct(p);
        assertEquals(validPrice, testRepo.findOne(105).getPret());
    }

    @ParameterizedTest(name = "{0}: Pret = {1}")
    @CsvSource({
            "TC03_BVA_Invalid, 0.0",
            "TC04_BVA_Invalid, 10000.01"
    })
    @DisplayName("BVA Invalid Price: Valorile imediat in afara limitelor pentru pret")
    void testAddProduct_BVA_InvalidPrice(String testId, double invalidPrice) {
        Product p = new Product(106, "Cafea Peste Limita", invalidPrice, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }

    @Test
    @DisplayName("TC05_BVA_Valid: Lungimea numelui este 1 caracter (Limita inferioara valida)")
    void testAddProduct_BVA_ValidName_LowerBound() {
        Product p = new Product(107, "A", 15.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);
        productService.addProduct(p);
        assertEquals("A", testRepo.findOne(107).getNume());
    }

    @Test
    @DisplayName("TC06_BVA_Valid: Lungimea numelui este 255 caractere (Limita superioara valida)")
    void testAddProduct_BVA_ValidName_UpperBound() {
        String maxLengthName = "A".repeat(255);
        Product p = new Product(108, maxLengthName, 15.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);
        productService.addProduct(p);
        assertEquals(maxLengthName, testRepo.findOne(108).getNume());
    }

    @Test
    @DisplayName("TC07_BVA_Invalid: Lungimea numelui este 0 (Imediat sub limita valida)")
    void testAddProduct_BVA_InvalidName_BelowLowerBound() {
        Product p = new Product(109, "", 15.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);
        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }
}