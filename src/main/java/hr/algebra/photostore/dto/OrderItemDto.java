package hr.algebra.photostore.dto;

import hr.algebra.photostore.model.OrderItem;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        Long pictureId,
        String pictureTitle,
        String pictureImageUrl,
        Integer quantity,
        BigDecimal priceAtPurchase
) {
    public static OrderItemDto from(OrderItem item) {
        return new OrderItemDto(
                item.getId(),
                item.getPicture().getId(),
                item.getPicture().getTitle(),
                item.getPicture().getImageUrl(),
                item.getQuantity(),
                item.getPriceAtPurchase());
    }
}