package com.vndat00.springbootboilerplate.constant;

public class MessageConstant {
  // Common
  public static final String INTERNAL_SERVER_ERROR = "internal_server_error";
  public static final String PAGE_NOT_FOUND = "page_not_found";
  public static final String INVALID_REQUEST = "invalid_request";
  public static final String RATE_LIMIT_EXCEEDED = "rate_limit_exceeded";
  public static final String INVALID_PATTERN = "invalid_pattern";
  public static final String BAD_REQUEST = "bad_request";

  // Unauthorized
  public static final String FORBIDDEN_ERROR = "forbidden_error";
  public static final String UNAUTHORIZED = "unauthorized";
  public static final String YOU_MUST_LOGIN_COMPANY = "you_must_login_company";
  public static final String YOU_MUST_LOGIN_SUPERVISOR = "you_must_login_supervisor";
  public static final String YOU_MUST_LOGIN_OWNER = "you_must_login_owner";
  public static final String YOU_MUST_USE_IN_COMPANY = "you_must_use_in_company";
  public static final String YOU_MUST_USE_IN_OWNER = "you_must_use_in_owner";
  public static final String CANNOT_UNLINK_LAST_OAUTH_PROVIDER =
      "cannot_unlink_last_oauth_provider";
  public static final String OAUTH_PROVIDER_NOT_LINKED = "oauth_provider_not_linked";
  public static final String CANNOT_UNLINK_LOCAL_PROVIDER = "cannot_unlink_local_provider";
  public static final String OAUTH_PROVIDER_ALREADY_LINKED = "oauth_provider_already_linked";
  public static final String OAUTH_PROVIDER_LINKED_TO_OTHER_ACCOUNT =
      "oauth_provider_linked_to_other_account";
  public static final String INVALID_OAUTH_TOKEN = "invalid_oauth_token";
  public static final String APPLE_USER_NOT_FOUND = "apple_user_not_found";

  // Conflict error
  public static final String CONFLICT_ACTION = "conflict_action";

  // Upload file
  public static final String FILE_NOT_FORMAT = "file_not_format";
  public static final String UPLOAD_FILE_FAILED = "upload_file_failed";
  public static final String MAXIMUM_UPLOAD_SIZE_EXCEEDED = "maximum_upload_size_exceeded";
  public static final String FILE_IS_DELETED_FAILED = "file_is_deleted_failed";
  public static final String FILE_URL_IS_ERROR = "file_url_is_error";
  public static final String FILE_NOT_FOUND = "file_not_found";
  public static final String COPY_FILE_FAILED = "copy_file_failed";

  // Note
  public static final String NOTE_NOT_FOUND = "note_not_found";

  // Storage Object
  public static final String STORAGE_OBJECT_NOT_FOUND = "storage_object_not_found";
  public static final String STORAGE_OBJECT_NOT_UPLOADED = "storage_object_not_uploaded";
  public static final String STORAGE_BACKEND_UPLOAD_FAILED = "storage_backend_upload_failed";
  public static final String STORAGE_UPLOAD_NOT_FOUND_ON_BLOB = "storage_upload_not_found_on_blob";
}
