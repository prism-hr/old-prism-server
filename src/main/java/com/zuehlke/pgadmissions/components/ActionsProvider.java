package com.zuehlke.pgadmissions.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
import com.zuehlke.pgadmissions.services.StateTransitionViewResolver;

@Component
public class ActionsProvider {

    private StateTransitionViewResolver stateTransitionViewResolver;


    public ActionsProvider() {
    }

    @Autowired
    public ActionsProvider(StateTransitionViewResolver stateTransitionViewResolver) {
        this.stateTransitionViewResolver = stateTransitionViewResolver;
    }

    public ActionsDefinitions calculateActions(final RegisteredUser user, final ApplicationForm application) {
        ApplicationFormStatus nextStatus = stateTransitionViewResolver.getNextStatus(application);

        ActionsDefinitions actions = new ActionsDefinitions();
        
        for(ApplicationFormAction action : ApplicationFormAction.values()){
            action.applyAction(actions, user, application, nextStatus);
        }

        return actions;
    }
}
