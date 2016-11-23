package uk.co.alumeni.prism.rest.representation.resource;

import uk.co.alumeni.prism.rest.representation.action.ActionRepresentationSimple;

import java.util.List;

public class ResourceListRowRepresentation extends ResourceRepresentationStandard {

    private List<ActionRepresentationSimple> actions;

    public List<ActionRepresentationSimple> getActions() {
        return actions;
    }

    public void setActions(List<ActionRepresentationSimple> actions) {
        this.actions = actions;
    }

}
