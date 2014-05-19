package com.zuehlke.pgadmissions.domain.enums;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;

public enum ApplicationFormAction {

    APPLICATION_ASSESS_ELIGIBILITY, //
    APPLICATION_ASSIGN_INTERVIEWERS, //
    APPLICATION_ASSIGN_REVIEWERS, //
    APPLICATION_ASSIGN_SUPERVISORS, //
    APPLICATION_COMMENT, //
    APPLICATION_COMPLETE, //
    APPLICATION_COMPLETE_APPROVAL_STAGE, //
    APPLICATION_COMPLETE_INTERVIEW_STAGE, //
    APPLICATION_COMPLETE_REVIEW_STAGE, //
    APPLICATION_COMPLETE_VALIDATION_STAGE, //
    APPLICATION_CONFIRM_ELIGIBILITY, //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS, //
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION, //
    APPLICATION_CONFIRM_REJECTION, //
    APPLICATION_CONFIRM_SUPERVISION, //
    APPLICATION_CORRECT, //
    APPLICATION_EDIT_AS_ADMINISTRATOR, //
    APPLICATION_EDIT_AS_CREATOR, //
    APPLICATION_EMAIL_CREATOR, //
    APPLICATION_ESCALATE, //
    APPLICATION_EXPORT, //
    APPLICATION_MOVE_TO_DIFFERENT_STAGE, //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY, //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK, //
    APPLICATION_PROVIDE_REFERENCE, //
    APPLICATION_PROVIDE_REVIEW, //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY, //
    APPLICATION_VIEW_AS_CREATOR, //
    APPLICATION_VIEW_AS_RECRUITER, //
    APPLICATION_VIEW_AS_REFEREE, //
    APPLICATION_WITHDRAW, //
    INSTITUTION_CONFIGURE, //
    INSTITUTION_CREATE_PROGRAM, //
    INSTITUTION_VIEW_APPLICATION_LIST, //
    INSTITUTION_VIEW_PROGRAM_LIST, //
    INSTITUTION_VIEW_PROJECT_LIST, //
    PROGRAM_COMPLETE_APPROVAL_STAGE, //
    PROGRAM_CONFIGURE, //
    PROGRAM_CREATE_APPLICATION, //
    PROGRAM_CREATE_PROJECT, //
    PROGRAM_EDIT, //
    PROGRAM_EMAIL_CREATOR, //
    PROGRAM_ESCALATE, //
    PROGRAM_REACTIVATE, //
    PROGRAM_VIEW, //
    PROGRAM_VIEW_APPLICATION_LIST, //
    PROGRAM_VIEW_PROJECT_LIST, //
    PROGRAM_WITHDRAW, //
    PROJECT_CONFIGURE, //
    PROJECT_CREATE_APPLICATION, //
    PROJECT_ESCALATE, //
    PROJECT_REACTIVATE, //
    PROJECT_VIEW, //
    PROJECT_VIEW_APPLICATION_LIST, //
    SYSTEM_CONFIGURE, //
    SYSTEM_MANAGE_ACCOUNT, //
    SYSTEM_VIEW_APPLICATION_LIST, //
    SYSTEM_VIEW_INSTITUTION_LIST, //
    SYSTEM_VIEW_PROGRAM_LIST, //
    SYSTEM_VIEW_PROJECT_LIST;

    public Class<? extends PrismScope> getScopeClass() {
        String scopeName = getPrefix();

        Class<? extends PrismScope> scopeClass = null;
        if ("APPLICATION".equals(scopeName)) {
            scopeClass = Application.class;
        } else if ("PROJECT".equals(scopeName)) {
            scopeClass = Project.class;
        } else if ("PROGRAM".equals(scopeName)) {
            scopeClass = Program.class;
        }
        return scopeClass;
    }

    public String getScopeName() {
        String scopeName = getPrefix();
        return scopeName.toLowerCase();
    }

    private String getPrefix() {
        String actionName = name();
        String scopeName = actionName.substring(0, actionName.indexOf('_'));
        return scopeName;
    }
    
}