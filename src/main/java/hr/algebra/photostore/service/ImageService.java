package hr.algebra.photostore.service;

import hr.algebra.photostore.exceptions.ImageUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService {

    private static final String IMAGE_DIR = "src/main/resources/static/images/";

    public List<String> getAllImageNames() {
        List<String> images = new ArrayList<>();
        File folder = new File(IMAGE_DIR);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) ->
                    name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                            name.endsWith(".png") || name.endsWith(".gif") ||
                            name.endsWith(".webp")
            );

            if (files != null) {
                for (File file : files) {
                    images.add(file.getName());
                }
            }
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
}