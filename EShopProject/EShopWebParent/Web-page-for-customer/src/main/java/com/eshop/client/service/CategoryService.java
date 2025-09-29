package com.eshop.client.service;

import com.eshop.client.dto.CategoryNodeDTO;
import com.eshop.client.exception.CategoryNotFoundException;
import com.eshop.client.repository.CategoryRepository;
import com.eshop.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> listNoChildrenCategories() {
        List<Category> enabled = categoryRepository.findAllEnabled();
        List<Category> res = new ArrayList<>();
        for (Category category : enabled) {
            Set<Category> ch = category.getChildren();
            if (ch == null || ch.isEmpty()) res.add(category);
        }
        return res;
    }

    public Category getCategory(String alias) throws CategoryNotFoundException {
        Category c = categoryRepository.findByAliasEnabled(alias);
        if (c == null) throw new CategoryNotFoundException("Category not found with alias " + alias);
        return c;
    }

    public List<CategoryNodeDTO> listCategoryTree() {
        List<Category> categories = categoryRepository.findAllEnabled(); // tất cả đã enabled

        // map id -> node dto
        Map<Integer, CategoryNodeDTO> nodes = new LinkedHashMap<>();
        for (Category category : categories) {
            nodes.put(category.getId(), CategoryNodeDTO.leaf(
                    category.getId(), category.getName(), category.getAlias(), category.getImage()
            ));
        }
        // gắn children vào parent
        List<CategoryNodeDTO> roots = new ArrayList<>();
        for (Category category : categories) {
            var node = nodes.get(category.getId());
            if (category.getParent() == null) {
                roots.add(node);
            } else {
                var parentNode = nodes.get(category.getParent().getId());
                if (parentNode != null) {
                    parentNode.children().add(node);
                } else {
                    // parent bị disable hoặc không load → đẩy tạm vào roots để không mất dữ liệu
                    roots.add(node);
                }
            }
        }
        // sort children theo tên (nếu muốn)
        sortTreeByName(roots);
        return roots;
    }

    private void sortTreeByName(List<CategoryNodeDTO> list) {
        list.sort(Comparator.comparing(CategoryNodeDTO::name, String.CASE_INSENSITIVE_ORDER));
        for (CategoryNodeDTO node : list) {
            sortTreeByName(node.children());
        }
    }

    public List<Category> listTopLevelParents() {
        return categoryRepository.findAllTopLevel();
    }

    public List<Category> getAncestorsPath(Integer id) throws CategoryNotFoundException {
        Category cur = categoryRepository.getByIdOrThrow(id);
        LinkedList<Category> path = new LinkedList<>();
        while (cur != null) {
            path.addFirst(cur);
            cur = cur.getParent();
        }
        return path;
    }
}
