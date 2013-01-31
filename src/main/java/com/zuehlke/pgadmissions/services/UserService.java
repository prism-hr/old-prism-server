package com.zuehlke.pgadmissions.services;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.controllers.LinkAccountsException;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.utils.UserFactory;

@Service("userService")
public class UserService {

	private final UserDAO userDAO;
	private final RoleDAO roleDAO;
	private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	private final JavaMailSender mailsender;
	private final Logger log = Logger.getLogger(UserService.class);
	private final UserFactory userFactory;
	private final MessageSource msgSource;
	private final EncryptionUtils encryptionUtils;

	public UserService() {
		this(null, null, null, null, null, null, null);
	}

	@Autowired
	public UserService(UserDAO userDAO, RoleDAO roleDAO, UserFactory userFactory, MimeMessagePreparatorFactory mimeMessagePreparatorFactory,
			JavaMailSender mailsender, MessageSource msgSource, EncryptionUtils encryptionUtils) {
		this.userDAO = userDAO;
		this.roleDAO = roleDAO;
		this.userFactory = userFactory;
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.mailsender = mailsender;
		this.msgSource = msgSource;
		this.encryptionUtils = encryptionUtils;
	}

	public RegisteredUser getUser(Integer id) {
		return userDAO.get(id);
	}

	public List<RegisteredUser> getUsersInRole(Authority auth) {
		return userDAO.getUsersInRole(roleDAO.getRoleByAuthority(auth));
	}

	@Transactional
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

	@Transactional
	public void updateUserWithNewRoles(RegisteredUser selectedUser, Program selectedProgram, Authority... newAuthorities) {
		//Please note:
		//it is a deliberate decision to never remove people from SUPERADMIN role.
	
		for (Authority authority : Authority.values()) {
			addToRoleIfRequired(selectedUser, newAuthorities, authority);
		}

		addOrRemoveFromProgramsOfWhichAdministratorIfRequired(selectedUser, selectedProgram, newAuthorities);
		addOrRemoveFromProgramsOfWhichApproverIfRequired(selectedUser, selectedProgram, newAuthorities);
		addOrRemoveFromProgramsOfWhichReviewerIfRequired(selectedUser, selectedProgram, newAuthorities);
		addOrRemoveFromProgramsOfWhichInterviewerIfRequired(selectedUser, selectedProgram, newAuthorities);
		addOrRemoveFromProgramsOfWhichSupervisorIfRequired(selectedUser, selectedProgram, newAuthorities);
		userDAO.save(selectedUser);

	}

