package com.zuehlke.pgadmissions.domain.user;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "USER")
public class User implements UserDetails, IUniqueEntity {

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

    @Column(name = "locale", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;

    @OneToOne
    @JoinColumn(name = "portrait_document_id")
    private Document portraitDocument;

    @Column(name = "linkedin_uri")
    private String linkedinUri;

    @Column(name = "twitter_uri")
    private String twitterUri;

    @Column(name = "activation_code", nullable = false, unique = true)
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
    @JoinColumn(name = "latest_creation_scope_id")
    private Scope latestCreationScope;

    @ManyToOne
    @JoinColumn(name = "parent_user_id")
    private User parentUser;

    @OneToMany(mappedBy = "user")
    private Set<UserRole> userRoles = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<Document> documents = Sets.newHashSet();

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

    public final String getFullName() {
        return fullName;
    }

    public final void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public final PrismLocale getLocale() {
        return locale;
    }

    public final void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    public final Document getPortraitDocument() {
        return portraitDocument;
    }

    public final void setPortraitDocument(Document portraitDocument) {
        this.portraitDocument = portraitDocument;
    }

    public final String getLinkedinUri() {
        return linkedinUri;
    }

    public final void setLinkedinUri(String linkedinUri) {
        this.linkedinUri = linkedinUri;
    }

    public final String getTwitterUri() {
        return twitterUri;
    }

    public final void setTwitterUri(String twitterUri) {
        this.twitterUri = twitterUri;
    }

    public final void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
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

    public final LocalDate getLastNotifiedDateSystem() {
        return lastNotifiedDateSystem;
    }

    public final void setLastNotifiedDateSystem(LocalDate lastNotifiedDateSystem) {
        this.lastNotifiedDateSystem = lastNotifiedDateSystem;
    }

    public final LocalDate getLastNotifiedDateInstitution() {
        return lastNotifiedDateInstitution;
    }

    public final void setLastNotifiedDateInstitution(LocalDate lastNotifiedDateInstitution) {
        this.lastNotifiedDateInstitution = lastNotifiedDateInstitution;
    }

    public final LocalDate getLastNotifiedDateProgram() {
        return lastNotifiedDateProgram;
    }

    public final void setLastNotifiedDateProgram(LocalDate lastNotifiedDateProgram) {
        this.lastNotifiedDateProgram = lastNotifiedDateProgram;
    }

    public final LocalDate getLastNotifiedDateProject() {
        return lastNotifiedDateProject;
    }

    public final void setLastNotifiedDateProject(LocalDate lastNotifiedDateProject) {
        this.lastNotifiedDateProject = lastNotifiedDateProject;
    }

    public final LocalDate getLastNotifiedDateApplication() {
        return lastNotifiedDateApplication;
    }

    public final void setLastNotifiedDateApplication(LocalDate lastNotifiedDateApplication) {
        this.lastNotifiedDateApplication = lastNotifiedDateApplication;
    }

    public final Scope getLatestCreationScope() {
        return latestCreationScope;
    }

    public final void setLatestCreationScope(Scope latestCreationScope) {
        this.latestCreationScope = latestCreationScope;
    }

    public final User getParentUser() {
        return parentUser;
    }

    public final void setParentUser(User parentUser) {
        this.parentUser = parentUser;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
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
        return (LocalDate) ReflectionUtils.getProperty(this, "lastNotifiedDate" + resourceClass.getSimpleName());
    }

    public <T extends Resource> void setLastNotifiedDate(Class<T> resourceClass, LocalDate lastNotifiedDate) {
        ReflectionUtils.setProperty(this, "lastNotifiedDate" + resourceClass.getSimpleName(), lastNotifiedDate);
    }
    
    public String getSearchEngineRepresentation() {
        return fullName + " " + email.replace("@", " ").replace(".", " ");
    }

    @Override
    public boolean isEnabled() {
        return userAccount != null && userAccount.getEnabled();
    }

    public String getIndexName() {
        return lastName + " " + firstName;
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
