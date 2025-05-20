package com.example.store.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class ImageStorageService {
    private final Path rootLocation = Paths.get("uploads/images/");

    public String saveImage(MultipartFile file){
        try {
            if (!Files.exists(rootLocation)){
                Files.createDirectories(rootLocation);
            }
            String originalFilename = file.getOriginalFilename();
            String sanitizedFilename = originalFilename != null
                    ? originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_")
                    : "image.png";

            String filename = UUID.randomUUID() + "_" + sanitizedFilename;

            Path destination = rootLocation.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException e){
            log.error("Couldn't store file {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to store image file", e);
        }
    }
    //Xoá 1 file ảnh
    public void deleteImage(String filename){
        try {
            Path filePath = rootLocation.resolve(filename);
            if (Files.exists(filePath)){
                Files.delete(filePath);
            } else {
               log.warn("File {} does not exist, skipping delete", filename);
            }
        } catch (IOException e){
            log.error("Couldn't delete file {}", filename, e);
            throw new RuntimeException("Failed to delete image file", e);
        }
    }

    //Cập nhật
    public String updateImage(MultipartFile file, String oldFilename){
        deleteImage(oldFilename);//Xóa ảnh cũ
        return saveImage(file);
    }
}
