package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.springframework.security.core.userdetails.UserDetails;

import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

@Entity(name = "REGISTERED_USER")
@Access(AccessType.FIELD)
public class RegisteredUser extends DomainObject<Integer> implements UserDetails {

	private static final long serialVersionUID = 7913035836949510857L;
	private String firstName;

	private String lastName;
	private String email;
	private String username;
	private String password;
	private boolean enabled;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private String activationCode;
	
	
	@ManyToOne
	@JoinColumn(name = "originally_project_id")
	private Project projectOriginallyAppliedTo;

	@OneToMany
	@JoinTable(name = "USER_ROLE_LINK", joinColumns = { @JoinColumn(name = "REGISTERED_USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "APPLICATION_ROLE_ID") })
	private List<Role> roles = new ArrayList<Role>();

	@Transient
	private String rolesList = "";

	public String getRolesList() {
		return rolesList;
	}

	public void setRolesList() {
		StringBuilder roles = new StringBuilder();
		for (Role role: this.roles) {
			roles.append(role.getAuthority());
			roles.append(",");
		}

		String result = roles.toString();
		if (!result.isEmpty()) {
			result = result.substring(0, result.length()-1);
		}
		this.rolesList = result;
	}

	public List<Role> getRoles() {
		return roles;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstLame) {
		this.firstName = firstLame;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	@Transient
	public Collection<Role> getAuthorities() {
		return getRoles();
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {

		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;

	}

	public boolean isInRole(Authority authority) {
		for (Role role : roles) {
			if (role.getAuthorityEnum() == authority) {
				return true;
			}
		}
		return false;
	}

	public boolean isInRole(String strAuthority) {
		try {
			return isInRole(Authority.valueOf(strAuthority));
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public boolean canSee(ApplicationForm applicationForm) {

		if (applicationForm.getSubmissionStatus() == SubmissionStatus.UNSUBMITTED &&
				!isInRole(Authority.APPLICANT)) {
			return false;
		}

		if(isInRole(Authority.SUPERADMINISTRATOR)){
			return true;
		}

		if (isInRole(Authority.ADMINISTRATOR)) {
			return applicationForm.getProject().getProgram().getAdministrators().contains(this);
		}

		if (isInRole(Authority.REVIEWER)) {
			return applicationForm.getReviewers().contains(this);
		}

		if (isInRole(Authority.APPROVER)) {
			return applicationForm.getProject().getProgram().isApprover(this);
		}

		if(this.equals(applicationForm.getApplicant())){
			return true;
		}
		return false;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public void setRolesList(String rolesList) {
		this.rolesList = rolesList;
	}

	public Project getProjectOriginallyAppliedTo() {
		return projectOriginallyAppliedTo;
	}

	public void setProjectOriginallyAppliedTo(Project projectOriginallyAppliedTo) {
		this.projectOriginallyAppliedTo = projectOriginallyAppliedTo;
	}
}
