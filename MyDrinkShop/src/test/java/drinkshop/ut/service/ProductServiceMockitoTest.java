package drinkshop.ut.service;

import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.service.ProductService;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceMockitoTest {

    private Product product;
    private ProductValidator productValidator;
    private Repository<Integer, Product> productRepo;

    private ProductService productService;

    @BeforeEach
    public void setUp(){
        product = mock(Product.class);
        productValidator = mock(ProductValidator.class);
        productRepo = mock(Repository.class);

        productService = new ProductService(productRepo, productValidator);
    }

    @AfterEach
    public void tearDown(){
        productService = null;
        productRepo = null;
        productValidator = null;
        product = null;
    }

    @Test
    @Order(1)
    public void testGetAllValid() {
        Product p1 = mock(Product.class);
        Product p2 = mock(Product.class);
        when(productRepo.findAll()).thenReturn(Arrays.asList(p1, p2));

        assert 2 == productService.getAllProducts().size();

        verify(productValidator, never()).validate(p1);
        verify(productRepo, times(1)).findAll();
    }

    @Test
    @Order(2)
    void testAddProductInvalid() {
        when(product.getId()).thenReturn(-1);
        doThrow(new ValidationException("ID invalid!\n")).when(productValidator).validate(product);
        when(productRepo.save(product)).thenReturn(product);

        try{
            productService.addProduct(product);
        }catch (Exception e){
            assert e.getClass().equals(ValidationException.class);
        }

        verify(product, never()).getId();
        verify(productValidator, times(1)).validate(product);
        verify(productRepo, never()).save(any());
    }

    @Test
    @Order(3)
    void testAddProductValid() {
        Product p1 = mock(Product.class);

        doNothing().when(productValidator).validate(product);
        when(productRepo.save(product)).thenReturn(product);

        try{
            productService.addProduct(product);
        }catch (Exception e){
            fail("Invalid add operation");
        }

        verify(productValidator, times(1)).validate(product);
        verify(productRepo, times(1)).save(product);

        verify(productValidator, times(0)).validate(p1);
        verify(productRepo, never()).save(p1);
    }
}