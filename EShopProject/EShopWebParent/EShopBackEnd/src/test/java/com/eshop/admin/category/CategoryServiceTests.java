package com.eshop.admin.category;

import com.eshop.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

//used to test service
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @MockBean
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void testCheckUniqueReturnDuplicateName() {
        Integer id = null;
        String  name = "Desktops";
        String alias = "Desktops";

        Category category = new Category(id , name, alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(category);
        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUniqueCategory(id, name ,alias);

        assertThat(result).isEqualTo("DuplicateName");
    }


    @Test
    public void testCheckUniqueReturnDuplicateAlias() {
        Integer id = null;
        String  name = "Desktops";
        String alias = "Desktops";

        Category category = new Category(id , name, alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUniqueCategory(id, name ,alias);

        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    public void testCheckUniqueReturnOK() {
        Integer id = null;
        String  name = "Desktops";
        String alias = "Desktops";

        Category category = new Category(id , name, alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUniqueCategory(id, name ,alias);

        assertThat(result).isEqualTo("OK");
    }

    @Test
    public void testCheckUniqueInEditModReturnOK() {
        Integer id = 2;
        String  name = "Desktops";
        String alias = "Desktops";

        Category category = new Category(id , name, alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUniqueCategory(id, name ,alias);

        assertThat(result).isEqualTo("OK");
    }

    @Test
    public void testCheckUniqueInEditModReturnDuplicateName() {
        Integer id = 2;
        String  name = "Desktops";
        String alias = "Desktops";

        Category category = new Category(1 , name, alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(category);
        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUniqueCategory(id, name ,alias);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void testCheckUniqueInEditModReturnDuplicateAlias() {
        Integer id = 2;
        String  name = "Desktops";
        String alias = "Desktops";

        Category category = new Category(1 , name, alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUniqueCategory(id, name ,alias);

        assertThat(result).isEqualTo("DuplicateAlias");
    }
}
