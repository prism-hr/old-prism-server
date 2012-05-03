package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;

public class RegisteredUserBuilder {
	private String firstName;

	private String lastName;
	private String email;
	private String username;
	private String password;
	private String confirmPassword;
	private Integer id;
	private boolean enabled = true;
	private boolean accountNonExpired = true;
	private boolean accountNonLocked = true;
	private boolean credentialsNonExpired = true;
	private String activationCode;
	private Program programOriginallyAppliedTo;
	private List<Referee> referees = new ArrayList<Referee>();
	private List<Comment> comments = new ArrayList<Comment>();

	private List<Role> roles = new ArrayList<Role>();	
	private List<Program> programsOfWhichAdministrator = new ArrayList<Program>();
	private List<Program> programsOfWhichApprover = new ArrayList<Program>();		
	private List<Program> programsOfWhichReviewer = new ArrayList<Program>();
	
	private List<NotificationRecord> notificationRecords = new ArrayList<NotificationRecord>();
	
	
	public RegisteredUserBuilder notificationRecords(NotificationRecord...notifications) {
		this.notificationRecords.addAll(Arrays.asList(notifications));
		return this;
	} 
	
	public RegisteredUserBuilder programsOfWhichAdministrator(Program...programs) {
		this.programsOfWhichAdministrator.addAll(Arrays.asList(programs));
		return this;
	} 
	
	public RegisteredUserBuilder programsOfWhichApprover(Program...programs) {
		this.programsOfWhichApprover.addAll(Arrays.asList(programs));
		return this;
	} 
	
	public RegisteredUserBuilder programsOfWhichReviewer(Program...programs) {
		this.programsOfWhichReviewer.addAll(Arrays.asList(programs));
		return this;
	} 
	
	public RegisteredUserBuilder referees(Referee...referees) {
		this.referees.addAll(Arrays.asList(referees));
		return this;
	} 
	
	
	public RegisteredUserBuilder comments(Comment...comments) {
		this.comments.addAll(Arrays.asList(comments));
		return this;
	} 
	
	public RegisteredUserBuilder programOriginallyAppliedTo(Program programOriginallyAppliedTo) {
		this.programOriginallyAppliedTo = programOriginallyAppliedTo;
		return this;
	}
	
	public RegisteredUserBuilder role(Role role) {
		this.roles.add(role);
		return this;
	}

	public RegisteredUserBuilder roles(Role... roles) {
		for (Role role : roles) {
			this.roles.add(role);
		}
		return this;
	}
	
	public RegisteredUserBuilder email(String email) {
		this.email = email;
		return this;
	}
	
	public RegisteredUserBuilder lastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	
	public RegisteredUserBuilder activationCode(String activationCode) {
		this.activationCode = activationCode;
		return this;
	}

	public RegisteredUserBuilder firstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public RegisteredUserBuilder username(String username) {
		this.username = username;
		return this;
	}
	
	
	public RegisteredUserBuilder password(String password) {
		this.password = password;
		return this;
	}
	
	public RegisteredUserBuilder confirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
		return this;
	}

	public RegisteredUserBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public RegisteredUserBuilder enabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public RegisteredUserBuilder accountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
		return this;
	}

	public RegisteredUserBuilder accountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
		return this;
	}

	public RegisteredUserBuilder credentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
		return this;
	}
	
	public RegisteredUser toUser() {
		RegisteredUser user = new RegisteredUser();
		user.setId(id);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		user.setUsername(username);
		user.setPassword(password);
		user.setEnabled(enabled);
		user.setAccountNonExpired(accountNonExpired);
		user.setAccountNonLocked(accountNonLocked);
		user.setCredentialsNonExpired(credentialsNonExpired);
		user.setActivationCode(activationCode);
		user.getRoles().addAll(roles);
		user.setProgramOriginallyAppliedTo(programOriginallyAppliedTo);
		user.setProgramsOfWhichAdministrator(programsOfWhichAdministrator);
		user.setProgramsOfWhichApprover(programsOfWhichApprover);
		user.setProgramsOfWhichReviewer(programsOfWhichReviewer);
		user.setConfirmPassword(confirmPassword);
		user.setReferees(referees);
		user.setComments(comments);
		user.setNotificationRecords(notificationRecords);
		return user;
	}

}
