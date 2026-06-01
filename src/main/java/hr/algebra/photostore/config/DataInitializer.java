package hr.algebra.photostore.config;

import hr.algebra.photostore.enums.Role;
import hr.algebra.photostore.model.Category;
import hr.algebra.photostore.model.Picture;
import hr.algebra.photostore.model.User;
import hr.algebra.photostore.repository.CategoryRepository;
import hr.algebra.photostore.repository.PictureRepository;
import hr.algebra.photostore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PictureRepository pictureRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.init.admin-password}")
    private String adminPassword;

    @Value("${app.init.user-password}")
    private String userPassword;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           PictureRepository pictureRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.pictureRepository = pictureRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedCategoriesAndPictures();
    }

    private void seedUsers() {
        if (!userRepository.existsByEmail("admin@photostore.com")) {
            User admin = new User();
            admin.setEmail("admin@photostore.com");
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setAddress("123 Admin Street");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        if (!userRepository.existsByEmail("user@photostore.com")) {
            User user = new User();
            user.setEmail("user@photostore.com");
            user.setPasswordHash(passwordEncoder.encode(userPassword));
            user.setFirstName("Dorian");
            user.setLastName("Jovic");
            user.setAddress("Dubrava 154");
            user.setRole(Role.USER);
            userRepository.save(user);
        }
    }

    private void seedCategoriesAndPictures() {
        if (categoryRepository.count() != 0) {
            return;
        }

        Category landscapes = createCategory("Landscapes", "Nature and landscape photography");
        Category portraits = createCategory("Portraits", "Portrait photography");
        Category abstracts = createCategory("Abstract", "Abstract and artistic photography");
        createCategory("Nightlife", "Photography of the great city night life");

        createPicture("Sunset Beach", "Beautiful sunset at the beach",
                "9.99", "https://picsum.photos/seed/sunset/600/400", 100, landscapes);
        createPicture("Mountain View", "Panoramic mountain landscape",
                "14.99", "https://picsum.photos/seed/mountain/600/400", 50, landscapes);
        createPicture("City Portrait", "Urban portrait photography",
                "12.99", "https://picsum.photos/seed/portrait/600/400", 75, portraits);
        createPicture("Abstract Lines", "Geometric abstract composition",
                "19.99", "https://picsum.photos/seed/abstract/600/400", 30, abstracts);
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    private Picture createPicture(String title, String description, String price,
                                  String imageUrl, int stock, Category category) {
        Picture picture = new Picture();
        picture.setTitle(title);
        picture.setDescription(description);
        picture.setPrice(new BigDecimal(price));
        picture.setImageUrl(imageUrl);
        picture.setStockQuantity(stock);
        picture.setCategory(category);
        return pictureRepository.save(picture);
    }
}