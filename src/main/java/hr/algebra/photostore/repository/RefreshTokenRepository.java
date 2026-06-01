package hr.algebra.photostore.repository;

import hr.algebra.photostore.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserInfo_Email(String email);
    void deleteByToken(String token);
    void deleteByUserInfo_Email(String email);
}
