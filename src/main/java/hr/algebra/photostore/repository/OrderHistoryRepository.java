package hr.algebra.photostore.repository;

import hr.algebra.photostore.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN FETCH o.user ORDER BY o.createdAt DESC")
    List<Order> findAllOrders();

    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.user.email LIKE %:email% ORDER BY o.createdAt DESC")
    List<Order> findByEmail(@Param("email") String email);

    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.createdAt >= :from AND o.createdAt <= :to ORDER BY o.createdAt DESC")
    List<Order> findByDateRange(@Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);

    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.user.email LIKE %:email% AND o.createdAt >= :from AND o.createdAt <= :to ORDER BY o.createdAt DESC")
    List<Order> findByEmailAndDateRange(@Param("email") String email,
                                        @Param("from") LocalDateTime from,
                                        @Param("to") LocalDateTime to);
}