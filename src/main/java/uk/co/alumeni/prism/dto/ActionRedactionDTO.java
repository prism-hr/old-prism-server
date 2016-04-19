package uk.co.alumeni.prism.dto;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionRedactionType;

public class ActionRedactionDTO {

    PrismAction actionId;

    PrismActionRedactionType redactionType;

    public final PrismAction getActionId() {
        return actionId;
    }

    public final void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public final PrismActionRedactionType getRedactionType() {
        return redactionType;
    }

    public final void setRedactionType(PrismActionRedactionType redactionType) {
        this.redactionType = redactionType;
    }

}
