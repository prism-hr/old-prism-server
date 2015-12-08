package uk.co.alumeni.prism.rest.representation.advert;

import uk.co.alumeni.prism.domain.definitions.PrismDurationUnit;

import java.math.BigDecimal;

public class AdvertFinancialDetailRepresentation {

    private String currency;

    private PrismDurationUnit interval;

    private Integer hoursWeekMinimum;

    private Integer hoursWeekMaximum;

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

    public Integer getHoursWeekMinimum() {
        return hoursWeekMinimum;
    }

    public void setHoursWeekMinimum(Integer hoursWeekMinimum) {
        this.hoursWeekMinimum = hoursWeekMinimum;
    }

    public Integer getHoursWeekMaximum() {
        return hoursWeekMaximum;
    }

    public void setHoursWeekMaximum(Integer hoursWeekMaximum) {
        this.hoursWeekMaximum = hoursWeekMaximum;
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

    public AdvertFinancialDetailRepresentation withHoursWeekMinimum(Integer hoursWeekMinimum) {
        this.hoursWeekMinimum = hoursWeekMinimum;
        return this;
    }

    public AdvertFinancialDetailRepresentation withHoursWeekMaximum(Integer hoursWeekMaximum) {
        this.hoursWeekMaximum = hoursWeekMaximum;
        return this;
    }

    public AdvertFinancialDetailRepresentation withMinimum(BigDecimal minimum) {
        this.minimum = minimum;
        return this;
    }

    public AdvertFinancialDetailRepresentation withMaximum(BigDecimal maximum) {
        this.maximum = maximum;
        return this;
    }

}
