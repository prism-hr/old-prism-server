package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.collect.HashMultimap;

import java.util.Set;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;

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
	APPLICATION_SUGGESTED_SUPERVISOR(RECRUITER, false, APPLICATION), //
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

}
