package com.vndat00.springbootboilerplate.advice;

import com.vndat00.springbootboilerplate.common.CommonFunction;
import com.vndat00.springbootboilerplate.constant.MessageConstant;
import com.vndat00.springbootboilerplate.exception.*;
import com.vndat00.springbootboilerplate.payload.general.ResponseDataAPI;
import com.vndat00.springbootboilerplate.payload.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.rmi.ServerError;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private static final String SERVER_ERROR_CODE = "ERR.SERVER";
  private static final String INVALID_ARGUMENT = "ERR.INVALID_ARGUMENT";
  private static final String ACCESS_DENIED_ERROR = "forbidden_error";

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ResponseDataAPI> notFoundException(
      NotFoundException ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ResponseDataAPI> badRequestException(
      BadRequestException ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ResponseDataAPI> forbiddenException(
      ForbiddenException ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ResponseDataAPI> unauthorizedException(
      UnauthorizedException ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ResponseDataAPI> conflictException(
      ConflictException ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(UnprocessableRequestException.class)
  public ResponseEntity<ResponseDataAPI> unprocessableRequestException(
      UnprocessableRequestException ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    if (!StringUtils.isEmpty(ex.getDetail())) {
      error.setMessage(ex.getDetail());
    }
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(ServerError.class)
  public ResponseEntity<ResponseDataAPI> serverError(ServerError ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    String msg =
        String.format(
            "Parameter '%s' should be of type %s",
            ex.getName(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
    ErrorResponse error = new ErrorResponse(INVALID_ARGUMENT, msg);
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ResponseDataAPI> accessDeniedExceptionHandle(
      AccessDeniedException ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ACCESS_DENIED_ERROR);
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(RateLimitException.class)
  public ResponseEntity<ResponseDataAPI> rateLimitExceptionHandle(
      RateLimitException ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.TOO_MANY_REQUESTS);
  }

  @ExceptionHandler
  protected ResponseEntity<Object> handleBindException(
      BindException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

    List<ObjectError> listError = ex.getBindingResult().getAllErrors();
    ObjectError objectError = listError.get(listError.size() - 1);
    String error = CommonFunction.convertToSnakeCase(Objects.requireNonNull(objectError.getCode()));
    String fieldName = CommonFunction.convertToSnakeCase(((FieldError) objectError).getField());
    String resource = CommonFunction.convertToSnakeCase(objectError.getObjectName());

    ErrorResponse errorResponse = CommonFunction.getValidationError(resource, fieldName, error);

    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(errorResponse);

    return new ResponseEntity<>(responseDataAPI, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResponseDataAPI> globalExceptionHandler(Exception ex) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected @Nullable ResponseEntity<Object> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ErrorResponse error =
        CommonFunction.getExceptionError(MessageConstant.MAXIMUM_UPLOAD_SIZE_EXCEEDED);
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(
        responseDataAPI, HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    List<ObjectError> listError = ex.getBindingResult().getAllErrors();
    ObjectError objectError = listError.get(listError.size() - 1);
    String error = CommonFunction.convertToSnakeCase(Objects.requireNonNull(objectError.getCode()));
    String fieldName = CommonFunction.convertToSnakeCase(((FieldError) objectError).getField());
    String resource = CommonFunction.convertToSnakeCase(objectError.getObjectName());

    ErrorResponse errorResponse = CommonFunction.getValidationError(resource, fieldName, error);

    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(errorResponse);

    return new ResponseEntity<>(responseDataAPI, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(MessageConstant.PAGE_NOT_FOUND);
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.NOT_FOUND);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ErrorResponse error = new ErrorResponse(SERVER_ERROR_CODE, ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getMethod());
    builder.append(" method is not supported for this request. Supported methods are ");
    Objects.requireNonNull(ex.getSupportedHttpMethods())
        .forEach(t -> builder.append(t).append(" "));

    ErrorResponse error = new ErrorResponse(SERVER_ERROR_CODE, builder.toString());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);

    return new ResponseEntity<>(responseDataAPI, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));

    ErrorResponse error = new ErrorResponse(SERVER_ERROR_CODE, builder.toString());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ErrorResponse error = new ErrorResponse(SERVER_ERROR_CODE, ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ResponseDataAPI> constraintViolationException(
      ConstraintViolationException ex, HttpServletRequest request) {
    Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

    if (violations.isEmpty()) {
      ErrorResponse error = new ErrorResponse(INVALID_ARGUMENT, ex.getMessage());
      ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
      return new ResponseEntity<>(responseDataAPI, HttpStatus.BAD_REQUEST);
    }

    ConstraintViolation<?> violation = violations.iterator().next();
    String propertyPath = violation.getPropertyPath().toString();

    String fieldName =
        propertyPath.contains(".")
            ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1)
            : propertyPath;

    String pathWithoutField = propertyPath.substring(0, propertyPath.lastIndexOf('.'));
    String resource =
        pathWithoutField.contains(".")
            ? pathWithoutField.substring(pathWithoutField.lastIndexOf('.') + 1)
            : pathWithoutField;

    String errorCode =
        violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();

    ErrorResponse errorResponse =
        CommonFunction.getValidationError(
            CommonFunction.convertToSnakeCase(resource),
            CommonFunction.convertToSnakeCase(fieldName),
            CommonFunction.convertToSnakeCase(errorCode));

    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(errorResponse);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.BAD_REQUEST);
  }
}
