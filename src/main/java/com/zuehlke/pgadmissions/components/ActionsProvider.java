package com.zuehlke.pgadmissions.components;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ActionDefinition;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;

@Component
public class ActionsProvider {

    @Autowired
    private ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    @Transactional
    public ApplicationDescriptor getApplicationDescriptorForUser(final ApplicationForm application, final RegisteredUser user) {
        List<ActionDefinition> requiredActions = applicationFormUserRoleDAO.findRequiredActionsByUserAndApplicationForm(user, application);
        List<ActionDefinition> optionalActions = applicationFormUserRoleDAO.findOptionalActionsByUserAndApplicationForm(user, application);
        Boolean raisesUpdateFlag = applicationFormUserRoleDAO.findRaisesUpdateFlagByUserAndApplicationForm(user, application);
        Boolean raisesUrgentFlag = applicationFormUserRoleDAO.findRaisesUrgentFlagByUserAndApplicationForm(user, application);

        ApplicationDescriptor applicationDescriptor = new ApplicationDescriptor();
        applicationDescriptor.getActionDefinitions().addAll(requiredActions);
        applicationDescriptor.getActionDefinitions().addAll(optionalActions);
        applicationDescriptor.setNeedsToSeeUpdateFlag(raisesUpdateFlag);
        applicationDescriptor.setNeedsToSeeUrgentFlag(raisesUrgentFlag);
        return applicationDescriptor;
    }
    
    public void validateAction(final ApplicationForm applicationForm, final RegisteredUser user, final ApplicationFormAction action) {
        boolean actionAvailable = applicationFormUserRoleDAO.checkActionAvailableForUserAndApplicationForm(user, applicationForm, action);
        
        if (!actionAvailable) {
            throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
        }
    }

}
