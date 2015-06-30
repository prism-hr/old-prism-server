package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationSimple;

public class ResourceRepresentationListExtended extends ResourceRepresentationListSimple {

    private List<ActionRepresentationSimple> actions;

    public List<ActionRepresentationSimple> getActions() {
        return actions;
    }

    public void setActions(List<ActionRepresentationSimple> actions) {
        this.actions = actions;
    }

    public ResourceRepresentationListExtended withResourceScope(PrismScope resourceScope) {
        setResourceScope(resourceScope);
        return this;
    }

    public ResourceRepresentationListExtended withId(Integer id) {
        setId(id);
        return this;
    }

}
