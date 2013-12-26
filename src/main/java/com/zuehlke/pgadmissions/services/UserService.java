package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
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
import com.zuehlke.pgadmissions.domain.Role;
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
    private final EncryptionUtils encryptionUtils;
    private final MailSendingService mailService;
    private final ProgramsService programsService;

    public UserService() {
        this(null, null, null, null, null, null, null);
    }

    @Autowired
    public UserService(UserDAO userDAO, RoleDAO roleDAO, ApplicationsFilteringDAO filteringDAO, EncryptionUtils encryptionUtils,
            MailSendingService mailService, ProgramsService programsService, ApplicationFormUserRoleService applicationFormUserRoleService) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.filteringDAO = filteringDAO;
        this.encryptionUtils = encryptionUtils;
        this.mailService = mailService;
        this.programsService = programsService;
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

    protected void addRoleToUser(RegisteredUser user, Authority authority) {
    	Role roleToAssign = roleDAO.getRoleByAuthority(authority);
    	List<Role> userRoles = user.getRoles();
    	
    	if (!userRoles.contains(roleToAssign)) {
            user.getRoles().add(roleDAO.getRoleByAuthority(authority));
            
            if (BooleanUtils.isTrue(roleToAssign.getDoSendRoleNotification())) {
            	addPendingRoleNotification(user, authority);
            }
    	}
    	
    	userDAO.save(user);
    }
    
    protected void revokeRoleFromUser(RegisteredUser user, Authority authority) {
    	user.getRoles().remove(roleDAO.getRoleByAuthority(authority));
    }

    protected void updateUserWithNewRoles(final RegisteredUser selectedUser, final Program selectedProgram, final Authority... newAuthorities) {
        for (Authority authority : newAuthorities) {
            if (!selectedUser.isInRole(authority)) {
                selectedUser.getRoles().add(roleDAO.getRoleByAuthority(authority));
            }
        }

        addOrRemoveFromProgramsOfWhichAdministratorIfRequired(selectedUser, selectedProgram, newAuthorities);
        addOrRemoveFromProgramsOfWhichApproverIfRequired(selectedUser, selectedProgram, newAuthorities);
        addOrRemoveFromProgramsOfWhichViewerIfRequired(selectedUser, selectedProgram, newAuthorities);

        userDAO.save(selectedUser);
    }

    protected void deleteUserFromProgramme(final RegisteredUser selectedUser, final Program selectedProgram) {
    	deleteUserFromProgramAdministratorRole(selectedUser, selectedProgram);
    	deleteUserFromProgramApproverRole(selectedUser, selectedProgram);
    	deleteUserFromProgramViewerRole(selectedUser, selectedProgram);
    	
        userDAO.save(selectedUser);
    }
    
    private void deleteUserFromProgramAdministratorRole(final RegisteredUser selectedUser, final Program selectedProgram) {
        selectedUser.getProgramsOfWhichAdministrator().remove(selectedProgram);
        
    	if (selectedUser.getProgramsOfWhichAdministrator().isEmpty()) {
            selectedUser.removeRole(Authority.ADMINISTRATOR);
        }
    }
    
    private void deleteUserFromProgramApproverRole(final RegisteredUser selectedUser, final Program selectedProgram) {
    	selectedUser.getProgramsOfWhichApprover().remove(selectedProgram);
    	
    	if (selectedUser.getProgramsOfWhichApprover().isEmpty()) {
            selectedUser.removeRole(Authority.APPROVER);
        }
    }
    
    private void deleteUserFromProgramViewerRole(final RegisteredUser selectedUser, final Program selectedProgram) {
    	selectedUser.getProgramsOfWhichViewer().remove(selectedProgram);
    	
    	if (selectedUser.getProgramsOfWhichViewer().isEmpty()) {
            selectedUser.removeRole(Authority.VIEWER);
        }
    }

    private void addOrRemoveFromProgramsOfWhichAdministratorIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority... newAuthorities) {
        if (newAuthoritiesContains(newAuthorities, Authority.ADMINISTRATOR) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichAdministrator())) {
            selectedUser.getProgramsOfWhichAdministrator().add(selectedProgram);
        } else if (!newAuthoritiesContains(newAuthorities, Authority.ADMINISTRATOR) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichAdministrator())) {
        	deleteUserFromProgramAdministratorRole(selectedUser, selectedProgram);
        }
    }

    private void addOrRemoveFromProgramsOfWhichApproverIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority... newAuthorities) {
        if (newAuthoritiesContains(newAuthorities, Authority.APPROVER) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichApprover())) {
            selectedUser.getProgramsOfWhichApprover().add(selectedProgram);
        } else if (!newAuthoritiesContains(newAuthorities, Authority.APPROVER) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichApprover())) {
        	deleteUserFromProgramApproverRole(selectedUser, selectedProgram);
        }
    }

    private void addOrRemoveFromProgramsOfWhichViewerIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority... newAuthorities) {
        if (newAuthoritiesContains(newAuthorities, Authority.VIEWER) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichViewer())) {
            selectedUser.getProgramsOfWhichViewer().add(selectedProgram);
        } else if (!newAuthoritiesContains(newAuthorities, Authority.VIEWER) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichViewer())) {
        	deleteUserFromProgramViewerRole(selectedUser, selectedProgram);
        }
    }
    
    private void addPendingRoleNotification(RegisteredUser selectedUser, Authority authority) {
        PendingRoleNotification pendingRoleNotification = new PendingRoleNotification();
        pendingRoleNotification.setRole(roleDAO.getRoleByAuthority(authority));
        pendingRoleNotification.setAddedByUser(getCurrentUser());
        selectedUser.getPendingRoleNotifications().add(pendingRoleNotification);
    }

    private void addPendingProgramRoleNotification(RegisteredUser selectedUser, Authority authority, Program program) {
        PendingRoleNotification pendingRoleNotification = new PendingRoleNotification();
        pendingRoleNotification.setRole(roleDAO.getRoleByAuthority(authority));
        pendingRoleNotification.setProgram(program);
        pendingRoleNotification.setAddedByUser(getCurrentUser());
        selectedUser.getPendingRoleNotifications().add(pendingRoleNotification);
    }

    private boolean newAuthoritiesContains(Authority[] newAuthorities, Authority authority) {
        return Arrays.asList(newAuthorities).contains(authority);
    }
    
    private RegisteredUser createRegisteredUser(final String firstname, final String lastname, final String email) {
        RegisteredUser user = new RegisteredUser();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setUsername(email);
        user.setEmail(email);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setEnabled(false);
        user.setCredentialsNonExpired(true);
        user.setActivationCode(encryptionUtils.generateUUID());
        return user;
    }
    
    public RegisteredUser createNewUserInRoles(String firstname, String lastname, String email, Authority... authorities) {
        RegisteredUser newUser = userDAO.getUserByEmail(email);
        
        if (newUser != null) {
            throw new IllegalStateException(String.format("User with email: %s already exists!", email));
        }
    	
        newUser = createRegisteredUser(firstname, lastname, email);
        List<Role> userRoles = newUser.getRoles();
        
        for (Authority authority : authorities) {
        	Role roleToAssign = roleDAO.getRoleByAuthority(authority);
            userRoles.add(roleToAssign);
            
            if (BooleanUtils.isTrue(roleToAssign.getDoSendRoleNotification())) {
            	addPendingRoleNotification(newUser, authority);
            }
        }
        
        userRoles.add(roleDAO.getRoleByAuthority(Authority.SAFETYNET));
        userDAO.save(newUser);
        return newUser;
    }

    public RegisteredUser createNewUserInRoles(final String firstName, final String lastName, final String email, 
    		final DirectURLsEnum directURL, final ApplicationForm application, final Authority... authorities) {
        RegisteredUser newUser = createNewUserInRoles(firstName, lastName, email, authorities);
        
        if (directURL != null && application != null) {
            newUser.setDirectToUrl(directURL.displayValue() + application.getApplicationNumber());
        }
        
        userDAO.save(newUser);
        return newUser;
    }
    
    public RegisteredUser createNewUserForProgramme(final String firstName, final String lastName, final String email, final Program program,
            final Authority... authorities) {
        RegisteredUser newUser = userDAO.getUserByEmail(email);

        if (newUser != null) {
            throw new IllegalStateException(String.format("User with email: %s already exists!", email));
        }

        List<Authority> authList = new ArrayList<Authority>(Arrays.asList(authorities));
        newUser = createNewUserInRoles(firstName, lastName, email, authorities);

        if (authList.contains(Authority.ADMINISTRATOR)) {
            newUser.getProgramsOfWhichAdministrator().add(program);
            addPendingProgramRoleNotification(newUser, Authority.ADMINISTRATOR, program);
        }

        if (authList.contains(Authority.APPROVER)) {
            newUser.getProgramsOfWhichApprover().add(program);
            addPendingProgramRoleNotification(newUser, Authority.APPROVER, program);
        }

        if (authList.contains(Authority.VIEWER)) {
            newUser.getProgramsOfWhichViewer().add(program);
            addPendingProgramRoleNotification(newUser, Authority.VIEWER, program);
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
        if (storedUser == null) {
            log.info("Reset password request failed, e-mail not found: " + email + ".");
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