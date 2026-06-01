package hr.algebra.photostore.service;

import hr.algebra.photostore.exceptions.ImageUploadException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService {

    private static final String IMAGE_DIR = "src/main/resources/static/images/";
    private static final String CLASSPATH_IMAGES = "classpath:/static/images/*.{jpg,jpeg,png,gif,webp}";

    public List<String> getAllImageNames() {
        List<String> images = new ArrayList<>();

        try {
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(CLASSPATH_IMAGES);
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null) {
                    images.add(filename);
                }
            }
        } catch (IOException e) {
            images.addAll(getFallbackImages());
        }

        return images;
    }

    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new ImageUploadException("File is empty");
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(IMAGE_DIR + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        return fileName;
    }

    private List<String> getFallbackImages() {
        List<String> images = new ArrayList<>();
        java.io.File folder = new java.io.File(IMAGE_DIR);
        if (folder.exists() && folder.isDirectory()) {
            java.io.File[] files = folder.listFiles((dir, name) ->
                    name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                            name.endsWith(".png") || name.endsWith(".gif") ||
                            name.endsWith(".webp")
            );
            if (files != null) {
                for (java.io.File file : files) {
                    images.add(file.getName());
                }
            }
        }
        return images;
    }
}