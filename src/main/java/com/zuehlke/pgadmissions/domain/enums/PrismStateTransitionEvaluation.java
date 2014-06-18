package com.zuehlke.pgadmissions.domain.enums;

import org.springframework.util.StringUtils;

public enum PrismStateTransitionEvaluation {
    
    APPLICATION_COMPLETED_OUTCOME, //
    APPLICATION_CONFIRM_SUPERVISION_OUTCOME, //
    APPLICATION_ELIGIBILITY_ASSESSED_OUTCOME, //
    APPLICATION_EXPORTED_OUTCOME, //
    APPLICATION_INTERVIEW_AVAILABILITY_OUTCOME, //
    APPLICATION_INTERVIEW_FEEDBACK_OUTCOME, //
    APPLICATION_INTERVIEW_SCHEDULED_OUTCOME, //
    APPLICATION_PROCESSING_COMPLETED_OUTCOME, //
    APPLICATION_RECRUITMENT_OUTCOME, //
    APPLICATION_REVIEW_OUTCOME, //
    APPLICATION_STAGE_COMPLETED_OUTCOME, //
    PROGRAM_APPROVED_OUTCOME, //
    PROGRAM_CONFIGURED_OUTCOME, //
    PROGRAM_CREATED_OUTCOME, //
    PROGRAM_EXPIRED_OUTCOME, //
    PROGRAM_REACTIVATED_OUTCOME, //
    PROJECT_CONFIGURED_OUTCOME, //
    PROJECT_REACTIVATED_OUTCOME;
    
    public static String INCORRECT_PROCESSOR_TYPE = "Tried to invoke state transition processor on incorrect resource type";
    
    public String getMethodName() {
        String[] methodNameParts = name().split("_");
        String methodName = "get";
        for (String methodNamePart : methodNameParts) {
            methodName = methodName + StringUtils.capitalize(methodNamePart.toLowerCase());
        }
        return methodName;
    }

}
