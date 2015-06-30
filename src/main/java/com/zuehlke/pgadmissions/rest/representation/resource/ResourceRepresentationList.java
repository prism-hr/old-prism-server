package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationSimple;

public class ResourceRepresentationList extends ResourceRepresentationStandard {
    
    private List<ActionRepresentationSimple> actions;
    
    private String sequenceIdentifier;
    
    public List<ActionRepresentationSimple> getActions() {
        return actions;
    }

    public void setActions(List<ActionRepresentationSimple> actions) {
        this.actions = actions;
    }
    
    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    public ResourceRepresentationList withResourceScope(PrismScope resourceScope) {
        setResourceScope(resourceScope);
        return this;
    }
    
    public ResourceRepresentationList withId(Integer id) {
        setId(id);
        return this;
    }
    
}
