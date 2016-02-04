package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.OauthProvider;

@Entity
@Table(name = "USER_ACCOUNT_EXTERNAL", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_account_id", "external_account_type" }) })
public class UserAccountExternal implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "external_account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OauthProvider accountType;

    @Column(name = "external_account_identifier", nullable = false)
    private String accountIdentifier;

    @Lob
    @Column(name = "external_account_profile_url")
    private String accountProfileUrl;

    @Lob
    @Column(name = "external_account_image_url")
    private String accountImageUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public OauthProvider getAccountType() {
        return accountType;
    }

    public void setAccountType(OauthProvider accountType) {
        this.accountType = accountType;
    }

    public String getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(String accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

    public final String getAccountProfileUrl() {
        return accountProfileUrl;
    }

    public final void setAccountProfileUrl(String accountProfileUrl) {
        this.accountProfileUrl = accountProfileUrl;
    }

    public final String getAccountImageUrl() {
        return accountImageUrl;
    }

    public final void setAccountImageUrl(String accountImageUrl) {
        this.accountImageUrl = accountImageUrl;
    }

    public UserAccountExternal withUserAccount(final UserAccount userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public UserAccountExternal withAccountType(final OauthProvider accountType) {
        this.accountType = accountType;
        return this;
    }

    public UserAccountExternal withAccountIdentifier(final String accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
        return this;
    }

    public UserAccountExternal withAccountProfileUrl(String accountProfileUrl) {
        this.accountProfileUrl = accountProfileUrl;
        return this;
    }

    public UserAccountExternal withAccountImageUrl(String accountImageUrl) {
        this.accountImageUrl = accountImageUrl;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("userAccount", userAccount).addProperty("accountType", accountType);
    }

}
