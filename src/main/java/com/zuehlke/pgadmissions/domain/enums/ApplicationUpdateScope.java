package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationUpdateScope {
  
    /** Every user is entitled to see this type of update. */
    ALL_USERS,
    
    /**internal group: reviewers, interviewers, supervisors, admitter and administrator. */
    INTERNAL;

}