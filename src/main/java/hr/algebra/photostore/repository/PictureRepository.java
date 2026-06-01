package hr.algebra.photostore.repository;

import hr.algebra.photostore.model.Picture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, Long> {

    List<Picture> findByCategoryId(Long categoryId);
    long countByCategoryId(Long categoryId);
    Page<Picture> findByCategoryId(Long categoryId, Pageable pageable);
}
