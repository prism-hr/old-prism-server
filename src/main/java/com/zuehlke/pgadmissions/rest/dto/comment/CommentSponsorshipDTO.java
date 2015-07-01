package com.zuehlke.pgadmissions.rest.dto.comment;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.rest.dto.InstitutionPartnerDTO;

public class CommentSponsorshipDTO {

    @Valid
    private InstitutionPartnerDTO sponsor;

    @NotEmpty
    private String currency;

    @NotNull
    private BigDecimal amountSpecified;

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

}
