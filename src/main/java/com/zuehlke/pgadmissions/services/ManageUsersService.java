package com.zuehlke.pgadmissions.services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class ManageUsersService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MailSendingService mailService;

    public RegisteredUser setUserRoles(String firstname, String lastname, String email, boolean createIfNotExist, boolean replaceRoles, PrismScope scope,
            Authority... authorities) {
        RegisteredUser user = userService.getUser(firstname, lastname, email, createIfNotExist);
        if (replaceRoles) {
            roleService.removeRoles(user, scope);
        }
        roleService.createSystemUserRoles(user, authorities);
        return user;
    }

    public void addRoleToUser(RegisteredUser user, Authority authority) {
        // if (!user.getRoles().contains(authority)) {
        // user.getRoles().add(roleDAO.getById(authority));
        // if (Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMITTER).contains(authority)) {
        // applicationFormUserRoleService.insertUserRole(user, authority);
        // }
        // userDAO.save(user);
        // }
    }

    public void updateUserWithNewRoles(final RegisteredUser selectedUser, final Program selectedProgram, final Authority... newAuthorities) {
        // Please note: it is a deliberate decision to never remove people from SUPERADMIN role.

        for (Authority authority : Authority.values()) {
            addToRoleIfRequired(selectedUser, newAuthorities, authority);
        }

        addOrRemoveFromProgramsOfWhichAdministratorIfRequired(selectedUser, selectedProgram, newAuthorities);
        addOrRemoveFromProgramsOfWhichApproverIfRequired(selectedUser, selectedProgram, newAuthorities);
        addOrRemoveFromProgramsOfWhichViewerIfRequired(selectedUser, selectedProgram, newAuthorities);

    }

    public void deleteUserFromProgramme(final RegisteredUser selectedUser, final Program selectedProgram) {
        // selectedUser.getProgramsOfWhichAdministrator().remove(selectedProgram);
        // applicationFormUserRoleService.deleteProgramRole(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
        // if (selectedUser.getProgramsOfWhichAdministrator().isEmpty()) {
        // selectedUser.removeRole(Authority.ADMINISTRATOR);
        // }
        // selectedUser.getProgramsOfWhichApprover().remove(selectedProgram);
        // applicationFormUserRoleService.deleteProgramRole(selectedUser, selectedProgram, Authority.APPROVER);
        // if (selectedUser.getProgramsOfWhichApprover().isEmpty()) {
        // selectedUser.removeRole(Authority.APPROVER);
        // }
        // selectedUser.getProgramsOfWhichViewer().remove(selectedProgram);
        // applicationFormUserRoleService.deleteProgramRole(selectedUser, selectedProgram, Authority.VIEWER);
        // userDAO.save(selectedUser);
    }

    private void addOrRemoveFromProgramsOfWhichAdministratorIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
        // if (newAuthoritiesContains(newAuthorities, Authority.ADMINISTRATOR) && !listContainsId(selectedProgram,
        // selectedUser.getProgramsOfWhichAdministrator())) {
        // selectedUser.getProgramsOfWhichAdministrator().add(selectedProgram);
        // applicationFormUserRoleService.insertProgramRole(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
        // } else if (!newAuthoritiesContains(newAuthorities, Authority.ADMINISTRATOR)
        // && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichAdministrator())) {
        // selectedUser.getProgramsOfWhichAdministrator().remove(selectedProgram);
        // applicationFormUserRoleService.deleteProgramRole(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
        // }
    }

    private void addOrRemoveFromProgramsOfWhichApproverIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
        // if (newAuthoritiesContains(newAuthorities, Authority.APPROVER) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichApprover())) {
        // selectedUser.getProgramsOfWhichApprover().add(selectedProgram);
        // applicationFormUserRoleService.insertProgramRole(selectedUser, selectedProgram, Authority.APPROVER);
        // } else if (!newAuthoritiesContains(newAuthorities, Authority.APPROVER) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichApprover()))
        // {
        // selectedUser.getProgramsOfWhichApprover().remove(selectedProgram);
        // applicationFormUserRoleService.deleteProgramRole(selectedUser, selectedProgram, Authority.APPROVER);
        // }
    }

    private void addOrRemoveFromProgramsOfWhichViewerIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
        // if (newAuthoritiesContains(newAuthorities, Authority.VIEWER) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichViewer())) {
        // selectedUser.getProgramsOfWhichViewer().add(selectedProgram);
        // applicationFormUserRoleService.insertProgramRole(selectedUser, selectedProgram, Authority.VIEWER);
        // } else if (!newAuthoritiesContains(newAuthorities, Authority.VIEWER) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichViewer())) {
        // selectedUser.getProgramsOfWhichViewer().remove(selectedProgram);
        // applicationFormUserRoleService.deleteProgramRole(selectedUser, selectedProgram, Authority.VIEWER);
        // }
    }

    private void addToRoleIfRequired(RegisteredUser selectedUser, Authority[] newAuthorities, Authority authority) {
        // if (!selectedUser.isInRole(authority) && newAuthoritiesContains(newAuthorities, authority)) {
        // selectedUser.getRoles().add(roleDAO.getById(authority));
        // if (Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMITTER).contains(authority)) {
        // applicationFormUserRoleService.insertUserRole(selectedUser, authority);
        // }
        // }
    }

    private void addPendingRoleNotificationToUser(RegisteredUser selectedUser, Authority authority, Program program) {
        // PendingRoleNotification pendingRoleNotification = new PendingRoleNotification();
        // pendingRoleNotification.setRole(roleDAO.getById(authority));
        // pendingRoleNotification.setProgram(program);
        // pendingRoleNotification.setAddedByUser(getCurrentUser());
        // selectedUser.getPendingRoleNotifications().add(pendingRoleNotification);
    }

    private boolean newAuthoritiesContains(Authority[] newAuthorities, Authority authority) {
        return Arrays.asList(newAuthorities).contains(authority);
    }

    public void setDirectURLAndSaveUser(DirectURLsEnum directURL, ApplicationForm application, RegisteredUser newUser) {
        if (directURL != null && application != null) {
            newUser.setDirectToUrl(directURL.displayValue() + application.getApplicationNumber());
        }
    }

}
