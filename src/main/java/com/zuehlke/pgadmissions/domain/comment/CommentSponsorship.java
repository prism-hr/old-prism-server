package com.zuehlke.pgadmissions.domain.comment;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.zuehlke.pgadmissions.domain.institution.Institution;

@Embeddable
public class CommentSponsorship {

    @ManyToOne
    @JoinColumn(name = "institution_sponsor_id")
    private Institution sponsor;

    @Column(name = "sponsorship_currency_specified")
    private String currencySpecified;

    @Column(name = "sponsorship_currency_converted")
    private String currencyConverted;

    @Column(name = "sponsorship_amount_specified")
    private BigDecimal amountSpecified;

    @Column(name = "sponsorship_amount_converted")
    private BigDecimal amountConverted;

    @Column(name = "sponsorship_target_fulfilled")
    private Boolean targetFulfilled;

    @OneToOne
    @JoinColumn(name = "sponsorship_rejection_id")
    private Comment rejection;

    public Institution getSponsor() {
        return sponsor;
    }

    public void setSponsor(Institution sponsor) {
        this.sponsor = sponsor;
    }

    public String getCurrencySpecified() {
        return currencySpecified;
    }

    public void setCurrencySpecified(String currencySpecified) {
        this.currencySpecified = currencySpecified;
    }

    public String getCurrencyConverted() {
        return currencyConverted;
    }

    public void setCurrencyConverted(String currencyConverted) {
        this.currencyConverted = currencyConverted;
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

    public Boolean getTargetFulfilled() {
        return targetFulfilled;
    }

    public void setTargetFulfilled(Boolean targetFulfilled) {
        this.targetFulfilled = targetFulfilled;
    }

    public Comment getRejection() {
        return rejection;
    }

    public void setRejection(Comment rejection) {
        this.rejection = rejection;
    }

    @Override
    public String toString() {
        return currencySpecified + " " + amountConverted.toPlainString();
    }

}
