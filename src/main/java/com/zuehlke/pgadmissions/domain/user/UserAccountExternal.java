package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.definitions.OauthProvider;

@Entity
@Table(name = "USER_ACCOUNT_EXTERNAL", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_account_id", "external_account_type"}),
        @UniqueConstraint(columnNames = {"external_account_type", "external_account_identifier"})})
public class UserAccountExternal {

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

}
