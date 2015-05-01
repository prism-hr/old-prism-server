package com.zuehlke.pgadmissions.domain.comment;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CommentSponsorship {

    @Column(name = "sponsorship_currency")
    private String currency;

    @Column(name = "sponsorship_amount_specified")
    private BigDecimal amountSpecified;

    @Column(name = "sponsorship_amount_converted")
    private BigDecimal amountConverted;

    @Column(name = "sponsorship_confirmed")
    private Boolean confirmed;

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

    @Override
    public String toString() {
        return currency + " " + amountConverted.toPlainString();
    }

}
