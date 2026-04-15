package drinkshop.it.service.td.breadthfirst;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.ProductService;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceIntTest {
    private ProductValidator productValidator;
    private Repository<Integer, Product> productRepo;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productValidator = new ProductValidator();
        productRepo = new FileProductRepository("data/test_products.txt");
        productService = new ProductService(productRepo, productValidator);
    }

    @AfterEach
    void cleanUp() {
        new File("data/test_products.txt").delete();
    }

    @Test
    @Order(1)
    void testAddValid_withRealObjects() {
        Product product = new Product(100, "Apa Plata", 5.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);

        try{
            productService.addProduct(product);
        }catch (Exception e){
            fail("Invalid add operation " + e);
        }

        assertEquals(1, productRepo.findAll().size());
        assertEquals("Apa Plata", productService.findById(100).getNume());
    }

    @Test
    @Order(2)
    void testAddInvalid_withRealObjects() {
        Product product = new Product(-1, "", -5.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);

        Assertions.assertThrows(ValidationException.class, () -> {
            productService.addProduct(product);
        });

        assertEquals(0, productRepo.findAll().size());
    }
}