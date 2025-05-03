package com.example.imageprocessingapi.service;

import com.example.imageprocessingapi.model.ImageMetadata;
import com.example.imageprocessingapi.repository.ImageMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private final Path uploadDir = Paths.get("uploads");
    
    @Autowired
    private ImageMetadataRepository imageMetadataRepository;

    public ImageService() throws IOException {
        Files.createDirectories(uploadDir);
        Files.createDirectories(uploadDir.resolve("converted"));
    }

    public String saveImage(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Save metadata to database
        ImageMetadata metadata = new ImageMetadata();
        metadata.setFileName(filename);
        metadata.setSize(file.getSize());
        metadata.setConverted(false);
        metadata.setUploadedAt(LocalDateTime.now());
        imageMetadataRepository.save(metadata);
        
        return "File uploaded successfully: " + filename;
    }

    public void resizeImage(String filename) throws IOException {
        Path originalPath = uploadDir.resolve(filename);
        if (!Files.exists(originalPath)) {
            throw new FileNotFoundException("Original file not found: " + filename);
        }

        BufferedImage originalImage = ImageIO.read(originalPath.toFile());
        BufferedImage resized = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(originalImage, 0, 0, 512, 512, null);
        g.dispose();

        Path resizedPath = uploadDir.resolve("converted").resolve(filename);
        ImageIO.write(resized, "jpg", resizedPath.toFile());

        // Update metadata in database
        ImageMetadata metadata = imageMetadataRepository.findByFileName(filename);
        if (metadata != null) {
            metadata.setConverted(true);
            imageMetadataRepository.save(metadata);
        }
    }

    public List<String> listUploadedImages() throws IOException {
        return Files.list(uploadDir)
                .filter(Files::isRegularFile)
                .map(p -> p.getFileName().toString())
                .filter(name -> !name.startsWith("."))
                .collect(Collectors.toList());
    }

    public ResponseEntity<Resource> downloadResizedImage(String filename) throws IOException {
        Path filePath = uploadDir.resolve("converted").resolve(filename);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Converted file not found: " + filename);
        }

        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            throw new FileNotFoundException("File not found: " + filename);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
