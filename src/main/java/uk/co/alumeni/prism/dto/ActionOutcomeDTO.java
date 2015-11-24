package uk.co.alumeni.prism.dto;

import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import uk.co.alumeni.prism.domain.definitions.PrismConnectionState;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;

public class ActionOutcomeDTO {

    private User user;

    private Resource resource;

    private Resource transitionResource;

    private Action transitionAction;

    private PrismConnectionState connectState;

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

    public PrismConnectionState getConnectState() {
        return connectState;
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

    public ActionOutcomeDTO withConnectState(PrismConnectionState connectState) {
        this.connectState = connectState;
        return this;
    }

    public String createRedirectionUrl() {
        Map<String, String> params = Maps.newLinkedHashMap();
        params.put("resource", transitionResource.getId().toString());
        params.put("action", transitionAction.getId().toString());
        return "redirect:/execute?" + Joiner.on("&").withKeyValueSeparator("=").join(params);
    }

}
