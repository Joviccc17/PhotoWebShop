package hr.algebra.photostore.dto;

import hr.algebra.photostore.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        String userEmail,
        String userFirstName,
        String userLastName,
        String userAddress,
        String status,
        String paymentMethod,
        LocalDateTime createdAt,
        List<OrderItemDto> items,
        BigDecimal total
) {
    public static OrderDto from(Order o) {
        List<OrderItemDto> dtoItems = o.getItems().stream().map(OrderItemDto::from).toList();
        BigDecimal total = dtoItems.stream()
                .map(i -> i.priceAtPurchase().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new OrderDto(
                o.getId(),
                o.getUser().getEmail(),
                o.getUser().getFirstName(),
                o.getUser().getLastName(),
                o.getUser().getAddress(),
                o.getStatus().name(),
                o.getPaymentMethod().name(),
                o.getCreatedAt(),
                dtoItems,
                total);
    }
}