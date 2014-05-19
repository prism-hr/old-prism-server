package com.zuehlke.pgadmissions.dto;

import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;

public class ActionOutcome {

    private User user;

    private PrismScope scope;

    private ApplicationFormAction nextAction;

    public ActionOutcome(User user, PrismScope scope, ApplicationFormAction nextAction) {
        this.user = user;
        this.scope = scope;
        this.nextAction = nextAction;
    }

    public User getUser() {
        return user;
    }

    public PrismScope getScope() {
        return scope;
    }

    public ApplicationFormAction getNextAction() {
        return nextAction;
    }

    public String createRedirectionUrl() {
        Map<String, String> params = Maps.newLinkedHashMap();
        params.put("resource", scope.getId().toString());
        params.put("action", nextAction.name());

        return "redirect:/execute?" + Joiner.on("&").withKeyValueSeparator("=").join(params);
    }

}