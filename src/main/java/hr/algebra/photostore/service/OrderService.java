package hr.algebra.photostore.service;

import hr.algebra.photostore.enums.OrderStatus;
import hr.algebra.photostore.enums.PaymentMethod;
import hr.algebra.photostore.exceptions.InsufficientStockException;
import hr.algebra.photostore.exceptions.ResourceNotFoundException;
import hr.algebra.photostore.model.*;
import hr.algebra.photostore.repository.OrderHistoryRepository;
import hr.algebra.photostore.repository.OrderRepository;
import hr.algebra.photostore.repository.PictureRepository;
import hr.algebra.photostore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PictureRepository pictureRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderNotificationService notificationService;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        PictureRepository pictureRepository,
                        OrderHistoryRepository orderHistoryRepository,
                        OrderNotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.pictureRepository = pictureRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Order createOrder(String userEmail, ShoppingCart cart, PaymentMethod paymentMethod) {
        if (cart.isEmpty()) {
            throw new InsufficientStockException("Cart is empty");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + userEmail));

        Order order = buildOrder(user, paymentMethod);

        for (CartItem cartItem : cart.getItems()) {
            Picture picture = pictureRepository.findById(cartItem.getPictureId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Picture not found: " + cartItem.getPictureId()));

            if (picture.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        "Not enough stock for: " + picture.getTitle());
            }

            picture.setStockQuantity(
                    picture.getStockQuantity() - cartItem.getQuantity());
            pictureRepository.save(picture);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setPicture(picture);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getPrice());
            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        cart.clear();
        notificationService.sendOrderConfirmation(savedOrder);

        return savedOrder;
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + email));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found: " + id));
    }

    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found: " + id));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersWithFilters(String email,
                                            LocalDate dateFrom,
                                            LocalDate dateTo) {
        LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime to = dateTo != null ? dateTo.atTime(23, 59, 59) : null;
        String emailFilter = (email != null && !email.isBlank()) ? email : null;
        return orderHistoryRepository.findWithFilters(emailFilter, from, to);
    }

    private Order buildOrder(User user, PaymentMethod paymentMethod) {
        Order order = new Order();
        order.setUser(user);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(paymentMethod == PaymentMethod.PAYPAL
                ? OrderStatus.PAID
                : OrderStatus.COMPLETED);
        return order;
    }
}