package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;
import java.util.Set;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;

public class ResourceProcessingRepresentation {

    private Set<ActionRepresentation> actions;

    private List<PrismState> recommendedNextStates;

    private List<ResourceUserRolesRepresentation> users;

    private List<WorkflowPropertyConfigurationRepresentation> workflowPropertyConfigurations;

    private ResourceAttributesRepresentation attributes;

    private List<PrismAction> partnerActions;

    public Set<ActionRepresentation> getActions() {
        return actions;
    }

    public void setActions(Set<ActionRepresentation> actions) {
        this.actions = actions;
    }

    public List<PrismState> getRecommendedNextStates() {
        return recommendedNextStates;
    }

    public void setRecommendedNextStates(List<PrismState> recommendedNextStates) {
        this.recommendedNextStates = recommendedNextStates;
    }

    public List<ResourceUserRolesRepresentation> getUsers() {
        return users;
    }

    public void setUsers(List<ResourceUserRolesRepresentation> users) {
        this.users = users;
    }

    public List<WorkflowPropertyConfigurationRepresentation> getWorkflowPropertyConfigurations() {
        return workflowPropertyConfigurations;
    }

    public void setWorkflowPropertyConfigurations(List<WorkflowPropertyConfigurationRepresentation> workflowPropertyConfigurations) {
        this.workflowPropertyConfigurations = workflowPropertyConfigurations;
    }

    public ResourceAttributesRepresentation getAttributes() {
        return attributes;
    }

    public void setAttributes(ResourceAttributesRepresentation attributes) {
        this.attributes = attributes;
    }

    public List<PrismAction> getPartnerActions() {
        return partnerActions;
    }

    public void setPartnerActions(List<PrismAction> partnerActions) {
        this.partnerActions = partnerActions;
    }

}
