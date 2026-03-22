package com.vndat00.springbootboilerplate.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "server-properties")
public class ServerProperties {
    private ServerProperties() {}

    private static String serverUrl;

    private static String serverVersion;

    public static void setServerUrl(String url) {
        serverUrl = url;
    }

    public static void setServerVersion(String version) {
        serverVersion = version;
    }

    public static String getServerUrl() {
        return serverUrl;
    }

    public static String getServerVersion() {
        return serverVersion;
    }
}

