package com.zuehlke.pgadmissions.dto;

import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;

public class ActionOutcome {

    private User user;

    private PrismResource scope;

    private SystemAction nextAction;

    public ActionOutcome(User user, PrismResource scope, SystemAction nextAction) {
        this.user = user;
        this.scope = scope;
        this.nextAction = nextAction;
    }

    public User getUser() {
        return user;
    }

    public PrismResource getScope() {
        return scope;
    }

    public SystemAction getNextAction() {
        return nextAction;
    }

    public String createRedirectionUrl() {
        Map<String, String> params = Maps.newLinkedHashMap();
        params.put("resource", scope.getId().toString());
        params.put("action", nextAction.name());

        return "redirect:/execute?" + Joiner.on("&").withKeyValueSeparator("=").join(params);
    }

}