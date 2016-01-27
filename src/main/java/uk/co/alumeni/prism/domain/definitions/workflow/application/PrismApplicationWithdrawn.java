package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;

public class PrismApplicationWithdrawn {

	public static PrismStateAction applicationEscalateWithdrawn() {
		return applicationEscalate(APPLICATION_WITHDRAWN_COMPLETED);
	}

}
