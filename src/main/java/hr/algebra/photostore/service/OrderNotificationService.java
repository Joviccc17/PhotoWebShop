package hr.algebra.photostore.service;

import hr.algebra.photostore.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OrderNotificationService {

    private static final Logger log = LoggerFactory.getLogger(OrderNotificationService.class);

    @Async
    public void sendOrderConfirmation(Order order) {
        log.info("Async: Sending order confirmation for order #{}...", order.getId());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("Async: Order confirmation sent for order #{} to {}",
                order.getId(), order.getUser().getEmail());
    }
}