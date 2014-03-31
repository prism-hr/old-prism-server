package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@AnalyzerDef(name = "registeredUserAnalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = { @TokenFilterDef(factory = LowerCaseFilterFactory.class) })
@Indexed
@Entity(name = "REGISTERED_USER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RegisteredUser implements Comparable<RegisteredUser>, Serializable {

    private static final long serialVersionUID = 7913035836949510857L;

    @Id
    @GeneratedValue
    private Integer id;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    @Field(analyzer = @Analyzer(definition = "registeredUserAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String firstName;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    @Field(analyzer = @Analyzer(definition = "registeredUserAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String firstName2;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    @Field(analyzer = @Analyzer(definition = "registeredUserAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String firstName3;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 40)
    @Field(analyzer = @Analyzer(definition = "registeredUserAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String lastName;

    @ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
    @Field(analyzer = @Analyzer(definition = "registeredUserAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String email;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
    private String username;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String password;

    @Transient
    private String newPassword;

    @Transient
    private String confirmPassword;

    @Column(name = "original_querystring")
    private String originalApplicationQueryString;

    @JoinColumn(name = "filtering_id")
    @OneToOne(fetch = FetchType.LAZY)
    private ApplicationsFiltering filtering;

    @Column(name = "upi")
    private String upi;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getOriginalApplicationQueryString() {
        return originalApplicationQueryString;
    }

    public void setOriginalApplicationQueryString(String originalApplicationQueryString) {
        this.originalApplicationQueryString = originalApplicationQueryString;
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

    public void setUpi(String upi) {
        this.upi = upi;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getDirectToUrl() {
        return directToUrl;
    }

    public void setDirectToUrl(String directToUrl) {
        this.directToUrl = directToUrl;
    }

    public List<PendingRoleNotification> getPendingRoleNotifications() {
        return pendingRoleNotifications;
    }

    public List<Referee> getReferees() {
        return referees;
    }

    public List<RegisteredUser> getLinkedAccounts() {
        return linkedAccounts;
    }

    public RegisteredUser getPrimaryAccount() {
        return primaryAccount;
    }

    public void setPrimaryAccount(RegisteredUser primaryAccount) {
        this.primaryAccount = primaryAccount;
    }

    public String getUclUserId() {
        return uclUserId;
    }

    public void setUclUserId(String uclUserId) {
        this.uclUserId = uclUserId;
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

    public List<ResearchOpportunitiesFeed> getResearchOpportunitiesFeeds() {
        return researchOpportunitiesFeeds;
    }

    public String getDisplayName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public int compareTo(final RegisteredUser other) {
        int firstNameResult = this.firstName.compareTo(other.firstName);
        if (firstNameResult == 0) {
            return this.lastName.compareTo(other.lastName);
        }
        return firstNameResult;
    }
}
