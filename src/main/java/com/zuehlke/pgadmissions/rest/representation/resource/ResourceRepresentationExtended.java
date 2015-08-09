package com.zuehlke.pgadmissions.rest.representation.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.comment.TimelineRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationClient;

import java.util.List;

import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "scope")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApplicationRepresentationClient.class, name = "APPLICATION"),
        @JsonSubTypes.Type(value = ResourceOpportunityRepresentationClient.class, name = "PROJECT"),
        @JsonSubTypes.Type(value = ResourceOpportunityRepresentationClient.class, name = "PROGRAM"),
        @JsonSubTypes.Type(value = ResourceParentDivisionRepresentationClient.class, name = "DEPARTMENT"),
        @JsonSubTypes.Type(value = InstitutionRepresentationClient.class, name = "INSTITUTION")
})
public class ResourceRepresentationExtended extends ResourceRepresentationStandard {

    private List<ActionRepresentationExtended> actions;

    private TimelineRepresentation timeline;

    private List<ResourceUserRolesRepresentation> userRoles;

    private List<WorkflowPropertyConfigurationRepresentation> workflowConfigurations;

    private List<ResourceConditionRepresentation> conditions;

    public List<ActionRepresentationExtended> getActions() {
        return actions;
    }

    public void setActions(List<ActionRepresentationExtended> actions) {
        this.actions = actions;
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
        setProperty(this, parentResource.getScope().getLowerCamelName(), parentResource);
    }

}
