package jaz_s32706_nbp.pjatk.dto;

import java.util.List;

public record NbpExchangeRatesResponse(
        String table,
        String currency,
        String code,
        List<NbpRateResponse> rates
) {
}
