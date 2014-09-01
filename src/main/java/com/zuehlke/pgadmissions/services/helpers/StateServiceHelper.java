package com.zuehlke.pgadmissions.services.helpers;

import java.util.HashMap;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class StateServiceHelper {
    
    @Autowired
    private ActionService actionService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private StateService stateService;

    @Autowired
    private SystemService systemService;

    public void executePendingStateTransitions() throws Exception {
        HashMap<Resource, Action> transitions = Maps.newHashMap();
        transitions.putAll(resourceService.getResourcePropagations());
        transitions.putAll(resourceService.getResourceEscalations());

        for (Resource resource : transitions.keySet()) {
            Action action = transitions.get(resource);
            Comment comment = new Comment().withResource(resource).withUser(systemService.getSystem().getUser()).withAction(action).withDeclinedResponse(false)
                    .withCreatedTimestamp(new DateTime());
            stateService.executeStateTransition(resource, action, comment);
        }
    }

}
