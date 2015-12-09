package uk.co.alumeni.prism.rest.representation.advert;

import java.math.BigDecimal;

import uk.co.alumeni.prism.domain.definitions.PrismDurationUnit;

public class AdvertFinancialDetailRepresentation {

    private PrismDurationUnit interval;

    private Integer hoursWeekMinimum;

    private Integer hoursWeekMaximum;

    private String currency;

    private BigDecimal minimum;

    private BigDecimal maximum;

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public AdvertFinancialDetailRepresentation withInterval(PrismDurationUnit interval) {
        this.interval = interval;
        return this;
    }

    public AdvertFinancialDetailRepresentation withCurrency(String currency) {
        this.currency = currency;
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
