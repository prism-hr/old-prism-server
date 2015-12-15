package uk.co.alumeni.prism.rest.dto.advert;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.format.annotation.NumberFormat;

import uk.co.alumeni.prism.domain.definitions.PrismAdvertBenefit;
import uk.co.alumeni.prism.domain.definitions.PrismDurationUnit;
import uk.co.alumeni.prism.domain.definitions.PrismPaymentOption;

public class AdvertFinancialDetailDTO {

    @Min(1)
    @Max(48)
    private Integer hoursWeekMinimum;

    @Min(1)
    @Max(48)
    private Integer hoursWeekMaximum;

    private PrismPaymentOption paymentOption;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal minimum;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal maximum;

    private String currency;

    private PrismDurationUnit interval;

    private List<PrismAdvertBenefit> benefits;

    private String benefitsDescription;

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

    public PrismDurationUnit getInterval() {
        return interval;
    }

    public void setInterval(PrismDurationUnit interval) {
        this.interval = interval;
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

}
