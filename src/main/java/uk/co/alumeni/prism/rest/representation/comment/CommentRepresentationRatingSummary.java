package uk.co.alumeni.prism.rest.representation.comment;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentation;

public class CommentRepresentationRatingSummary extends ActionRepresentation {

    private Integer providedCount;

    private Integer declinedCount;

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

    public CommentRepresentationRatingSummary withId(PrismAction id) {
        setId(id);
        return this;
    }

}
