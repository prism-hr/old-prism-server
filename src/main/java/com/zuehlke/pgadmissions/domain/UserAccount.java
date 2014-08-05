package com.zuehlke.pgadmissions.domain;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "USER_ACCOUNT")
public class UserAccount {

    @Id
    @GeneratedValue
    private Integer id;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    @Column(name = "password", nullable = false)
    private String password;

    @Transient
    private String newPassword;

    @Transient
    private String confirmPassword;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_account_id", nullable = false)
    @MapKeyColumn(name = "scope_id", nullable = false)
    private Map<Scope, Filter> filters = Maps.newHashMap();

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<Scope, Filter> getFilters() {
        return filters;
    }

    public UserAccount withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserAccount withNewPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

    public UserAccount withConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        return this;
    }

    public UserAccount withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
