package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_HIRING_MANAGER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;

public enum PrismRoleGroup {
    
    APPLICATION_PARENT_ADMINISTRATOR_GROUP(INSTITUTION_ADMINISTRATOR, DEPARTMENT_ADMINISTRATOR, PROGRAM_ADMINISTRATOR, PROJECT_ADMINISTRATOR), //
    APPLICATION_PARENT_APPROVER_GROUP(INSTITUTION_ADMINISTRATOR, DEPARTMENT_ADMINISTRATOR, DEPARTMENT_APPROVER, PROGRAM_ADMINISTRATOR, PROGRAM_APPROVER, //
            PROJECT_ADMINISTRATOR, PROJECT_APPROVER), //
    APPLICATION_ADMINISTRATOR_GROUP(INSTITUTION_ADMINISTRATOR, DEPARTMENT_ADMINISTRATOR, PROGRAM_ADMINISTRATOR, PROJECT_ADMINISTRATOR, //
            APPLICATION_ADMINISTRATOR), //
    APPLICATION_APPROVER_GROUP(INSTITUTION_ADMINISTRATOR, DEPARTMENT_ADMINISTRATOR, DEPARTMENT_APPROVER, PROGRAM_ADMINISTRATOR, PROGRAM_APPROVER, //
            PROJECT_ADMINISTRATOR, PROJECT_APPROVER, APPLICATION_ADMINISTRATOR, APPLICATION_HIRING_MANAGER), //
    APPLICATION_PARENT_VIEWER_GROUP(INSTITUTION_ADMINISTRATOR, INSTITUTION_APPROVER, INSTITUTION_VIEWER, DEPARTMENT_ADMINISTRATOR, DEPARTMENT_APPROVER, //
            DEPARTMENT_VIEWER, PROGRAM_ADMINISTRATOR, PROGRAM_APPROVER, PROGRAM_VIEWER, PROJECT_ADMINISTRATOR, PROJECT_APPROVER, PROJECT_VIEWER), //
    APPLICATION_POTENTIAL_SUPERVISOR_GROUP(APPLICATION_HIRING_MANAGER, APPLICATION_REVIEWER, APPLICATION_INTERVIEWER, APPLICATION_VIEWER_RECRUITER), //
    APPLICATION_POTENTIAL_INTERVIEW_GROUP(APPLICATION_POTENTIAL_INTERVIEWER, APPLICATION_POTENTIAL_INTERVIEWEE), //
    APPLICATION_CONFIRMED_INTERVIEW_GROUP(APPLICATION_INTERVIEWER, APPLICATION_INTERVIEWEE), //
    
    PROJECT_PARENT_ADMINISTRATOR_GROUP(INSTITUTION_ADMINISTRATOR, DEPARTMENT_ADMINISTRATOR, PROGRAM_ADMINISTRATOR), //
    PROJECT_ADMINISTRATOR_GROUP(INSTITUTION_ADMINISTRATOR, DEPARTMENT_ADMINISTRATOR, PROGRAM_ADMINISTRATOR, PROJECT_ADMINISTRATOR), //

    PROGRAM_PARENT_ADMINISTRATOR_GROUP(SYSTEM_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR, DEPARTMENT_ADMINISTRATOR), //
    PROGRAM_ADMINISTRATOR_GROUP(SYSTEM_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR, DEPARTMENT_ADMINISTRATOR, PROGRAM_ADMINISTRATOR), //
    
    DEPARTMENT_PARENT_ADMINISTRATOR_GROUP(SYSTEM_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR), //
    DEPARTMENT_ADMINISTRATOR_GROUP(SYSTEM_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR, DEPARTMENT_ADMINISTRATOR), //
    
    INSTITUTION_ADMINISTRATOR_GROUP(SYSTEM_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR); //

    private PrismRole[] roles;

    private PrismRoleGroup(PrismRole... roles) {
        this.roles = roles;
    }

    public PrismRole[] getRoles() {
        return roles;
    }

}
