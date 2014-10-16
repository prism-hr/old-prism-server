package com.zuehlke.pgadmissions.rest.representation;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ActionRepresentation;

public class AbstractResourceRepresentation {

    private Integer id;

    private String code;

    private PrismState state;

    private PrismScope resourceScope;

    private UserRepresentation user;

    private LocalDate dueDate;

    private DateTime createdTimestamp;

    private DateTime updatedTimestamp;

    private List<ActionRepresentation> actions;

    private List<PrismActionEnhancement> actionEnhancements;

    private List<PrismState> nextStates;

    private Set<Set<CommentRepresentation>> comments;

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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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

    public final List<ActionRepresentation> getActions() {
        return actions;
    }

    public final void setActions(List<ActionRepresentation> actions) {
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

    public Set<Set<CommentRepresentation>> getComments() {
        return comments;
    }

    public void setComments(Set<Set<CommentRepresentation>> comments) {
        this.comments = comments;
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
