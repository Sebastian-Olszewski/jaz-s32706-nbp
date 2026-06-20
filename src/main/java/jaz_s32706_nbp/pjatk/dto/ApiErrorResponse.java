package jaz_s32706_nbp.pjatk.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Odpowiedź zwracana w przypadku błędu")
public record ApiErrorResponse(
        @Schema(description = "Kod statusu HTTP zwrócony przez aplikację", example = "404")
        int status,

        @Schema(description = "Nazwa błędu", example = "Not Found")
        String error,

        @Schema(description = "Opis błędu", example = "NBP nie zwróciło danych dla podanej waluty lub podanego przedziału dat")
        String message,

        @Schema(description = "Data i godzina błędu", example = "2026-06-20T09:15:30")
        LocalDateTime timestamp
) {
}
