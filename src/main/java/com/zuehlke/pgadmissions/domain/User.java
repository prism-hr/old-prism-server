package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@AnalyzerDef(name = "userAnalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = { @TokenFilterDef(factory = LowerCaseFilterFactory.class) })
@Indexed
@Entity(name = "USER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User implements UserDetails, Comparable<User>, Serializable {

    private static final long serialVersionUID = 7913035836949510857L;

    @Id
    @GeneratedValue
    private Integer id;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    @Field(analyzer = @Analyzer(definition = "userAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String firstName;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    @Field(analyzer = @Analyzer(definition = "userAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String firstName2;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    @Field(analyzer = @Analyzer(definition = "userAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String firstName3;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 40)
    @Field(analyzer = @Analyzer(definition = "userAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String lastName;

    @ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
    @Field(analyzer = @Analyzer(definition = "userAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String email;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
    private String username;

    @Column(name = "advert_id")
    private Advert advert;

    @Column(name = "upi")
    private String upi;

    private boolean enabled;

    @Column(name = "activation_code")
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
    private List<User> linkedAccounts = new ArrayList<User>();

    @ManyToOne
    @JoinColumn(name = "primary_account_id")
    private User primaryAccount;

    @Column(name = "ucl_user_id")
    private String uclUserId;

    @Column(name = "latest_task_notification_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date latestTaskNotificationDate;

    @Column(name = "latest_update_notification_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date latestUpdateNotificationDate;

    @Column(name = "latest_opportunity_request_notification_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date latestOpportunityRequestNotificationDate;

    @OneToMany(mappedBy = "user")
    private Set<UserRole> userRoles;

    @OneToMany(mappedBy = "user")
    private List<ResearchOpportunitiesFeed> researchOpportunitiesFeeds = new ArrayList<ResearchOpportunitiesFeed>();

    @JoinColumn(name = "user_account_id")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAccount account;

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

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
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

    public List<User> getLinkedAccounts() {
        return linkedAccounts;
    }

    public User getPrimaryAccount() {
        return primaryAccount;
    }

    public void setPrimaryAccount(User primaryAccount) {
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

    public Date getLatestOpportunityRequestNotificationDate() {
        return latestOpportunityRequestNotificationDate;
    }

    public void setLatestOpportunityRequestNotificationDate(Date latestOpportunityRequestNotificationDate) {
        this.latestOpportunityRequestNotificationDate = latestOpportunityRequestNotificationDate;
    }

    public List<ResearchOpportunitiesFeed> getResearchOpportunitiesFeeds() {
        return researchOpportunitiesFeeds;
    }

    public UserAccount getAccount() {
        return account;
    }

    public void setAccount(UserAccount account) {
        this.account = account;
    }

    public String getDisplayName() {
        return firstName + " " + lastName;
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
    public int compareTo(final User other) {
        int firstNameResult = this.firstName.compareTo(other.firstName);
        if (firstNameResult == 0) {
            return this.lastName.compareTo(other.lastName);
        }
        return firstNameResult;
    }

    @Override
    public String getPassword() {
        return account != null ? account.getPassword() : null;
    }
}
