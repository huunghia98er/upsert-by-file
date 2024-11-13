package org.mos.vcs.controller;

import com.opencsv.exceptions.CsvException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mos.vcs.config.ApplicationProperties;
import org.mos.vcs.dto.ImportResponse;
import org.mos.vcs.service.FileService;
import org.mos.vcs.service.StorageEngineService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/12
 */

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final FileService fileService;
    private final MinioClient minioClient;
    private final ApplicationProperties properties;
    private final StorageEngineService storageEngineService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam MultipartFile file) throws IOException, CsvException {
        ImportResponse response = fileService.uploadFile(file);
        log.info("Import response: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(properties.getMinio().getBucketName())
                            .object(fileName)
                            .build()
            );

            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            StringBuilder content = new StringBuilder();

            int ch;
            while ((ch = reader.read()) != -1) {
                content.append((char) ch);
            }

            byte[] byteArray = content.toString().getBytes(StandardCharsets.UTF_8);

            ByteArrayResource resource = new ByteArrayResource(byteArray);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + ".csv\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .body(resource);

        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
