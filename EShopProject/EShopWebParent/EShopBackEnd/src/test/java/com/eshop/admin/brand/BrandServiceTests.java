package com.eshop.admin.brand;

import com.eshop.common.entity.Brand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BrandServiceTests {

    @MockBean private BrandRepository brandRepository;

    @InjectMocks private BrandService brandService;

    @Test
    public void testCheckUniqueInNewModeReturnDuplicate() {
        Integer id = null;
        String name = "DELL";
        Brand brand = new Brand(name);
        brand.setLogo("dsfsdf");

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUnique(id, name);
        assertThat(result).isEqualTo("Duplicate");
    }
}
