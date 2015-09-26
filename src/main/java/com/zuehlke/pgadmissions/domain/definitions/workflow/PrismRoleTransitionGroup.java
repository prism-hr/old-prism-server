package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_APPOINTEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_HIRING_MANAGER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_REFEREE;
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
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.BRANCH;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.EXHUME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.RETIRE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.REVIVE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.UPDATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowConstraint.APPLICATION_HIRING_MANAGERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowConstraint.APPLICATION_INTERVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowConstraint.APPLICATION_REFEREES;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowConstraint.APPLICATION_REVIEWERS;

public enum PrismRoleTransitionGroup {

    APPLICATION_CREATE_CREATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_CREATOR) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(APPLICATION_CREATOR) //
                    .withRestrictToOwner() //
                    .withMinimumPermitted(1) //
                    .withMaximumPermitted(1)), //

    APPLICATION_CREATE_REFEREE_GROUP(
            new PrismRoleTransition() //
                    .withRole(APPLICATION_REFEREE) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(APPLICATION_REFEREE) //
                    .withPropertyDefinition(APPLICATION_REFEREES)), //

    APPLICATION_UPDATE_REFEREE_GROUP(
            new PrismRoleTransition() //
                    .withRole(APPLICATION_REFEREE) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(APPLICATION_REFEREE) //
                    .withPropertyDefinition(APPLICATION_REFEREES), //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_REFEREE) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(APPLICATION_REFEREE)), //

    APPLICATION_RETIRE_REFEREE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_REFEREE) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_REFEREE)), //

    APPLICATION_PROVIDE_REFERENCE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_REFEREE) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_REFEREE) //
                    .withRestrictToOwner()), //

    APPLICATION_CREATE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(APPLICATION_ADMINISTRATOR) //
                    .withMaximumPermitted(1)), //

    APPLICATION_CREATE_REVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_REVIEWER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(APPLICATION_REVIEWER) //
                    .withPropertyDefinition(APPLICATION_REVIEWERS)), //

    APPLICATION_RETIRE_REVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_REVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER)), //

    APPLICATION_PROVIDE_REVIEW_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_REVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER) //
                    .withRestrictToOwner()),

    APPLICATION_RETIRE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_ADMINISTRATOR) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER)),

    APPLICATION_CREATE_POTENTIAL_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_CREATOR) //
                    .withTransitionType(BRANCH) //
                    .withTransitionRole(APPLICATION_POTENTIAL_INTERVIEWEE)),

    APPLICATION_CREATE_POTENTIAL_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withPropertyDefinition(APPLICATION_INTERVIEWERS)),

    APPLICATION_CREATE_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_CREATOR) //
                    .withTransitionType(BRANCH) //
                    .withTransitionRole(APPLICATION_INTERVIEWEE)), //

    APPLICATION_CREATE_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_INTERVIEWER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(APPLICATION_INTERVIEWER) //
                    .withPropertyDefinition(APPLICATION_INTERVIEWERS)),

    APPLICATION_RETIRE_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_INTERVIEWEE) //
                    .withTransitionType(RETIRE) //
                    .withTransitionRole(APPLICATION_INTERVIEWEE), //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWEE) //
                    .withTransitionType(RETIRE) //
                    .withTransitionRole(APPLICATION_POTENTIAL_INTERVIEWEE)),

    APPLICATION_RETIRE_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER), //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER)), //

    APPLICATION_RETIRE_POTENTIAL_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWEE) //
                    .withTransitionType(RETIRE) //
                    .withTransitionRole(APPLICATION_POTENTIAL_INTERVIEWEE)),

    APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_INTERVIEWEE) //
                    .withTransitionType(RETIRE) //
                    .withTransitionRole(APPLICATION_INTERVIEWEE)),

    APPLICATION_RETIRE_POTENTIAL_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER)), //

    APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER)), //

    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWEE) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_INTERVIEWEE) //
                    .withRestrictToOwner(), //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_INTERVIEWER) //
                    .withRestrictToOwner()), //

    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER) //
                    .withRestrictToOwner()), //

    APPLICATION_CREATE_HIRING_MANAGER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_HIRING_MANAGER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(APPLICATION_HIRING_MANAGER) //
                    .withPropertyDefinition(APPLICATION_HIRING_MANAGERS)), //

    APPLICATION_RETIRE_HIRING_MANAGER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_HIRING_MANAGER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER)),

    APPLICATION_CONFIRM_APPOINTMENT_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_HIRING_MANAGER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER) //
                    .withRestrictToOwner()),

    APPLICATION_EXHUME_REFEREE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_VIEWER_REFEREE) //
                    .withTransitionType(EXHUME) //
                    .withTransitionRole(APPLICATION_REFEREE)),
    
    APPLICATION_CREATE_APPOINTEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_CREATOR) //
                    .withTransitionType(BRANCH) //
                    .withTransitionRole(APPLICATION_APPOINTEE)),
    
    APPLICATION_RETIRE_APPOINTEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_APPOINTEE) //
                    .withTransitionType(RETIRE) //
                    .withTransitionRole(APPLICATION_APPOINTEE)),

    PROJECT_CREATE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PROJECT_ADMINISTRATOR) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(PROJECT_ADMINISTRATOR) //
                    .withRestrictToOwner() //
                    .withMinimumPermitted(1) //
                    .withMaximumPermitted(1)),

    PROJECT_REVIVE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PROJECT_ADMINISTRATOR) //
                    .withTransitionType(REVIVE) //
                    .withTransitionRole(PROJECT_ADMINISTRATOR)),

    PROJECT_MANAGE_USERS_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PROJECT_ADMINISTRATOR) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(PROJECT_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(PROJECT_ADMINISTRATOR) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(PROJECT_ADMINISTRATOR),
            new PrismRoleTransition() //
                    .withRole(PROJECT_APPROVER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(PROJECT_APPROVER), //
            new PrismRoleTransition() //
                    .withRole(PROJECT_APPROVER) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(PROJECT_APPROVER),
            new PrismRoleTransition() //
                    .withRole(PROJECT_VIEWER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(PROJECT_VIEWER), //
            new PrismRoleTransition() //
                    .withRole(PROJECT_VIEWER) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(PROJECT_VIEWER)), //

    PROGRAM_CREATE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PROGRAM_ADMINISTRATOR) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(PROGRAM_ADMINISTRATOR) //
                    .withRestrictToOwner() //
                    .withMinimumPermitted(1) //
                    .withMaximumPermitted(1)),

    PROGRAM_REVIVE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PROGRAM_ADMINISTRATOR) //
                    .withTransitionType(REVIVE) //
                    .withTransitionRole(PROGRAM_ADMINISTRATOR)),

    PROGRAM_MANAGE_USERS_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PROGRAM_ADMINISTRATOR) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(PROGRAM_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(PROGRAM_ADMINISTRATOR) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(PROGRAM_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(PROGRAM_APPROVER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(PROGRAM_APPROVER), //
            new PrismRoleTransition() //
                    .withRole(PROGRAM_APPROVER) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(PROGRAM_APPROVER),
            new PrismRoleTransition() //
                    .withRole(PROGRAM_VIEWER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(PROGRAM_VIEWER),
            new PrismRoleTransition() //
                    .withRole(PROGRAM_VIEWER) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(PROGRAM_VIEWER)),

    DEPARTMENT_CREATE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(DEPARTMENT_ADMINISTRATOR) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(DEPARTMENT_ADMINISTRATOR) //
                    .withRestrictToOwner() //
                    .withMinimumPermitted(1) //
                    .withMaximumPermitted(1)),

    DEPARTMENT_REVIVE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(DEPARTMENT_ADMINISTRATOR) //
                    .withTransitionType(REVIVE) //
                    .withTransitionRole(DEPARTMENT_ADMINISTRATOR)),

    DEPARTMENT_MANAGE_USERS_GROUP( //
            new PrismRoleTransition() //
                    .withRole(DEPARTMENT_ADMINISTRATOR) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(DEPARTMENT_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(DEPARTMENT_ADMINISTRATOR) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(DEPARTMENT_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(DEPARTMENT_APPROVER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(DEPARTMENT_APPROVER), //
            new PrismRoleTransition() //
                    .withRole(DEPARTMENT_APPROVER) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(DEPARTMENT_APPROVER),
            new PrismRoleTransition() //
                    .withRole(DEPARTMENT_VIEWER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(DEPARTMENT_VIEWER),
            new PrismRoleTransition() //
                    .withRole(DEPARTMENT_VIEWER) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(DEPARTMENT_VIEWER)),

    INSTITUTION_CREATE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(INSTITUTION_ADMINISTRATOR) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(INSTITUTION_ADMINISTRATOR) //
                    .withRestrictToOwner() //
                    .withMinimumPermitted(1) //
                    .withMaximumPermitted(1)),

    INSTITUTION_REVIVE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(INSTITUTION_ADMINISTRATOR) //
                    .withTransitionType(REVIVE) //
                    .withTransitionRole(INSTITUTION_ADMINISTRATOR)),

    INSTITUTION_MANAGE_USERS_GROUP( //
            new PrismRoleTransition() //
                    .withRole(INSTITUTION_ADMINISTRATOR) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(INSTITUTION_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(INSTITUTION_ADMINISTRATOR) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(INSTITUTION_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(INSTITUTION_APPROVER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(INSTITUTION_APPROVER),
            new PrismRoleTransition() //
                    .withRole(INSTITUTION_APPROVER) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(INSTITUTION_APPROVER),
            new PrismRoleTransition() //
                    .withRole(INSTITUTION_VIEWER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(INSTITUTION_VIEWER),
            new PrismRoleTransition() //
                    .withRole(INSTITUTION_VIEWER) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(INSTITUTION_VIEWER)),

    SYSTEM_CREATE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(SYSTEM_ADMINISTRATOR) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(SYSTEM_ADMINISTRATOR) //
                    .withRestrictToOwner()
                    .withMinimumPermitted(1) //
                    .withMaximumPermitted(1)),

    SYSTEM_MANAGE_USER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(SYSTEM_ADMINISTRATOR) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(SYSTEM_ADMINISTRATOR),
            new PrismRoleTransition() //
                    .withRole(SYSTEM_ADMINISTRATOR) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(SYSTEM_ADMINISTRATOR));

    private PrismRoleTransition[] roleTransitions;

    private PrismRoleTransitionGroup(PrismRoleTransition... roleTransitions) {
        this.roleTransitions = roleTransitions;
    }

    public PrismRoleTransition[] getRoleTransitions() {
        return roleTransitions;
    }

}
