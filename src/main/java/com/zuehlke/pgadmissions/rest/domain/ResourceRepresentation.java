package com.zuehlke.pgadmissions.rest.domain;


import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

import java.util.List;

public class ResourceRepresentation {

    private Integer id;

    private String code;

    private PrismState state;

    private List<PrismAction> actions;

    private List<CommentRepresentation> comments;

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

    public List<PrismAction> getActions() {
        return actions;
    }

    public void setActions(List<PrismAction> actions) {
        this.actions = actions;
    }

    public List<CommentRepresentation> getComments() {
        return comments;
    }

    public void setComments(List<CommentRepresentation> comments) {
        this.comments = comments;
    }
}


