package com.zuehlke.pgadmissions.dto;

import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;

public class ActionOutcome {

    private User user;

    private Resource resource;

    private Resource transitionResource;

    private Action transitionAction;

    public User getUser() {
        return user;
    }

    public Resource getResource() {
        return resource;
    }

    public Resource getTransitionResource() {
        return transitionResource;
    }

    public Action getTransitionAction() {
        return transitionAction;
    }
    
    public ActionOutcome withUser(User user) {
        this.user = user;
        return this;
    }
    
    public ActionOutcome withResource(Resource resource) {
        this.resource = resource;
        return this;
    }
    
    public ActionOutcome withTransitionResource(Resource transitionResource) {
        this.transitionResource = transitionResource;
        return this;
    }
    
    public ActionOutcome withTransitionAction(Action transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }

    public String createRedirectionUrl() {
        Map<String, String> params = Maps.newLinkedHashMap();
        params.put("resource", transitionResource.getId().toString());
        params.put("action", transitionAction.getId().toString());
        return "redirect:/execute?" + Joiner.on("&").withKeyValueSeparator("=").join(params);
    }

}