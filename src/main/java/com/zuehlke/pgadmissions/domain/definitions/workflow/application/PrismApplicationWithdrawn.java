package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;

public class PrismApplicationWithdrawn {

	public static PrismStateAction applicationEscalateWithdrawn() {
		return applicationEscalate(APPLICATION_WITHDRAWN_COMPLETED);
	}

}
