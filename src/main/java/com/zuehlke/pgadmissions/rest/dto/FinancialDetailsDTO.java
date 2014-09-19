package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;

import java.math.BigDecimal;

public class FinancialDetailsDTO {

    private String currency;

    private DurationUnit interval;

    private BigDecimal minimum;

    private BigDecimal maximum;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public DurationUnit getInterval() {
        return interval;
    }

    public void setInterval(DurationUnit interval) {
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