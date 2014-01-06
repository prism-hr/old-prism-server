package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.springframework.security.core.userdetails.UserDetails;

import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityGroup;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "REGISTERED_USER")
@Indexed
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RegisteredUser implements UserDetails,
		Comparable<RegisteredUser>, Serializable {

	private static final long serialVersionUID = 7913035836949510857L;

	@Id
	@GeneratedValue
	private Integer id;

	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	private String firstName;

	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	private String firstName2;

	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	private String firstName3;

	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 40)
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	private String lastName;

	@ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	private String email;

	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
	private String username;

	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
	private String password;

	@Transient
	private String newPassword;

	@Transient
	private String confirmPassword;

	@Transient
	private Referee currentReferee;

	@Column(name = "original_querystring")
	private String originalApplicationQueryString;

	@JoinColumn(name = "filtering_id")
	@OneToOne(fetch = FetchType.LAZY)
	private ApplicationsFiltering filtering;

	@Column(name = "upi")
	private String upi;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<ApplicationFormUserRole> applicationFormUserRoles;

	private boolean enabled;

	private boolean accountNonExpired;

	private boolean accountNonLocked;

	private boolean credentialsNonExpired;

	private String activationCode;

	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = {
			javax.persistence.CascadeType.PERSIST,
			javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "user_id")
	@Valid
	private List<Comment> comments = new ArrayList<Comment>();

	@Column(name = "direct_to_url")
	private String directToUrl;

	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = {
			javax.persistence.CascadeType.PERSIST,
			javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "user_id")
	private List<PendingRoleNotification> pendingRoleNotifications = new ArrayList<PendingRoleNotification>();

	@OneToMany(fetch = FetchType.LAZY, cascade = {
			javax.persistence.CascadeType.PERSIST,
			javax.persistence.CascadeType.REMOVE })
	@JoinColumn(name = "registered_user_id")
	private List<Referee> referees = new ArrayList<Referee>();

	@OneToMany(mappedBy = "primaryAccount", fetch = FetchType.LAZY)
	private List<RegisteredUser> linkedAccounts = new ArrayList<RegisteredUser>();

	@ManyToOne
	@JoinColumn(name = "primary_account_id", nullable = true)
	private RegisteredUser primaryAccount;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "USER_ROLE", joinColumns = { @JoinColumn(name = "registered_user_id") }, inverseJoinColumns = { @JoinColumn(name = "application_role_id") })
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private List<Role> roles = new ArrayList<Role>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "PROGRAM_ADMINISTRATOR_LINK", joinColumns = { @JoinColumn(name = "administrator_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@OrderBy("title")
	private List<Program> programsOfWhichAdministrator = new ArrayList<Program>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "PROGRAM_APPROVER_LINK", joinColumns = { @JoinColumn(name = "registered_user_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@OrderBy("title")
	private List<Program> programsOfWhichApprover = new ArrayList<Program>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "PROGRAM_VIEWER_LINK", joinColumns = { @JoinColumn(name = "viewer_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@OrderBy("title")
	private List<Program> programsOfWhichViewer = new ArrayList<Program>();

	@Column(name = "ucl_user_id")
	private String uclUserId;

	@Column(name = "latest_task_notification_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date latestTaskNotificationDate;
	
	@Column(name = "latest_update_notification_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date latestUpdateNotificationDate;

	@Column(name = "application_list_last_access_timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date applicationListLastAccessTimestamp;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<ResearchOpportunitiesFeed> researchOpportunitiesFeeds = new ArrayList<ResearchOpportunitiesFeed>();

	@Override
	public int compareTo(final RegisteredUser other) {
		int firstNameResult = this.firstName.compareTo(other.firstName);
		if (firstNameResult == 0) {
			return this.lastName.compareTo(other.lastName);
		}
		return firstNameResult;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public List<RegisteredUser> getAllLinkedAccounts() {
		List<RegisteredUser> linkedAccountsList = new ArrayList<RegisteredUser>();
		if (this.primaryAccount == null) {
			linkedAccountsList.add(this);
			linkedAccountsList.addAll(getLinkedAccounts());
		} else {
			linkedAccountsList.add(getPrimaryAccount());
			for (RegisteredUser u : getPrimaryAccount().getLinkedAccounts()) {
				linkedAccountsList.add(u);
			}
		}
		return linkedAccountsList;
	}

	@Override
	@Transient
	public Collection<Role> getAuthorities() {
		return getRoles();
	}
	
	public List<Authority> getAuthoritiesForSystem() {
		List<Authority> foundRoles = new ArrayList<Authority>();
		for (Authority role : AuthorityGroup.SYSTEM.authorities()) {
			if (isInSystemRole(role)) {
				foundRoles.add(role);
			}
		}
		return foundRoles;
	}
	
	public List<Authority> getAuthoritiesForProgram(final Program program) {
		List<Authority> rolesFound = new ArrayList<Authority>();
		List<Program> programsToSearch = new ArrayList<Program>();
		for (Authority authority : AuthorityGroup.PROGRAM.authorities()) {
			switch (authority) {
				case ADMINISTRATOR:
					programsToSearch = getProgramsOfWhichAdministrator();
					break;
				case APPROVER:
					programsToSearch = getProgramsOfWhichApprover();
					break;
				case VIEWER:
					programsToSearch = getProgramsOfWhichViewer();
					break;
				default: break;
			}
			if (programsToSearch.contains(program)) {
				rolesFound.add(authority);
			}
		}
		return rolesFound;
	}
	
	public List<Authority> getAuthoritiesForProject(final Project project) {
		List<Authority> rolesFound = new ArrayList<Authority>();
		List<Authority> rolesToSearch = Arrays.asList(AuthorityGroup.PROJECT.authorities());
		for (Authority authority : rolesToSearch) {
			switch (authority) {
				case PROJECTADMINISTRATOR:
					if (this.equals(project.getAdministrator()) || this.equals(project.getPrimarySupervisor())) {
						rolesFound.add(authority);
					}
					break;
				case PROJECTAUTHOR:
					if (this.equals(project.getAuthor())) {
						rolesFound.add(authority);
					}
					break;
				default: break;
			}
			if (rolesFound.containsAll(rolesToSearch)) {
				break;
			}
		}
		return rolesFound;
	}
	
	public List<Authority> getAuthoritiesForApplication(final ApplicationForm application) {
		List<Authority> rolesFound = new ArrayList<Authority>();
		List<Authority> rolesToSearch = Arrays.asList(AuthorityGroup.APPLICATION.authorities());
		for (ApplicationFormUserRole role : applicationFormUserRoles) {
			Authority authority = role.getRole().getId();
			if (rolesToSearch.contains(application)) {
				rolesFound.add(authority);
			}
		}
		return rolesFound;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public Referee getCurrentReferee() {
		return currentReferee;
	}

	public String getDirectToUrl() {
		return directToUrl;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getFirstName2() {
		return firstName2;
	}

	public String getFirstName3() {
		return firstName3;
	}

	public String getDisplayName() {
		return firstName + " " + lastName;
	}

	public Integer getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public List<RegisteredUser> getLinkedAccounts() {
		return linkedAccounts;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getOriginalApplicationQueryString() {
		return originalApplicationQueryString;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public List<PendingRoleNotification> getPendingRoleNotifications() {
		return pendingRoleNotifications;
	}

	public RegisteredUser getPrimaryAccount() {
		return primaryAccount;
	}

	public List<Program> getProgramsOfWhichAdministrator() {
		return programsOfWhichAdministrator;
	}

	public List<Program> getProgramsOfWhichApprover() {
		return programsOfWhichApprover;
	}

	public List<Program> getProgramsOfWhichViewer() {
		return programsOfWhichViewer;
	}

	public List<Referee> getReferees() {
		return referees;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public String getUclUserId() {
		return uclUserId;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setAccountNonExpired(final boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public void setAccountNonLocked(final boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public void setConfirmPassword(final String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public void setCredentialsNonExpired(final boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public void setCurrentReferee(final Referee currentReferee) {
		this.currentReferee = currentReferee;
	}

	public void setDirectToUrl(final String directToUrl) {
		this.directToUrl = directToUrl;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public void setFirstName(final String firstLame) {
		this.firstName = firstLame;
	}

	public void setFirstName2(final String firstName2) {
		this.firstName2 = firstName2;
	}

	public void setFirstName3(final String firstName3) {
		this.firstName3 = firstName3;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public void setLinkedAccounts(final List<RegisteredUser> linkedAccounts) {
		this.linkedAccounts = linkedAccounts;
	}

	public void setNewPassword(final String newPassword) {
		this.newPassword = newPassword;
	}

	public void setOriginalApplicationQueryString(final String queryString) {
		this.originalApplicationQueryString = queryString;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setPendingRoleNotifications(final List<PendingRoleNotification> pendingRoleNotifications) {
		this.pendingRoleNotifications.clear();
		this.pendingRoleNotifications.addAll(pendingRoleNotifications);
	}

	public void setPrimaryAccount(final RegisteredUser primary) {
		this.primaryAccount = primary;
	}

	public void setProgramsOfWhichAdministrator(final List<Program> programsOfWhichAdministrator) {
		this.programsOfWhichAdministrator = programsOfWhichAdministrator;
	}

	public void setProgramsOfWhichApprover(final List<Program> programsOfWhichApprover) {
		this.programsOfWhichApprover = programsOfWhichApprover;
	}

	public void setProgramsOfWhichViewer(final List<Program> programsOfWhichViewer) {
		this.programsOfWhichViewer = programsOfWhichViewer;
	}

	public void setReferees(final List<Referee> referees) {
		this.referees = referees;
	}

	public void setUclUserId(final String uclUserId) {
		this.uclUserId = uclUserId;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public ApplicationsFiltering getFiltering() {
		return filtering;
	}

	public void setFiltering(ApplicationsFiltering filtering) {
		this.filtering = filtering;
	}

	public String getUpi() {
		return upi;
	}

	public void setUpi(final String upi) {
		this.upi = upi;
	}

	public List<ResearchOpportunitiesFeed> getResearchOpportunitiesFeeds() {
		return researchOpportunitiesFeeds;
	}

	public void setResearchOpportunitiesFeeds(List<ResearchOpportunitiesFeed> researchOpportunitiesFeeds) {
		this.researchOpportunitiesFeeds = researchOpportunitiesFeeds;
	}

	public Date getLatestTaskNotificationDate() {
		return latestTaskNotificationDate;
	}

	public void setLatestTaskNotificationDate(Date latestTaskNotificationDate) {
		this.latestTaskNotificationDate = latestTaskNotificationDate;
	}	
	
	public Date getLatestUpdateNotificationDate() {
		return latestUpdateNotificationDate;
	}

	public void setLatestUpdateNotificationDate(Date latestUpdateNotificationDate) {
		this.latestUpdateNotificationDate = latestUpdateNotificationDate;
	}	

	public Date getApplicationListLastAccessTimestamp() {
		return applicationListLastAccessTimestamp;
	}

	public void setApplicationListLastAccessTimestamp(Date applicationListLastAccessTimestamp) {
		this.applicationListLastAccessTimestamp = applicationListLastAccessTimestamp;
	}
	
	public List<ApplicationFormUserRole> getApplicationFormUserRoles() {
		return applicationFormUserRoles;
	}
	
	public void setApplicationFormUserRoles(List<ApplicationFormUserRole> applicationFormUserRoles) {
		this.applicationFormUserRoles = applicationFormUserRoles;
	}
	
    public boolean isInSystemRole(Authority... authorities) {
    	return hasAuthorityInAuthorityGroup(getAuthoritiesForSystem());
    }
    
    public boolean isInProgramRole(final Program program, final Authority... authorities) {
    	if (isInSystemRole(authorities)) {
    		return true;
    	}
    	return hasAuthorityInAuthorityGroup(getAuthoritiesForProgram(program), authorities);
    }
    
    public boolean isInProjectRole(final Project project, final Authority... authorities) {
    	if (isInProgramRole(project.getProgram(), authorities)) {
    		return true;
    	}
    	return hasAuthorityInAuthorityGroup(getAuthoritiesForProject(project), authorities);
    }
    
    public boolean isInApplicationRole(final ApplicationForm application, final Authority... authorities) {
    	if (isInProgramRole(application.getProgram(), authorities)) {
    		return true;
    	}
    	Project project = application.getProject();
    	if (project != null) {
    		if (isInProjectRole(project, authorities)) {
    			return true;
    		}
    	}
    	return hasAuthorityInAuthorityGroup(getAuthoritiesForApplication(application), authorities);
    }
    
    public void removeRole(Authority... authorities) {
    	List<Authority> rolesToRemove = Arrays.asList(authorities);
    	List<Role> rolesToSearch = getRoles();
    	for (Role role : rolesToSearch) {
    		Authority authority = role.getId();
    		if (rolesToRemove.contains(authority)) {
    			rolesToRemove.remove(authority);
    			rolesToSearch.remove(role);
    		}
    		if (rolesToRemove.isEmpty()) {
    			break;
    		}
    	}
    }
    
    private boolean hasAuthorityInAuthorityGroup (List<Authority> roles, Authority... authorities) {
    	for (Authority authority : authorities) {
    		if (roles.contains(authority)) {
    			return true;
    		}
    	}
    	return false;
    }
    
}