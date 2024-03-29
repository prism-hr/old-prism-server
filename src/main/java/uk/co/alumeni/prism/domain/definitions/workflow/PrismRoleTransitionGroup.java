package uk.co.alumeni.prism.domain.definitions.workflow;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowConstraint.*;

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
                    .withPropertyDefinition(APPLICATION_REFEREE_ASSIGNMENT)), //

    APPLICATION_UPDATE_REFEREE_GROUP(
            new PrismRoleTransition() //
                    .withRole(APPLICATION_REFEREE) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(APPLICATION_REFEREE) //
                    .withPropertyDefinition(APPLICATION_REFEREE_ASSIGNMENT), //
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

    APPLICATION_CREATE_REVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_REVIEWER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(APPLICATION_REVIEWER) //
                    .withPropertyDefinition(APPLICATION_REVIEWER_ASSIGNMENT)), //

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
                    .withPropertyDefinition(APPLICATION_INTERVIEWER_ASSIGNMENT)),

    APPLICATION_CREATE_SCHEDULED_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_CREATOR) //
                    .withTransitionType(BRANCH) //
                    .withTransitionRole(APPLICATION_SCHEDULED_INTERVIEWEE)), //

    APPLICATION_CREATE_SCHEDULED_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_SCHEDULED_INTERVIEWER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(APPLICATION_SCHEDULED_INTERVIEWER) //
                    .withPropertyDefinition(APPLICATION_INTERVIEWER_ASSIGNMENT)),

    APPLICATION_CREATE_CONFIRMED_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_CREATOR) //
                    .withTransitionType(BRANCH) //
                    .withTransitionRole(APPLICATION_INTERVIEWEE)), //

    APPLICATION_CREATE_CONFIRMED_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_INTERVIEWER) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(APPLICATION_INTERVIEWER) //
                    .withPropertyDefinition(APPLICATION_INTERVIEWER_ASSIGNMENT)),

    APPLICATION_RETIRE_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWEE) //
                    .withTransitionType(RETIRE) //
                    .withTransitionRole(APPLICATION_POTENTIAL_INTERVIEWEE),
            new PrismRoleTransition() //
                    .withRole(APPLICATION_SCHEDULED_INTERVIEWEE) //
                    .withTransitionType(RETIRE) //
                    .withTransitionRole(APPLICATION_SCHEDULED_INTERVIEWEE)),

    APPLICATION_RETIRE_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER),
            new PrismRoleTransition() //
                    .withRole(APPLICATION_SCHEDULED_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER)), //

    APPLICATION_UPDATE_POTENTIAL_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWEE) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_SCHEDULED_INTERVIEWEE)),

    APPLICATION_RETIRE_POTENTIAL_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWEE) //
                    .withTransitionType(RETIRE) //
                    .withTransitionRole(APPLICATION_POTENTIAL_INTERVIEWEE)),

    APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_SCHEDULED_INTERVIEWEE) //
                    .withTransitionType(RETIRE) //
                    .withTransitionRole(APPLICATION_SCHEDULED_INTERVIEWEE)),

    APPLICATION_REVIVE_SCHEDULED_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_INTERVIEWEE) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_SCHEDULED_INTERVIEWEE)), //

    APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_INTERVIEWEE) //
                    .withTransitionType(RETIRE) //
                    .withTransitionRole(APPLICATION_INTERVIEWEE)),

    APPLICATION_UPDATE_POTENTIAL_INTERVIEWER_GROUP_SCHEDULED( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_SCHEDULED_INTERVIEWER)), //

    APPLICATION_UPDATE_POTENTIAL_INTERVIEWER_GROUP_CONFIRMED( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_INTERVIEWER)), //

    APPLICATION_UPDATE_SCHEDULED_INTERVIEWER_GROUP_CONFIRMED( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_SCHEDULED_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_INTERVIEWER)), //

    APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_SCHEDULED_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER)), //

    APPLICATION_REVIVE_SCHEDULED_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_SCHEDULED_INTERVIEWER)), //

    APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER)), //

    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWEE) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_SCHEDULED_INTERVIEWEE) //
                    .withRestrictToOwner(), //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_SCHEDULED_INTERVIEWER) //
                    .withRestrictToOwner()), //

    APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_SCHEDULED_INTERVIEWEE) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_INTERVIEWEE) //
                    .withRestrictToOwner(), //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_SCHEDULED_INTERVIEWER) //
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
                    .withPropertyDefinition(APPLICATION_HIRING_MANAGER_ASSIGNMENT)), //

    APPLICATION_RETIRE_HIRING_MANAGER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_HIRING_MANAGER) //
                    .withTransitionType(UPDATE) //
                    .withTransitionRole(APPLICATION_VIEWER_RECRUITER)),

    APPLICATION_CONFIRM_APPOINTMENT_GROUP( //
            new PrismRoleTransition() //
                    .withRole(APPLICATION_HIRING_MANAGER) //
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
                    .withRole(DEPARTMENT_STUDENT) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(DEPARTMENT_STUDENT),
            new PrismRoleTransition() //
                    .withRole(DEPARTMENT_STUDENT) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(DEPARTMENT_STUDENT),
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
                    .withRole(INSTITUTION_STUDENT) //
                    .withTransitionType(CREATE) //
                    .withTransitionRole(INSTITUTION_STUDENT),
            new PrismRoleTransition() //
                    .withRole(INSTITUTION_STUDENT) //
                    .withTransitionType(DELETE) //
                    .withTransitionRole(INSTITUTION_STUDENT),
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
