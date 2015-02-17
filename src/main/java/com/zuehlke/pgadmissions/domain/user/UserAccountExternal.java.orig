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

import com.zuehlke.pgadmissions.domain.definitions.AuthenticationProvider;

@Entity
@Table(name = "USER_ACCOUNT_EXTERNAL", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_account_id", "external_account_type" }),
        @UniqueConstraint(columnNames = { "external_account_type", "external_account_identifier" }) })
public class UserAccountExternal {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "external_account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthenticationProvider accountType;

    @Column(name = "external_account_identifier", nullable = false)
    private String accountIdentifier;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final UserAccount getUserAccount() {
        return userAccount;
    }

    public final void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public final AuthenticationProvider getAccountType() {
        return accountType;
    }

    public final void setAccountType(AuthenticationProvider accountType) {
        this.accountType = accountType;
    }

    public final String getAccountIdentifier() {
        return accountIdentifier;
    }

    public final void setAccountIdentifier(String accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

}
