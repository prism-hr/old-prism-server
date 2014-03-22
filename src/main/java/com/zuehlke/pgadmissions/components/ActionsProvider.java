package com.zuehlke.pgadmissions.components;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.dto.ActionDefinition;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;

@Component
public class ActionsProvider {

    @Autowired
    private ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    @Transactional
    public ApplicationDescriptor getApplicationDescriptorForUser(final ApplicationForm application, final RegisteredUser user) {
        ApplicationDescriptor applicationDescriptor = new ApplicationDescriptor();
        applicationDescriptor.getActionDefinitions().addAll(applicationFormUserRoleDAO.selectUserActions(user.getId(), application.getId()));
        applicationDescriptor.setNeedsToSeeUrgentFlag(applicationFormUserRoleDAO.getRaisesUrgentFlagByUserAndApplicationForm(user, application));
        applicationDescriptor.setNeedsToSeeUpdateFlag(applicationFormUserRoleDAO.getRaisesUpdateFlagByUserAndApplicationForm(user, application));
        return applicationDescriptor;
    }

    public void validateAction(final ApplicationForm application, final RegisteredUser user, final ApplicationFormAction action) {
        if (!checkActionAvailable(application, user, action)) {
            throw new ActionNoLongerRequiredException(application.getApplicationNumber());
        }
    }

    public boolean checkActionAvailable(final ApplicationForm application, final RegisteredUser user, final ApplicationFormAction action) {
        return !applicationFormUserRoleDAO.selectUserActionById(application.getId(), user.getId(), action).isEmpty();
    }

    public ApplicationFormAction getPrecedentAction(final ApplicationForm application, final RegisteredUser user, final ActionType actionType) {
        List<ActionDefinition> precedentAction = applicationFormUserRoleDAO.selectUserActionByActionType(application.getId(), user.getId(), actionType);
        if (precedentAction.isEmpty()) {
            throw new ActionNoLongerRequiredException(application.getApplicationNumber());
        }
        return precedentAction.get(0).getAction();
    }

}
