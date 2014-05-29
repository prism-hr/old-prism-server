package com.zuehlke.pgadmissions.domain.enums;

import org.springframework.util.StringUtils;

public enum StateTransitionEvaluation {

    APPLICATION_COMPLETED_OUTCOME, //
    APPLICATION_ELIGIBILITY_ASSESSED_OUTCOME, //
    APPLICATION_EXPORTED_OUTCOME, //
    APPLICATION_INTERVIEW_SCHEDULED_OUTCOME, //
    APPLICATION_PROCESSING_COMPLETED_OUTCOME, //
    APPLICATION_STAGE_COMPLETED_OUTCOME, //
    PROGRAM_APPROVED_OUTCOME, //
    PROGRAM_CONFIGURED_OUTCOME, //
    PROGRAM_CREATED_OUTCOME, //
    PROGRAM_EXPIRED_OUTCOME, //
    PROGRAM_REACTIVATED_OUTCOME, //
    PROJECT_CONFIGURED_OUTCOME, //
    PROJECT_REACTIVATED_OUTCOME;
    
    public String getProcessorMethodName() {
        String[] methodNameParts = name().split("_");
        String methodName = "get";
        for (String methodNamePart : methodNameParts) {
            methodName = methodName + StringUtils.capitalize(methodNamePart.toLowerCase());
        }
        return methodName;
    }

}
