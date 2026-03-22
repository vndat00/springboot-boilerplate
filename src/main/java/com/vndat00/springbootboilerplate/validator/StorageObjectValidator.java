package com.vndat00.springbootboilerplate.validator;

import com.vndat00.springbootboilerplate.domain.record.StoragePolicy;
import com.vndat00.springbootboilerplate.payload.request.storage.BackendUploadRequest;
import com.vndat00.springbootboilerplate.payload.request.storage.PrepareUploadRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class StorageObjectValidator {
    public void validate(PrepareUploadRequest request, StoragePolicy policy) {
        // TODO: validate file name, content type, size, etc. based on the policy
    }

    public void validate(BackendUploadRequest request, MultipartFile file, StoragePolicy policy) {
        // TODO: validate file name, content type, size, etc. based on the policy
    }
}
