package uk.co.alumeni.prism.domain.advert;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.domain.definitions.PrismDurationUnit;
import uk.co.alumeni.prism.domain.definitions.PrismPaymentOption;

@Embeddable
public class AdvertFinancialDetail {

    @Column(name = "pay_option")
    @Enumerated(EnumType.STRING)
    private PrismPaymentOption option;
    
    @Column(name = "pay_interval")
    @Enumerated(EnumType.STRING)
    private PrismDurationUnit interval;

    @Column(name = "pay_hours_week_minimum")
    private Integer hoursWeekMinimum;

    @Column(name = "pay_hours_week_maximum")
    private Integer hoursWeekMaximum;

    @Column(name = "pay_currency")
    private String currency;

    @Column(name = "pay_minimum")
    private BigDecimal minimum;

    @Column(name = "pay_maximum")
    private BigDecimal maximum;

    @Column(name = "pay_minimum_normalized")
    private BigDecimal minimumNormalized;

    @Column(name = "pay_maximum_normalized")
    private BigDecimal maximumNormalized;

    @Column(name = "pay_minimum_normalized_hour")
    private BigDecimal minimumNormalizedHour;

    @Column(name = "pay_maximum_normalized_hour")
    private BigDecimal maximumNormalizedHour;
    
    @Lob
    @Column(name = "pay_benefit")
    private String benefit;

    @Lob
    @Column(name = "pay_benefit_description")
    private String benefitDescription;
    
    @Column(name = "pay_last_conversion_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate lastConversionDate;

    public PrismPaymentOption getOption() {
        return option;
    }

    public void setOption(PrismPaymentOption option) {
        this.option = option;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public BigDecimal getMinimumNormalized() {
        return minimumNormalized;
    }

    public void setMinimumNormalized(BigDecimal minimumNormalized) {
        this.minimumNormalized = minimumNormalized;
    }

    public BigDecimal getMaximumNormalized() {
        return maximumNormalized;
    }

    public void setMaximumNormalized(BigDecimal maximumNormalized) {
        this.maximumNormalized = maximumNormalized;
    }

    public BigDecimal getMinimumNormalizedHour() {
        return minimumNormalizedHour;
    }

    public void setMinimumNormalizedHour(BigDecimal minimumNormalizedHour) {
        this.minimumNormalizedHour = minimumNormalizedHour;
    }

    public BigDecimal getMaximumNormalizedHour() {
        return maximumNormalizedHour;
    }

    public void setMaximumNormalizedHour(BigDecimal maximumNormalizedHour) {
        this.maximumNormalizedHour = maximumNormalizedHour;
    }
    
    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    public String getBenefitDescription() {
        return benefitDescription;
    }

    public void setBenefitDescription(String benefitDescription) {
        this.benefitDescription = benefitDescription;
    }

    public LocalDate getLastConversionDate() {
        return lastConversionDate;
    }

    public void setLastConversionDate(LocalDate lastConversionDate) {
        this.lastConversionDate = lastConversionDate;
    }

}
