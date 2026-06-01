package hr.algebra.photostore.service;

import hr.algebra.photostore.exceptions.CategoryDeleteException;
import hr.algebra.photostore.exceptions.ResourceNotFoundException;
import hr.algebra.photostore.model.Category;
import hr.algebra.photostore.repository.CategoryRepository;
import hr.algebra.photostore.repository.PictureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PictureRepository pictureRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           PictureRepository pictureRepository) {
        this.categoryRepository = categoryRepository;
        this.pictureRepository = pictureRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id));
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteById(Long id) {
        long pictureCount = pictureRepository.countByCategoryId(id);
        if (pictureCount > 0) {
            throw new CategoryDeleteException(
                    "Cannot delete category with existing pictures. Remove the pictures first.");
        }
        categoryRepository.deleteById(id);
    }
}