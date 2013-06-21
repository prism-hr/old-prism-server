package com.zuehlke.pgadmissions.components;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;

@Component
public class ActionsProvider {

    public ActionsDefinitions calculateActions(final RegisteredUser user, final ApplicationForm application) {
        ApplicationFormStatus nextStatus = application.getNextStatus();

        ActionsDefinitions actions = new ActionsDefinitions();

        for (ApplicationFormAction action : ApplicationFormAction.values()) {
            action.applyAction(actions, user, application, nextStatus);
        }

        return actions;
    }

    public void validateAction(final ApplicationForm applicationForm, final RegisteredUser user, final ApplicationFormAction action) {
        ActionsDefinitions actions = new ActionsDefinitions();
        action.applyAction(actions, user, applicationForm, applicationForm.getNextStatus());

        if (actions.getActions().isEmpty()) {
            throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
        }
    }

}
