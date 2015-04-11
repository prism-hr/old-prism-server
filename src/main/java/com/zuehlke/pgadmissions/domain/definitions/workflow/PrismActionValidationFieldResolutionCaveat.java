package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuehlke.pgadmissions.workflow.resolvers.validation.ApplicationDelegateAdministrationProcessor;
import com.zuehlke.pgadmissions.workflow.resolvers.validation.PrismValidationCaveatProcessor;

public enum PrismActionValidationFieldResolutionCaveat {

	APPLICATION_DELEGATE_ADMINISTRATION(ApplicationDelegateAdministrationProcessor.class);

	private Class<? extends PrismValidationCaveatProcessor> processor;

	private PrismActionValidationFieldResolutionCaveat(Class<? extends PrismValidationCaveatProcessor> processor) {
		this.processor = processor;
	}

	public Class<? extends PrismValidationCaveatProcessor> getProcessor() {
		return processor;
	}

}
