package com.zuehlke.pgadmissions.rest.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SponsorshipDTO {

    @Valid
    private InstitutionPartnerDTO sponsor;

    @NotEmpty
    private String currency;

    @NotNull
    private BigDecimal amountSpecified;

    @NotNull
    private BigDecimal amountConverted;

    public InstitutionPartnerDTO getSponsor() {
        return sponsor;
    }

    public void setSponsor(InstitutionPartnerDTO sponsor) {
        this.sponsor = sponsor;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmountSpecified() {
        return amountSpecified;
    }

    public void setAmountSpecified(BigDecimal amountSpecified) {
        this.amountSpecified = amountSpecified;
    }

    public BigDecimal getAmountConverted() {
        return amountConverted;
    }

    public void setAmountConverted(BigDecimal amountConverted) {
        this.amountConverted = amountConverted;
    }
}
