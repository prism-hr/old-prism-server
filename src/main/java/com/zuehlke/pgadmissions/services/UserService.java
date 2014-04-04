package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service("userService")
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private ApplicationsFilteringDAO filteringDAO;

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Autowired
    private MailSendingService mailService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    public void save(RegisteredUser user) {
        userDAO.save(user);
    }
    
    public RegisteredUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            RegisteredUser currentUser = (RegisteredUser) authentication.getDetails();
            return userDAO.getPrimaryById(currentUser.getId());
        }
        return null;
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

    public RegisteredUser getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }

    public RegisteredUser getUserByEmailIncludingDisabledAccounts(String email) {
        return userDAO.getUserByEmailIncludingDisabledAccounts(email);
    }

    public RegisteredUser getUserByEmailDisabledAccountsOnly(String email) {
        return userDAO.getDisabledUserByEmail(email);
    }
    
    public RegisteredUser createNewUser(final String firstname, final String lastname, final String email) {
        RegisteredUser user = new RegisteredUser();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setUsername(email);
        user.setEmail(email);
        user.setEnabled(false);
        user.setActivationCode(encryptionUtils.generateUUID());
        return user;
    }
    
    public RegisteredUser createNewUserInRoles(final String firstname, final String lastname, final String email, Authority... authority) {
        return null;
    }

    public void addRoleToUser(RegisteredUser user, Authority authority) {
//        if (!user.getRoles().contains(authority)) {
//            user.getRoles().add(roleDAO.getById(authority));
//            if (Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMITTER).contains(authority)) {
//                applicationFormUserRoleService.insertUserRole(user, authority);
//            }
//            userDAO.save(user);
//        }
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
//        selectedUser.getProgramsOfWhichAdministrator().remove(selectedProgram);
//        applicationFormUserRoleService.deleteProgramRole(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
//        if (selectedUser.getProgramsOfWhichAdministrator().isEmpty()) {
//            selectedUser.removeRole(Authority.ADMINISTRATOR);
//        }
//        selectedUser.getProgramsOfWhichApprover().remove(selectedProgram);
//        applicationFormUserRoleService.deleteProgramRole(selectedUser, selectedProgram, Authority.APPROVER);
//        if (selectedUser.getProgramsOfWhichApprover().isEmpty()) {
//            selectedUser.removeRole(Authority.APPROVER);
//        }
//        selectedUser.getProgramsOfWhichViewer().remove(selectedProgram);
//        applicationFormUserRoleService.deleteProgramRole(selectedUser, selectedProgram, Authority.VIEWER);
//        userDAO.save(selectedUser);
    }

    private void addOrRemoveFromProgramsOfWhichAdministratorIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
//        if (newAuthoritiesContains(newAuthorities, Authority.ADMINISTRATOR) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichAdministrator())) {
//            selectedUser.getProgramsOfWhichAdministrator().add(selectedProgram);
//            applicationFormUserRoleService.insertProgramRole(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
//        } else if (!newAuthoritiesContains(newAuthorities, Authority.ADMINISTRATOR)
//                && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichAdministrator())) {
//            selectedUser.getProgramsOfWhichAdministrator().remove(selectedProgram);
//            applicationFormUserRoleService.deleteProgramRole(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
//        }
    }

    private void addOrRemoveFromProgramsOfWhichApproverIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
//        if (newAuthoritiesContains(newAuthorities, Authority.APPROVER) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichApprover())) {
//            selectedUser.getProgramsOfWhichApprover().add(selectedProgram);
//            applicationFormUserRoleService.insertProgramRole(selectedUser, selectedProgram, Authority.APPROVER);
//        } else if (!newAuthoritiesContains(newAuthorities, Authority.APPROVER) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichApprover())) {
//            selectedUser.getProgramsOfWhichApprover().remove(selectedProgram);
//            applicationFormUserRoleService.deleteProgramRole(selectedUser, selectedProgram, Authority.APPROVER);
//        }
    }

    private void addOrRemoveFromProgramsOfWhichViewerIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
