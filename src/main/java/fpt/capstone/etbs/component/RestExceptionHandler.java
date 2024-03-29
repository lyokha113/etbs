package fpt.capstone.etbs.component;

import fpt.capstone.etbs.payload.ApiError;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log.trace(ex.getClass().getName(), ex);
    final List<String> errors = new ArrayList<>();
    for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.add(error.getField() + ": " + error.getDefaultMessage());
    }
    for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
      errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
    }
    final ApiError apiError =
        new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
    return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
  }

  @Override
  protected ResponseEntity<Object> handleBindException(
      final BindException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log.trace(ex.getClass().getName(), ex);
    final List<String> errors = new ArrayList<>();
    for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.add(error.getField() + ": " + error.getDefaultMessage());
    }
    for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
      errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
    }
    final ApiError apiError =
        new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
    return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(
      final TypeMismatchException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log.trace(ex.getClass().getName(), ex);
    final String error =
        ex.getValue()
            + " value for "
            + ex.getPropertyName()
            + " should be of type "
            + ex.getRequiredType();
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(
      final MissingServletRequestPartException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log.trace(ex.getClass().getName(), ex);
    final String error = ex.getRequestPartName() + " part is missing";
    final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      final MissingServletRequestParameterException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log.trace(ex.getClass().getName(), ex);
    final String error = ex.getParameterName() + " parameter is missing";
    final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      final HttpMessageNotReadableException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log.trace(ex.getClass().getName(), ex);
    final String error = ex.getHttpInputMessage() + " http message is missing";
    final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      final NoHandlerFoundException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log.trace(ex.getClass().getName(), ex);
    final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
    final ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), error);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      final HttpRequestMethodNotSupportedException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log.trace(ex.getClass().getName(), ex);
    final StringBuilder builder = new StringBuilder();
    builder.append(ex.getMethod());
    builder.append(" method is not supported for this request. Supported methods are ");
    ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
    final ApiError apiError =
        new ApiError(HttpStatus.METHOD_NOT_ALLOWED, ex.getLocalizedMessage(), builder.toString());
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      final HttpMediaTypeNotSupportedException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log.trace(ex.getClass().getName(), ex);
    final StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));

    final ApiError apiError =
        new ApiError(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            ex.getLocalizedMessage(),
            builder.substring(0, builder.length() - 2));
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      final MethodArgumentTypeMismatchException ex, final WebRequest request) {
    log.trace(ex.getClass().getName(), ex);
    final String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();
    final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolation(
      final ConstraintViolationException ex, final WebRequest request) {
    logger.info(ex.getClass().getName());
    final List<String> errors = new ArrayList<>();
    for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      errors.add(
          violation.getRootBeanClass().getName()
              + " "
              + violation.getPropertyPath()
              + ": "
              + violation.getMessage());
    }
    final ApiError apiError =
        new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
    log.trace(ex.getClass().getName(), ex);
    final ApiError apiError =
        new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "Error occurred");
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }
}
