package com.example.imageprocessingapi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "image_metadata")
public class ImageMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false, unique = true)
    private String fileName;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private boolean converted;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    // Default constructor required by JPA
    public ImageMetadata() {
        this.uploadedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public boolean isConverted() { return converted; }
    public void setConverted(boolean converted) { this.converted = converted; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}