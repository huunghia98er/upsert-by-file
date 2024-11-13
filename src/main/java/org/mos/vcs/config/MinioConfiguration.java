package org.mos.vcs.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/13
 */

@Configuration
public class MinioConfiguration {

    @Bean
    public MinioClient minioClient(ApplicationProperties applicationProperties) {
        return MinioClient.builder()
            .endpoint(applicationProperties.getMinio().getUrl())
            .credentials(
                    applicationProperties.getMinio().getAccessKey(),
                    applicationProperties.getMinio().getSecretKey()
            )
            .build();
    }

}
