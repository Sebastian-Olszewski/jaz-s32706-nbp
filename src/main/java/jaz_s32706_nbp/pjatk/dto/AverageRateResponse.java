package jaz_s32706_nbp.pjatk.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Odpowiedź zawierająca obliczony średni kurs waluty oraz dane zapisanego zapytania")
public record AverageRateResponse(
        @Schema(description = "Id zapisanego zapytania w bazie danych", example = "1")
        Long id,

        @Schema(description = "Kod waluty ISO 4217", example = "USD")
        String currency,

        @Schema(description = "Data rozpoczęcia przedziału", example = "2024-01-01")
        LocalDate startDate,

        @Schema(description = "Data końca przedziału", example = "2024-01-31")
        LocalDate endDate,

        @Schema(description = "Obliczony średni kurs waluty", example = "4.0123")
        BigDecimal averageRate,

        @Schema(description = "Data i godzina wykonania zapytania", example = "2026-06-20T09:15:30")
        LocalDateTime queryDateTime
) {
}
