package com.example.knotejava.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "knote")
@Getter
@Setter
public class NoteProperties {

    @Value("${minio.schema:http}")
    private String minioSchema;

    @Value("${minio.host:localhost}")
    private String minioHost;

    @Value("${minio.port:9000}")
    private String minioPort;

    @Value("${minio.bucket:image-storage}")
    private String minioBucket;

    @Value("${minio.access.key:}")
    private String minioAccessKey;

    @Value("${minio.secret.key:}")
    private String minioSecretKey;

    @Value("${minio.useSSL:false}")
    private boolean minioUseSSL;

    @Value("${minio.reconnect.enabled:true}")
    private boolean minioReconnectEnabled;
}
