package org.mos.vcs.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/13
 */

@Getter
@Configuration
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private final Minio minio = new Minio();

    @Getter
    @Setter
    public static class Minio {
        private String url;
        private String accessKey;
        private String secretKey;
        private String bucketName;

        private Minio() {
        }
    }

}
