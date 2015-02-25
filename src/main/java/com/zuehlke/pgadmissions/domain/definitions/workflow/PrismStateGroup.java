package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_APPROVAL_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_APPROVED_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_INTERVIEW_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_REFERENCE_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_REJECTED_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_REVIEW_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_UNSUBMITTED_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_VALIDATION_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_VERIFICATION_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_WITHDRAWN_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.INSTITUTION_APPROVAL_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.INSTITUTION_APPROVED_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.INSTITUTION_REJECTED_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.INSTITUTION_WITHDRAWN_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROGRAM_APPROVAL_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROGRAM_APPROVED_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROGRAM_DISABLED_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROGRAM_REJECTED_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROGRAM_WITHDRAWN_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROJECT_APPROVAL_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROJECT_APPROVED_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROJECT_DISABLED_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROJECT_REJECTED_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROJECT_WITHDRAWN_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RUNNING_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;

public enum PrismStateGroup {

	APPLICATION_UNSUBMITTED(1, false, APPLICATION, null, APPLICATION_UNSUBMITTED_STATE_GROUP), //
	APPLICATION_VALIDATION(2, false, APPLICATION, null, APPLICATION_VALIDATION_STATE_GROUP), //
	APPLICATION_VERIFICATION(3, false, APPLICATION, null, APPLICATION_VERIFICATION_STATE_GROUP), //
	APPLICATION_REFERENCE(4, false, APPLICATION, null, APPLICATION_REFERENCE_STATE_GROUP), //
	APPLICATION_REVIEW(5, true, APPLICATION, null, APPLICATION_REVIEW_STATE_GROUP), //
	APPLICATION_SHORTLISTING(6, true, APPLICATION, Lists.newArrayList(PROJECT, PROGRAM), APPLICATION_REVIEW_STATE_GROUP), //
	APPLICATION_INTERVIEW(7, true, APPLICATION, null, APPLICATION_INTERVIEW_STATE_GROUP), //
	APPLICATION_APPROVAL(8, true, APPLICATION, null, APPLICATION_APPROVAL_STATE_GROUP), //
	APPLICATION_APPROVED(9, true, APPLICATION, null, APPLICATION_APPROVED_STATE_GROUP), //
	APPLICATION_REJECTED(10, true, APPLICATION, null, APPLICATION_REJECTED_STATE_GROUP), //
	APPLICATION_WITHDRAWN(11, false, APPLICATION, null, APPLICATION_WITHDRAWN_STATE_GROUP), //
	PROJECT_APPROVAL(1, false, PROJECT, null, PROJECT_APPROVAL_STATE_GROUP), //
	PROJECT_APPROVED(2, true, PROJECT, null, PROJECT_APPROVED_STATE_GROUP), //
	PROJECT_REJECTED(3, false, PROJECT, null, PROJECT_REJECTED_STATE_GROUP), //
	PROJECT_DISABLED(4, true, PROJECT, null, PROJECT_DISABLED_STATE_GROUP), //
	PROJECT_WITHDRAWN(5, false, PROJECT, null, PROJECT_WITHDRAWN_STATE_GROUP), //
	PROGRAM_APPROVAL(1, false, PROGRAM, null, PROGRAM_APPROVAL_STATE_GROUP), //
	PROGRAM_APPROVED(2, true, PROGRAM, null, PROGRAM_APPROVED_STATE_GROUP), //
	PROGRAM_REJECTED(3, false, PROGRAM, null, PROGRAM_REJECTED_STATE_GROUP), //
	PROGRAM_DISABLED(4, true, PROGRAM, null, PROGRAM_DISABLED_STATE_GROUP), //
	PROGRAM_WITHDRAWN(5, false, PROGRAM, null, PROGRAM_WITHDRAWN_STATE_GROUP), //
	INSTITUTION_APPROVAL(1, false, INSTITUTION, null, INSTITUTION_APPROVAL_STATE_GROUP), //
	INSTITUTION_APPROVED(2, false, INSTITUTION, null, INSTITUTION_APPROVED_STATE_GROUP), //
	INSTITUTION_REJECTED(3, false, INSTITUTION, null, INSTITUTION_REJECTED_STATE_GROUP), //
	INSTITUTION_WITHDRAWN(4, false, INSTITUTION, null, INSTITUTION_WITHDRAWN_STATE_GROUP), //
	SYSTEM_RUNNING(1, false, SYSTEM, null, SYSTEM_RUNNING_STATE_GROUP);

	private Integer sequenceOrder;

	private boolean repeatable;

	private PrismScope scope;

	private List<PrismScope> batchScopes;

	private PrismDisplayPropertyDefinition displayProperty;

	private PrismStateGroup(Integer sequenceOrder, boolean repeatable, PrismScope scope, List<PrismScope> batchScopes,
	        PrismDisplayPropertyDefinition displayProperty) {
		this.sequenceOrder = sequenceOrder;
		this.repeatable = repeatable;
		this.scope = scope;
		this.batchScopes = batchScopes;
		this.displayProperty = displayProperty;
	}

	public int getSequenceOrder() {
		return sequenceOrder;
	}

	public final boolean isRepeatable() {
		return repeatable;
	}

	public PrismScope getScope() {
		return scope;
	}

	public List<PrismScope> getBatchScopes() {
		return batchScopes == null ? Lists.<PrismScope> newArrayList() : batchScopes;
	}

	public final PrismDisplayPropertyDefinition getDisplayProperty() {
		return displayProperty;
	}

}
