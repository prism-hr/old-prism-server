package com.zuehlke.pgadmissions.services;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormActionRequired;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramUserRole;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.ProjectUserRole;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.SystemUserRole;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service
@Transactional
public class RoleService {
 
    @Autowired
    private RoleDAO roleDAO;
    
    public Role getById(Authority authority) {
        return roleDAO.getById(authority);
    }
    
    public void createSystemUserRoles(RegisteredUser user, Authority... authorities) {
        for (Authority authority : authorities) {
            SystemUserRole systemUserRole = new SystemUserRole(user, roleDAO.getById(authority));
            roleDAO.saveSystemUserRole(systemUserRole);
        }
    }
    
    public void createInstitutionUserRoles(Institution institution, RegisteredUser user, Authority... authorities) {
        for (Authority authority : authorities) {
            InstitutionUserRole institutionUserRole = new InstitutionUserRole(institution, user, roleDAO.getById(authority));
            roleDAO.saveInstitutionUserRole(institutionUserRole);
        }
    }

    public void createProgramUserRoles(Program program, RegisteredUser user, Authority... authorities) {
        for (Authority authority : authorities) {
            ProgramUserRole programUserRole = new ProgramUserRole(program, user, roleDAO.getById(authority));
            roleDAO.saveProgramUserRole(programUserRole);
        }
    }

    public void createProjectUserRoles(Project project, RegisteredUser user, Authority... authorities) {
        for (Authority authority : authorities) {
            ProjectUserRole projectUserRole = new ProjectUserRole(project, user, roleDAO.getById(authority));
            roleDAO.saveProjectUserRole(projectUserRole);
        }
    }

    public ApplicationFormUserRole createApplicationFormUserRole(ApplicationForm applicationForm, RegisteredUser user, Authority authority,
            Boolean interestedInApplicant, HashSet<ApplicationFormActionRequired> actions) {
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRole(applicationForm, user, roleDAO.getById(authority), interestedInApplicant,
                actions);
        roleDAO.saveApplicationFormUserRole(applicationFormUserRole);
        boolean raisesUrgentFlag = false;
        for (ApplicationFormActionRequired action : applicationFormUserRole.getActions()) {
            if (action.getRaisesUrgentFlag()) {
                raisesUrgentFlag = true;
            }
        }
        applicationFormUserRole.setRaisesUrgentFlag(raisesUrgentFlag);
        return applicationFormUserRole;
    }
    
    public boolean checkUserHasRole(RegisteredUser user, Authority authority) {
        return roleDAO.getUserRoles(user).contains(roleDAO.getById(authority));
    }
    
    public boolean checkUserHasSystemRole(RegisteredUser user, Authority authority) {
        return roleDAO.getSystemUserRoles(user).contains(roleDAO.getById(authority));
    }
    
    public boolean checkUserHasInstitutionRole(Institution institution, RegisteredUser user, Authority authority) {
        return roleDAO.getInstitutionUserRoles(institution, user).contains(roleDAO.getById(authority));
    }
    
    public boolean checkUserHasProgramRole(Program program, RegisteredUser user, Authority authority) {
        return roleDAO.getProgramUserRoles(program, user).contains(roleDAO.getById(authority));
    }
    
    public boolean checkUserHasProjectRole(Project project, RegisteredUser user, Authority authority) {
        return roleDAO.getProjectUserRoles(project, user).contains(roleDAO.getById(authority));
    }
    
    public boolean checkUserHasApplicationRole(ApplicationForm application, RegisteredUser user, Authority authority) {
        return roleDAO.getApplicationFormUserRoles(application, user).contains(roleDAO.getById(authority));
    }
    
}