	private void addOrRemoveFromProgramsOfWhichSupervisorIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
		if (newAuthoritiesContains(newAuthorities, Authority.SUPERVISOR) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichSupervisor())) {
			selectedUser.getProgramsOfWhichSupervisor().add(selectedProgram);			
		} else if (!newAuthoritiesContains(newAuthorities, Authority.SUPERVISOR) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichSupervisor())) {
			selectedUser.getProgramsOfWhichSupervisor().remove(selectedProgram);
		}
	}


	private void addOrRemoveFromProgramsOfWhichAdministratorIfRequired(RegisteredUser selectedUser, Program selectedProgram, Authority[] newAuthorities) {
		if (newAuthoritiesContains(newAuthorities, Authority.ADMINISTRATOR) && !listContainsId(selectedProgram, selectedUser.getProgramsOfWhichAdministrator())) {
			selectedUser.getProgramsOfWhichAdministrator().add(selectedProgram);			
		} else if (!newAuthoritiesContains(newAuthorities, Authority.ADMINISTRATOR) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichAdministrator())) {
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
		} else if (!newAuthoritiesContains(newAuthorities, Authority.INTERVIEWER) && listContainsId(selectedProgram, selectedUser.getProgramsOfWhichInterviewer())) {
			selectedUser.getProgramsOfWhichInterviewer().remove(selectedProgram);
		}
	}

	private void addToRoleIfRequired(RegisteredUser selectedUser, Authority[] newAuthorities, Authority authority) {
		if (!selectedUser.isInRole(authority) && newAuthoritiesContains(newAuthorities, authority)) {
			selectedUser.getRoles().add(roleDAO.getRoleByAuthority(authority));			
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
	
	@Transactional
	public RegisteredUser createNewUserInRole(String firstName, String lastName, String email, Authority authority, DirectURLsEnum directURL, ApplicationForm application)  {
		RegisteredUser newUser = userDAO.getUserByEmail(email);
		if (newUser != null) {
			throw new IllegalStateException(String.format("user with email: %s already exists!", email));
		}
		newUser = userFactory.createNewUserInRoles(firstName, lastName, email, authority);
		setDirectURLAndSaveUser(directURL, application, newUser);
		return newUser;
	}

	public void setDirectURLAndSaveUser(DirectURLsEnum directURL,
			ApplicationForm application, RegisteredUser newUser) {
		if(directURL != null && application != null){
			newUser.setDirectToUrl(directURL.displayValue() + application.getApplicationNumber());
		}
		userDAO.save(newUser);
	}

	@Transactional
	public RegisteredUser createNewUserForProgramme(String firstName, String lastName, String email, Program program, Authority... authorities) {
		RegisteredUser newUser = userDAO.getUserByEmail(email);
		if (newUser != null) {
			throw new IllegalStateException(String.format("user with email: %s already exists!", email));
		}
		newUser = userFactory.createNewUserInRoles(firstName, lastName, email, authorities);
		if (Arrays.asList(authorities).contains(Authority.SUPERADMINISTRATOR)) {
			addPendingRoleNotificationToUser(newUser, Authority.SUPERADMINISTRATOR, null);
		}
		if (Arrays.asList(authorities).contains(Authority.REVIEWER)) {
			newUser.getProgramsOfWhichReviewer().add(program);
			addPendingRoleNotificationToUser(newUser, Authority.REVIEWER, program);
		}
		if (Arrays.asList(authorities).contains(Authority.ADMINISTRATOR)) {
			newUser.getProgramsOfWhichAdministrator().add(program);
			addPendingRoleNotificationToUser(newUser, Authority.ADMINISTRATOR, program);
		}
		if (Arrays.asList(authorities).contains(Authority.APPROVER)) {
			newUser.getProgramsOfWhichApprover().add(program);
			addPendingRoleNotificationToUser(newUser, Authority.APPROVER, program);
		}
		if (Arrays.asList(authorities).contains(Authority.INTERVIEWER)) {
			newUser.getProgramsOfWhichInterviewer().add(program);
			addPendingRoleNotificationToUser(newUser, Authority.INTERVIEWER, program);
		}
		if (Arrays.asList(authorities).contains(Authority.SUPERVISOR)) {
			newUser.getProgramsOfWhichSupervisor().add(program);
			addPendingRoleNotificationToUser(newUser, Authority.SUPERVISOR, program);
		}
		userDAO.save(newUser);
		return newUser;
	}

	@Transactional
	public List<RegisteredUser> getAllPreviousInterviewersOfProgram(Program program) {
		return userDAO.getAllPreviousInterviewersOfProgram(program);
	}

	@Transactional
	public List<RegisteredUser> getAllPreviousReviewersOfProgram(Program program) {
		return userDAO.getAllPreviousReviewersOfProgram(program);
	}

	@Transactional
	public List<RegisteredUser> getAllPreviousSupervisorsOfProgram(
			Program program) {
		return userDAO.getAllPreviousSupervisorsOfProgram(program);
	}

	@Transactional
	public List<RegisteredUser> getReviewersWillingToInterview(ApplicationForm applicationForm) {
		return userDAO.getReviewersWillingToInterview(applicationForm);
	}

	@Transactional
	public void updateCurrentUser(RegisteredUser user) {
		RegisteredUser currentUser = getCurrentUser();
		currentUser.setFirstName(user.getFirstName());
		currentUser.setLastName(user.getLastName());
		currentUser.setEmail(user.getEmail());
		if (StringUtils.isNotBlank(user.getNewPassword())) {
			currentUser.setPassword(encryptionUtils.getMD5Hash(user.getNewPassword()));
		}
		currentUser.setUsername(user.getEmail());
		save(currentUser);
	}

	@Transactional
	public void resetPassword(String email) {
		RegisteredUser storedUser = userDAO.getUserByEmail(email);
		if (storedUser == null) { // user-mail not found -> ignore
			log.info("reset password request failed, e-mail not found: " + email);
			return;
		}
		try {
			String newPassword = encryptionUtils.generateUserPassword();
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("user", storedUser);
			model.put("newPassword", newPassword);
			model.put("host", Environment.getInstance().getApplicationHostName());
			InternetAddress toAddress = createUserAddress(storedUser);
			String subject = msgSource.getMessage("user.password.reset", null, null);

			mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,//
					"private/mail/new_password_confirmation.ftl", model, null));

			String hashPassword = encryptionUtils.getMD5Hash(newPassword);
			storedUser.setPassword(hashPassword);
			userDAO.save(storedUser);
		} catch (Exception e) {
			log.warn("error while sending email", e);
		}
	}
	
    @Transactional
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
    
    @Transactional
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
 
	private InternetAddress createUserAddress(RegisteredUser user) {
		try {
			return new InternetAddress(user.getEmail(), user.getFirstName() + " " + user.getLastName());
		} catch (UnsupportedEncodingException e) { // this shouldn't happen...
			throw new RuntimeException(e);
		}
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
}
