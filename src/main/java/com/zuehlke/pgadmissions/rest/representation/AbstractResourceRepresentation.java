package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbstractResourceRepresentation {

    private Integer id;

    private String code;

    private PrismState state;

    private PrismStateGroup stateGroup;

    private List<PrismStateGroup> secondaryStates;

    private PrismScope resourceScope;

    private UserRepresentation user;

    private LocalDate endDate;

    private DateTime createdTimestamp;

    private DateTime updatedTimestamp;

    private Set<ActionRepresentation> actions;

    private List<PrismState> recommendedNextStates;

    private TimelineRepresentation timeline;

    private List<ResourceUserRolesRepresentation> users;

    private List<WorkflowPropertyConfigurationRepresentation> workflowPropertyConfigurations;

    private Map<PrismDisplayPropertyDefinition, String> displayProperties;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PrismState getState() {
        return state;
    }

    public void setState(PrismState state) {
        this.state = state;
    }

    public PrismStateGroup getStateGroup() {
        return stateGroup;
    }

    public void setStateGroup(PrismStateGroup stateGroup) {
        this.stateGroup = stateGroup;
    }

    public List<PrismStateGroup> getSecondaryStates() {
        return secondaryStates;
    }

    public void setSecondaryStates(List<PrismStateGroup> secondaryStates) {
        this.secondaryStates = secondaryStates;
    }

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
    }

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    public TimelineRepresentation getTimeline() {
        return timeline;
    }

    public void setTimeline(TimelineRepresentation timeline) {
        this.timeline = timeline;
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

    public Map<PrismDisplayPropertyDefinition, String> getDisplayProperties() {
        return displayProperties;
    }

    public void setDisplayProperties(Map<PrismDisplayPropertyDefinition, String> displayProperties) {
        this.displayProperties = displayProperties;
    }
}
