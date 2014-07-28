package com.zuehlke.pgadmissions.rest.representation.application;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class ActionRepresentation {

    private PrismAction name;

    private Boolean raisesUrgentFlag;

    public ActionRepresentation(PrismAction prismAction, Boolean raisesUrgentFlag) {
        this.name = prismAction;
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public PrismAction getName() {
        return name;
    }

    public void setName(PrismAction name) {
        this.name = name;
    }

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }
}
