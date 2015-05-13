package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class FinancialDetailsDTO {

    @NotEmpty
    private String currency;

    @NotNull
    private PrismDurationUnit interval;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal minimum;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal maximum;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PrismDurationUnit getInterval() {
        return interval;
    }

    public void setInterval(PrismDurationUnit interval) {
        this.interval = interval;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public void setMinimum(BigDecimal minimum) {
        this.minimum = minimum;
    }

    public BigDecimal getMaximum() {
        return maximum;
    }

    public void setMaximum(BigDecimal maximum) {
        this.maximum = maximum;
    }
}
