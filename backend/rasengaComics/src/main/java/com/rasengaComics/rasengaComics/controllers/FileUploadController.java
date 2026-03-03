package com.rasengaComics.rasengaComics.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {
    
    // Directorio de uploads (relativo a src/main/resources/static)
    private static final String UPLOAD_DIR = "uploads/products";
    
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, String> response = new HashMap<>();
            
            // Validar que el archivo no esté vacío
            if (file.isEmpty()) {
                response.put("error", "El archivo está vacío");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("error", "El archivo debe ser una imagen");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Generar nombre único para el archivo
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName != null ? 
                originalFileName.substring(originalFileName.lastIndexOf(".")) : ".jpg";
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            
            // Crear directorio si no existe
            Path uploadPath = Paths.get("src/main/resources/static/" + UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            
            // Guardar archivo
            Path filePath = uploadPath.resolve(newFileName);
            Files.write(filePath, file.getBytes());
            
            // Retornar ruta relativa para acceder al archivo
            String imageUrl = "/" + UPLOAD_DIR + "/" + newFileName;
            response.put("url", imageUrl);
            response.put("message", "Imagen subida exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al subir la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error inesperado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
