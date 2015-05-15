package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuehlke.pgadmissions.workflow.resolvers.state.termination.ApplicationProvidedReferenceTerminationResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.termination.StateTerminationResolver;

public enum PrismStateTerminationEvaluation {

    APPLICATION_REFERENCED_TERMINATION(ApplicationProvidedReferenceTerminationResolver.class);
    
    private Class<? extends StateTerminationResolver> resolver;

	private PrismStateTerminationEvaluation(Class<? extends StateTerminationResolver> resolver) {
	    this.resolver = resolver;
    }

	public Class<? extends StateTerminationResolver> getResolver() {
		return resolver;
	}

}
