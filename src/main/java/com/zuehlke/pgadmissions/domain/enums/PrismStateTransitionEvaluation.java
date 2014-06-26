package com.zuehlke.pgadmissions.domain.enums;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.WordUtils;

public enum PrismStateTransitionEvaluation {

    APPLICATION_COMPLETED_OUTCOME(Arrays.asList(PrismAction.APPLICATION_COMPLETE)), //
    APPLICATION_SUPERVISION_CONFIRMED_OUTCOME(Arrays.asList(PrismAction.APPLICATION_CONFIRM_SUPERVISION)), //
    APPLICATION_ELIGIBILITY_ASSESSED_OUTCOME(Arrays.asList(PrismAction.APPLICATION_ASSESS_ELIGIBILITY)), //
    APPLICATION_EVALUATED_OUTCOME(Arrays.asList(PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE, PrismAction.APPLICATION_COMPLETE_INTERVIEW_STAGE,
            PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE, PrismAction.APPLICATION_COMPLETE_VALIDATION_STAGE, PrismAction.APPLICATION_MOVE_TO_DIFFERENT_STAGE)), //
    APPLICATION_EXPORTED_OUTCOME(Arrays.asList(PrismAction.APPLICATION_EXPORT)), //
    APPLICATION_INTERVIEW_RSVPED_OUTCOME(Arrays.asList(PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY)), //
    APPLICATION_INTERVIEWED_OUTCOME(Arrays.asList(PrismAction.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK)), //
    APPLICATION_INTERVIEW_SCHEDULED_OUTCOME(Arrays.asList(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS, PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS)), //
    APPLICATION_PROCESSED_OUTCOME(Arrays.asList(PrismAction.APPLICATION_WITHDRAW, PrismAction.APPLICATION_TERMINATE,
            PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION, PrismAction.APPLICATION_CONFIRM_REJECTION)), //
    APPLICATION_RECRUITED_OUTCOME(Arrays.asList(PrismAction.PROGRAM_CONCLUDE, PrismAction.PROJECT_CONCLUDE)), //
    APPLICATION_REVIEWED_OUTCOME(Arrays.asList(PrismAction.APPLICATION_PROVIDE_REVIEW)), //
    PROGRAM_APPROVED_OUTCOME(Arrays.asList(PrismAction.PROGRAM_COMPLETE_APPROVAL_STAGE)), //
    PROGRAM_CONFIGURED_OUTCOME(Arrays.asList(PrismAction.PROGRAM_CONFIGURE)), //
    PROGRAM_CREATED_OUTCOME(Arrays.asList(PrismAction.INSTITUTION_CREATE_PROGRAM)), //
    PROGRAM_EXPIRED_OUTCOME(Arrays.asList(PrismAction.PROGRAM_ESCALATE)), //
    PROGRAM_REACTIVATED_OUTCOME(Arrays.asList(PrismAction.PROGRAM_RESTORE)), //
    PROJECT_CONFIGURED_OUTCOME(Arrays.asList(PrismAction.PROJECT_CONFIGURE)), //
    PROJECT_REACTIVATED_OUTCOME(Arrays.asList(PrismAction.PROJECT_RESTORE));

    private List<PrismAction> invokingActions;

    private PrismStateTransitionEvaluation(List<PrismAction> invokingActions) {
        this.invokingActions = invokingActions;
    }

    public String getMethodName() {
        String[] nameParts = name().split("_");
        String methodName = "get";
        for (String namePart : nameParts) {
            methodName = methodName + WordUtils.capitalizeFully(namePart);
        }
        return methodName;
    }

    public List<PrismAction> getInvokingActions() {
        return invokingActions;
    }

}
