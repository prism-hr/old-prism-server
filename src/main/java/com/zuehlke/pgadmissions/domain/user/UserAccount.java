package com.zuehlke.pgadmissions.domain.user;

import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.ResourceListFilter;
import com.zuehlke.pgadmissions.domain.workflow.Scope;

@Entity
@Table(name = "user_account")
public class UserAccount {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "password")
    private String password;
    
    @OneToOne
    @JoinColumn(name = "user_account_external_id", unique = true)
    private UserAccountExternal primaryExternalAccount;

    @OneToOne
    @JoinColumn(name = "portrait_image_id")
    private Document portraitImage;
    
    @Column(name = "temporary_password")
    private String temporaryPassword;

    @Column(name = "temporary_password_expiry_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime temporaryPasswordExpiryTimestamp;

    @Column(name = "send_application_recommendation_notification", nullable = false)
    private Boolean sendApplicationRecommendationNotification;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToMany(mappedBy = "userAccount")
    @MapKeyJoinColumn(name = "scope_id")
    private Map<Scope, ResourceListFilter> filters = Maps.newHashMap();

    @OneToMany(mappedBy = "userAccount")
    private Set<UserAccountExternal> externalAccounts = Sets.newHashSet();

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserAccountExternal getPrimaryExternalAccount() {
        return primaryExternalAccount;
    }

    public void setPrimaryExternalAccount(UserAccountExternal externalAccount) {
        this.primaryExternalAccount = externalAccount;
    }

    public Document getPortraitImage() {
        return portraitImage;
    }

    public void setPortraitImage(Document portraitImage) {
        this.portraitImage = portraitImage;
    }

    public String getTemporaryPassword() {
        return temporaryPassword;
    }

    public void setTemporaryPassword(String temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }

    public DateTime getTemporaryPasswordExpiryTimestamp() {
        return temporaryPasswordExpiryTimestamp;
    }

    public void setTemporaryPasswordExpiryTimestamp(DateTime temporaryPasswordExpiryTimestamp) {
        this.temporaryPasswordExpiryTimestamp = temporaryPasswordExpiryTimestamp;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getSendApplicationRecommendationNotification() {
        return sendApplicationRecommendationNotification;
    }

    public void setSendApplicationRecommendationNotification(Boolean sendApplicationRecommendationNotification) {
        this.sendApplicationRecommendationNotification = sendApplicationRecommendationNotification;
    }

    public Map<Scope, ResourceListFilter> getFilters() {
        return filters;
    }

    public Set<UserAccountExternal> getExternalAccounts() {
        return externalAccounts;
    }

    public UserAccount withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserAccount withSendApplicationRecommendationNotification(Boolean sendApplicationRecommendationNotification) {
        this.sendApplicationRecommendationNotification = sendApplicationRecommendationNotification;
        return this;
    }

    public UserAccount withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

}
