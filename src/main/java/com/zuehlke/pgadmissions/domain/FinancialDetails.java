package com.zuehlke.pgadmissions.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;

@Embeddable
public class FinancialDetails {

    @Column(name = "fee_interval")
    @Enumerated(EnumType.STRING)
    private DurationUnit interval;

    @Column(name = "fee_currency")
    private String currency;

    @Column(name = "fee_currency_at_locale")
    private String currencyAtLocale;

    @Column(name = "month_fee_minimum_specified")
    private BigDecimal monthMinimumSpecified;

    @Column(name = "month_fee_maximum_specified")
    private BigDecimal monthMaximumSpecified;

    @Column(name = "year_fee_minimum_specified")
    private BigDecimal yearMinimumSpecified;

    @Column(name = "year_fee_maximum_specified")
    private BigDecimal yearMaximumSpecified;

    @Column(name = "month_fee_minimum_at_locale")
    private BigDecimal monthMinimumAtLocale;

    @Column(name = "month_fee_maximum_at_locale")
    private BigDecimal monthMaximumAtLocale;

    @Column(name = "year_fee_minimum_at_locale")
    private BigDecimal yearMinimumAtLocale;

    @Column(name = "year_fee_maximum_at_locale")
    private BigDecimal yearMaximumAtLocale;

    public DurationUnit getInterval() {
        return interval;
    }

    public void setInterval(DurationUnit interval) {
        this.interval = interval;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyAtLocale() {
        return currencyAtLocale;
    }

    public void setCurrencyAtLocale(String currencyAtLocale) {
        this.currencyAtLocale = currencyAtLocale;
    }

    public BigDecimal getMonthMinimumSpecified() {
        return monthMinimumSpecified;
    }

    public void setMonthMinimumSpecified(BigDecimal monthMinimumSpecified) {
        this.monthMinimumSpecified = monthMinimumSpecified;
    }

    public BigDecimal getMonthMaximumSpecified() {
        return monthMaximumSpecified;
    }

    public void setMonthMaximumSpecified(BigDecimal monthMaximumSpecified) {
        this.monthMaximumSpecified = monthMaximumSpecified;
    }

    public BigDecimal getYearMinimumSpecified() {
        return yearMinimumSpecified;
    }

    public void setYearMinimumSpecified(BigDecimal yearMinimumSpecified) {
        this.yearMinimumSpecified = yearMinimumSpecified;
    }

    public BigDecimal getYearMaximumSpecified() {
        return yearMaximumSpecified;
    }

    public void setYearMaximumSpecified(BigDecimal yearMaximumSpecified) {
        this.yearMaximumSpecified = yearMaximumSpecified;
    }

    public BigDecimal getMonthMinimumAtLocale() {
        return monthMinimumAtLocale;
    }

    public void setMonthMinimumAtLocale(BigDecimal monthMinimumAtLocale) {
        this.monthMinimumAtLocale = monthMinimumAtLocale;
    }

    public BigDecimal getMonthMaximumAtLocale() {
        return monthMaximumAtLocale;
    }

    public void setMonthMaximumAtLocale(BigDecimal monthMaximumAtLocale) {
        this.monthMaximumAtLocale = monthMaximumAtLocale;
    }

    public BigDecimal getYearMinimumAtLocale() {
        return yearMinimumAtLocale;
    }

    public void setYearMinimumAtLocale(BigDecimal yearMinimumAtLocale) {
        this.yearMinimumAtLocale = yearMinimumAtLocale;
    }

    public BigDecimal getYearMaximumAtLocale() {
        return yearMaximumAtLocale;
    }

    public void setYearMaximumAtLocale(BigDecimal yearMaximumAtLocale) {
        this.yearMaximumAtLocale = yearMaximumAtLocale;
    }
}
