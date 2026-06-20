package jaz_s32706_nbp.pjatk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jaz_s32706_nbp.pjatk.dto.ApiErrorResponse;
import jaz_s32706_nbp.pjatk.dto.AverageRateResponse;
import jaz_s32706_nbp.pjatk.service.CurrencyRateService;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/currency-rates")
@Tag(name = "Kursy walut NBP", description = "Endpoint do obliczania średniego kursu waluty na podstawie danych z API NBP")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;

    public CurrencyRateController(CurrencyRateService currencyRateService) {
        this.currencyRateService = currencyRateService;
    }

    @GetMapping(value = "/average", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Oblicza średni kurs waluty",
            description = "Pobiera kursy średnie z API NBP dla podanej waluty i przedziału dat, oblicza średnią arytmetyczną oraz zapisuje wykonane zapytanie w bazie H2"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Poprawnie obliczono średni kurs", content = @Content(schema = @Schema(implementation = AverageRateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowa waluta, format daty albo zakres dat", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NBP nie posiada danych dla podanych parametrów", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "API NBP jest niedostępne albo zwróciło błąd serwerowy", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<AverageRateResponse> getAverageRate(
            @Parameter(description = "Trzyliterowy kod waluty ISO 4217", example = "USD", required = true)
            @RequestParam
            @Pattern(regexp = "^[A-Za-z]{3}$", message = "Waluta musi mieć trzyliterowy kod, np. USD")
            String currency,

            @Parameter(description = "Data rozpoczęcia przedziału w formacie YYYY-MM-DD", example = "2024-01-01", required = true)
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "Data końca przedziału w formacie YYYY-MM-DD", example = "2024-01-31", required = true)
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return ResponseEntity.ok(currencyRateService.calculateAverageRate(currency, startDate, endDate));
    }
}
