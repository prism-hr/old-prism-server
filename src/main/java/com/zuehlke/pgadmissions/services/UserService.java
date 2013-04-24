package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationsFilterDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.exceptions.LinkAccountsException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.UserFactory;

@Service("userService")
@Transactional
public class UserService {

	private final Logger log = LoggerFactory.getLogger(UserService.class);
    
	private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final UserFactory userFactory;
    private final EncryptionUtils encryptionUtils;
    private final ApplicationsFilterDAO applicationsFilterDAO;
    private final MailSendingService mailService;

	public UserService() {
		this(null, null, null, null, null, null);
	}

	@Autowired
	public UserService(UserDAO userDAO, RoleDAO roleDAO, UserFactory userFactory,
			EncryptionUtils encryptionUtils, ApplicationsFilterDAO applicationsFilterDAO,
			MailSendingService mailService) {
		this.userDAO = userDAO;
		this.roleDAO = roleDAO;
		this.userFactory = userFactory;
		this.encryptionUtils = encryptionUtils;
		this.applicationsFilterDAO=applicationsFilterDAO;
		this.mailService = mailService;
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
        return userDAO.get(currentUser.getId());
    }

    public void addRoleToUser(RegisteredUser user, Authority authority) {
        user.getRoles().add(roleDAO.getRoleByAuthority(authority));
    }

    public void updateUserWithNewRoles(final RegisteredUser selectedUser, final Program selectedProgram, final Authority... newAuthorities) {
        // Please note: it is a deliberate decision to never remove people from SUPERADMIN role.

        for (Authority authority : Authority.values()) {
            addToRoleIfRequired(selectedUser, newAuthorities, authority);
        }

        addOrRemoveFromProgramsOfWhichAdministratorIfRequired(selectedUser, selectedProgram, newAuthorities);
        addOrRemoveFromProgramsOfWhichApproverIfRequired(selectedUser, selectedProgram, newAuthorities);
        addOrRemoveFromProgramsOfWhichReviewerIfRequired(selectedUser, selectedProgram, newAuthorities);
        addOrRemoveFromProgramsOfWhichInterviewerIfRequired(selectedUser, selectedProgram, newAuthorities);
        addOrRemoveFromProgramsOfWhichSupervisorIfRequired(selectedUser, selectedProgram, newAuthorities);
        addToProgramsOfWhichViewerIfRequired(selectedUser, selectedProgram);

        userDAO.save(selectedUser);
    }

    public void deleteUserFromProgramme(final RegisteredUser selectedUser, final Program selectedProgram) {
        selectedUser.getProgramsOfWhichAdministrator().remove(selectedProgram);
        selectedUser.getProgramsOfWhichApprover().remove(selectedProgram);
        selectedUser.getProgramsOfWhichReviewer().remove(selectedProgram);
        selectedUser.getProgramsOfWhichInterviewer().remove(selectedProgram);
        selectedUser.getProgramsOfWhichSupervisor().remove(selectedProgram);
        selectedUser.getProgramsOfWhichViewer().remove(selectedProgram);
        userDAO.save(selectedUser);
    }

    private void addToProgramsOfWhichViewerIfRequired(RegisteredUser selectedUser, Program selectedProgram) {
        if (!selectedUser.getProgramsOfWhichViewer().contains(selectedProgram)) {
            selectedUser.getProgramsOfWhichViewer().add(selectedProgram);
        }
    }

