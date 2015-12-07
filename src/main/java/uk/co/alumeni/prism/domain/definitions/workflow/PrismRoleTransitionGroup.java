package uk.co.alumeni.prism.domain.definitions.workflow;

public enum PrismRoleTransitionGroup {

    APPLICATION_CREATE_CREATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_CREATOR) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                    .withRestrictToOwner() //
                    .withMinimumPermitted(1) //
                    .withMaximumPermitted(1)), //

    APPLICATION_CREATE_REFEREE_GROUP(
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_REFEREE) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.APPLICATION_REFEREE) //
                    .withPropertyDefinition(PrismWorkflowConstraint.APPLICATION_REFEREE_ASSIGNMENT)), //

    APPLICATION_UPDATE_REFEREE_GROUP(
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_REFEREE) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.APPLICATION_REFEREE) //
                    .withPropertyDefinition(PrismWorkflowConstraint.APPLICATION_REFEREE_ASSIGNMENT), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_REFEREE) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.APPLICATION_REFEREE)), //

    APPLICATION_RETIRE_REFEREE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_REFEREE) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_VIEWER_REFEREE)), //

    APPLICATION_PROVIDE_REFERENCE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_REFEREE) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_VIEWER_REFEREE) //
                    .withRestrictToOwner()), //

    APPLICATION_CREATE_REVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_REVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.APPLICATION_REVIEWER) //
                    .withPropertyDefinition(PrismWorkflowConstraint.APPLICATION_REVIEWER_ASSIGNMENT)), //

    APPLICATION_RETIRE_REVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_REVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER)), //

    APPLICATION_PROVIDE_REVIEW_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_REVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                    .withRestrictToOwner()),

    APPLICATION_CREATE_POTENTIAL_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_CREATOR) //
                    .withTransitionType(PrismRoleTransitionType.BRANCH) //
                    .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE)),

    APPLICATION_CREATE_POTENTIAL_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withPropertyDefinition(PrismWorkflowConstraint.APPLICATION_INTERVIEWER_ASSIGNMENT)),

    APPLICATION_CREATE_SCHEDULED_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_CREATOR) //
                    .withTransitionType(PrismRoleTransitionType.BRANCH) //
                    .withTransitionRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWEE)), //

    APPLICATION_CREATE_SCHEDULED_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWER) //
                    .withPropertyDefinition(PrismWorkflowConstraint.APPLICATION_INTERVIEWER_ASSIGNMENT)),

    APPLICATION_CREATE_CONFIRMED_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_CREATOR) //
                    .withTransitionType(PrismRoleTransitionType.BRANCH) //
                    .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE)), //

    APPLICATION_CREATE_CONFIRMED_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER) //
                    .withPropertyDefinition(PrismWorkflowConstraint.APPLICATION_INTERVIEWER_ASSIGNMENT)),

    APPLICATION_RETIRE_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                    .withTransitionType(PrismRoleTransitionType.RETIRE) //
                    .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE),
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWEE) //
                    .withTransitionType(PrismRoleTransitionType.RETIRE) //
                    .withTransitionRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWEE)),

    APPLICATION_RETIRE_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER),
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER)), //

    APPLICATION_UPDATE_POTENTIAL_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWEE)),

    APPLICATION_RETIRE_POTENTIAL_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                    .withTransitionType(PrismRoleTransitionType.RETIRE) //
                    .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE)),

    APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWEE) //
                    .withTransitionType(PrismRoleTransitionType.RETIRE) //
                    .withTransitionRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWEE)),

    APPLICATION_REVIVE_SCHEDULED_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWEE)), //

    APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                    .withTransitionType(PrismRoleTransitionType.RETIRE) //
                    .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE)),

    APPLICATION_UPDATE_POTENTIAL_INTERVIEWER_GROUP_SCHEDULED( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWER)), //

    APPLICATION_UPDATE_POTENTIAL_INTERVIEWER_GROUP_CONFIRMED( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER)), //

    APPLICATION_UPDATE_SCHEDULED_INTERVIEWER_GROUP_CONFIRMED( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER)), //

    APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER)), //

    APPLICATION_REVIVE_SCHEDULED_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWER)), //

    APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER)), //

    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWEE) //
                    .withRestrictToOwner(), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWER) //
                    .withRestrictToOwner()), //

    APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWEE) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                    .withRestrictToOwner(), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_SCHEDULED_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER) //
                    .withRestrictToOwner()), //

    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                    .withRestrictToOwner()), //

    APPLICATION_CREATE_HIRING_MANAGER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_HIRING_MANAGER) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.APPLICATION_HIRING_MANAGER) //
                    .withPropertyDefinition(PrismWorkflowConstraint.APPLICATION_HIRING_MANAGER_ASSIGNMENT)), //

    APPLICATION_RETIRE_HIRING_MANAGER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_HIRING_MANAGER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER)),

    APPLICATION_CONFIRM_APPOINTMENT_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_HIRING_MANAGER) //
                    .withTransitionType(PrismRoleTransitionType.UPDATE) //
                    .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                    .withRestrictToOwner()),

    APPLICATION_EXHUME_REFEREE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_VIEWER_REFEREE) //
                    .withTransitionType(PrismRoleTransitionType.EXHUME) //
                    .withTransitionRole(PrismRole.APPLICATION_REFEREE)),

    APPLICATION_CREATE_APPOINTEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_CREATOR) //
                    .withTransitionType(PrismRoleTransitionType.BRANCH) //
                    .withTransitionRole(PrismRole.APPLICATION_APPOINTEE)),

    APPLICATION_RETIRE_APPOINTEE_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.APPLICATION_APPOINTEE) //
                    .withTransitionType(PrismRoleTransitionType.RETIRE) //
                    .withTransitionRole(PrismRole.APPLICATION_APPOINTEE)),

    PROJECT_CREATE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.PROJECT_ADMINISTRATOR) //
                    .withRestrictToOwner() //
                    .withMinimumPermitted(1) //
                    .withMaximumPermitted(1)),

    PROJECT_REVIVE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.REVIVE) //
                    .withTransitionRole(PrismRole.PROJECT_ADMINISTRATOR)),

    PROJECT_MANAGE_USERS_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.PROJECT_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.PROJECT_ADMINISTRATOR),
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROJECT_APPROVER) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.PROJECT_APPROVER), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROJECT_APPROVER) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.PROJECT_APPROVER),
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROJECT_VIEWER) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.PROJECT_VIEWER), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROJECT_VIEWER) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.PROJECT_VIEWER)), //

    PROGRAM_CREATE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                    .withRestrictToOwner() //
                    .withMinimumPermitted(1) //
                    .withMaximumPermitted(1)),

    PROGRAM_REVIVE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.REVIVE) //
                    .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR)),

    PROGRAM_MANAGE_USERS_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROGRAM_APPROVER) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.PROGRAM_APPROVER), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROGRAM_APPROVER) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.PROGRAM_APPROVER),
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROGRAM_VIEWER) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.PROGRAM_VIEWER),
            new PrismRoleTransition() //
                    .withRole(PrismRole.PROGRAM_VIEWER) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.PROGRAM_VIEWER)),

    DEPARTMENT_CREATE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.DEPARTMENT_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.DEPARTMENT_ADMINISTRATOR) //
                    .withRestrictToOwner() //
                    .withMinimumPermitted(1) //
                    .withMaximumPermitted(1)),

    DEPARTMENT_REVIVE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.DEPARTMENT_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.REVIVE) //
                    .withTransitionRole(PrismRole.DEPARTMENT_ADMINISTRATOR)),

    DEPARTMENT_MANAGE_USERS_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.DEPARTMENT_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.DEPARTMENT_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.DEPARTMENT_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.DEPARTMENT_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.DEPARTMENT_APPROVER) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.DEPARTMENT_APPROVER), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.DEPARTMENT_APPROVER) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.DEPARTMENT_APPROVER),
            new PrismRoleTransition() //
                    .withRole(PrismRole.DEPARTMENT_STUDENT) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.DEPARTMENT_STUDENT),
            new PrismRoleTransition() //
                    .withRole(PrismRole.DEPARTMENT_STUDENT) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.DEPARTMENT_STUDENT),
            new PrismRoleTransition() //
                    .withRole(PrismRole.DEPARTMENT_VIEWER) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.DEPARTMENT_VIEWER),
            new PrismRoleTransition() //
                    .withRole(PrismRole.DEPARTMENT_VIEWER) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.DEPARTMENT_VIEWER)),

    INSTITUTION_CREATE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                    .withRestrictToOwner() //
                    .withMinimumPermitted(1) //
                    .withMaximumPermitted(1)),

    INSTITUTION_REVIVE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.REVIVE) //
                    .withTransitionRole(PrismRole.INSTITUTION_ADMINISTRATOR)),

    INSTITUTION_MANAGE_USERS_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.INSTITUTION_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.INSTITUTION_ADMINISTRATOR), //
            new PrismRoleTransition() //
                    .withRole(PrismRole.INSTITUTION_APPROVER) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.INSTITUTION_APPROVER),
            new PrismRoleTransition() //
                    .withRole(PrismRole.INSTITUTION_APPROVER) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.INSTITUTION_APPROVER),
            new PrismRoleTransition() //
                    .withRole(PrismRole.INSTITUTION_STUDENT) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.INSTITUTION_STUDENT),
            new PrismRoleTransition() //
                    .withRole(PrismRole.INSTITUTION_STUDENT) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.INSTITUTION_STUDENT),
            new PrismRoleTransition() //
                    .withRole(PrismRole.INSTITUTION_VIEWER) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.INSTITUTION_VIEWER),
            new PrismRoleTransition() //
                    .withRole(PrismRole.INSTITUTION_VIEWER) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.INSTITUTION_VIEWER)),

    SYSTEM_CREATE_ADMINISTRATOR_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                    .withRestrictToOwner()
                    .withMinimumPermitted(1) //
                    .withMaximumPermitted(1)),

    SYSTEM_MANAGE_USER_GROUP( //
            new PrismRoleTransition() //
                    .withRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.CREATE) //
                    .withTransitionRole(PrismRole.SYSTEM_ADMINISTRATOR),
            new PrismRoleTransition() //
                    .withRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                    .withTransitionType(PrismRoleTransitionType.DELETE) //
                    .withTransitionRole(PrismRole.SYSTEM_ADMINISTRATOR));

    private PrismRoleTransition[] roleTransitions;

    private PrismRoleTransitionGroup(PrismRoleTransition... roleTransitions) {
        this.roleTransitions = roleTransitions;
    }

    public PrismRoleTransition[] getRoleTransitions() {
        return roleTransitions;
    }

}
