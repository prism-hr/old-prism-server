package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.NewRolesDTO;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
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
	
	UserService() {
		this(null, null, null, null, null);
	}

	@Autowired
	public UserService(UserDAO userDAO, RoleDAO roleDAO, UserFactory userFactory, MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender) {
		this.userDAO = userDAO;
		this.roleDAO = roleDAO;
		this.userFactory = userFactory;
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.mailsender = mailsender;
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

	
	public List<RegisteredUser> getAllUsers(){
		return userDAO.getAllUsers();
	}
	
	
	public RegisteredUser getUserByUsername(String username) {
		return userDAO.getUserByUsername(username);
	}


	public List<RegisteredUser> getSuperAdmins() {
		return getUsersInRole(Authority.SUPERADMINISTRATOR);
	}

	public List<RegisteredUser> getAllUsersForProgram(Program program) {
		return userDAO.getUsersForProgram(program);
	}
	
	public List<RegisteredUser> getAllInternalUsers() {
		List<RegisteredUser> availableUsers = new ArrayList<RegisteredUser>();
		availableUsers.addAll(getUsersInRole(Authority.ADMINISTRATOR));
		List<RegisteredUser> approvers = getUsersInRole(Authority.APPROVER);
		for (RegisteredUser approver : approvers) {
			if(!availableUsers.contains(approver)){
				availableUsers.add(approver);
			}
		}
		List<RegisteredUser> reviewers = getUsersInRole(Authority.REVIEWER);
		for (RegisteredUser reviewer : reviewers) {
			if(!availableUsers.contains(reviewer)){
				availableUsers.add(reviewer);
			}
		}
		List<RegisteredUser> superadmins = getUsersInRole(Authority.SUPERADMINISTRATOR);
		for (RegisteredUser superadmin : superadmins) {
			if(!availableUsers.contains(superadmin)){
				availableUsers.add(superadmin);
			}
		}
		return availableUsers;
	}

	
	public RegisteredUser getUserByEmail(String email) {
		return userDAO.getUserByEmail(email);
		
	}
	
	public RegisteredUser getUserByEmailIncludingDisabledAccounts(String email) {
		return userDAO.getUserByEmailIncludingDisabledAccounts(email);
		
	}

	@Transactional 
	public void saveAndEmailRegisterConfirmationToReferee(RegisteredUser referee) {
		save(referee);
		sendMailToReferee(referee);
	}

	private void sendMailToReferee(RegisteredUser referee) {
		try {
			RegisteredUser applicant = referee.getCurrentReferee().getApplication().getApplicant();
			List<RegisteredUser> administrators = referee.getCurrentReferee().getApplication().getProgram().getAdministrators();
			String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("referee", referee);
			model.put("applicant", applicant);			
			model.put("adminsEmails", adminsEmails);
			model.put("host", Environment.getInstance().getApplicationHostName());
			InternetAddress toAddress = new InternetAddress(referee.getEmail(), referee.getFirstName() + " " + referee.getLastName());
			mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Referee Registration",
					"private/referees/mail/register_referee_confirmation.ftl", model));
		} catch (Throwable e) {
			log.warn("error while sending email", e);
		}

		
	}
	
	private String getAdminsEmailsCommaSeparatedAsString(List<RegisteredUser> administrators) {
		StringBuilder adminsMails = new StringBuilder();
		for (RegisteredUser admin : administrators) {
			adminsMails.append(admin.getEmail());
			adminsMails.append(", ");
		}
		String result = adminsMails.toString();
		if (!result.isEmpty()) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	
	@Transactional
	public RegisteredUser getCurrentUser() {
		RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		return userDAO.get(currentUser.getId());
	}

	public RegisteredUser createNewUser(String firstname, String lastname, String email) {
		RegisteredUser user = new RegisteredUser();
		user.setFirstName(firstname);
		user.setLastName(lastname);
		user.setUsername(email);
		user.setEmail(email);
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setEnabled(false);
		user.setCredentialsNonExpired(true);
		return user;
	}
	
	public void addRoleToUser (RegisteredUser user, Authority authority){
		user.getRoles().add(roleDAO.getRoleByAuthority(authority));
	}

	@Transactional
	public void updateUserWithNewRoles(RegisteredUser selectedUser, Program selectedProgram, NewRolesDTO newRolesDTO) {
		if (getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			removeFromSuperadminRoleIfRequired(selectedUser, newRolesDTO);
		}
		for (Authority authority : Authority.values()) {
			addToRoleIfRequired(selectedUser, newRolesDTO, authority);
		}

		addOrRemoveFromProgramsOfWhichAdministratorIfRequired(selectedUser, selectedProgram, newRolesDTO);
		addOrRemoveFromProgramsOfWhichApproverIfRequired(selectedUser, selectedProgram, newRolesDTO);
		addOrRemoveFromProgramsOfWhichReviewerIfRequired(selectedUser, selectedProgram, newRolesDTO);
		addOrRemoveFromProgramsOfWhichInterviewerIfRequired(selectedUser, selectedProgram, newRolesDTO);
		userDAO.save(selectedUser);
		
	}
	private void removeFromSuperadminRoleIfRequired(RegisteredUser selectedUser, NewRolesDTO newRolesDTO) {
		if (getRole(newRolesDTO, Authority.SUPERADMINISTRATOR) == null) {
			Role superAdminRole = selectedUser.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
			selectedUser.getRoles().remove(superAdminRole);
		}
	}

	private void addOrRemoveFromProgramsOfWhichAdministratorIfRequired(RegisteredUser selectedUser, Program selectedProgram, NewRolesDTO newRolesDTO) {
		if (getRole(newRolesDTO, Authority.ADMINISTRATOR) != null && !selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichAdministrator().add(selectedProgram);
		} else if (getRole(newRolesDTO, Authority.ADMINISTRATOR) == null && selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichAdministrator().remove(selectedProgram);
		}

	}

	private void addOrRemoveFromProgramsOfWhichApproverIfRequired(RegisteredUser selectedUser, Program selectedProgram, NewRolesDTO newRolesDTO) {
		if (getRole(newRolesDTO, Authority.APPROVER) != null && !selectedUser.getProgramsOfWhichApprover().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichApprover().add(selectedProgram);
		} else if (getRole(newRolesDTO, Authority.APPROVER) == null && selectedUser.getProgramsOfWhichApprover().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichApprover().remove(selectedProgram);
		}
	}

	private void addOrRemoveFromProgramsOfWhichReviewerIfRequired(RegisteredUser selectedUser, Program selectedProgram, NewRolesDTO newRolesDTO) {
		if (getRole(newRolesDTO, Authority.REVIEWER) != null && !selectedUser.getProgramsOfWhichReviewer().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichReviewer().add(selectedProgram);
		} else if (getRole(newRolesDTO, Authority.REVIEWER) == null && selectedUser.getProgramsOfWhichReviewer().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichReviewer().remove(selectedProgram);
		}
	}

	private void addOrRemoveFromProgramsOfWhichInterviewerIfRequired(RegisteredUser selectedUser, Program selectedProgram, NewRolesDTO newRolesDTO) {
		
		if (getRole(newRolesDTO, Authority.INTERVIEWER) != null && !selectedUser.getProgramsOfWhichInterviewer().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichInterviewer().add(selectedProgram);
		} else if (getRole(newRolesDTO, Authority.INTERVIEWER) == null && selectedUser.getProgramsOfWhichInterviewer().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichInterviewer().remove(selectedProgram);
		}
	}

	private void addToRoleIfRequired(RegisteredUser selectedUser, NewRolesDTO newRolesDTO, Authority authority) {
		if (!selectedUser.isInRole(authority) && getRole(newRolesDTO, authority) != null) {
			selectedUser.getRoles().add(getRole(newRolesDTO, authority));
		}
	}

	private Role getRole(NewRolesDTO newRolesDTO, Authority authority) {
		
		for (Role role : newRolesDTO.getNewRoles()) {
			if (role.getAuthorityEnum() == authority) {
				return role;
			}
		}
		return null;
	}

	@Transactional
	public RegisteredUser createNewUserForProgramme(String firstName, String lastName, String email, Program program,  Authority...authorities) {
	RegisteredUser newUser = userDAO.getUserByEmail(email);
		if (newUser != null) {
			throw new IllegalStateException(String.format("user with email: %s already exists!", email));
		}
		newUser = userFactory.createNewUserInRoles(firstName, lastName, email, authorities);
		if(Arrays.asList(authorities).contains(Authority.REVIEWER)){
			newUser.getProgramsOfWhichReviewer().add(program);
		}
		if(Arrays.asList(authorities).contains(Authority.ADMINISTRATOR)){
			newUser.getProgramsOfWhichAdministrator().add(program);
		}
		if(Arrays.asList(authorities).contains(Authority.APPROVER)){
			newUser.getProgramsOfWhichApprover().add(program);
		}
		if(Arrays.asList(authorities).contains(Authority.INTERVIEWER)){
			newUser.getProgramsOfWhichInterviewer().add(program);
		}
		userDAO.save(newUser);
		return newUser;
	
	}

}
