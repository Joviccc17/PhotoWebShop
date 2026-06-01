package hr.algebra.photostore.service;

import hr.algebra.photostore.enums.Role;
import hr.algebra.photostore.exceptions.UserAlreadyExistsException;
import hr.algebra.photostore.model.User;
import hr.algebra.photostore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String password,
                         String firstName, String lastName, String address) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already registered: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAddress(address);
        user.setRole(Role.USER);

        return userRepository.save(user);
    }
}