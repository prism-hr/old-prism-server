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

    @Column(name = "sponsorship_currency")
    private String currency;

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
        return currency + " " + amountConverted.toPlainString();
    }

}
