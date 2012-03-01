package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.dto.QualificationDTO;

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
	private String address;

	private List<Qualification> qualifications = new ArrayList<Qualification>();
	
	@OneToMany
	@JoinTable(name = "USER_ROLE_LINK", joinColumns = { @JoinColumn(name = "REGISTERED_USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "APPLICATION_ROLE_ID") })
	private List<Role> roles = new ArrayList<Role>();
	
	public List<Role> getRoles() {
		return roles;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "APPLICATION_FORM_QUALIFICATION", joinColumns = { @JoinColumn(name = "applicant_id") }, inverseJoinColumns = { @JoinColumn(name = "application_form_id") })
	@Access(AccessType.PROPERTY)
	public List<Qualification> getQualifications() {
		return qualifications;
	}
	
	public void setQualifications(List<Qualification> qualifications) {	
		for (Qualification qualification : qualifications) {
			Assert.notNull(qualification.getDegree());
		}
		if(this.qualifications.size() == qualifications.size() && this.qualifications.containsAll(qualifications)){
			return;
		}
		this.qualifications.clear();
		this.qualifications.addAll(qualifications);
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
		
		if(isInRole(Authority.ADMINISTRATOR)){
			return true;
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

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public boolean hasQualifications(){
		return !qualifications.isEmpty();
	}


}
