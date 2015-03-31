package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.RETIRE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.REVIVE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.UPDATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_REVIEWER;

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
	                .withPropertyDefinition(APPLICATION_ASSIGN_REFEREE)), //

	APPLICATION_DELETE_REFEREE_GROUP( //
	        new PrismRoleTransition() //
	                .withRole(APPLICATION_REFEREE) //
	                .withTransitionType(UPDATE) //
	                .withTransitionRole(APPLICATION_VIEWER_REFEREE)), //

	APPLICATION_REVIVE_REFEREE_GROUP( //
	        new PrismRoleTransition() //
	                .withRole(APPLICATION_REFEREE) //
	                .withTransitionType(REVIVE) //
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
	                .withPropertyDefinition(APPLICATION_ASSIGN_REVIEWER)), //	

	APPLICATION_DELETE_REVIEWER_GROUP( //
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

	APPLICATION_DELETE_ADMINISTRATOR_GROUP( //
	        new PrismRoleTransition() //
	                .withRole(APPLICATION_ADMINISTRATOR) //
	                .withTransitionType(UPDATE) //
	                .withTransitionRole(APPLICATION_VIEWER_RECRUITER)),

	APPLICATION_CREATE_INTERVIEWER_GROUP( //
	        new PrismRoleTransition() //
	                .withRole(APPLICATION_INTERVIEWER) //
	                .withTransitionType(CREATE) //
	                .withTransitionRole(APPLICATION_INTERVIEWER) //
	                .withPropertyDefinition(APPLICATION_ASSIGN_INTERVIEWER)),

	APPLICATION_DELETE_INTERVIEWEE_GROUP( //
	        new PrismRoleTransition() //
	                .withRole(APPLICATION_INTERVIEWEE) //
	                .withTransitionType(RETIRE) //
	                .withTransitionRole(APPLICATION_INTERVIEWEE), //
	        new PrismRoleTransition() //
	                .withRole(APPLICATION_POTENTIAL_INTERVIEWEE) //
	                .withTransitionType(RETIRE) //
	                .withTransitionRole(APPLICATION_POTENTIAL_INTERVIEWEE)),

	APPLICATION_DELETE_INTERVIEWER_GROUP( //
	        new PrismRoleTransition() //
	                .withRole(APPLICATION_INTERVIEWER) //
	                .withTransitionType(UPDATE) //
	                .withTransitionRole(APPLICATION_VIEWER_RECRUITER), //
	        new PrismRoleTransition() //
	                .withRole(APPLICATION_POTENTIAL_INTERVIEWER) //
	                .withTransitionType(UPDATE) //
	                .withTransitionRole(APPLICATION_VIEWER_RECRUITER)), //

	APPLICATION_DELETE_POTENTIAL_INTERVIEWEE_GROUP( //
	        new PrismRoleTransition() //
	                .withRole(APPLICATION_POTENTIAL_INTERVIEWEE) //
	                .withTransitionType(RETIRE) //
	                .withTransitionRole(APPLICATION_POTENTIAL_INTERVIEWEE)),

	APPLICATION_DELETE_CONFIRMED_INTERVIEWEE_GROUP( //
	        new PrismRoleTransition() //
	                .withRole(APPLICATION_INTERVIEWEE) //
	                .withTransitionType(RETIRE) //
	                .withTransitionRole(APPLICATION_INTERVIEWEE)),

	APPLICATION_DELETE_POTENTIAL_INTERVIEWER_GROUP( //
	        new PrismRoleTransition() //
	                .withRole(APPLICATION_POTENTIAL_INTERVIEWER) //
	                .withTransitionType(UPDATE) //
	                .withTransitionRole(APPLICATION_VIEWER_RECRUITER)), //

	APPLICATION_DELETE_CONFIRMED_INTERVIEWER_GROUP( //
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

	PROJECT_CREATE_ADMINISTRATOR_GROUP( //
	        new PrismRoleTransition() //
	                .withRole(PROJECT_ADMINISTRATOR) //
	                .withTransitionType(CREATE) //
	                .withTransitionRole(PROJECT_ADMINISTRATOR) //
	                .withRestrictToOwner() //
	                .withMinimumPermitted(1) //
	                .withMaximumPermitted(1)),

	PROJECT_MANAGE_USERS_GROUP( //
	        new PrismRoleTransition() //
	                .withRole(PROJECT_ADMINISTRATOR) //
	                .withTransitionType(CREATE) //
	                .withTransitionRole(PROJECT_ADMINISTRATOR), //
	        new PrismRoleTransition() //
	                .withRole(PROJECT_ADMINISTRATOR) //
	                .withTransitionType(DELETE) //
	                .withTransitionRole(PROJECT_ADMINISTRATOR), //
	        new PrismRoleTransition() //
	                .withRole(PROJECT_PRIMARY_SUPERVISOR) //
	                .withTransitionType(CREATE) //
	                .withTransitionRole(PROJECT_PRIMARY_SUPERVISOR), //
	        new PrismRoleTransition() //
	                .withRole(PROJECT_PRIMARY_SUPERVISOR) //
	                .withTransitionType(DELETE) //
	                .withTransitionRole(PROJECT_PRIMARY_SUPERVISOR), //
	        new PrismRoleTransition() //
	                .withRole(PROJECT_SECONDARY_SUPERVISOR) //
	                .withTransitionType(CREATE) //
	                .withTransitionRole(PROJECT_SECONDARY_SUPERVISOR), //
	        new PrismRoleTransition() //
	                .withRole(PROJECT_SECONDARY_SUPERVISOR) //
	                .withTransitionType(DELETE) //
	                .withTransitionRole(PROJECT_PRIMARY_SUPERVISOR)), //

	PROGRAM_CREATE_ADMINISTRATOR_GROUP( //
	        new PrismRoleTransition() //
	                .withRole(PROGRAM_ADMINISTRATOR) //
	                .withTransitionType(CREATE) //
	                .withTransitionRole(PROGRAM_ADMINISTRATOR) //
	                .withRestrictToOwner() //
	                .withMinimumPermitted(1) //
	                .withMaximumPermitted(1)),

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
	                .withTransitionRole(PROGRAM_VIEWER),
	        new PrismRoleTransition() //
	                .withRole(PROGRAM_VIEWER) //
	                .withTransitionType(CREATE) //
	                .withTransitionRole(PROGRAM_VIEWER),
	        new PrismRoleTransition() //
	                .withRole(PROGRAM_VIEWER) //
	                .withTransitionType(DELETE) //
	                .withTransitionRole(PROGRAM_VIEWER)),

	INSTITUTION_CREATE_ADMINISTRATOR_GROUP( //
	        new PrismRoleTransition() //
	                .withRole(INSTITUTION_ADMINISTRATOR) //
	                .withTransitionType(CREATE) //
	                .withTransitionRole(INSTITUTION_ADMINISTRATOR) //
	                .withRestrictToOwner() //
	                .withMinimumPermitted(1) //
	                .withMaximumPermitted(1)),

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
	                .withRole(INSTITUTION_ADMITTER) //
	                .withTransitionType(CREATE) //
	                .withTransitionRole(INSTITUTION_ADMITTER),
	        new PrismRoleTransition() //
	                .withRole(INSTITUTION_ADMITTER) //
	                .withTransitionType(DELETE) //
	                .withTransitionRole(INSTITUTION_ADMITTER));

	private PrismRoleTransition[] roleTransitions;

	private PrismRoleTransitionGroup(PrismRoleTransition... roleTransitions) {
		this.roleTransitions = roleTransitions;
	}

	public PrismRoleTransition[] getRoleTransitions() {
		return roleTransitions;
	}

}