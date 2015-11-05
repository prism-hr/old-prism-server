package com.zuehlke.pgadmissions.rest.representation.advert;

import java.math.BigDecimal;

import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;

public class AdvertFinancialDetailRepresentation {

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
    
    public AdvertFinancialDetailRepresentation withCurrency(String currency) {
        this.currency = currency;
        return this;
    }
    
    public AdvertFinancialDetailRepresentation withInterval(PrismDurationUnit interval) {
        this.interval = interval;
        return this;
    }
    
}
