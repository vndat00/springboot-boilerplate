package com.vndat00.springbootboilerplate.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "local")
public class LocalProperties {
    private LocalProperties() {
        /* This utility class should not be instantiated */
    }

    private static String baseDir;
}
