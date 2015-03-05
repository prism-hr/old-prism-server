package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_REVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_SUGGESTED_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory.ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory.APPLICANT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory.RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory.REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;

public enum PrismRole {

	APPLICATION_ADMINISTRATOR(ADMINISTRATOR, false, APPLICATION, SYSTEM_ROLE_ADMINISTRATOR), //
	APPLICATION_CREATOR(APPLICANT, true, APPLICATION, SYSTEM_ROLE_CREATOR), //
	APPLICATION_INTERVIEWEE(APPLICANT, false, APPLICATION, SYSTEM_ROLE_INTERVIEWEE), //
	APPLICATION_INTERVIEWER(RECRUITER, false, APPLICATION, SYSTEM_ROLE_INTERVIEWER), //
	APPLICATION_POTENTIAL_INTERVIEWEE(APPLICANT, false, APPLICATION, SYSTEM_ROLE_INTERVIEWEE), //
	APPLICATION_POTENTIAL_INTERVIEWER(RECRUITER, false, APPLICATION, SYSTEM_ROLE_INTERVIEWER), //
	APPLICATION_PRIMARY_SUPERVISOR(RECRUITER, false, APPLICATION, SYSTEM_ROLE_PRIMARY_SUPERVISOR), //
	APPLICATION_REFEREE(REFEREE, false, APPLICATION, SYSTEM_ROLE_REFEREE), //
	APPLICATION_REVIEWER(RECRUITER, false, APPLICATION, SYSTEM_ROLE_REVIEWER), //
	APPLICATION_SECONDARY_SUPERVISOR(RECRUITER, false, APPLICATION, SYSTEM_ROLE_SECONDARY_SUPERVISOR), //
	APPLICATION_SUGGESTED_SUPERVISOR(RECRUITER, false, APPLICATION, SYSTEM_ROLE_SUGGESTED_SUPERVISOR), //
	APPLICATION_VIEWER_RECRUITER(RECRUITER, false, APPLICATION, SYSTEM_ROLE_VIEWER), //
	APPLICATION_VIEWER_REFEREE(REFEREE, false, APPLICATION, SYSTEM_ROLE_REFEREE), //
	INSTITUTION_ADMINISTRATOR(ADMINISTRATOR, true, INSTITUTION, SYSTEM_ROLE_ADMINISTRATOR), //
	INSTITUTION_ADMITTER(RECRUITER, false, INSTITUTION, SYSTEM_ROLE_ADMITTER), //
	PROGRAM_ADMINISTRATOR(ADMINISTRATOR, true, PROGRAM, SYSTEM_ROLE_ADMINISTRATOR), //
	PROGRAM_APPROVER(RECRUITER, false, PROGRAM, SYSTEM_ROLE_APPROVER), //
	PROGRAM_VIEWER(RECRUITER, false, PROGRAM, SYSTEM_ROLE_VIEWER), //
	PROJECT_ADMINISTRATOR(ADMINISTRATOR, false, PROJECT, SYSTEM_ROLE_ADMINISTRATOR), //
	PROJECT_PRIMARY_SUPERVISOR(RECRUITER, true, PROJECT, SYSTEM_ROLE_PRIMARY_SUPERVISOR), //
	PROJECT_SECONDARY_SUPERVISOR(RECRUITER, false, PROJECT, SYSTEM_ROLE_SECONDARY_SUPERVISOR), //
	SYSTEM_ADMINISTRATOR(ADMINISTRATOR, true, SYSTEM, SYSTEM_ROLE_ADMINISTRATOR);

	private PrismRoleCategory roleCategory;

	private boolean scopeOwner;

	private PrismScope scope;

	private PrismDisplayPropertyDefinition displayPropertyDefinition;

	private static HashMultimap<PrismScope, PrismRole> scopeOwners = HashMultimap.create();

	private PrismRole(PrismRoleCategory roleCategory, boolean scopeOwner, PrismScope scope, PrismDisplayPropertyDefinition displayPropertyDefinition) {
		this.roleCategory = roleCategory;
		this.scopeOwner = scopeOwner;
		this.scope = scope;
		this.displayPropertyDefinition = displayPropertyDefinition;
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

	public final PrismDisplayPropertyDefinition getDisplayPropertyDefinition() {
		return displayPropertyDefinition;
	}

	public static Set<PrismRole> getScopeOwners(PrismScope scope) {
		return scopeOwners.get(scope);
	}

}
