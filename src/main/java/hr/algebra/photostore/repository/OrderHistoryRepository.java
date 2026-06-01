package hr.algebra.photostore.repository;

import hr.algebra.photostore.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.user JOIN FETCH o.items i JOIN FETCH i.picture WHERE " +
            "(:email IS NULL OR o.user.email LIKE %:email%) AND " +
            "(:from IS NULL OR o.createdAt >= :from) AND " +
            "(:to IS NULL OR o.createdAt <= :to) " +
            "ORDER BY o.createdAt DESC")
    List<Order> findWithFilters(@Param("email") String email,
                                @Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);
}