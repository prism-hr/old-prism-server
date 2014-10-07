package com.zuehlke.pgadmissions.rest.representation.workflow;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;

public class ActionRepresentation {

    private PrismAction id;

    private PrismActionCategory actionCategory;

    public ActionRepresentation(PrismAction id, PrismActionCategory actionCategory) {
        this.id = id;
        this.actionCategory = actionCategory;
    }

    public PrismAction getId() {
        return id;
    }

    public PrismActionCategory getActionCategory() {
        return actionCategory;
    }
}
