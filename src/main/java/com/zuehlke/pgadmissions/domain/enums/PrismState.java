package com.zuehlke.pgadmissions.domain.enums;

import java.util.Map;

import com.google.common.collect.Maps;

public enum PrismState {

    APPLICATION_APPROVAL, //
    APPLICATION_APPROVAL_PENDING_COMPLETION, //
    APPLICATION_APPROVAL_PENDING_FEEDBACK, //
    APPLICATION_APPROVED, //
    APPLICATION_APPROVED_COMPLETED, //
    APPLICATION_APPROVED_PENDING_CORRECTION, //
    APPLICATION_APPROVED_PENDING_EXPORT, //
    APPLICATION_INTERVIEW, //
    APPLICATION_INTERVIEW_PENDING_AVAILABILITY, //
    APPLICATION_INTERVIEW_PENDING_COMPLETION, //
    APPLICATION_INTERVIEW_PENDING_FEEDBACK, //
    APPLICATION_INTERVIEW_PENDING_INTERVIEW, //
    APPLICATION_INTERVIEW_PENDING_SCHEDULING, //
    APPLICATION_REJECTED, //
    APPLICATION_REJECTED_COMPLETED, //
    APPLICATION_REJECTED_PENDING_CORRECTION, //
    APPLICATION_REJECTED_PENDING_EXPORT, //
    APPLICATION_REVIEW, //
    APPLICATION_REVIEW_PENDING_COMPLETION, //
    APPLICATION_REVIEW_PENDING_FEEDBACK, //
    APPLICATION_UNSUBMITTED, //
    APPLICATION_UNSUBMITTED_PENDING_COMPLETION, //
    APPLICATION_VALIDATION, //
    APPLICATION_VALIDATION_PENDING_COMPLETION, //
    APPLICATION_VALIDATION_PENDING_FEEDBACK, //
    APPLICATION_WITHDRAWN, //
    APPLICATION_WITHDRAWN_COMPLETED, //
    APPLICATION_WITHDRAWN_PENDING_CORRECTION, //
    INSTITUTION_APPROVED, //
    PROGRAM_APPROVAL, //
    PROGRAM_APPROVAL_PENDING_CORRECTION, //
    PROGRAM_APPROVED, //
    PROGRAM_DEACTIVATED, //
    PROGRAM_DISABLED, //
    PROGRAM_DISABLED_COMPLETED, //
    PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION, //
    PROGRAM_DISABLED_PENDING_REACTIVATION, //
    PROGRAM_REJECTED, //
    PROGRAM_WITHDRAWN, //
    PROJECT_APPROVED, //
    PROJECT_DEACTIVATED, //
    PROJECT_DISABLED, //
    PROJECT_DISABLED_COMPLETED, //
    PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION, //
    PROJECT_DISABLED_PENDING_REACTIVATION, //
    SYSTEM_APPROVED;
    
    private String displayValue = null;

    private static Map<String, PrismState> displayNameToStateMap;

    private PrismState(String displayValue) {
        this.displayValue = displayValue;
    }

    private PrismState() {
    }

    static {
        displayNameToStateMap = Maps.newHashMap();
        for (PrismState state : PrismState.values()) {
            if (state.displayValue != null) {
                displayNameToStateMap.put(state.displayValue, state);
            }
        }
    }

    public static PrismState convert(String searchStr) {
        return displayNameToStateMap.get(searchStr);
    }
    
    public PrismScope getResourceType() {
        String stateName = name();
        String resourceName = stateName.substring(0, stateName.indexOf('_'));
        return PrismScope.valueOf(resourceName);
    }

}
