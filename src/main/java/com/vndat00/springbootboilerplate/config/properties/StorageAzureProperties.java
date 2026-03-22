package com.vndat00.springbootboilerplate.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "storage.azure")
public class StorageAzureProperties {
    private String accountName;
    private String accountKey;
    private String endpoint;
    private String connectionString;
    private String container = "app-storage";
    private Integer sasExpiryMinutes = 10;
}
