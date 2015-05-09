package com.zuehlke.pgadmissions.domain.comment;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.zuehlke.pgadmissions.domain.institution.Institution;

@Embeddable
public class CommentSponsorship {

    @ManyToOne
    @JoinColumn(name = "institution_sponsor_id")
    private Institution sponsor;

    @Column(name = "sponsorship_currency")
    private String currency;

    @Column(name = "sponsorship_amount_specified")
    private BigDecimal amountSpecified;

    @Column(name = "sponsorship_amount_converted")
    private BigDecimal amountConverted;

    @Column(name = "sponsorship_confirmed")
    private Boolean confirmed;

    @Column(name = "sponsorship_target_fulfilled")
    private Boolean targetFulfilled;

    public Institution getSponsor() {
        return sponsor;
    }

    public void setSponsor(Institution sponsor) {
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

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Boolean getTargetFulfilled() {
        return targetFulfilled;
    }

    public void setTargetFulfilled(Boolean targetFulfilled) {
        this.targetFulfilled = targetFulfilled;
    }

    @Override
    public String toString() {
        return currency + " " + amountConverted.toPlainString();
    }

}
