package hr.algebra.photostore.dto;

import hr.algebra.photostore.model.Picture;

import java.math.BigDecimal;

public record PictureDto(
        Long id,
        String title,
        String description,
        BigDecimal price,
        String imageUrl,
        Integer stockQuantity,
        Long categoryId,
        String categoryName
) {
    public static PictureDto from(Picture p) {
        return new PictureDto(
                p.getId(), p.getTitle(), p.getDescription(),
                p.getPrice(), p.getImageUrl(), p.getStockQuantity(),
                p.getCategory().getId(), p.getCategory().getName());
    }
}