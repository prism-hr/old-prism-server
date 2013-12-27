package com.zuehlke.pgadmissions.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ConfigurationSection;
import com.zuehlke.pgadmissions.domain.enums.ContentSection;
import com.zuehlke.pgadmissions.domain.enums.ProspectusSection;
import com.zuehlke.pgadmissions.domain.enums.UserManagementSection;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;

@Component
public class ContentAccessProvider extends ActionsProvider {
	
	@Autowired
	private ApplicationFormDAO applicationFormDAO;
	private ProgramDAO programDAO;
    
    public void validateCanEditAsApplicant(ApplicationForm application, RegisteredUser user) {
    	if (application == null || !(user.isApplicant(application) && super.checkActionAvailable(application, user, ApplicationFormAction.VIEW_EDIT))) {
    		throw new ResourceNotFoundException();
    	} else if (application.isDecided()) {
    		throw new CannotUpdateApplicationException(application.getApplicationNumber());
    	}
    }
    
    public void validateCanView(ApplicationForm application, RegisteredUser user) {
    	if (application == null || !super.checkActionAvailable(application, user, ApplicationFormAction.VIEW)) {
    		throw new ResourceNotFoundException();
    	}
    }

    public void validateCanConfigureSystem(RegisteredUser user) {
    	if (!checkCanConfigureSystem(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public void validateCanConfigureServiceLevels(RegisteredUser user) {
    	if (!checkCanConfigureServiceLevels(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public void validateCanConfigureInterfaces(RegisteredUser user) {
    	if (!checkCanConfigureInterfaces(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public void validateCanConfigureNotifications(RegisteredUser user) {
    	if (!checkCanConfigureNotifications(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public void validateCanConfigureForms(RegisteredUser user) {
    	if (!checkCanConfigureForms(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanConfigureSystem(RegisteredUser user) {
    	return checkCanConfigureServiceLevels(user) || checkCanConfigureInterfaces(user) || checkCanConfigureNotifications(user) || checkCanConfigureForms(user);
    }
    
    public boolean checkCanConfigureServiceLevels(RegisteredUser user) {
    	return user.isInRole(Authority.SUPERADMINISTRATOR);
    }
    
    public boolean checkCanConfigureInterfaces(RegisteredUser user) {
    	return user.isInRole(Authority.SUPERADMINISTRATOR);
    }
    
    public boolean checkCanConfigureNotifications(RegisteredUser user) {
    	return user.isInRole(Authority.SUPERADMINISTRATOR);
    }
    
    public boolean checkCanConfigureForms(RegisteredUser user) {
    	return hasAdminRightsForActivePrograms(user);
    }
    
    public void validateCanManageUsers(RegisteredUser user) {
    	if (!checkCanManageUsers(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public void validateCanManageSuperAdministrators(RegisteredUser user) {
    	if (!checkCanManageSuperAdministrators(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public void validateCanManageProgramUsers(RegisteredUser user) {
    	if (!checkCanManageProgramUsers(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public void validateCanManageAdmitters(RegisteredUser user) {
    	if (!checkCanManageAdmitters(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanManageUsers(RegisteredUser user) {
    	return checkCanManageSuperAdministrators(user) || checkCanManageProgramUsers(user) || checkCanManageAdmitters(user);
    }
    
    public boolean checkCanManageSuperAdministrators(RegisteredUser user) {
    	return user.isInRole(Authority.SUPERADMINISTRATOR);
    }
    
    public boolean checkCanManageProgramUsers(RegisteredUser user) {
    	return hasAdminRightsForActivePrograms(user);
    }
    
    public boolean checkCanManageAdmitters(RegisteredUser user) {
    	return user.isInRole(Authority.SUPERADMINISTRATOR) || user.isInRole(Authority.ADMITTER);
    }
    
    public void validateCanManagePropsectus(RegisteredUser user) {
    	if (!checkCanManageProspectus(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public void validateCanManageProgramAdverts(RegisteredUser user) {
    	if (!checkCanManageProgramAdverts(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public void validateCanManageProjectAdverts(RegisteredUser user) {
    	if (!checkCanManageProjectAdverts(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public void validateCanManageAdvertFeeds(RegisteredUser user) {
    	if (!checkCanManageAdvertFeeds(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public void validateCanManageExternalIdentity(RegisteredUser user) {
    	if (!checkCanManageExternalIdentity(user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanManageProspectus(RegisteredUser user) {
    	return checkCanManageProgramAdverts(user) || checkCanManageProjectAdverts(user) || checkCanManageAdvertFeeds(user) || checkCanManageExternalIdentity(user);
    }
    
    public boolean checkCanManageProgramAdverts(RegisteredUser user) {
    	return hasAdminRightsForActivePrograms(user);
    }
    
    public boolean checkCanManageProjectAdverts(RegisteredUser user) {
    	return hasAcademicRightsForActiveProgams(user);
    }
    
    public boolean checkCanManageAdvertFeeds(RegisteredUser user) {
    	return hasAcademicRightsForActiveProgams(user);
    }
    
    public boolean checkCanManageExternalIdentity(RegisteredUser user) {
    	return hasAcademicRightsForActiveProgams(user);
    }
    
    public HashMap<ContentSection, Boolean> getContentPermissions(RegisteredUser user) {
    	HashMap<ContentSection, Boolean> permissions = new HashMap<ContentSection, Boolean>();
    	permissions.put(ContentSection.ACCOUNT, true);
    	permissions.put(ContentSection.APPLICATIONS, true);
    	permissions.put(ContentSection.USERS, checkCanManageUsers(user));
    	permissions.put(ContentSection.CONFIGURATION, checkCanConfigureSystem(user));
    	permissions.put(ContentSection.PROSPECTUS, checkCanManageProspectus(user));
    	return permissions;
    }
    
    public HashMap<ConfigurationSection, Boolean> getConfigurationPermissions(RegisteredUser user) {
    	HashMap<ConfigurationSection, Boolean> permissions = new HashMap<ConfigurationSection, Boolean>();
    	permissions.put(ConfigurationSection.SERVICELEVELS, checkCanConfigureServiceLevels(user));
    	permissions.put(ConfigurationSection.INTERFACES, checkCanConfigureInterfaces(user));
    	permissions.put(ConfigurationSection.NOTIFICATIONS, checkCanConfigureNotifications(user));
    	permissions.put(ConfigurationSection.FORMS, checkCanConfigureForms(user));
    	return permissions;
    }
    
    public HashMap<UserManagementSection, Boolean> getUserManagementPermissions(RegisteredUser user) {
    	HashMap<UserManagementSection, Boolean> permissions = new HashMap<UserManagementSection, Boolean>();
    	permissions.put(UserManagementSection.SUPERADMINISTRATOR, checkCanManageSuperAdministrators(user));
    	permissions.put(UserManagementSection.PROGRAMUSER, checkCanManageProgramUsers(user));
    	permissions.put(UserManagementSection.ADMITTER, checkCanManageAdmitters(user));
    	return permissions;
    }
    
    public HashMap<ProspectusSection, Boolean> getProspectusPermissions(RegisteredUser user) {
    	HashMap<ProspectusSection, Boolean> permissions = new HashMap<ProspectusSection, Boolean>();
    	permissions.put(ProspectusSection.PROGRAMADVERT, checkCanManageProgramAdverts(user));
    	permissions.put(ProspectusSection.PROJECTADVERT, checkCanManageProjectAdverts(user));
    	permissions.put(ProspectusSection.ADVERTFEED, checkCanManageAdvertFeeds(user));
    	permissions.put(ProspectusSection.EXTERNALIDENTITY, checkCanManageExternalIdentity(user));
    	return permissions;
    }
    
    public void validateCanDownloadApplication(ApplicationForm applicationForm, RegisteredUser user) {
    	if (!checkCanDownloadApplication(applicationForm, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanDownloadApplication(ApplicationForm applicationForm, RegisteredUser user) {
    	return super.checkActionAvailable(applicationForm, user, ApplicationFormAction.VIEW);
    }
    
    public void validateCanSeeReport(ApplicationForm applicationForm, RegisteredUser user) {
    	if (!checkCanSeeReport(applicationForm, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanSeeReport(ApplicationForm applicationForm, RegisteredUser user) {
    	return canSeeAssessments(applicationForm, user);
    }
    
    public void validateCanSeeReference(ReferenceComment comment, RegisteredUser user) {
    	if (!checkCanSeeReference(comment, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanSeeReference(ReferenceComment comment, RegisteredUser user) {
    	return canSeeAssessments(comment.getApplication(), user) || comment.getUser() == user;
    }
    
    public void validateCanSeeEqualOpportunitiesInformation(ApplicationForm applicationForm, RegisteredUser user) {
    	if (!checkCanSeeEqualOpportunitiesInformation(applicationForm, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanSeeEqualOpportunitiesInformation(ApplicationForm applicationForm, RegisteredUser user) {
    	return user.isApplicant(applicationForm);
    }
    
    public void validateCanSeeCriminalConvictionsInformation(ApplicationForm applicationForm, RegisteredUser user) {
    	if (!checkCanSeeCriminalConvictionsInformation(applicationForm, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanSeeCriminalConvictionsInformation(ApplicationForm applicationForm, RegisteredUser user) {
    	if (user.isApplicant(applicationForm)) {
    		return true;
    	} else {
	    	List<Authority> rolesThatCanSee = new ArrayList<Authority>();
	    	rolesThatCanSee.add(Authority.SUPERADMINISTRATOR);
	    	rolesThatCanSee.add(Authority.ADMITTER);
	    	rolesThatCanSee.add(Authority.ADMINISTRATOR);
	    	rolesThatCanSee.add(Authority.APPROVER);
	    	rolesThatCanSee.add(Authority.PROJECTADMINISTRATOR);
	    	for (Authority role : super.applicationFormUserRoleDAO.getUserRolesForApplication(applicationForm, user)) {
	    		if (rolesThatCanSee.contains(role)) {
	    			return true;
	    		}
	    	}
	    	return false;
    	}
    }
    
    private boolean hasAdminRightsForActivePrograms(RegisteredUser user) {
    	List<Program> programs = new ArrayList<Program>();
    	if (user.isInRole(Authority.SUPERADMINISTRATOR)) {
    		programs = programDAO.getAllPrograms();
    	} else if (user.isInRole(Authority.ADMINISTRATOR)) {
    		programs = user.getProgramsOfWhichAdministrator();
    	} else {
    		return false;
    	}
    	return containsActiveProgram(programs);
    }
    
    private boolean hasAcademicRightsForActiveProgams(RegisteredUser user) {
    	if (hasAdminRightsForActivePrograms(user)) {
    		return true;
    	} 
    	return containsActiveProgram(programDAO.getProgramsOfWhichPotentialSupervisor(user));
    }
    
    private boolean containsActiveProgram(List<Program> programs) {
    	for (Program program : programs) {
    		if (program.isEnabled() || !applicationFormDAO.getActiveApplicationsByProgram(program).isEmpty()) {
    			return true;
    		}
    	}
		return false;
    }
    
    private boolean canSeeAssessments(ApplicationForm applicationForm, RegisteredUser user) {
    	if (!super.checkActionAvailable(applicationForm, user, ApplicationFormAction.VIEW)) {
    		return false;
    	} else if (user.isApplicant(applicationForm)) {
    		return false;
    	}
    	List<Authority> roles = super.applicationFormUserRoleDAO.getUserRolesForApplication(applicationForm, user);
    	return !(roles.size() == 1 && roles.get(0) == Authority.REFEREE);
    }
    
}