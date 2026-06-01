package hr.algebra.photostore.dto;

import hr.algebra.photostore.model.Category;

public record CategoryDto(Long id, String name, String description, int pictureCount) {

    public static CategoryDto from(Category c) {
        return new CategoryDto(c.getId(), c.getName(), c.getDescription(), c.getPictures().size());
    }
}