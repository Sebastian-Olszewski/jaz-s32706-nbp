package jaz_s32706_nbp.pjatk.exception;

import org.springframework.http.HttpStatusCode;

public class NbpApiException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public NbpApiException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
