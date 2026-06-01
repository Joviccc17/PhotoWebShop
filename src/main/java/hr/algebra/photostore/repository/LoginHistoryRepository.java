package hr.algebra.photostore.repository;

import hr.algebra.photostore.model.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    List<LoginHistory> findAllByOrderByLoginTimeDesc();
}
