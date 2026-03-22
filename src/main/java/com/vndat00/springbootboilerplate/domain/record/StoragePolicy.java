package com.vndat00.springbootboilerplate.domain.record;

import java.util.Set;

public record StoragePolicy(
        long maxFileSize,
        Set<String> allowedContentTypes,
        int maxFiles,
        boolean directUploadAllowed) {}
