package uk.co.alumeni.prism.rest.representation.comment;

import static uk.co.alumeni.prism.PrismConstants.RATING_PRECISION;

import java.math.BigDecimal;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentation;

public class CommentRepresentationRatingSummary extends ActionRepresentation {

    private Integer providedCount = 0;

    private Integer declinedCount = 0;

    private BigDecimal ratingAverage = new BigDecimal(0).setScale(RATING_PRECISION);

    public Integer getProvidedCount() {
        return providedCount;
    }

    public void setProvidedCount(Integer providedCount) {
        this.providedCount = providedCount;
    }

    public Integer getDeclinedCount() {
        return declinedCount;
    }

    public void setDeclinedCount(Integer declinedCount) {
        this.declinedCount = declinedCount;
    }

    public BigDecimal getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(BigDecimal ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public CommentRepresentationRatingSummary withId(PrismAction id) {
        setId(id);
        return this;
    }

}
