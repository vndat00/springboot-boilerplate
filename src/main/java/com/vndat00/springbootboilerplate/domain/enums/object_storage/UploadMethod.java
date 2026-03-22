package com.vndat00.springbootboilerplate.domain.enums.object_storage;

public enum UploadMethod {
    // Azure Blob Storage
    ABS_DIRECT_SAS,
    ABS_BACKEND_PROXY,

    // Amazon S3
    S3_DIRECT_PRESIGNED_URL,
    S3_BACKEND_PROXY,

    // Google Cloud Storage
    GCS_DIRECT_SIGNED_URL,
    GCS_BACKEND_PROXY,

    // Other providers can be added here
     OTHER_DIRECT,
     OTHER_BACKEND_PROXY
}
