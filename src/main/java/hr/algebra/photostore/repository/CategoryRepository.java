package hr.algebra.photostore.repository;

import hr.algebra.photostore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
