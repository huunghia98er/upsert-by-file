package org.mos.vcs.service;

import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mos.vcs.config.ApplicationProperties;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/13
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageEngineService {
    private final MinioClient minioClient;
    private final ApplicationProperties properties;

    public void uploadFile(String filePath, String fileName, String contentType) {
        try {
            boolean isExist = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(properties.getMinio().getBucketName())
                            .build()
            );

            if (!isExist) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(properties.getMinio().getBucketName())
                                .build()
                );
            }

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(properties.getMinio().getBucketName())
                            .object(fileName)
                            .filename(filePath)
                            .contentType(contentType + "; charset=utf-8")
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadFile(String fileName) {
        String filePath = "./data/" + fileName + ".csv";
        try {
            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(properties.getMinio().getBucketName())
                            .object(fileName)
                            .build());
                 OutputStream outputStream = new FileOutputStream(filePath)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                log.info("File đã được tải về thành công: " + filePath);
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }
}
