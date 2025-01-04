package com.carrent.controller;

import com.carrent.service.IpfsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/ipfs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class IpfsController {
    private final IpfsService ipfsService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String hash = ipfsService.uploadFile(file);
            return ResponseEntity.ok(hash);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        }
    }

    @PostMapping("/upload/json")
    public ResponseEntity<String> uploadJson(@RequestBody String jsonContent) {
        try {
            String hash = ipfsService.uploadJson(jsonContent);
            return ResponseEntity.ok(hash);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload JSON: " + e.getMessage());
        }
    }

    @GetMapping(value = "/{hash}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadFile(@PathVariable String hash) {
        try {
            byte[] data = ipfsService.downloadFile(hash);
            return ResponseEntity.ok(data);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