//        if (newAuthoritiesContains(newAuthorities, Authority.VIEWER) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichViewer())) {
//            selectedUser.getProgramsOfWhichViewer().add(selectedProgram);
//            applicationFormUserRoleService.insertProgramRole(selectedUser, selectedProgram, Authority.VIEWER);
//        } else if (!newAuthoritiesContains(newAuthorities, Authority.VIEWER) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichViewer())) {
//            selectedUser.getProgramsOfWhichViewer().remove(selectedProgram);
//            applicationFormUserRoleService.deleteProgramRole(selectedUser, selectedProgram, Authority.VIEWER);
//        }
    }

    private void addToRoleIfRequired(RegisteredUser selectedUser, Authority[] newAuthorities, Authority authority) {
//        if (!selectedUser.isInRole(authority) && newAuthoritiesContains(newAuthorities, authority)) {
//            selectedUser.getRoles().add(roleDAO.getById(authority));
//            if (Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMITTER).contains(authority)) {
//            	applicationFormUserRoleService.insertUserRole(selectedUser, authority);
//            }
//        }
    }

    private void addPendingRoleNotificationToUser(RegisteredUser selectedUser, Authority authority, Program program) {
        PendingRoleNotification pendingRoleNotification = new PendingRoleNotification();
        pendingRoleNotification.setRole(roleDAO.getById(authority));
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
        
        newUser = createNewUserInRoles(firstName, lastName, email, authorities);
        
        for (Authority authority : authorities) {
            if (Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMITTER, Authority.STATEADMINISTRATOR).contains(authority)) {
            	applicationFormUserRoleService.insertUserRole(newUser, authority);
            }
        }
        
        userDAO.save(newUser);
        return newUser;
    }

    public RegisteredUser createNewUserInRole(final String firstName, final String lastName, final String email, 
    		final DirectURLsEnum directURL, final ApplicationForm application, final Authority... authorities) {
        RegisteredUser newUser = createNewUserInRole(firstName, lastName, email, authorities);
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

        List<Authority> authList = Arrays.asList(authorities);

        newUser = createNewUserInRoles(firstName, lastName, email, authorities);
        userDAO.save(newUser);
        
        if (authList.contains(Authority.SUPERADMINISTRATOR)) {
            addPendingRoleNotificationToUser(newUser, Authority.SUPERADMINISTRATOR, null);
            applicationFormUserRoleService.insertUserRole(newUser, Authority.SUPERADMINISTRATOR);
        }

//        if (authList.contains(Authority.ADMINISTRATOR)) {
//            newUser.getProgramsOfWhichAdministrator().add(program);
//            addPendingRoleNotificationToUser(newUser, Authority.ADMINISTRATOR, program);
//            applicationFormUserRoleService.insertProgramRole(newUser, program, Authority.ADMINISTRATOR);
//        }
//
//        if (authList.contains(Authority.APPROVER)) {
//            newUser.getProgramsOfWhichApprover().add(program);
//            addPendingRoleNotificationToUser(newUser, Authority.APPROVER, program);
//            applicationFormUserRoleService.insertProgramRole(newUser, program, Authority.APPROVER);
//        }
//
//        if (authList.contains(Authority.VIEWER)) {
//            newUser.getProgramsOfWhichViewer().add(program);
//            addPendingRoleNotificationToUser(newUser, Authority.VIEWER, program);
//            applicationFormUserRoleService.insertProgramRole(newUser, program, Authority.VIEWER);
//        }
        
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

    private boolean listContainsId(Program program, HashSet<Program> programs) {
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
    
    public void setApplicationFormListLastAccessTimestamp(RegisteredUser registeredUser) {
    	registeredUser.setApplicationListLastAccessTimestamp(new Date());
    	userDAO.save(registeredUser);
    	
    }

    public RegisteredUser getById(int id) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
