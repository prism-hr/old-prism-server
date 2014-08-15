package com.zuehlke.pgadmissions.domain;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.rest.validation.annotation.ESAPIConstraint;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "USER_ACCOUNT")
public class UserAccount {

    @Id
    @GeneratedValue
    private Integer id;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "temporary_password")
    private String temporaryPassword;

    @Column(name = "temporary_password_expiry_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime temporaryPasswordExpiryTimestamp;

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

    public Map<Scope, Filter> getFilters() {
        return filters;
    }

    public UserAccount withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserAccount withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
