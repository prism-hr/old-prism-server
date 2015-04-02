package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_PENDING_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedPendingCorrection.applicationCorrect;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedPendingExport.applicationEscalateApprovedPendingExport;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedPendingExport.applicationViewEditApprovedPendingExport;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationRejectedPendingExport.applicationReverseRejection;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationCommentVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationEmailCreatorVerification;

public class PrismApplicationRejectedPendingCorrection extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentVerification()); //

		stateActions.add(applicationCorrect(APPLICATION_REJECTED_PENDING_EXPORT)); //

		stateActions.add(applicationEmailCreatorVerification()); //

		stateActions.add(applicationEscalateApprovedPendingExport()); //

		stateActions.add(applicationReverseRejection()); //

		stateActions.add(applicationViewEditApprovedPendingExport());
	}

}
