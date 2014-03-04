package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.springframework.security.core.userdetails.UserDetails;

import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "REGISTERED_USER")
@Indexed
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RegisteredUser extends Authorisable implements UserDetails, Comparable<RegisteredUser>, Serializable {

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

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @Valid
    private List<Comment> comments = new ArrayList<Comment>();

    @Column(name = "direct_to_url")
    private String directToUrl;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<PendingRoleNotification> pendingRoleNotifications = new ArrayList<PendingRoleNotification>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "registered_user_id")
    private List<Referee> referees = new ArrayList<Referee>();

    @OneToMany(mappedBy = "primaryAccount")
    private List<RegisteredUser> linkedAccounts = new ArrayList<RegisteredUser>();

    @ManyToOne
    @JoinColumn(name = "primary_account_id")
    private RegisteredUser primaryAccount;

    @ManyToMany
    @JoinTable(name = "USER_ROLE_LINK", joinColumns = { @JoinColumn(name = "REGISTERED_USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "APPLICATION_ROLE_ID") })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Role> roles = new ArrayList<Role>();

    @ManyToMany
    @JoinTable(name = "PROGRAM_ADMINISTRATOR_LINK", joinColumns = { @JoinColumn(name = "administrator_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Program> programsOfWhichAdministrator = new ArrayList<Program>();

    @ManyToMany
    @JoinTable(name = "PROGRAM_APPROVER_LINK", joinColumns = { @JoinColumn(name = "registered_user_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Program> programsOfWhichApprover = new ArrayList<Program>();

    @ManyToMany
    @JoinTable(name = "PROGRAM_VIEWER_LINK", joinColumns = { @JoinColumn(name = "viewer_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Program> programsOfWhichViewer = new ArrayList<Program>();

    @ManyToMany
    @JoinTable(name = "INSTITUTION_ADMINISTRATOR_LINK", joinColumns = { @JoinColumn(name = "administrator_id"), }, inverseJoinColumns = { @JoinColumn(name = "institution_id") })
    private List<QualificationInstitution> institutions = new ArrayList<QualificationInstitution>();

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

    @Column(name = "latest_opportunity_request_notification_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date latestOpportunityRequestNotificationDate;

    @OneToMany(mappedBy = "user")
    private List<ResearchOpportunitiesFeed> researchOpportunitiesFeeds = new ArrayList<ResearchOpportunitiesFeed>();

    public boolean canEditAsApplicant(ApplicationForm applicationForm) {
        return canEditApplicationAsApplicant(applicationForm, this);
    }


    public boolean canSeeReference(final ReferenceComment reference) {
        return canSeeReference(reference, this);
    }

    public boolean canSeeRestrictedInformation(final ApplicationForm form) {
        return canSeeRestrictedInformation(form, this);
    }

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

    public List<Authority> getAuthoritiesForProgram(final Program programme) {
        return getAuthoritiesForProgramme(programme, this);
    }

    public String getAuthoritiesForProgramAsString(final Program programme) {
        List<Authority> authoritiesForProgram = getAuthoritiesForProgram(programme);
        StringBuffer stringBuffer = new StringBuffer();
        if (isInRole(Authority.SUPERADMINISTRATOR)) {
            stringBuffer.append("Superadministrator");
        }
        for (Authority authority : authoritiesForProgram) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(StringUtils.capitalize(authority.toString().toLowerCase()));
        }
        return stringBuffer.toString();
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
        Collections.sort(programsOfWhichAdministrator, new Comparator<Program>() {
            @Override
            public int compare(Program o1, Program o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
        return programsOfWhichAdministrator;
    }

    public List<Program> getProgramsOfWhichApprover() {
        return programsOfWhichApprover;
    }

    public List<Program> getProgramsOfWhichViewer() {
        return programsOfWhichViewer;
    }

    public List<QualificationInstitution> getInstitutions() {
        return institutions;
    }

    public Referee getRefereeForApplicationForm(final ApplicationForm form) {
        for (Referee referee : referees) {
            if (referee.getApplication() != null && referee.getApplication().getId().equals(form.getId()) && !referee.isDeclined()) {
                return referee;
            }
        }
        return null;
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

    public boolean hasAdminRightsOnApplication(final ApplicationForm form) {
        return hasAdminRightsOnApplication(form, this);
    }

    public boolean hasRefereesInApplicationForm(final ApplicationForm form) {
        return getRefereeForApplicationForm(form) != null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public boolean isAdminInProgramme(final Program programme) {
        return isAdminInProgramme(programme, this);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isInRole(final Authority authority) {
        return isInRole(this, authority);
    }

    public boolean isInRole(final String strAuthority) {
        return isInRole(this, strAuthority);
    }

    public boolean isNotInRole(final Authority authority) {
        return !isInRole(this, authority);
    }

    public boolean isNotInRole(final String strAuthority) {
        return !isInRole(this, strAuthority);
    }

    public boolean isInRoleInProgram(final Authority authority, final Program programme) {
        return isInRoleInProgramme(programme, this, authority);
    }

    public boolean isInRoleInProgram(final String strAuthority, final Program programme) {
        return isInRoleInProgramme(programme, this, strAuthority);
    }

    public boolean isNotInRoleInProgram(final Authority authority, Program program) {
        return !isInRoleInProgramme(program, this, authority);
    }

    public boolean isNotInRoleInProgram(final String strAuthority, final Program programme) {
        return !isInRoleInProgramme(programme, this, strAuthority);
    }

    public boolean isApproverInProgram(final Program programme) {
        return isApproverInProgramme(programme, this);
    }

    public boolean isApplicant(final ApplicationForm form) {
        return isApplicant(form, this);
    }

    public boolean isRefereeOfApplicationForm(final ApplicationForm form) {
        return isRefereeOfApplication(form, this);
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

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }

    public void setOriginalApplicationQueryString(final String queryString) {
        this.originalApplicationQueryString = queryString;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setPrimaryAccount(final RegisteredUser primary) {
        this.primaryAccount = primary;
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

    @Override
    public String toString() {
        return String.format("RegisteredUser [id=%s, firstName=%s, lastName=%s, email=%s, enabled=%s]", id, firstName, lastName, email, enabled);
    }

    public void removeRole(final Authority authority) {
        CollectionUtils.filter(roles, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((Role) object).getId() != authority;
            }
        });
    }

    public List<ResearchOpportunitiesFeed> getResearchOpportunitiesFeeds() {
        return researchOpportunitiesFeeds;
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

    public Date getLatestOpportunityRequestNotificationDate() {
        return latestOpportunityRequestNotificationDate;
    }

    public void setLatestOpportunityRequestNotificationDate(Date latestOpportunityRequestNotificationDate) {
        this.latestOpportunityRequestNotificationDate = latestOpportunityRequestNotificationDate;
    }

    public List<ApplicationFormUserRole> getApplicationFormUserRoles() {
        return applicationFormUserRoles;
    }

}