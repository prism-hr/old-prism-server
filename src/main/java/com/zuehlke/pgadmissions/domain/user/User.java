package com.zuehlke.pgadmissions.domain.user;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

@Entity
@Table(name = "USER")
public class User implements UserDetails, UniqueEntity {

	private static final long serialVersionUID = 5910410212695389060L;

	@Id
	@GeneratedValue
	private Integer id;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "first_name_2")
	private String firstName2;

	@Column(name = "first_name_3")
	private String firstName3;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Column(name = "full_name", nullable = false)
	private String fullName;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Lob
	@Column(name = "email_bounced_message")
	private String emailBouncedMessage;

	@Column(name = "locale", nullable = false)
	@Enumerated(EnumType.STRING)
	private PrismLocale locale;

	@OneToOne
	@JoinColumn(name = "portrait_document_id")
	private Document portraitDocument;

	@Column(name = "activation_code", unique = true)
	private String activationCode;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_account_id")
	private UserAccount userAccount;

	@Column(name = "last_notified_date_system")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	private LocalDate lastNotifiedDateSystem;

	@Column(name = "last_notified_date_institution")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	private LocalDate lastNotifiedDateInstitution;

	@Column(name = "last_notified_date_program")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	private LocalDate lastNotifiedDateProgram;

	@Column(name = "last_notified_date_project")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	private LocalDate lastNotifiedDateProject;

	@Column(name = "last_notified_date_application")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	private LocalDate lastNotifiedDateApplication;

	@ManyToOne
	@JoinColumn(name = "parent_user_id")
	private User parentUser;

	@OrderBy(clause = "last_name asc, first_name asc")
	@OneToMany(mappedBy = "parentUser")
	private Set<User> childUsers = Sets.newHashSet();

	@OneToMany(mappedBy = "user")
	private Set<UserRole> userRoles = Sets.newHashSet();

	@OneToMany(mappedBy = "user")
	private Set<Application> applications = Sets.newHashSet();

	@OneToMany(mappedBy = "user")
	private Set<UserInstitutionIdentity> institutionIdentities = Sets.newHashSet();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName2() {
		return firstName2;
	}

	public void setFirstName2(String firstName2) {
		this.firstName2 = firstName2;
	}

	public String getFirstName3() {
		return firstName3;
	}

	public void setFirstName3(String firstName3) {
		this.firstName3 = firstName3;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailBouncedMessage() {
		return emailBouncedMessage;
	}

	public void setEmailBouncedMessage(String emailBouncedMessage) {
		this.emailBouncedMessage = emailBouncedMessage;
	}

	public PrismLocale getLocale() {
		return locale;
	}

	public void setLocale(PrismLocale locale) {
		this.locale = locale;
	}

	public Document getPortraitDocument() {
		return portraitDocument;
	}

	public void setPortraitDocument(Document portraitDocument) {
		this.portraitDocument = portraitDocument;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount account) {
		this.userAccount = account;
	}

	public LocalDate getLastNotifiedDateSystem() {
		return lastNotifiedDateSystem;
	}

	public void setLastNotifiedDateSystem(LocalDate lastNotifiedDateSystem) {
		this.lastNotifiedDateSystem = lastNotifiedDateSystem;
	}

	public LocalDate getLastNotifiedDateInstitution() {
		return lastNotifiedDateInstitution;
	}

	public void setLastNotifiedDateInstitution(LocalDate lastNotifiedDateInstitution) {
		this.lastNotifiedDateInstitution = lastNotifiedDateInstitution;
	}

	public LocalDate getLastNotifiedDateProgram() {
		return lastNotifiedDateProgram;
	}

	public void setLastNotifiedDateProgram(LocalDate lastNotifiedDateProgram) {
		this.lastNotifiedDateProgram = lastNotifiedDateProgram;
	}

	public LocalDate getLastNotifiedDateProject() {
		return lastNotifiedDateProject;
	}

	public void setLastNotifiedDateProject(LocalDate lastNotifiedDateProject) {
		this.lastNotifiedDateProject = lastNotifiedDateProject;
	}

	public LocalDate getLastNotifiedDateApplication() {
		return lastNotifiedDateApplication;
	}

	public void setLastNotifiedDateApplication(LocalDate lastNotifiedDateApplication) {
		this.lastNotifiedDateApplication = lastNotifiedDateApplication;
	}

	public User getParentUser() {
		return parentUser;
	}

	public void setParentUser(User parentUser) {
		this.parentUser = parentUser;
	}

	public Set<User> getChildUsers() {
		return childUsers;
	}

	public Set<UserRole> getUserRoles() {
		return userRoles;
	}

	public Set<Application> getApplications() {
		return applications;
	}

	public Set<UserInstitutionIdentity> getInstitutionIdentities() {
		return institutionIdentities;
	}

	public User withId(Integer id) {
		this.id = id;
		return this;
	}

	public User withFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public User withFirstName2(String firstName2) {
		this.firstName2 = firstName2;
		return this;
	}

	public User withFirstName3(String firstName3) {
		this.firstName3 = firstName3;
		return this;
	}

	public User withLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public User withFullName(String fullName) {
		this.fullName = fullName;
		return this;
	}

	public User withEmail(String email) {
		this.email = email;
		return this;
	}

	public User withEmailBouncedMessage(final String emailBouncedMessage) {
		this.emailBouncedMessage = emailBouncedMessage;
		return this;
	}

	public User withLocale(PrismLocale locale) {
		this.locale = locale;
		return this;
	}

	public User withActivationCode(String activationCode) {
		this.activationCode = activationCode;
		return this;
	}

	public User withAccount(UserAccount account) {
		this.userAccount = account;
		return this;
	}

	public User withParentUser(User parentUser) {
		this.parentUser = parentUser;
		return this;
	}

	public <T extends Resource> LocalDate getLastNotifiedDate(Class<T> resourceClass) {
		return (LocalDate) PrismReflectionUtils.getProperty(this, "lastNotifiedDate" + resourceClass.getSimpleName());
	}

	public <T extends Resource> void setLastNotifiedDate(Class<T> resourceClass, LocalDate lastNotifiedDate) {
		PrismReflectionUtils.setProperty(this, "lastNotifiedDate" + resourceClass.getSimpleName(), lastNotifiedDate);
	}

	public String getSearchEngineRepresentation() {
		return fullName + " " + email.replace("@", " ").replace(".", " ");
	}

	@Override
	public boolean isEnabled() {
		return userAccount != null && userAccount.getEnabled();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public String getPassword() {
		return userAccount != null ? userAccount.getPassword() : null;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String toString() {
		return firstName + " " + lastName + " " + "(" + email + ")";
	}

	@Override
	public ResourceSignature getResourceSignature() {
		return new ResourceSignature().addProperty("email", email);
	}

}
