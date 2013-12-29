package com.zuehlke.pgadmissions.security;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityGroup;
import com.zuehlke.pgadmissions.domain.enums.ConfigurationSection;
import com.zuehlke.pgadmissions.domain.enums.ContentSection;
import com.zuehlke.pgadmissions.domain.enums.ProspectusSection;
import com.zuehlke.pgadmissions.domain.enums.UserManagementSection;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;

/**
 * Organises all of the logic that the system needs to make decisions about granting access to content and functions.
 * Any new content access logic should be added here so that it can be maintained easily moving forwards.
 * @author Alastair Knowles
 */

@Component
public class ContentAccessProvider extends ActionsProvider {

	private final ApplicationFormDAO applicationFormDAO;
	private final ProgramDAO programDAO;
	private final RoleDAO roleDAO;
	
    public ContentAccessProvider() {
        this(null, null, null);
    }
    
	@Autowired
    public ContentAccessProvider(ApplicationFormDAO applicationFormDAO, ProgramDAO programDAO, RoleDAO roleDAO) {
        this.applicationFormDAO = applicationFormDAO;
        this.programDAO = programDAO;
        this.roleDAO = roleDAO;
    }
    
    public void validateCanEditAsApplicant(ApplicationForm application, RegisteredUser user) {
    	if (!checkCanEditAsApplicant(application, user)) {
    		throw new CannotUpdateApplicationException(application.getApplicationNumber());
    	}
    }
    
    public boolean checkCanEditAsApplicant(ApplicationForm application, RegisteredUser user) {
    	return application != null && user.isApplicant(application) && super.checkActionAvailable(application, user, ApplicationFormAction.VIEW_EDIT);
    }
    
