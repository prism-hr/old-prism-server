package com.zuehlke.pgadmissions.rest.representation;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class SecuredResourceRepresentation {

    private Integer id;

    private String code;

    private PrismState state;

    private PrismScope resourceScope;

    private List<PrismAction> actions;
    
    private List<PrismActionEnhancement> actionEnhancements;

    private List<CommentRepresentation> comments;

    private List<RegisteredUserRepresentation> users;

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

    public List<PrismAction> getActions() {
        return actions;
    }

    public void setActions(List<PrismAction> actions) {
        this.actions = actions;
    }

    public List<PrismActionEnhancement> getActionEnhancements() {
        return actionEnhancements;
    }

    public void setActionEnhancements(List<PrismActionEnhancement> actionEnhancements) {
        this.actionEnhancements = actionEnhancements;
    }

    public List<CommentRepresentation> getComments() {
        return comments;
    }

    public void setComments(List<CommentRepresentation> comments) {
        this.comments = comments;
    }

    public List<RegisteredUserRepresentation> getUsers() {
        return users;
    }

    public void setUsers(List<RegisteredUserRepresentation> users) {
        this.users = users;
    }
    
}
