package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.rest.representation.StateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.TimelineRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;

public class ResourceRepresentationExtended extends ResourceRepresentationSimple {

    private UserRepresentation user;

    private ResourceRepresentationSimple institution;

    private ResourceRepresentationSimple department;

    private ResourceRepresentationSimple program;

    private ResourceRepresentationSimple project;

    private StateRepresentation state;

    private StateRepresentation previousState;

    private List<StateRepresentation> secondaryStates;

    private DateTime createdTimestamp;

    private DateTime updatedTimestamp;

    private TimelineRepresentation timeline;

    private List<ActionRepresentation> actions;

    private List<ResourceUserRolesRepresentation> userRoles;

    private List<WorkflowPropertyConfigurationRepresentation> workflowConfigurations;

    private List<ResourceConditionRepresentation> conditions;

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation userRepresentation) {
        this.user = userRepresentation;
    }

    public ResourceRepresentationSimple getInstitution() {
        return institution;
    }

    public void setInstitution(ResourceRepresentationSimple institution) {
        this.institution = institution;
    }
    
    public ResourceRepresentationSimple getDepartment() {
        return department;
    }

    public void setDepartment(ResourceRepresentationSimple department) {
        this.department = department;
    }

    public ResourceRepresentationSimple getProgram() {
        return program;
    }

    public void setProgram(ResourceRepresentationSimple program) {
        this.program = program;
    }

    public ResourceRepresentationSimple getProject() {
        return project;
    }

    public void setProject(ResourceRepresentationSimple project) {
        this.project = project;
    }

    public StateRepresentation getState() {
        return state;
    }

    public void setState(StateRepresentation state) {
        this.state = state;
    }

    public StateRepresentation getPreviousState() {
        return previousState;
    }

    public void setPreviousState(StateRepresentation previousState) {
        this.previousState = previousState;
    }

    public List<StateRepresentation> getSecondaryStates() {
        return secondaryStates;
    }

    public void setSecondaryStates(List<StateRepresentation> secondaryStates) {
        this.secondaryStates = secondaryStates;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public TimelineRepresentation getTimeline() {
        return timeline;
    }

    public void setTimeline(TimelineRepresentation timeline) {
        this.timeline = timeline;
    }

    public List<ActionRepresentation> getActions() {
        return actions;
    }

    public void setActions(List<ActionRepresentation> actions) {
        this.actions = actions;
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
