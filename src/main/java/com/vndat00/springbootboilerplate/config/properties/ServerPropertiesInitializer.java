package com.vndat00.springbootboilerplate.config.properties;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerPropertiesInitializer {
    @Value("${server-properties.url}")
    private String serverUrl;

    @Value("${server-properties.version}")
    private String serverVersion;

    @PostConstruct
    public void init() {
        ServerProperties.setServerUrl(serverUrl);
        ServerProperties.setServerVersion(serverVersion);
    }
}

