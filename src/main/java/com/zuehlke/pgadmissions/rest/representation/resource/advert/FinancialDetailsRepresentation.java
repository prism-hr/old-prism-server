package com.zuehlke.pgadmissions.rest.representation.resource.advert;

import java.math.BigDecimal;

import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;

public class FinancialDetailsRepresentation {

    private String currency;

    private PrismDurationUnit interval;

    private BigDecimal minimum;

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
