package jaz_s32706_nbp.pjatk.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jaz_s32706_nbp.pjatk.dto.ApiErrorResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(BadRequestException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(NbpApiException.class)
    public ResponseEntity<ApiErrorResponse> handleNbpApiException(NbpApiException exception) {
        HttpStatusCode status = mapNbpStatus(exception.getStatusCode());
        return buildResponse(status, exception.getMessage());
    }

    @ExceptionHandler(NbpUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleNbpUnavailableException(NbpUnavailableException exception) {
        return buildResponse(HttpStatus.BAD_GATEWAY, exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .orElse("Nieprawidłowe parametry zapytania");
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Nieprawidłowy format parametru: " + exception.getName());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Brak wymaganego parametru: " + exception.getParameterName());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception exception) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Wystąpił nieoczekiwany błąd aplikacji");
    }

    private HttpStatusCode mapNbpStatus(HttpStatusCode statusCode) {
        if (statusCode.is4xxClientError()) {
            return statusCode;
        }
        return HttpStatus.BAD_GATEWAY;
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatusCode status, String message) {
        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                getReasonPhrase(status),
                message,
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(response);
    }

    private String getReasonPhrase(HttpStatusCode status) {
        HttpStatus resolvedStatus = HttpStatus.resolve(status.value());
        if (resolvedStatus == null) {
            return "HTTP " + status.value();
        }
        return resolvedStatus.getReasonPhrase();
    }
}
