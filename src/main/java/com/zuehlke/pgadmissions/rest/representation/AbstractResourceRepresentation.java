package com.zuehlke.pgadmissions.rest.representation;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation;

public class AbstractResourceRepresentation {

    private Integer id;

    private String code;

    private PrismState state;

    private PrismScope resourceScope;

    private UserRepresentation user;

    private LocalDate endDate;

    private DateTime createdTimestamp;

    private DateTime updatedTimestamp;

    private Set<ActionRepresentation> actions = Sets.newLinkedHashSet();

    private List<PrismActionEnhancement> actionEnhancements;

    private List<PrismState> nextStates;
    
    private List<PrismState> recommendedNextStates;

    private TimelineRepresentation timeline;

    private List<ResourceUserRolesRepresentation> users;

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

    public final LocalDate getEndDate() {
        return endDate;
    }

    public final void setEndDate(LocalDate endDate) {
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
    
    public final Set<ActionRepresentation> getActions() {
        return actions;
    }

    public final void setActions(Set<ActionRepresentation> actions) {
        this.actions = actions;
    }

    public List<PrismActionEnhancement> getActionEnhancements() {
        return actionEnhancements;
    }

    public void setActionEnhancements(List<PrismActionEnhancement> actionEnhancements) {
        this.actionEnhancements = actionEnhancements;
    }

    public List<PrismState> getNextStates() {
        return nextStates;
    }

    public void setNextStates(List<PrismState> nextStates) {
        this.nextStates = nextStates;
    }

    public final List<PrismState> getRecommendedNextStates() {
        return recommendedNextStates;
    }

    public final void setRecommendedNextStates(List<PrismState> recommendedNextStates) {
        this.recommendedNextStates = recommendedNextStates;
    }

    public final TimelineRepresentation getTimeline() {
        return timeline;
    }

    public final void setTimeline(TimelineRepresentation timeline) {
        this.timeline = timeline;
    }

    public List<ResourceUserRolesRepresentation> getUsers() {
        return users;
    }

    public void setUsers(List<ResourceUserRolesRepresentation> users) {
        this.users = users;
    }

    public static class RoleRepresentation {

        private PrismRole id;

        private Boolean value;

        public RoleRepresentation() {
        }

        public RoleRepresentation(PrismRole id, Boolean value) {
            this.id = id;
            this.value = value;
        }

        public PrismRole getId() {
            return id;
        }

        public void setId(PrismRole id) {
            this.id = id;
        }

        public final Boolean getValue() {
            return value;
        }

        public final void setValue(Boolean value) {
            this.value = value;
        }

    }

}
