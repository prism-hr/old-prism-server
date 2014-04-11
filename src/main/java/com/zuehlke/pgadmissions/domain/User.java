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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
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
    @Column(name = "first_name")
    private String firstName;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    @Field(analyzer = @Analyzer(definition = "userAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    @Column(name = "first_name_2")
    private String firstName2;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    @Field(analyzer = @Analyzer(definition = "userAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    @Column(name = "first_name_3")
    private String firstName3;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 40)
    @Field(analyzer = @Analyzer(definition = "userAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    @Column(name = "last_name")
    private String lastName;

    @ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
    @Field(analyzer = @Analyzer(definition = "userAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String email;

    @Column(name = "advert_id")
    private Advert advert;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "action_id")
    @Enumerated(EnumType.STRING)
    private ApplicationFormAction action;

    @JoinColumn(name = "application_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ApplicationForm application;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @Valid
    private List<Comment> comments = new ArrayList<Comment>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<PendingRoleNotification> pendingRoleNotifications = new ArrayList<PendingRoleNotification>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "registered_user_id")
    private List<Referee> referees = new ArrayList<Referee>();

    @OneToMany(mappedBy = "parentUser")
    private List<User> linkedAccounts = new ArrayList<User>();

    @ManyToOne
    @JoinColumn(name = "parent_user_id")
    private User parentUser;

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

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public boolean isEnabled() {
        return account != null && account.isEnabled();
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public ApplicationFormAction getAction() {
        return action;
    }

    public void setAction(ApplicationFormAction action) {
        this.action = action;
    }

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    public List<Comment> getComments() {
        return comments;
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

    public User getParentUser() {
        return parentUser;
    }

    public void setParentUser(User parentUser) {
        this.parentUser = parentUser;
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
    
    @Override
    public String getUsername() {
        return email;
    }
}
