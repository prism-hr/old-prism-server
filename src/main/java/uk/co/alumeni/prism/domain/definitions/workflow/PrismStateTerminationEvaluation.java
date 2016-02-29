package uk.co.alumeni.prism.domain.definitions.workflow;

import uk.co.alumeni.prism.workflow.resolvers.state.termination.ApplicationProvidedReferenceTerminationResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.termination.StateTerminationResolver;

public enum PrismStateTerminationEvaluation {

    APPLICATION_REFERENCED_TERMINATION(ApplicationProvidedReferenceTerminationResolver.class);

    private Class<? extends StateTerminationResolver<?>> resolver;

	private PrismStateTerminationEvaluation(Class<? extends StateTerminationResolver<?>> resolver) {
	    this.resolver = resolver;
    }

	public Class<? extends StateTerminationResolver<?>> getResolver() {
		return resolver;
	}

}
