package com.zuehlke.pgadmissions.rest.representation.resource;

import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.comment.TimelineRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;

public class ResourceRepresentationExtended extends ResourceRepresentationListSimple {

    private Set<ActionRepresentationExtended> actions = Sets.newLinkedHashSet();
    
    private TimelineRepresentation timeline;

    private List<ResourceUserRolesRepresentation> userRoles;

    private List<WorkflowPropertyConfigurationRepresentation> workflowConfigurations;

    private List<ResourceConditionRepresentation> conditions;

    public Set<ActionRepresentationExtended> getActions() {
        return actions;
    }
    
    public void setActions(Collection<ActionRepresentationExtended> actions) {
        actions.addAll(actions);
    }
    
    public TimelineRepresentation getTimeline() {
        return timeline;
    }

    public void setTimeline(TimelineRepresentation timeline) {
        this.timeline = timeline;
    }

    public List<ResourceUserRolesRepresentation> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<ResourceUserRolesRepresentation> userRoles) {
        this.userRoles = userRoles;
    }

    public List<WorkflowPropertyConfigurationRepresentation> getWorkflowConfigurations() {
        return workflowConfigurations;
    }

    public void setWorkflowConfigurations(List<WorkflowPropertyConfigurationRepresentation> workflowConfigurations) {
        this.workflowConfigurations = workflowConfigurations;
    }

    public List<ResourceConditionRepresentation> getConditions() {
        return conditions;
    }

    public void setConditions(List<ResourceConditionRepresentation> conditions) {
        this.conditions = conditions;
    }
    
    public void setParentResource(ResourceRepresentationSimple parentResource) {
        setProperty(this, parentResource.getResourceScope().getLowerCamelName(), parentResource);
    }

}
