package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public class AdvertRepresentation {

    private Integer id;

    private String title;

    private String summary;

    private String description;

    private String applyLink;

    private Integer studyDurationMinimum;

    private Integer studyDurationMaximum;

    private String currency;

    private String currencyAtLocale;

    private DurationUnit feeInterval;

    private BigDecimal feeMinimum;

    private BigDecimal feeMaximum;

    private DurationUnit payInterval;

    private BigDecimal payMinimum;

    private BigDecimal payMaximum;

    private LocalDate closingDate;

    private UserRepresentation user;

    private PrismScope resourceScope;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplyLink() {
        return applyLink;
    }

    public void setApplyLink(String applyLink) {
        this.applyLink = applyLink;
    }

    public Integer getStudyDurationMinimum() {
        return studyDurationMinimum;
    }

    public void setStudyDurationMinimum(Integer studyDurationMinimum) {
        this.studyDurationMinimum = studyDurationMinimum;
    }

    public Integer getStudyDurationMaximum() {
        return studyDurationMaximum;
    }

    public void setStudyDurationMaximum(Integer studyDurationMaximum) {
        this.studyDurationMaximum = studyDurationMaximum;
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

    public DurationUnit getFeeInterval() {
        return feeInterval;
    }

    public void setFeeInterval(DurationUnit feeInterval) {
        this.feeInterval = feeInterval;
    }

    public BigDecimal getFeeMinimum() {
        return feeMinimum;
    }

    public void setFeeMinimum(BigDecimal feeMinimum) {
        this.feeMinimum = feeMinimum;
    }

    public BigDecimal getFeeMaximum() {
        return feeMaximum;
    }

    public void setFeeMaximum(BigDecimal feeMaximum) {
        this.feeMaximum = feeMaximum;
    }

    public DurationUnit getPayInterval() {
        return payInterval;
    }

    public void setPayInterval(DurationUnit payInterval) {
        this.payInterval = payInterval;
    }

    public BigDecimal getPayMinimum() {
        return payMinimum;
    }

    public void setPayMinimum(BigDecimal payMinimum) {
        this.payMinimum = payMinimum;
    }

    public BigDecimal getPayMaximum() {
        return payMaximum;
    }

    public void setPayMaximum(BigDecimal payMaximum) {
        this.payMaximum = payMaximum;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
    }
}
