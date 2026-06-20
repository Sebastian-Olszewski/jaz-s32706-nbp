package jaz_s32706_nbp.pjatk.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import jaz_s32706_nbp.pjatk.dto.AverageRateResponse;
import jaz_s32706_nbp.pjatk.dto.NbpExchangeRatesResponse;
import jaz_s32706_nbp.pjatk.dto.NbpRateResponse;
import jaz_s32706_nbp.pjatk.exception.BadRequestException;
import jaz_s32706_nbp.pjatk.exception.NbpApiException;
import jaz_s32706_nbp.pjatk.exception.NbpUnavailableException;
import jaz_s32706_nbp.pjatk.model.CurrencyQuery;
import jaz_s32706_nbp.pjatk.repository.CurrencyQueryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CurrencyRateService {

    private final RestClient nbpRestClient;
    private final CurrencyQueryRepository currencyQueryRepository;

    public CurrencyRateService(RestClient nbpRestClient, CurrencyQueryRepository currencyQueryRepository) {
        this.nbpRestClient = nbpRestClient;
        this.currencyQueryRepository = currencyQueryRepository;
    }

    @Transactional
    public AverageRateResponse calculateAverageRate(String currency, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        String formattedCurrency = currency.toUpperCase();
        NbpExchangeRatesResponse nbpResponse = getRatesFromNbp(formattedCurrency, startDate, endDate);
        BigDecimal averageRate = calculateAverageRate(nbpResponse.rates());
        CurrencyQuery savedQuery = currencyQueryRepository.save(new CurrencyQuery(
                formattedCurrency,
                startDate,
                endDate,
                averageRate,
                LocalDateTime.now()
        ));
        return mapToResponse(savedQuery);
    }

    private NbpExchangeRatesResponse getRatesFromNbp(String currency, LocalDate startDate, LocalDate endDate) {
        try {
            NbpExchangeRatesResponse response = nbpRestClient.get()
                    .uri("/api/exchangerates/rates/a/{currency}/{startDate}/{endDate}/?format=json", currency, startDate, endDate)
                    .retrieve()
                    .body(NbpExchangeRatesResponse.class);
            if (response == null) {
                throw new NbpApiException(org.springframework.http.HttpStatus.NOT_FOUND, "NBP nie zwróciło danych dla podanych parametrów");
            }
            return response;
        } catch (RestClientResponseException exception) {
            throw new NbpApiException(exception.getStatusCode(), buildNbpErrorMessage(exception.getStatusCode().value()));
        } catch (RestClientException exception) {
            throw new NbpUnavailableException("Nie można połączyć się z API NBP");
        }
    }

    private BigDecimal calculateAverageRate(List<NbpRateResponse> rates) {
        if (rates == null || rates.isEmpty()) {
            throw new NbpApiException(org.springframework.http.HttpStatus.NOT_FOUND, "NBP nie zwróciło kursów dla podanych parametrów");
        }
        BigDecimal sum = rates.stream()
                .map(NbpRateResponse::mid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(rates.size()), 4, RoundingMode.HALF_UP);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Data rozpoczęcia nie może być późniejsza niż data końcowa");
        }
        if (endDate.isAfter(LocalDate.now())) {
            throw new BadRequestException("Data końcowa nie może być z przyszłości");
        }
    }

    private String buildNbpErrorMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "NBP zwróciło błąd 400. Sprawdź poprawność waluty oraz przedziału dat";
            case 404 -> "NBP nie zwróciło danych dla podanej waluty lub podanego przedziału dat";
            default -> "NBP zwróciło błąd HTTP " + statusCode;
        };
    }

    private AverageRateResponse mapToResponse(CurrencyQuery query) {
        return new AverageRateResponse(
                query.getId(),
                query.getCurrency(),
                query.getStartDate(),
                query.getEndDate(),
                query.getAverageRate(),
                query.getQueryDateTime()
        );
    }
}
