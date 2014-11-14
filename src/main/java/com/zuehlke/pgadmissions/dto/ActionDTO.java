package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class ActionDTO {

    private PrismAction actionId;

    private Boolean raisesUrgentFlag;
    
    private PrismState transitionStateId;

    private PrismRole transitionRoleId;

    private PrismRoleTransitionType roleTransitionType;

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    public final PrismAction getActionId() {
        return actionId;
    }

    public final void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public final Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public final void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }
    
    public final PrismState getTransitionStateId() {
        return transitionStateId;
    }

    public final void setTransitionStateId(PrismState transitionStateId) {
        this.transitionStateId = transitionStateId;
    }

    public final PrismRole getTransitionRoleId() {
        return transitionRoleId;
    }

    public final void setTransitionRoleId(PrismRole transitionRoleId) {
        this.transitionRoleId = transitionRoleId;
    }

    public final PrismRoleTransitionType getRoleTransitionType() {
        return roleTransitionType;
    }

    public final void setRoleTransitionType(PrismRoleTransitionType roleTransitionType) {
        this.roleTransitionType = roleTransitionType;
    }

    public final Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public final void setMinimumPermitted(Integer minimumPermitted) {
        this.minimumPermitted = minimumPermitted;
    }

    public final Integer getMaximumPermitted() {
        return maximumPermitted;
    }

    public final void setMaximumPermitted(Integer maximumPermitted) {
        this.maximumPermitted = maximumPermitted;
    }

}
