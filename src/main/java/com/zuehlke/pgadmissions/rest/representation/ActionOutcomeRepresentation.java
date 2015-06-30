package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ActionOutcomeRepresentation {

    private ResourceRepresentation transitionResource;

    private PrismAction transitionAction;

    public ResourceRepresentation getTransitionResource() {
        return transitionResource;
    }

    public void setTransitionResource(ResourceRepresentation transitionResource) {
        this.transitionResource = transitionResource;
    }

    public PrismAction getTransitionAction() {
        return transitionAction;
    }

    public void setTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
    }

    public static class ResourceRepresentation {

        private Integer id;

        private PrismScope resourceScope;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public PrismScope getResourceScope() {
            return resourceScope;
        }

        public void setResourceScope(PrismScope resourceScope) {
            this.resourceScope = resourceScope;
        }

    }
}
