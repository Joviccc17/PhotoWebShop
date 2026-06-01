package hr.algebra.photostore.repository;

import hr.algebra.photostore.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findAllByOrderByCreatedAtDesc();

    @Query("SELECT o FROM Order o WHERE " +
            "(:email IS NULL OR o.user.email LIKE %:email%) AND " +
            "(:from IS NULL OR o.createdAt >= :from) AND " +
            "(:to IS NULL OR o.createdAt <= :to) " +
            "ORDER BY o.createdAt DESC")
    List<Order> findWithFilters(@Param("email") String email,
                                @Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);
}
