package com.example.knotejava.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "knote")
@Getter
@Setter
public class NoteProperties {
    @Value("${uploadDir:/tmp/uploads/}")
    private String uploadDir;
}
