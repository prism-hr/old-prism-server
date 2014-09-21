package com.zuehlke.pgadmissions.domain;

import java.math.BigDecimal;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;

@Embeddable
public class FinancialDetails {

    @Enumerated(EnumType.STRING)
    private DurationUnit interval;

    private String currencySpecified;

    private String currencyAtLocale;

    private BigDecimal monthMinimumSpecified;

    private BigDecimal monthMaximumSpecified;

    private BigDecimal yearMinimumSpecified;

    private BigDecimal yearMaximumSpecified;

    private BigDecimal monthMinimumAtLocale;

    private BigDecimal monthMaximumAtLocale;

    private BigDecimal yearMinimumAtLocale;

    private BigDecimal yearMaximumAtLocale;

    public DurationUnit getInterval() {
        return interval;
    }

    public void setInterval(DurationUnit interval) {
        this.interval = interval;
    }

    public final String getCurrencySpecified() {
        return currencySpecified;
    }

    public final void setCurrencySpecified(String currencySpecified) {
        this.currencySpecified = currencySpecified;
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
