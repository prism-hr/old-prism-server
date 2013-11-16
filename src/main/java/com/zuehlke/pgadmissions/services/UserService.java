package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationsFilteringDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.exceptions.LinkAccountsException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service("userService")
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final ApplicationsFilteringDAO filteringDAO;
    private final UserFactory userFactory;
    private final EncryptionUtils encryptionUtils;
    private final MailSendingService mailService;
    private final ProgramsService programsService;
    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    public UserService() {
        this(null, null, null, null, null, null, null, null);
    }

    @Autowired
    public UserService(UserDAO userDAO, RoleDAO roleDAO, ApplicationsFilteringDAO filteringDAO, UserFactory userFactory, EncryptionUtils encryptionUtils,
            MailSendingService mailService, ProgramsService programsService, ApplicationFormUserRoleService applicationFormUserRoleService) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.filteringDAO = filteringDAO;
        this.userFactory = userFactory;
        this.encryptionUtils = encryptionUtils;
        this.mailService = mailService;
        this.programsService = programsService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
    }

    public RegisteredUser getUser(Integer id) {
        return userDAO.get(id);
    }

    public List<RegisteredUser> getUsersInRole(Authority auth) {
        return userDAO.getUsersInRole(roleDAO.getRoleByAuthority(auth));
    }

    public void save(RegisteredUser user) {
        userDAO.save(user);
    }

    public List<RegisteredUser> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public RegisteredUser getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }

    public List<RegisteredUser> getAllUsersForProgram(Program program) {
        return userDAO.getUsersForProgram(program);
    }

    public List<RegisteredUser> getAllInternalUsers() {
        return userDAO.getInternalUsers();
    }

    public RegisteredUser getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }

    public RegisteredUser getUserByEmailIncludingDisabledAccounts(String email) {
        return userDAO.getUserByEmailIncludingDisabledAccounts(email);
    }

    public RegisteredUser getUserByEmailDisabledAccountsOnly(String email) {
        return userDAO.getDisabledUserByEmail(email);
    }

    public RegisteredUser getCurrentUser() {
        RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        RegisteredUser user = userDAO.get(currentUser.getId());
        boolean canManageProjects = !programsService.getProgramsForWhichCanManageProjects(user).isEmpty();
        user.setCanManageProjects(canManageProjects);
        return user;
    }

    public void addRoleToUser(RegisteredUser user, Authority authority) {
        user.getRoles().add(roleDAO.getRoleByAuthority(authority));
        if (Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMITTER).contains(authority)) {
        	applicationFormUserRoleService.createUserInRole(user, authority);
        }
    }

    public void updateUserWithNewRoles(final RegisteredUser selectedUser, final Program selectedProgram, final Authority... newAuthorities) {
        // Please note: it is a deliberate decision to never remove people from SUPERADMIN role.

        for (Authority authority : Authority.values()) {
            addToRoleIfRequired(selectedUser, newAuthorities, authority);
        }

        addOrRemoveFromProgramsOfWhichAdministratorIfRequired(selectedUser, selectedProgram, newAuthorities);
        addOrRemoveFromProgramsOfWhichApproverIfRequired(selectedUser, selectedProgram, newAuthorities);
        addOrRemoveFromProgramsOfWhichViewerIfRequired(selectedUser, selectedProgram, newAuthorities);

        userDAO.save(selectedUser);
    }

    public void deleteUserFromProgramme(final RegisteredUser selectedUser, final Program selectedProgram) {
        selectedUser.getProgramsOfWhichAdministrator().remove(selectedProgram);
        applicationFormUserRoleService.revokeUserFromProgramRole(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
        if (selectedUser.getProgramsOfWhichAdministrator().isEmpty()) {
            selectedUser.removeRole(Authority.ADMINISTRATOR);
        }
        selectedUser.getProgramsOfWhichApprover().remove(selectedProgram);
        applicationFormUserRoleService.revokeUserFromProgramRole(selectedUser, selectedProgram, Authority.APPROVER);
        if (selectedUser.getProgramsOfWhichApprover().isEmpty()) {
            selectedUser.removeRole(Authority.APPROVER);
        }
        selectedUser.getProgramsOfWhichViewer().remove(selectedProgram);
        applicationFormUserRoleService.revokeUserFromProgramRole(selectedUser, selectedProgram, Authority.VIEWER);
        userDAO.save(selectedUser);
    }

    private void addOrRemoveFromProgramsOfWhichAdministratorIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
        if (newAuthoritiesContains(newAuthorities, Authority.ADMINISTRATOR) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichAdministrator())) {
            selectedUser.getProgramsOfWhichAdministrator().add(selectedProgram);
            applicationFormUserRoleService.createUserInProgramRole(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
        } else if (!newAuthoritiesContains(newAuthorities, Authority.ADMINISTRATOR)
                && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichAdministrator())) {
            selectedUser.getProgramsOfWhichAdministrator().remove(selectedProgram);
            applicationFormUserRoleService.revokeUserFromProgramRole(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
        }
    }

    private void addOrRemoveFromProgramsOfWhichApproverIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
        if (newAuthoritiesContains(newAuthorities, Authority.APPROVER) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichApprover())) {
            selectedUser.getProgramsOfWhichApprover().add(selectedProgram);
            applicationFormUserRoleService.createUserInProgramRole(selectedUser, selectedProgram, Authority.APPROVER);
        } else if (!newAuthoritiesContains(newAuthorities, Authority.APPROVER) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichApprover())) {
            selectedUser.getProgramsOfWhichApprover().remove(selectedProgram);
            applicationFormUserRoleService.revokeUserFromProgramRole(selectedUser, selectedProgram, Authority.APPROVER);
        }
    }

    private void addOrRemoveFromProgramsOfWhichViewerIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
        if (newAuthoritiesContains(newAuthorities, Authority.VIEWER) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichViewer())) {
            selectedUser.getProgramsOfWhichViewer().add(selectedProgram);
            applicationFormUserRoleService.createUserInProgramRole(selectedUser, selectedProgram, Authority.VIEWER);
        } else if (!newAuthoritiesContains(newAuthorities, Authority.VIEWER) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichViewer())) {
            selectedUser.getProgramsOfWhichViewer().remove(selectedProgram);
            applicationFormUserRoleService.revokeUserFromProgramRole(selectedUser, selectedProgram, Authority.VIEWER);
        }
    }

    private void addToRoleIfRequired(RegisteredUser selectedUser, Authority[] newAuthorities, Authority authority) {
        if (!selectedUser.isInRole(authority) && newAuthoritiesContains(newAuthorities, authority)) {
            selectedUser.getRoles().add(roleDAO.getRoleByAuthority(authority));
            if (Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMITTER).contains(authority)) {
            	applicationFormUserRoleService.createUserInRole(selectedUser, authority);
            }
        }
    }

    private void addPendingRoleNotificationToUser(RegisteredUser selectedUser, Authority authority, Program program) {
        PendingRoleNotification pendingRoleNotification = new PendingRoleNotification();
        pendingRoleNotification.setRole(roleDAO.getRoleByAuthority(authority));
        pendingRoleNotification.setProgram(program);
        pendingRoleNotification.setAddedByUser(getCurrentUser());
        selectedUser.getPendingRoleNotifications().add(pendingRoleNotification);
    }

    private boolean newAuthoritiesContains(Authority[] newAuthorities, Authority authority) {
        return Arrays.asList(newAuthorities).contains(authority);
    }
    
    public RegisteredUser createNewUserInRole(final String firstName, final String lastName, final String email, 
    		final Authority... authorities) {
        RegisteredUser newUser = userDAO.getUserByEmail(email);
        if (newUser != null) {
            throw new IllegalStateException(String.format("user with email: %s already exists!", email));
        }
        return newUser;
    } 

    public RegisteredUser createNewUserInRole(final String firstName, final String lastName, final String email, 
    		final DirectURLsEnum directURL, final ApplicationForm application, final Authority... authorities) {
        RegisteredUser newUser = createNewUserInRole(firstName, lastName, email, authorities);
        for (Authority authority : authorities) {
            if (Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMITTER, Authority.SUGGESTEDSUPERVISOR).contains(authority)) {
            	applicationFormUserRoleService.createUserInRole(newUser, authority);
            }
        }
        setDirectURLAndSaveUser(directURL, application, newUser);
        return newUser;
    }

    public void setDirectURLAndSaveUser(DirectURLsEnum directURL, ApplicationForm application, RegisteredUser newUser) {
        if (directURL != null && application != null) {
            newUser.setDirectToUrl(directURL.displayValue() + application.getApplicationNumber());
        }
        userDAO.save(newUser);
    }

    public RegisteredUser createNewUserForProgramme(final String firstName, final String lastName, final String email, final Program program,
            final Authority... authorities) {
        RegisteredUser newUser = userDAO.getUserByEmail(email);

        if (newUser != null) {
            throw new IllegalStateException(String.format("user with email: %s already exists!", email));
        }

        List<Authority> authList = new ArrayList<Authority>(Arrays.asList(authorities));

        newUser = userFactory.createNewUserInRoles(firstName, lastName, email, authList);
        if (authList.contains(Authority.SUPERADMINISTRATOR)) {
            addPendingRoleNotificationToUser(newUser, Authority.SUPERADMINISTRATOR, null);
            applicationFormUserRoleService.createUserInRole(newUser, Authority.SUPERADMINISTRATOR);
        }

        if (authList.contains(Authority.ADMINISTRATOR)) {
            newUser.getProgramsOfWhichAdministrator().add(program);
            addPendingRoleNotificationToUser(newUser, Authority.ADMINISTRATOR, program);
            applicationFormUserRoleService.createUserInProgramRole(newUser, program, Authority.ADMINISTRATOR);
        }

        if (authList.contains(Authority.APPROVER)) {
            newUser.getProgramsOfWhichApprover().add(program);
            addPendingRoleNotificationToUser(newUser, Authority.APPROVER, program);
            applicationFormUserRoleService.createUserInProgramRole(newUser, program, Authority.APPROVER);
        }

        if (authList.contains(Authority.VIEWER)) {
            newUser.getProgramsOfWhichViewer().add(program);
            addPendingRoleNotificationToUser(newUser, Authority.VIEWER, program);
            applicationFormUserRoleService.createUserInProgramRole(newUser, program, Authority.VIEWER);
        }

        userDAO.save(newUser);
        
        return newUser;
    }

    public void updateCurrentUser(RegisteredUser user) {
        RegisteredUser currentUser = getCurrentUser();
        currentUser.setFirstName(user.getFirstName());
        currentUser.setFirstName2(user.getFirstName2());
        currentUser.setFirstName3(user.getFirstName3());
        currentUser.setLastName(user.getLastName());
        currentUser.setEmail(user.getEmail());
        if (StringUtils.isNotBlank(user.getNewPassword())) {
            currentUser.setPassword(encryptionUtils.getMD5Hash(user.getNewPassword()));
        }
        currentUser.setUsername(user.getEmail());
        save(currentUser);
    }

    public void resetPassword(String email) {
        RegisteredUser storedUser = userDAO.getUserByEmailIncludingDisabledAccounts(email);
        if (storedUser == null) { // user-mail not found -> ignore
            log.info("reset password request failed, e-mail not found: " + email);
            return;
        }
        try {
            String newPassword = encryptionUtils.generateUserPassword();

            mailService.sendResetPasswordMessage(storedUser, newPassword);

            String hashPassword = encryptionUtils.getMD5Hash(newPassword);
            storedUser.setPassword(hashPassword);
            userDAO.save(storedUser);
        } catch (Exception e) {
            log.warn("error while sending email", e);
        }
    }

    public void linkAccounts(String secondAccountEmail) throws LinkAccountsException {
        RegisteredUser secondAccount = getUserByEmail(secondAccountEmail);
        RegisteredUser currentAccount = getCurrentUser();

        if (listContainsId(secondAccount, currentAccount.getLinkedAccounts())) {
            return;
        }

        if (!currentAccount.isEnabled() || !secondAccount.isEnabled()) {
            throw new LinkAccountsException("account.not.enabled");
        }

        if (!currentAccount.isAccountNonExpired() || !secondAccount.isAccountNonExpired()) {
            throw new LinkAccountsException("account.not.enabled");
        }

        if (!currentAccount.isAccountNonLocked() || !secondAccount.isAccountNonLocked()) {
            throw new LinkAccountsException("account.not.enabled");
        }

        if (!currentAccount.isCredentialsNonExpired() || !secondAccount.isCredentialsNonExpired()) {
            throw new LinkAccountsException("account.not.enabled");
        }

        RegisteredUser primary = currentAccount.getPrimaryAccount();
        if (primary == null) {
            primary = currentAccount;
        }

        RegisteredUser secondPrimary = secondAccount.getPrimaryAccount();
        if (secondPrimary != null) {
            for (RegisteredUser u : secondPrimary.getLinkedAccounts()) {
                u.setPrimaryAccount(primary);
            }
            secondPrimary.setPrimaryAccount(primary);
        } else {
            secondAccount.setPrimaryAccount(primary);
            for (RegisteredUser u : secondAccount.getLinkedAccounts()) {
                u.setPrimaryAccount(primary);
            }
        }
    }

    public void deleteLinkedAccount(String accountToDeleteEmail) {
        RegisteredUser currentAccount = getCurrentUser();
        RegisteredUser accountToDelete = getUserByEmail(accountToDeleteEmail);

        RegisteredUser primary = accountToDelete.getPrimaryAccount();
        if (primary == null) {
            for (RegisteredUser u : accountToDelete.getLinkedAccounts()) {
                u.setPrimaryAccount(currentAccount);
            }
            currentAccount.setPrimaryAccount(null);
        } else {
            accountToDelete.setPrimaryAccount(null);
        }
    }

    private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (entry.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public RegisteredUser getUserByActivationCode(String activationCode) {
        return userDAO.getUserByActivationCode(activationCode);
    }

    private boolean listContainsId(Program program, List<Program> programs) {
        if (program == null) {
            return false;
        }
        for (Program entry : programs) {
            if (entry.getId().equals(program.getId())) {
                return true;
            }
        }
        return false;
    }

    public void setFiltering(final RegisteredUser user, final ApplicationsFiltering filtering) {
        ApplicationsFiltering mergedFilter = filteringDAO.merge(filtering);
        user.setFiltering(mergedFilter);
        userDAO.save(user);
    }

    public Long getNumberOfActiveApplicationsForApplicant(final RegisteredUser applicant) {
        return userDAO.getNumberOfActiveApplicationsForApplicant(applicant);
    }

    public List<RegisteredUser> getUsersWithUpi(final String upi) {
        return userDAO.getUsersWithUpi(upi);
    }
}