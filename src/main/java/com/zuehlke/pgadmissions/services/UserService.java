package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service("userService")
public class UserService {

	private final UserDAO userDAO;
	private final RoleDAO roleDAO;
	
	UserService() {
		this(null, null);
	}

	@Autowired
	public UserService(UserDAO userDAO, RoleDAO roleDAO) {
		this.userDAO = userDAO;
		this.roleDAO = roleDAO;
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

	

	public Role getRoleById(int id) {
		return userDAO.getRoleById(id);
		
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


}
