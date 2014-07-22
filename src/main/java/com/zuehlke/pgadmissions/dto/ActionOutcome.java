package com.zuehlke.pgadmissions.dto;

import java.util.Map;

import org.hibernate.Hibernate;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;

public class ActionOutcome {

    private User user;

    private Resource transitionResource;

    private Action transitionAction;

    public ActionOutcome(User user, Resource transitionResource, Action transitionAction) {
        this.user = user;
        this.transitionResource = transitionResource;
        this.transitionAction = transitionAction;
    }

    public User getUser() {
        return user;
    }

    public Resource getResource() {
        Hibernate.initialize(transitionResource);
        return transitionResource;
    }

    public Action getNextAction() {
        return transitionAction;
    }

    public String createRedirectionUrl() {
        Map<String, String> params = Maps.newLinkedHashMap();
        params.put("resource", transitionResource.getId().toString());
        params.put("action", transitionAction.getId().toString());
        return "redirect:/execute?" + Joiner.on("&").withKeyValueSeparator("=").join(params);
    }

}