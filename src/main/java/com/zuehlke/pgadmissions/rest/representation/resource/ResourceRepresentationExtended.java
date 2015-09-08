package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentTimelineRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationClient;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "scope")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApplicationRepresentationClient.class, name = "RESUME"),
        @JsonSubTypes.Type(value = ApplicationRepresentationClient.class, name = "APPLICATION"),
        @JsonSubTypes.Type(value = ProjectRepresentationClient.class, name = "PROJECT"),
        @JsonSubTypes.Type(value = ProgramRepresentationClient.class, name = "PROGRAM"),
        @JsonSubTypes.Type(value = DepartmentRepresentationClient.class, name = "DEPARTMENT"),
        @JsonSubTypes.Type(value = InstitutionRepresentationClient.class, name = "INSTITUTION")
})
public class ResourceRepresentationExtended extends ResourceRepresentationStandard {

    private List<ActionRepresentationExtended> actions;

    private CommentTimelineRepresentation timeline;

    private List<ResourceUserRolesRepresentation> userRoles;

    private List<WorkflowPropertyConfigurationRepresentation> workflowConfigurations;

    private List<ResourceConditionRepresentation> conditions;

    public List<ActionRepresentationExtended> getActions() {
        return actions;
    }

    public void setActions(List<ActionRepresentationExtended> actions) {
        this.actions = actions;
    }

    public CommentTimelineRepresentation getTimeline() {
        return timeline;
    }

    public void setTimeline(CommentTimelineRepresentation timeline) {
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

}