    private void addOrRemoveFromProgramsOfWhichSupervisorIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
        if (newAuthoritiesContains(newAuthorities, Authority.SUPERVISOR) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichSupervisor())) {
            selectedUser.getProgramsOfWhichSupervisor().add(selectedProgram);
        } else if (!newAuthoritiesContains(newAuthorities, Authority.SUPERVISOR)
                && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichSupervisor())) {
            selectedUser.getProgramsOfWhichSupervisor().remove(selectedProgram);
        }
    }

    private void addOrRemoveFromProgramsOfWhichAdministratorIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
        if (newAuthoritiesContains(newAuthorities, Authority.ADMINISTRATOR) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichAdministrator())) {
            selectedUser.getProgramsOfWhichAdministrator().add(selectedProgram);
        } else if (!newAuthoritiesContains(newAuthorities, Authority.ADMINISTRATOR)
                && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichAdministrator())) {
            selectedUser.getProgramsOfWhichAdministrator().remove(selectedProgram);
        }
    }

    private void addOrRemoveFromProgramsOfWhichApproverIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
        if (newAuthoritiesContains(newAuthorities, Authority.APPROVER) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichApprover())) {
            selectedUser.getProgramsOfWhichApprover().add(selectedProgram);
        } else if (!newAuthoritiesContains(newAuthorities, Authority.APPROVER) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichApprover())) {
            selectedUser.getProgramsOfWhichApprover().remove(selectedProgram);
        }
    }

    private void addOrRemoveFromProgramsOfWhichReviewerIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
        if (newAuthoritiesContains(newAuthorities, Authority.REVIEWER) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichReviewer())) {
            selectedUser.getProgramsOfWhichReviewer().add(selectedProgram);
        } else if (!newAuthoritiesContains(newAuthorities, Authority.REVIEWER) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichReviewer())) {
            selectedUser.getProgramsOfWhichReviewer().remove(selectedProgram);
        }
    }

    private void addOrRemoveFromProgramsOfWhichInterviewerIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
        if (newAuthoritiesContains(newAuthorities, Authority.INTERVIEWER) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichInterviewer())) {
            selectedUser.getProgramsOfWhichInterviewer().add(selectedProgram);
        } else if (!newAuthoritiesContains(newAuthorities, Authority.INTERVIEWER)
                && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichInterviewer())) {
            selectedUser.getProgramsOfWhichInterviewer().remove(selectedProgram);
        }
    }

    private void addToRoleIfRequired(RegisteredUser selectedUser, Authority[] newAuthorities, Authority authority) {
        if (!selectedUser.isInRole(authority) && newAuthoritiesContains(newAuthorities, authority)) {
            selectedUser.getRoles().add(roleDAO.getRoleByAuthority(authority));
        }
        if (selectedUser.isNotInRole(Authority.VIEWER)) {
            selectedUser.getRoles().add(roleDAO.getRoleByAuthority(Authority.VIEWER));
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

    public RegisteredUser createNewUserInRole(String firstName, String lastName, String email, Authority authority, DirectURLsEnum directURL,
            ApplicationForm application) {
        RegisteredUser newUser = userDAO.getUserByEmail(email);
        if (newUser != null) {
            throw new IllegalStateException(String.format("user with email: %s already exists!", email));
        }
        newUser = userFactory.createNewUserInRoles(firstName, lastName, email, authority);
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

        // make a new user a VIEWER per default for the programme
        List<Authority> authList = new ArrayList<Authority>(Arrays.asList(authorities));
        if (!authList.contains(Authority.VIEWER)) {
            authList.add(Authority.VIEWER);
        }

        newUser = userFactory.createNewUserInRoles(firstName, lastName, email, authList);
        if (authList.contains(Authority.SUPERADMINISTRATOR)) {
            addPendingRoleNotificationToUser(newUser, Authority.SUPERADMINISTRATOR, null);
        }

        if (authList.contains(Authority.REVIEWER)) {
            newUser.getProgramsOfWhichReviewer().add(program);
            addPendingRoleNotificationToUser(newUser, Authority.REVIEWER, program);
        }

        if (authList.contains(Authority.ADMINISTRATOR)) {
            newUser.getProgramsOfWhichAdministrator().add(program);
            addPendingRoleNotificationToUser(newUser, Authority.ADMINISTRATOR, program);
        }

        if (authList.contains(Authority.APPROVER)) {
            newUser.getProgramsOfWhichApprover().add(program);
            addPendingRoleNotificationToUser(newUser, Authority.APPROVER, program);
        }

        if (authList.contains(Authority.INTERVIEWER)) {
            newUser.getProgramsOfWhichInterviewer().add(program);
            addPendingRoleNotificationToUser(newUser, Authority.INTERVIEWER, program);
        }

        if (authList.contains(Authority.SUPERVISOR)) {
            newUser.getProgramsOfWhichSupervisor().add(program);
            addPendingRoleNotificationToUser(newUser, Authority.SUPERVISOR, program);
        }

        if (authList.contains(Authority.VIEWER)) {
            newUser.getProgramsOfWhichViewer().add(program);
            addPendingRoleNotificationToUser(newUser, Authority.VIEWER, program);
        }

        userDAO.save(newUser);
        return newUser;
    }

    public List<RegisteredUser> getAllPreviousInterviewersOfProgram(Program program) {
        return userDAO.getAllPreviousInterviewersOfProgram(program);
    }

    public List<RegisteredUser> getAllPreviousReviewersOfProgram(Program program) {
        return userDAO.getAllPreviousReviewersOfProgram(program);
    }

    public List<RegisteredUser> getAllPreviousSupervisorsOfProgram(Program program) {
        return userDAO.getAllPreviousSupervisorsOfProgram(program);
    }

    public List<RegisteredUser> getReviewersWillingToInterview(ApplicationForm applicationForm) {
        return userDAO.getReviewersWillingToInterview(applicationForm);
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
        RegisteredUser storedUser = userDAO.getUserByEmail(email);
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
        for (Program entry : programs) {
            if (entry.getId().equals(program.getId())) {
                return true;
            }
        }
        return false;
    }

    public void setFilters(RegisteredUser user, List<ApplicationsFilter> filters) {
        for (ApplicationsFilter existingFilter : user.getApplicationsFilters()) {
            applicationsFilterDAO.removeFilter(existingFilter);
        }
        user.setStoredFilters(true);
        user.getApplicationsFilters().clear();

        for (ApplicationsFilter filter : filters) {
            filter.setUser(user);
            user.getApplicationsFilters().add(filter);
            applicationsFilterDAO.save(filter);
        }
        userDAO.save(user);
    }
    
    public void sendEmailToDelegateAndRegisterReminder(ApplicationForm applicationForm, RegisteredUser delegate) {
    	NotificationRecord delegateReminder = new NotificationRecord(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER);
    	delegateReminder.setApplication(applicationForm);
    	delegateReminder.setUser(delegate);
    	delegateReminder.setDate(new Date());
    	applicationForm.setSuppressStateChangeNotifications(true);
    	applicationForm.addNotificationRecord(delegateReminder);
    	 try {
    		 mailService.scheduleInterviewAdministrationRequest(delegate, applicationForm);
         } catch (Exception e) {
             log.warn("error while sending email", e);
         }
    }

    public void resetDigestNotificationsForAllUsers() {
        userDAO.resetDigestNotificationsForAllUsers();
    }
    
    public List<Integer> getAllUsersInNeedOfADigestNotification() {
        return userDAO.getAllUserIdsInNeedOfADigestNotification();
    }
}
