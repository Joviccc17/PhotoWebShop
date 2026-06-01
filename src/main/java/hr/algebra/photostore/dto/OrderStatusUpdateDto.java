package hr.algebra.photostore.dto;

import hr.algebra.photostore.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateDto(@NotNull OrderStatus status) {}