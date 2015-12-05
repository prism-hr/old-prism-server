package uk.co.alumeni.prism.rest.dto.advert;

import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.NumberFormat;

import uk.co.alumeni.prism.domain.definitions.PrismDurationUnit;

public class AdvertFinancialDetailDTO {

    @NotEmpty
    private String currency;

    @NotNull
    private PrismDurationUnit interval;

    @Min(1)
    @Max(48)
    private Integer hoursWeekMinimum;

    @Min(1)
    @Max(48)
    private Integer hoursWeekMaximum;

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

}
