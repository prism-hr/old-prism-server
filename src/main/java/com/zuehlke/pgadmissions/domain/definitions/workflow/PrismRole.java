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

	APPLICATION_ADMINISTRATOR(ADMINISTRATOR, APPLICATION), //
	APPLICATION_CREATOR(APPLICANT, APPLICATION), //
	APPLICATION_INTERVIEWEE(APPLICANT, APPLICATION), //
	APPLICATION_INTERVIEWER(RECRUITER, APPLICATION), //
	APPLICATION_POTENTIAL_INTERVIEWEE(APPLICANT, APPLICATION), //
	APPLICATION_POTENTIAL_INTERVIEWER(RECRUITER, APPLICATION), //
	APPLICATION_PRIMARY_SUPERVISOR(RECRUITER, APPLICATION), //
	APPLICATION_REFEREE(REFEREE, APPLICATION), //
	APPLICATION_REVIEWER(RECRUITER, APPLICATION), //
	APPLICATION_SECONDARY_SUPERVISOR(RECRUITER, APPLICATION), //
	APPLICATION_VIEWER_RECRUITER(RECRUITER, APPLICATION), //
	APPLICATION_VIEWER_REFEREE(REFEREE, APPLICATION), //
	INSTITUTION_ADMINISTRATOR(ADMINISTRATOR, INSTITUTION), //
	INSTITUTION_ADMITTER(RECRUITER, INSTITUTION), //
	PROGRAM_ADMINISTRATOR(ADMINISTRATOR, PROGRAM), //
	PROGRAM_APPROVER(RECRUITER, PROGRAM), //
	PROGRAM_VIEWER(RECRUITER, PROGRAM), //
	PROJECT_ADMINISTRATOR(ADMINISTRATOR, PROJECT), //
	PROJECT_PRIMARY_SUPERVISOR(RECRUITER, PROJECT), //
	PROJECT_SECONDARY_SUPERVISOR(RECRUITER, PROJECT), //
	SYSTEM_ADMINISTRATOR(ADMINISTRATOR, SYSTEM);

	private PrismRoleCategory roleCategory;

	private PrismScope scope;

	private PrismRole(PrismRoleCategory roleCategory, PrismScope scope) {
		this.roleCategory = roleCategory;
		this.scope = scope;
	}

	public PrismRoleCategory getRoleCategory() {
		return roleCategory;
	}

	public PrismScope getScope() {
		return scope;
	}

}
