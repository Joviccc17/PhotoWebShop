package hr.algebra.photostore.service;

import hr.algebra.photostore.model.Picture;
import hr.algebra.photostore.repository.PictureRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PictureService {

    private final PictureRepository pictureRepository;

    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    public List<Picture> getAllPictures() {
        return pictureRepository.findAll();
    }

    public Picture getPictureById(Long id) {
        return pictureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Picture not found with that id: " + id));
    }

    public List<Picture> getPicturesByCategory(Long categoryId) {
        return pictureRepository.findByCategoryId(categoryId);
    }

    public Page<Picture> getPagedPictures(int page, Long categoryId) {
        PageRequest pageable = PageRequest.of(page, 6);
        if (categoryId != null) {
            return pictureRepository.findByCategoryId(categoryId, pageable);
        }
        return pictureRepository.findAll(pageable);
    }

    public Picture save(Picture picture) {
        return pictureRepository.save(picture);
    }

    public void deleteById(Long id) {
        pictureRepository.deleteById(id);
    }
}
