package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "USER_ACCOUNT")
public class UserAccount {

    @Id
    @GeneratedValue
    private Integer id;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String password;

    @Transient
    private String newPassword;

    @Transient
    private String confirmPassword;

    @JoinColumn(name = "application_filter_group_id")
    @OneToOne(fetch = FetchType.LAZY)
    private ApplicationsFiltering filtering;

    @Column(name = "application_list_last_access_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date applicationListLastAccessTimestamp;

    private boolean enabled;

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

    public ApplicationsFiltering getFiltering() {
        return filtering;
    }

    public void setFiltering(ApplicationsFiltering filtering) {
        this.filtering = filtering;
    }

    public Date getApplicationListLastAccessTimestamp() {
        return applicationListLastAccessTimestamp;
    }

    public void setApplicationListLastAccessTimestamp(Date applicationListLastAccessTimestamp) {
        this.applicationListLastAccessTimestamp = applicationListLastAccessTimestamp;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
