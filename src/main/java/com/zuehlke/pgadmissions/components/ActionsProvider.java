package com.zuehlke.pgadmissions.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;

@Component
public class ActionsProvider {

    @Autowired
    private ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

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
        }
    }
    
    public Boolean checkActionAvailable(final ApplicationForm applicationForm, final RegisteredUser user, final ApplicationFormAction action) {
    	return applicationFormUserRoleDAO.checkActionAvailableForUserAndApplicationForm(user, applicationForm, action);
    }
    
    public Boolean checkUserCanSeeApplication(final ApplicationForm applicationForm, final RegisteredUser user) {
    	return checkActionAvailable(applicationForm, user, ApplicationFormAction.VIEW);
    }
    
    public Boolean checkUserCanSeeReference(final ApplicationForm applicationForm, final RegisteredUser user, final ReferenceComment reference) {
    	return checkUserCanSeeApplication(applicationForm, user) && 
    			!user.isApplicant(applicationForm) && 
    			(!user.isRefereeOfApplicationForm(applicationForm) ||
    			user == reference.getUser());
    }
    
    public Boolean checkUserCanSeeEqualOpportunitiesInformation(ApplicationForm applicationForm, RegisteredUser user) {
    	return user.isApplicant(applicationForm) && checkActionAvailable(applicationForm, user, ApplicationFormAction.VIEW);
    }
    
    public Boolean checkUserCanSeeCriminalConvictionInformation(ApplicationForm applicationForm, RegisteredUser user) {
    	Program program = applicationForm.getProgram();
    	Project project = applicationForm.getProject();
    	
    	return checkActionAvailable(applicationForm, user, ApplicationFormAction.VIEW) &&
    			(user.isApplicant(applicationForm) ||
    			user.isInRole(Authority.SUPERADMINISTRATOR) ||
    			user.isInRole(Authority.ADMITTER) ||
    			user.isAdminInProgramme(program) ||
    			user.isApproverInProgram(program) ||
    			user.isAdminInProject(project));
    }
    
    public Boolean checkUserCanEditApplicationAsApplicant(final ApplicationForm applicationForm, final RegisteredUser user) {
    	return user.isApplicant(applicationForm) && 
    			checkActionAvailable(applicationForm, user, ApplicationFormAction.VIEW_EDIT);
    }
    
    public Boolean checkUserCanEditApplicationAsAdministrator(final ApplicationForm applicationForm, final RegisteredUser user) {
    	Program program = applicationForm.getProgram();
    	Project project = applicationForm.getProject();
    	
    	return checkActionAvailable(applicationForm, user, ApplicationFormAction.VIEW_EDIT) &&
    			(user.isInRole(Authority.SUPERADMINISTRATOR) ||
    			user.isAdminInProgramme(program) ||
    			user.isAdminInProject(project));
    }

}