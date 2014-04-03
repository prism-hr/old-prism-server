package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
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
            Boolean interestedInApplicant, ApplicationFormActionRequired... actions) {
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRole(applicationForm, user, roleDAO.getById(authority), interestedInApplicant,
                Sets.newHashSet(actions));
        ApplicationFormUserRole mergedApplicationFormUserRole = roleDAO.saveApplicationFormUserRole(applicationFormUserRole);
        boolean raisesUrgentFlag = false;
        for (ApplicationFormActionRequired action : mergedApplicationFormUserRole.getActions()) {
            if (action.getRaisesUrgentFlag()) {
                raisesUrgentFlag = true;
            }
        }
        applicationFormUserRole.setRaisesUrgentFlag(raisesUrgentFlag);
        return mergedApplicationFormUserRole;
    }

    public boolean hasAnyRole(RegisteredUser user, Authority... authorities) {
        for (Authority authority : authorities) {
            if (hasRole(user, authority, null)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRole(RegisteredUser user, Authority authority) {
        return hasRole(user, authority, null);
    }

    public boolean hasRole(RegisteredUser user, Authority authority, Object discriminator) {
        Role role = roleDAO.getById(authority);
        switch (role.getAuthorityScope()) {
        case SYSTEM:
            return roleDAO.getSystemUserRoles(user).contains(roleDAO.getById(authority));
        case INSTITUTION:
            return roleDAO.getInstitutionUserRoles((Institution) discriminator, user).contains(roleDAO.getById(authority));
        case PROGRAM:
            return roleDAO.getProgramUserRoles((Program) discriminator, user).contains(roleDAO.getById(authority));
        case PROJECT:
            return roleDAO.getProjectUserRoles((Project) discriminator, user).contains(roleDAO.getById(authority));
        case APPLICATION:
            return roleDAO.getApplicationFormUserRoles((ApplicationForm) discriminator, user).contains(roleDAO.getById(authority));
        }
        return roleDAO.getUserRoles(user).contains(roleDAO.getById(authority));
    }

    public List<Program> getProgramsByUserAndRole(RegisteredUser currentUser, Authority administrator) {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeRole(RegisteredUser user, Authority admitter) {
        // TODO Auto-generated method stub
        
    }

    public List<RegisteredUser> getProgramAdministrators(Program program) {
        // TODO Auto-generated method stub
        return null;
    }
}
