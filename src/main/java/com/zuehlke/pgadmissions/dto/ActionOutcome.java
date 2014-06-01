package com.zuehlke.pgadmissions.dto;

import java.util.Map;

import org.hibernate.Hibernate;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;

public class ActionOutcome {

    private User user;

    private PrismResource resource;

    private PrismAction nextAction;

    public ActionOutcome(User user, PrismResource resource, PrismAction nextAction) {
        this.user = user;
        this.resource = resource;
        this.nextAction = nextAction;
    }

    public User getUser() {
        return user;
    }

    public PrismResource getResource() {
        Hibernate.initialize(resource);
        return resource;
    }

    public PrismAction getNextAction() {
        return nextAction;
    }

    public String createRedirectionUrl() {
        Map<String, String> params = Maps.newLinkedHashMap();
        params.put("resource", resource.getId().toString());
        params.put("action", nextAction.name());

        return "redirect:/execute?" + Joiner.on("&").withKeyValueSeparator("=").join(params);
    }

}