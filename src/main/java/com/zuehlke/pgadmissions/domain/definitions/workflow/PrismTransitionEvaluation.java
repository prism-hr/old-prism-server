package com.zuehlke.pgadmissions.domain.definitions.workflow;

import org.apache.commons.lang.WordUtils;

public enum PrismTransitionEvaluation {

    APPLICATION_SUPERVISION_CONFIRMED_OUTCOME(false, PrismScope.APPLICATION), //
    APPLICATION_ELIGIBILITY_ASSESSED_OUTCOME(false, PrismScope.APPLICATION), //
    APPLICATION_STATE_COMPLETED_OUTCOME(true, PrismScope.APPLICATION), //
    APPLICATION_EXPORTED_OUTCOME(false, PrismScope.APPLICATION), //
    APPLICATION_INTERVIEW_RSVPED_OUTCOME(false, PrismScope.APPLICATION), //
    APPLICATION_INTERVIEWED_OUTCOME(false, PrismScope.APPLICATION), //
    APPLICATION_INTERVIEW_SCHEDULED_OUTCOME(false, PrismScope.APPLICATION), //
    APPLICATION_PROCESSED_OUTCOME(false, PrismScope.APPLICATION), //
    APPLICATION_RECRUITED_OUTCOME(false, PrismScope.APPLICATION), //
    APPLICATION_REVIEWED_OUTCOME(false, PrismScope.APPLICATION), //
    INSTITUTION_APPROVED_OUTCOME(true, PrismScope.INSTITUTION), //
    INSTITUTION_CREATED_OUTCOME(false, PrismScope.INSTITUTION), //
    PROGRAM_APPROVED_OUTCOME(true, PrismScope.PROGRAM), //
    PROGRAM_VIEW_EDIT_OUTCOME(true, PrismScope.PROGRAM), //
    PROGRAM_CREATED_OUTCOME(false, PrismScope.PROGRAM), //
    PROGRAM_EXPIRED_OUTCOME(false, PrismScope.PROGRAM), //
    PROGRAM_RESTORED_OUTCOME(false, PrismScope.PROGRAM), //
    PROJECT_APPROVED_OUTCOME(true, PrismScope.PROJECT), //
    PROJECT_VIEW_EDIT_OUTCOME(true, PrismScope.PROJECT), //
    PROJECT_CREATED_OUTCOME(false, PrismScope.PROJECT), //
    PROJECT_REACTIVATED_OUTCOME(false, PrismScope.PROJECT);
    
    private boolean nextStateSelection;
    
    private PrismScope scope;
    
    private PrismTransitionEvaluation(boolean nextStateSelection, PrismScope scope) {
        this.nextStateSelection = nextStateSelection;
        this.scope = scope;
    }
    
    public final boolean isNextStateSelection() {
        return nextStateSelection;
    }

    public final PrismScope getScope() {
        return scope;
    }

    public String getMethodName() {
        String[] nameParts = name().split("_");
        String methodName = "get";
        for (String namePart : nameParts) {
            methodName = methodName + WordUtils.capitalizeFully(namePart);
        }
        return methodName;
    }

}
