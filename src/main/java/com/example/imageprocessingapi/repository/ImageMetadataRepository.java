package com.example.imageprocessingapi.repository; // Correct package

import com.example.imageprocessingapi.model.ImageMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

// Must extend JpaRepository with correct generics

public interface ImageMetadataRepository extends JpaRepository<ImageMetadata, Long> {
    ImageMetadata findByFileName(String fileName);
}