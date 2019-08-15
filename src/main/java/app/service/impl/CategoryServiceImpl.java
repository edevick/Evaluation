package app.service.impl;

import app.model.Category;
import app.repository.CategoryRepository;
import app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository repository;

    @Override
    public List<Category> findAllCategories() {
        return repository.findAll();
    }

    @Override
    public Category findCategoryByName(String name) {
        return repository.findCategoryByName(name);
    }

    @Override
    public void save(Category category) {
        repository.saveAndFlush(category);
    }

    @Override
    public void delete(Category category) {
        repository.delete(category);
    }

    @Override
    public Category findCategoryById(long id) {
        return repository.findCategoryById(id);
    }
}
