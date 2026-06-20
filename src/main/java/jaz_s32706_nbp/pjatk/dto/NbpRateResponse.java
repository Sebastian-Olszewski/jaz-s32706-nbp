package jaz_s32706_nbp.pjatk.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NbpRateResponse(
        String no,
        LocalDate effectiveDate,
        BigDecimal mid
) {
}
