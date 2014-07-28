package com.zuehlke.pgadmissions.domain.definitions.workflow;

import org.apache.commons.lang.WordUtils;

public enum PrismTransitionEvaluation {

    APPLICATION_SUPERVISION_CONFIRMED_OUTCOME, //
    APPLICATION_ELIGIBILITY_ASSESSED_OUTCOME, //
    APPLICATION_EVALUATED_OUTCOME, //
    APPLICATION_EXPORTED_OUTCOME, //
    APPLICATION_INTERVIEW_RSVPED_OUTCOME, //
    APPLICATION_INTERVIEWED_OUTCOME, //
    APPLICATION_INTERVIEW_SCHEDULED_OUTCOME, //
    APPLICATION_PROCESSED_OUTCOME, //
    APPLICATION_RECRUITED_OUTCOME, //
    APPLICATION_REVIEWED_OUTCOME, //
    INSTITUTION_APPROVED_OUTCOME, //
    PROGRAM_APPROVED_OUTCOME, //
    PROGRAM_CONFIGURED_OUTCOME, //
    PROGRAM_CREATED_OUTCOME, //
    PROGRAM_EXPIRED_OUTCOME, //
    PROGRAM_REACTIVATED_OUTCOME, //
    PROJECT_APPROVED_OUTCOME, //
    PROJECT_CONFIGURED_OUTCOME, //
    PROJECT_CREATED_OUTCOME, //
    PROJECT_REACTIVATED_OUTCOME;
    
    public String getMethodName() {
        String[] nameParts = name().split("_");
        String methodName = "get";
        for (String namePart : nameParts) {
            methodName = methodName + WordUtils.capitalizeFully(namePart);
        }
        return methodName;
    }

}
