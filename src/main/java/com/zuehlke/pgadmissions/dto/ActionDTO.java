package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class ActionDTO {

    private PrismAction actionId;

    private PrismActionCategory actionCategory;

    private Boolean raisesUrgentFlag;

    private PrismActionEnhancement globalActionEnhancement;

    private PrismActionEnhancement customActionEnhancement;

    private Boolean primaryState;

    private PrismState transitionStateId;

    private Boolean nextStateSelection;

    private Boolean parallelizable;

    public final PrismAction getActionId() {
        return actionId;
    }

    public final void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public final PrismActionCategory getActionCategory() {
        return actionCategory;
    }

    public final void setActionCategory(PrismActionCategory actionCategory) {
        this.actionCategory = actionCategory;
    }

    public final Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public final void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public final PrismActionEnhancement getGlobalActionEnhancement() {
        return globalActionEnhancement;
    }

    public final void setGlobalActionEnhancement(PrismActionEnhancement globalActionEnhancement) {
        this.globalActionEnhancement = globalActionEnhancement;
    }

    public final PrismActionEnhancement getCustomActionEnhancement() {
        return customActionEnhancement;
    }

    public final void setCustomActionEnhancement(PrismActionEnhancement customActionEnhancement) {
        this.customActionEnhancement = customActionEnhancement;
    }

    public final Boolean getPrimaryState() {
        return primaryState;
    }

    public final void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }

    public final PrismState getTransitionStateId() {
        return transitionStateId;
    }

    public final void setTransitionStateId(PrismState transitionStateId) {
        this.transitionStateId = transitionStateId;
    }

    public final Boolean getNextStateSelection() {
        return nextStateSelection;
    }

    public final void setNextStateSelection(Boolean nextStateSelection) {
        this.nextStateSelection = nextStateSelection;
    }

    public final Boolean getParallelizable() {
        return parallelizable;
    }

    public final void setParallelizable(Boolean parallelizable) {
        this.parallelizable = parallelizable;
    }

    public boolean isCreateResourceAction() {
        return actionCategory == PrismActionCategory.CREATE_RESOURCE;
    }

}
