package drinkshop.it.service.td.breadthfirst;

import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.service.ProductService;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceLevel1ValidatorIntTest {
    private Product product;
    private ProductValidator productValidator;
    private Repository<Integer, Product> productRepo;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        product = mock(Product.class);
        productValidator = new ProductValidator();
        productRepo = mock(Repository.class);

        productService = new ProductService(productRepo, productValidator);
    }

    @Test
    @Order(1)
    void testAddValid_withRealValidator() {
        when(product.getId()).thenReturn(1);
        when(product.getNume()).thenReturn("Espresso");
        when(product.getPret()).thenReturn(10.0);
        when(productRepo.save(product)).thenReturn(product);

        try{
            productService.addProduct(product);
        }catch (Exception e){
            fail("Invalid add operation");
        }

        verify(productRepo, times(1)).save(product);
        verify(product, atLeastOnce()).getNume();
        verify(product, atLeastOnce()).getPret();
    }

    @Test
    @Order(2)
    void testAddInvalid_withRealValidator() {
        when(product.getId()).thenReturn(-1);
        when(product.getNume()).thenReturn("");
        when(product.getPret()).thenReturn(-5.0);

        Assertions.assertThrows(ValidationException.class, () -> {
            productService.addProduct(product);
        });

        verify(productRepo, never()).save(any());
        verify(product, atLeastOnce()).getId();
    }
}