package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationSimple;

public class ResourceListRowRepresentation extends ResourceRepresentationStandard {

    private List<ActionRepresentationSimple> actions;

    public List<ActionRepresentationSimple> getActions() {
        return actions;
    }

    public void setActions(List<ActionRepresentationSimple> actions) {
        this.actions = actions;
    }

}
