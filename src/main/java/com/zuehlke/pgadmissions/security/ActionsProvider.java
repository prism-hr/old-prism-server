package com.zuehlke.pgadmissions.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;

@Component
public class ActionsProvider {

    @Autowired
    protected ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    @Transactional
    public ApplicationDescriptor getApplicationDescriptorForUser(final ApplicationForm application, final RegisteredUser user) {
        ApplicationDescriptor applicationDescriptor = new ApplicationDescriptor();
        applicationDescriptor.getActionDefinitions().addAll(applicationFormUserRoleDAO.findActionsByUserAndApplicationForm(user, application));
        applicationDescriptor.setNeedsToSeeUrgentFlag(applicationFormUserRoleDAO.findRaisesUrgentFlagByUserAndApplicationForm(user, application));
        applicationDescriptor.setNeedsToSeeUpdateFlag(applicationFormUserRoleDAO.findRaisesUpdateFlagByUserAndApplicationForm(user, application));
        return applicationDescriptor;
    }
    
    public void validateAction(final ApplicationForm applicationForm, final RegisteredUser user, final ApplicationFormAction action) {
        if (!checkActionAvailable(applicationForm, user, action)) {
            throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
        } else if (applicationForm == null) {
        	throw new ResourceNotFoundException();
        }
    }
    
    public Boolean checkActionAvailable(final ApplicationForm applicationForm, final RegisteredUser user, final ApplicationFormAction action) {
    	return applicationFormUserRoleDAO.checkActionAvailableForUserAndApplicationForm(user, applicationForm, action);
    }

}