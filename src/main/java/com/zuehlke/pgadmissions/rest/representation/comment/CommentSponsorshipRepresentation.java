package com.zuehlke.pgadmissions.rest.representation.comment;

import java.math.BigDecimal;

import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;

public class CommentSponsorshipRepresentation {

    private InstitutionRepresentation sponsor;

    private BigDecimal amountConverted;

    private Boolean targetFulfilled;

    private Integer rejection;

    public InstitutionRepresentation getSponsor() {
        return sponsor;
    }

    public void setSponsor(InstitutionRepresentation sponsor) {
        this.sponsor = sponsor;
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

    public Integer getRejection() {
        return rejection;
    }

    public void setRejection(Integer rejection) {
        this.rejection = rejection;
    }

}
