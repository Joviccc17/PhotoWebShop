package hr.algebra.photostore.service;

import hr.algebra.photostore.model.CartItem;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@SessionScope
public class ShoppingCart {

    private final Map<Long, CartItem> items = new LinkedHashMap<>();

    public void addItem(Long pictureId, String title, BigDecimal price, String imageUrl, int quantity) {

        if (items.containsKey(pictureId)) {
            CartItem existingItem = items.get(pictureId);
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            items.put(pictureId, new CartItem(pictureId, title, price, imageUrl, quantity));
        }
    }

    public void updateQuantity(Long pictureId, int quantity) {

        if (quantity <= 0) {
            items.remove(pictureId);
        } else {
            CartItem item = items.get(pictureId);
            if (item != null) {
                item.setQuantity(quantity);
            }
        }
    }

    public void removeItem(Long pictureId) {

        items.remove(pictureId);
    }

    public void clear() {
        items.clear();
    }

    public Collection<CartItem> getItems() {
        return items.values();
    }

    public int getTotalItems() {
        return items.values().stream()
                .mapToInt(CartItem::getQuantity).sum();

    }

    public BigDecimal getTotalPrice() {

        return items.values().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
