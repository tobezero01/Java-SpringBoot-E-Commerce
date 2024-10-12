package com.eshop.admin.shippingrate;

import com.eshop.admin.exception.ShippingRateNotFoundException;
import com.eshop.admin.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ExceptionCollector;

import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class ShippingRateServiceTest {

    @MockBean private ShippingRateRepository shippingRateRepository;
    @MockBean private ProductRepository productRepository;

    @InjectMocks
    private ShippingRateService shippingRateService;

    @Test
    public void testCalculateShippingCost_NoRateFound() {
        Integer productId = 1;
        Integer countryId = 234;
        String state = "ADBC";
        Mockito.when(shippingRateRepository.findByCountryAndState(countryId, state)).thenReturn(null);
        assertThrows(ShippingRateNotFoundException.class, new ExceptionCollector.Executable() {

            @Override
            public void execute() throws Throwable {
                shippingRateService.calculateShippingCost(productId, countryId, state);
            }
        });
    }

    @Test
    public void testCalculateShippingCost_RateFound() {

    }

    private void assertThrows(Class<ShippingRateNotFoundException> shippingRateNotFoundExceptionClass, ExceptionCollector.Executable executable) {
    }
}
