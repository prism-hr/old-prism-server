package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

public enum PrismStateGroup {

	APPLICATION_UNSUBMITTED(false, APPLICATION), //
	APPLICATION_VALIDATION(false, APPLICATION), //
	APPLICATION_VERIFICATION(false, APPLICATION), //
	APPLICATION_REFERENCE(false, APPLICATION), //
	APPLICATION_REVIEW(true, APPLICATION), //
	APPLICATION_INTERVIEW(true, APPLICATION), //
	APPLICATION_APPROVAL(true, APPLICATION), //
	APPLICATION_APPROVED(true, APPLICATION), //
	APPLICATION_RESERVED(true, APPLICATION), //
	APPLICATION_REJECTED(true, APPLICATION), //
	APPLICATION_WITHDRAWN(false, APPLICATION), //
	PROJECT_APPROVAL(false, PROJECT), //
	PROJECT_APPROVED(true, PROJECT), //
	PROJECT_REJECTED(false, PROJECT), //
	PROJECT_DISABLED(true, PROJECT), //
	PROJECT_WITHDRAWN(false, PROJECT), //
	PROGRAM_APPROVAL(false, PROGRAM), //
	PROGRAM_APPROVED(true, PROGRAM), //
	PROGRAM_REJECTED(false, PROGRAM), //
	PROGRAM_DISABLED(true, PROGRAM), //
	PROGRAM_WITHDRAWN(false, PROGRAM), //
	INSTITUTION_APPROVAL(false, INSTITUTION), //
	INSTITUTION_APPROVED(false, INSTITUTION), //
	INSTITUTION_REJECTED(false, INSTITUTION), //
	INSTITUTION_WITHDRAWN(false, INSTITUTION), //
	SYSTEM_RUNNING(false, SYSTEM);

	private boolean repeatable;

	private PrismScope scope;

	private PrismStateGroup(boolean repeatable, PrismScope scope) {
		this.repeatable = repeatable;
		this.scope = scope;
	}

	public final boolean isRepeatable() {
		return repeatable;
	}

	public PrismScope getScope() {
		return scope;
	}

}
