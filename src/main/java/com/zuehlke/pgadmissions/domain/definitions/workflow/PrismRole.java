package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory.ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory.APPLICANT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory.RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory.REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.REVIVE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.UPDATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_REFEREE;

import java.util.Set;

import com.google.common.collect.HashMultimap;

public enum PrismRole {

	APPLICATION_ADMINISTRATOR(ADMINISTRATOR, false, APPLICATION), //
	APPLICATION_CREATOR(APPLICANT, true, APPLICATION), //
	APPLICATION_INTERVIEWEE(APPLICANT, false, APPLICATION), //
	APPLICATION_INTERVIEWER(RECRUITER, false, APPLICATION), //
	APPLICATION_POTENTIAL_INTERVIEWEE(APPLICANT, false, APPLICATION), //
	APPLICATION_POTENTIAL_INTERVIEWER(RECRUITER, false, APPLICATION), //
	APPLICATION_PRIMARY_SUPERVISOR(RECRUITER, false, APPLICATION), //
	APPLICATION_REFEREE(REFEREE, false, APPLICATION), //
	APPLICATION_REVIEWER(RECRUITER, false, APPLICATION), //
	APPLICATION_SECONDARY_SUPERVISOR(RECRUITER, false, APPLICATION), //
	APPLICATION_VIEWER_RECRUITER(RECRUITER, false, APPLICATION), //
	APPLICATION_VIEWER_REFEREE(REFEREE, false, APPLICATION), //
	INSTITUTION_ADMINISTRATOR(ADMINISTRATOR, true, INSTITUTION), //
	INSTITUTION_ADMITTER(RECRUITER, false, INSTITUTION), //
	PROGRAM_ADMINISTRATOR(ADMINISTRATOR, true, PROGRAM), //
	PROGRAM_APPROVER(RECRUITER, false, PROGRAM), //
	PROGRAM_VIEWER(RECRUITER, false, PROGRAM), //
	PROJECT_ADMINISTRATOR(ADMINISTRATOR, false, PROJECT), //
	PROJECT_PRIMARY_SUPERVISOR(RECRUITER, true, PROJECT), //
	PROJECT_SECONDARY_SUPERVISOR(RECRUITER, false, PROJECT), //
	SYSTEM_ADMINISTRATOR(ADMINISTRATOR, true, SYSTEM);

	private PrismRoleCategory roleCategory;

	private boolean scopeOwner;

	private PrismScope scope;

	private static HashMultimap<PrismScope, PrismRole> scopeOwners = HashMultimap.create();

	private PrismRole(PrismRoleCategory roleCategory, boolean scopeOwner, PrismScope scope) {
		this.roleCategory = roleCategory;
		this.scopeOwner = scopeOwner;
		this.scope = scope;
	}

	public PrismRoleCategory getRoleCategory() {
		return roleCategory;
	}

	public boolean isScopeOwner() {
		return scopeOwner;
	}

	public PrismScope getScope() {
		return scope;
	}

	public static Set<PrismRole> getScopeOwners(PrismScope scope) {
		return scopeOwners.get(scope);
	}

	public enum PrismRoleGroup {

		APPLICATION_PARENT_ADMINISTRATOR_GROUP(INSTITUTION_ADMINISTRATOR, PROGRAM_ADMINISTRATOR, PROJECT_ADMINISTRATOR), //

		APPLICATION_ADMINISTRATOR_GROUP(INSTITUTION_ADMINISTRATOR, PROGRAM_ADMINISTRATOR, PROJECT_ADMINISTRATOR, APPLICATION_ADMINISTRATOR), //

		APPLICATION_PARENT_VIEWER_GROUP(INSTITUTION_ADMINISTRATOR, INSTITUTION_ADMITTER, PROGRAM_ADMINISTRATOR, PROGRAM_APPROVER, PROGRAM_VIEWER,
		        PROJECT_ADMINISTRATOR, PROJECT_PRIMARY_SUPERVISOR, PROJECT_SECONDARY_SUPERVISOR), //

		APPLICATION_VIEWER_GROUP(INSTITUTION_ADMINISTRATOR, INSTITUTION_ADMITTER, PROGRAM_ADMINISTRATOR, PROGRAM_APPROVER, PROGRAM_VIEWER,
		        PROJECT_ADMINISTRATOR, PROJECT_PRIMARY_SUPERVISOR, PROJECT_SECONDARY_SUPERVISOR, APPLICATION_ADMINISTRATOR), //

		APPLICATION_SUPERVISOR_GROUP(PROJECT_PRIMARY_SUPERVISOR, PROJECT_SECONDARY_SUPERVISOR, APPLICATION_PRIMARY_SUPERVISOR, APPLICATION_SECONDARY_SUPERVISOR), //

		APPLICATION_RECRUITER_GROUP(PROGRAM_APPROVER, PROGRAM_VIEWER, PROJECT_PRIMARY_SUPERVISOR, PROJECT_SECONDARY_SUPERVISOR, APPLICATION_ADMINISTRATOR, //
		        APPLICATION_REVIEWER, APPLICATION_INTERVIEWER, APPLICATION_PRIMARY_SUPERVISOR, APPLICATION_SECONDARY_SUPERVISOR, APPLICATION_VIEWER_RECRUITER), //

		PROJECT_SUPERVISOR_GROUP(PROJECT_PRIMARY_SUPERVISOR, PROJECT_SECONDARY_SUPERVISOR),

		PROJECT_PARENT_ADMINISTRATOR_GROUP(INSTITUTION_ADMITTER, PROGRAM_ADMINISTRATOR),

		PROJECT_ADMINISTRATOR_GROUP(INSTITUTION_ADMINISTRATOR, PROGRAM_ADMINISTRATOR, PROJECT_ADMINISTRATOR),

		PROGRAM_PARENT_ADMINISTRATOR_GROUP(SYSTEM_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR),

		PROGRAM_ADMINISTRATOR_GROUP(SYSTEM_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR, PROGRAM_ADMINISTRATOR),

		INSTITUTION_ADMINISTRATOR_GROUP(SYSTEM_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR);

		private PrismRole[] roles;

		private PrismRoleGroup(PrismRole... roles) {
			this.roles = roles;
		}

		public PrismRole[] getRoles() {
			return roles;
		}

	}

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

		APPLICATION_PROVIDE_REFERENCE_GROUP( //
		        new PrismRoleTransition() //
		                .withRole(APPLICATION_REFEREE) //
		                .withTransitionType(UPDATE) //
		                .withTransitionRole(APPLICATION_VIEWER_REFEREE) //
		                .withRestrictToOwner()), //

		APPLICATION_REVIVE_REFEREE_GROUP( //
		        new PrismRoleTransition() //
		                .withRole(APPLICATION_REFEREE) //
		                .withTransitionType(REVIVE) //
		                .withTransitionRole(APPLICATION_VIEWER_REFEREE)), //

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

}
