package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.dto.ActionDefinition;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;

@Service
@Transactional
public class ActionService {

    @Autowired
    private ActionDAO actionDAO;
    
    public Action getById(ApplicationFormAction actionId) {
        return actionDAO.getById(actionId);
    }
    
    public List<ApplicationFormAction> getActionIdByActionType(ActionType actionType) {
        return actionDAO.getActionIdByActionType(actionType);
    }
    
    public void validateAction(final ApplicationForm application, final RegisteredUser user, final ApplicationFormAction action) {
        if (!checkActionAvailable(application, user, action)) {
            throw new ActionNoLongerRequiredException(application.getApplicationNumber());
        }
    }

    public boolean checkActionAvailable(final ApplicationForm application, final RegisteredUser user, final ApplicationFormAction action) {
        return !actionDAO.selectUserActionById(application.getId(), user.getId(), action).isEmpty();
    }

    public ApplicationFormAction getPrecedentAction(final ApplicationForm application, final RegisteredUser user, final ActionType actionType) {
        List<ActionDefinition> precedentAction = actionDAO.selectUserActionByActionType(application.getId(), user.getId(), actionType);
        if (precedentAction.isEmpty()) {
            throw new ActionNoLongerRequiredException(application.getApplicationNumber());
        }
        return precedentAction.get(0).getAction();
    }
    
}
