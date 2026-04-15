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

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceLevel1RepoIntTest {
    private Product product;
    private ProductValidator productValidator;
    private Repository<Integer, Product> productRepo;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productValidator = new ProductValidator();
        productRepo = new FileProductRepository("data/test_products.txt");
        product = mock(Product.class);
        productService = new ProductService(productRepo, productValidator);
    }

    @AfterEach
    void cleanUp() {
        new File("data/test_products.txt").delete();
    }

    @Test
    @Order(1)
    void testAddValid_withRealRepo() {
        when(product.getId()).thenReturn(999);
        when(product.getNume()).thenReturn("TestBautura");
        when(product.getPret()).thenReturn(15.0);
        when(product.getCategorie()).thenReturn(CategorieBautura.CLASSIC_COFFEE);
        when(product.getTip()).thenReturn(TipBautura.BASIC);

        try{
            productService.addProduct(product);
        }catch (Exception e){
            fail("Invalid add operation " + e);
        }

        verify(product, atLeastOnce()).getNume();
        verify(product, atLeastOnce()).getCategorie();
    }

    @Test
    @Order(2)
    void testAddInvalid_withRealRepo() {
        when(product.getId()).thenReturn(-1);
        when(product.getNume()).thenReturn("");

        Assertions.assertThrows(ValidationException.class, () -> {
            productService.addProduct(product);
        });

        verify(product, atLeastOnce()).getId();
    }
}