    public void validateCanViewApplication(ApplicationForm application, RegisteredUser user) {
    	if (!checkCanViewApplication(application, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanViewApplication(ApplicationForm application, RegisteredUser user) {
    	return application != null && super.checkActionAvailable(application, user, ApplicationFormAction.VIEW);
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
    	return hasRolesForSystem(user, AuthorityGroup.SYSTEMADMIN.authorities());
    }
    
    public boolean checkCanConfigureInterfaces(RegisteredUser user) {
    	return hasRolesForSystem(user, AuthorityGroup.SYSTEMADMIN.authorities());
    }
    
    public boolean checkCanConfigureNotifications(RegisteredUser user) {
    	return hasRolesForSystem(user, AuthorityGroup.SYSTEMADMIN.authorities());
    }
    
    public boolean checkCanConfigureForms(RegisteredUser user) {
    	return hasAdminRightsForPrograms(user);
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
    	return hasRolesForSystem(user, AuthorityGroup.SYSTEMADMIN.authorities());
    }
    
    public boolean checkCanManageProgramUsers(RegisteredUser user) {
    	return hasAdminRightsForPrograms(user);
    }
    
    public boolean checkCanManageAdmitters(RegisteredUser user) {
    	return hasRolesForSystem(user, AuthorityGroup.ADMITTERADMIN.authorities());
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
    	return hasProgramAuthorRightsForPrograms(user);
    }
    
    public boolean checkCanManageProjectAdverts(RegisteredUser user) {
    	return hasProjectAuthorRightsForProgams(user);
    }
    
    public boolean checkCanManageAdvertFeeds(RegisteredUser user) {
    	return hasProjectAuthorRightsForProgams(user);
    }
    
    public boolean checkCanManageExternalIdentity(RegisteredUser user) {
    	return hasProjectAuthorRightsForProgams(user);
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
    
    public void validateCanManageProgramAdvert(Program program, RegisteredUser user) {
    	if (!checkCanManageProgramAdvert(program, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanManageProgramAdvert(Program program, RegisteredUser user) {
    	return program.isEnabled() && hasRolesForProgram(program, user, AuthorityGroup.PROGRAMAUTHOR.authorities());
    }
    
    public void validateCanManageProgramProjectAdverts(Program program, RegisteredUser user) {
    	if (!checkCanManageProgramProjectAdverts(program, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanManageProgramProjectAdverts(Program program, RegisteredUser user) {
    	return program.isEnabled() && hasRolesForProgram(program, user, AuthorityGroup.PROJECTAUTHOR.authorities());
    }
    
    public void validateCanManageProjectAdvert(Project project, RegisteredUser user) {
    	if (!checkCanManageProjectAdvert(project, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanManageProjectAdvert(Project project, RegisteredUser user) {
    	return !project.isDisabled() && hasRolesForProject(project, user, AuthorityGroup.PROJECTEDITOR.authorities());
    }
    
    public void validateCanDownloadApplication(ApplicationForm application, RegisteredUser user) {
    	if (!checkCanDownloadApplication(application, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanDownloadApplication(ApplicationForm application, RegisteredUser user) {
    	return application != null && super.checkActionAvailable(application, user, ApplicationFormAction.VIEW);
    }
    
    public void validateCanSeeExtendedReport(ApplicationForm application, RegisteredUser user) {
    	if (!checkCanSeeExtendedReport(application, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanSeeExtendedReport(ApplicationForm application, RegisteredUser user) {
    	return application != null && hasRolesForApplication(application, user, AuthorityGroup.RATINGSVIEWER.authorities());
    }
    
    public void validateCanSeeReferences(ApplicationForm application, RegisteredUser user) {
    	if (!checkCanSeeReferences(application, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanSeeReferences(ApplicationForm application, RegisteredUser user) {
    	return application != null && hasRolesForApplication(application, user, AuthorityGroup.REFERENCESVIEWER.authorities());
    }
    
    public void validateCanSeeEqualOpportunitiesInformation(ApplicationForm application, RegisteredUser user) {
    	if (!checkCanSeeEqualOpportunitiesInformation(application, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanSeeEqualOpportunitiesInformation(ApplicationForm application, RegisteredUser user) {
    	return application != null && hasRolesForApplication(application, user, AuthorityGroup.EQUALOPPSVIEWER.authorities());
    }
    
    public void validateCanSeeCriminalConvictionsInformation(ApplicationForm application, RegisteredUser user) {
    	if (!checkCanSeeCriminalConvictionsInformation(application, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanSeeCriminalConvictionsInformation(ApplicationForm application, RegisteredUser user) {
    	return application != null && hasRolesForApplication(application, user, AuthorityGroup.CONVICTIONSVIEWER.authorities());
    }
    
    public void validateCanDeleteDocument(Document document, RegisteredUser user) {
    	if (!checkCanDeleteDocument(document, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanDeleteDocument(Document document, RegisteredUser user) {
    	return document != null && document.getUploadedBy() == user;
    }
    
    public void validateCanDeleteApplicationDocument(ApplicationForm application, RegisteredUser user) {
    	if (!checkCanEditAsApplicant(application, user)) {
    		throw new CannotUpdateApplicationException(application.getApplicationNumber());
    	}
    }
    
    public boolean checkCanDeleteApplicationDocument(ApplicationForm application, RegisteredUser user) {
    	return checkCanEditAsApplicant(application, user);
    }
    
    public void validateCanSeeDocument(Document document, RegisteredUser user) {
    	if (!checkCanSeeDocument(document, user)) {
    		throw new ResourceNotFoundException();
    	}
    }
    
    public boolean checkCanSeeDocument(Document document, RegisteredUser user) {
    	if (document != null) {
	    	ApplicationForm application = applicationFormDAO.getApplicationByDocument(document);
	    	if (application != null) {
	    		return super.checkActionAvailable(application, user, ApplicationFormAction.VIEW);
	    	}
    	}
	    return false;	
    }
    
    private boolean hasAdminRightsForPrograms(RegisteredUser user) {
    	return !programDAO.getProgramsOfWhichAdministrator(user).isEmpty();
    }
    
    private boolean hasProgramAuthorRightsForPrograms(RegisteredUser user) {
    	return !programDAO.getProgramsOfWhichAuthor(user).isEmpty();
    }
    
    private boolean hasProjectAuthorRightsForProgams(RegisteredUser user) {
    	return !programDAO.getProgramsOfWhichProjectAuthor(user).isEmpty();
    }
    

    
    private boolean hasRolesForSystem(RegisteredUser user, Authority... authorities) {
    	for (Authority authority : authorities) {
    		if (roleDAO.getUserRolesForSystem(user).contains(authority)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean hasRolesForProgram(Program program, RegisteredUser user, Authority... authorities) {
    	for (Authority authority : authorities) {
    		if (roleDAO.getUserRolesForProgram(program, user).contains(authority)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean hasRolesForProject(Project project, RegisteredUser user, Authority... authorities) {
    	for (Authority authority : authorities) {
    		if (roleDAO.getUserRolesForProject(project, user).contains(authority)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean hasRolesForApplication(ApplicationForm application, RegisteredUser user, Authority... authorities) {
    	for (Authority authority : authorities) {
    		if (roleDAO.getUserRolesForApplication(application, user).contains(authority)) {
    			return true;
    		}
    	}
    	return false;
    }
    
}