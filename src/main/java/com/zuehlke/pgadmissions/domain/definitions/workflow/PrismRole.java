package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory.ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory.APPLICANT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory.RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory.REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

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
	PROJECT_ADMINISTRATOR(ADMINISTRATOR, true, PROJECT), //
	PROJECT_PRIMARY_SUPERVISOR(RECRUITER, false, PROJECT), //
	PROJECT_SECONDARY_SUPERVISOR(RECRUITER, false, PROJECT), //
	SYSTEM_ADMINISTRATOR(ADMINISTRATOR, true, SYSTEM);

	private PrismRoleCategory roleCategory;

	private boolean scopeCreator;

	private PrismScope scope;

	private PrismRole(PrismRoleCategory roleCategory, boolean scopeCreator, PrismScope scope) {
		this.roleCategory = roleCategory;
		this.scopeCreator = scopeCreator;
		this.scope = scope;
	}

	public PrismRoleCategory getRoleCategory() {
		return roleCategory;
	}

	public boolean isScopeCreator() {
		return scopeCreator;
	}

	public PrismScope getScope() {
		return scope;
	}

}
