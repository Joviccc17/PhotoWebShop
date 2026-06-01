package hr.algebra.photostore.repository;

import hr.algebra.photostore.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN FETCH o.user JOIN FETCH o.items i JOIN FETCH i.picture WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o JOIN FETCH o.user JOIN FETCH o.items i JOIN FETCH i.picture ORDER BY o.createdAt DESC")
    List<Order> findAllByOrderByCreatedAtDesc();
}