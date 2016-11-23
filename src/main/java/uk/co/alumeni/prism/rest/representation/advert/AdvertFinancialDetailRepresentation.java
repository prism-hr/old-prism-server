package uk.co.alumeni.prism.rest.representation.advert;

import uk.co.alumeni.prism.domain.definitions.PrismAdvertBenefit;
import uk.co.alumeni.prism.domain.definitions.PrismDurationUnit;
import uk.co.alumeni.prism.domain.definitions.PrismPaymentOption;

import java.math.BigDecimal;
import java.util.List;

public class AdvertFinancialDetailRepresentation {

    private PrismDurationUnit interval;

    private Integer hoursWeekMinimum;

    private Integer hoursWeekMaximum;

    private PrismPaymentOption paymentOption;

    private String currency;

    private BigDecimal minimum;

    private BigDecimal maximum;

    private List<PrismAdvertBenefit> benefits;

    private String benefitsDescription;

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

    public PrismPaymentOption getPaymentOption() {
        return paymentOption;
    }

    public void setPaymentOption(PrismPaymentOption paymentOption) {
        this.paymentOption = paymentOption;
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

    public List<PrismAdvertBenefit> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<PrismAdvertBenefit> benefits) {
        this.benefits = benefits;
    }

    public String getBenefitsDescription() {
        return benefitsDescription;
    }

    public void setBenefitsDescription(String benefitsDescription) {
        this.benefitsDescription = benefitsDescription;
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

    public AdvertFinancialDetailRepresentation withPaymentOption(PrismPaymentOption paymentOption) {
        this.paymentOption = paymentOption;
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
