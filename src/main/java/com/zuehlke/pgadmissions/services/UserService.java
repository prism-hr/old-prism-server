package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
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
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.utils.MimeMessagePreparatorFactory;

@Service("userService")
public class UserService {

	private final UserDAO userDAO;
	private final RoleDAO roleDAO;
	private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	private final JavaMailSender mailsender;
	private final Logger log = Logger.getLogger(UserService.class);
	
	UserService() {
		this(null, null, null, null);
	}

	@Autowired
	public UserService(UserDAO userDAO, RoleDAO roleDAO, MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender) {
		this.userDAO = userDAO;
		this.roleDAO = roleDAO;
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.mailsender = mailsender;
	}
	

	public List<RegisteredUser> getReviewersForApplication(ApplicationForm application){
		List<RegisteredUser> users = userDAO.getAllUsers();
		List<RegisteredUser> reviewers = new ArrayList<RegisteredUser>();
		for (RegisteredUser user : users) {
			if (user.isInRole(Authority.REVIEWER) && !application.getReviewers().contains(user)) {
				reviewers.add(user);
			}
		}
		
		return reviewers;
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


}
