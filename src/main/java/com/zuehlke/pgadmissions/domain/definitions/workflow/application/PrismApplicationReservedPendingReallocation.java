package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReserved.applicationCompleteReserved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReserved.applicationEscalateReserved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReserved.applicationTerminateReserved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReserved.applicationWithdrawnReserved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReservedPendingReallocation extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //
        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(applicationEscalateReserved()); //

        stateActions.add(applicationCompleteReserved(state) //
                .withRaisesUrgentFlag());

        stateActions.add(applicationTerminateReserved());
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditWithViewerRecruiter(state)); //
        stateActions.add(applicationWithdrawnReserved());
    }

}
