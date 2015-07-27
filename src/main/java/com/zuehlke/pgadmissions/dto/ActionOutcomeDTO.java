package com.zuehlke.pgadmissions.dto;

import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;

public class ActionOutcomeDTO {

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
    
    public ActionOutcomeDTO withUser(User user) {
        this.user = user;
        return this;
    }
    
    public ActionOutcomeDTO withResource(Resource resource) {
        this.resource = resource;
        return this;
    }
    
    public ActionOutcomeDTO withTransitionResource(Resource transitionResource) {
        this.transitionResource = transitionResource;
        return this;
    }
    
    public ActionOutcomeDTO withTransitionAction(Action transitionAction) {
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