package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.google.common.base.Objects;

@Entity(name = "APPLICATION_FORM_USER_ROLE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplicationFormUserRole implements UserRole {

    @EmbeddedId
    private ApplicationFormUserRolePrimaryKey id;

    @Column(name = "is_interested_in_applicant")
    private Boolean interestedInApplicant = false;

    @Column(name = "raises_urgent_flag")
    private Boolean raisesUrgentFlag = false;

    @Column(name = "assigned_timestamp", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date assignedTimestamp = new Date();
    
    @OneToMany (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(name = "applicationFrom", referencedColumnName = "applicationForm", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "user", referencedColumnName = "user", insertable = false, nullable = false, updatable = false),
        @JoinColumn(name = "role", referencedColumnName = "role", insertable = false, nullable = false, updatable = false) })
    private HashSet<ApplicationFormActionRequired> actions = new HashSet<ApplicationFormActionRequired>();

    public ApplicationFormUserRole() {
    }
    
    public ApplicationFormUserRole(ApplicationForm applicationForm, User user, Role role, Boolean interestedInApplicant,
            HashSet<ApplicationFormActionRequired> actions) {
        setId(applicationForm, user, role);
        this.interestedInApplicant = interestedInApplicant;
        this.actions = actions;
    }

    public ApplicationFormUserRolePrimaryKey getId() {
        return id;
    }

    public void setId(ApplicationForm applicationForm, User user, Role role) {
        id.setApplicationForm(applicationForm);
        id.setUser(user);
        id.setRole(role);
    }

    public Boolean getInterestedInApplicant() {
        return interestedInApplicant;
    }

    public Boolean isInterestedInApplicant() {
        return interestedInApplicant;
    }

    public void setInterestedInApplicant(Boolean isInterestedInApplicant) {
        this.interestedInApplicant = isInterestedInApplicant;
    }

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public Date getAssignedTimestamp() {
        return assignedTimestamp;
    }

    public void setAssignedTimestamp(Date assignedTimestamp) {
        this.assignedTimestamp = assignedTimestamp;
    }

    public HashSet<ApplicationFormActionRequired> getActions() {
        return actions;
    }

    public void setActions(HashSet<ApplicationFormActionRequired> actions) {
        this.actions.addAll(actions);
    }

    @Embeddable
    public static class ApplicationFormUserRolePrimaryKey implements Serializable {

        private static final long serialVersionUID = 662732181186688410L;

        @Column(name = "application_form_id")
        protected ApplicationForm applicationForm;

        @Column(name = "registered_user_id")
        protected User user;

        @Column(name = "application_role_id")
        protected Role role;

        public ApplicationFormUserRolePrimaryKey() {
        }

        public ApplicationFormUserRolePrimaryKey(ApplicationForm applicationForm, User user, Role role) {
            this.applicationForm = applicationForm;
            this.user = user;
            this.role = role;
        }

        public ApplicationForm getApplicationForm() {
            return applicationForm;
        }

        public void setApplicationForm(ApplicationForm applicationForm) {
            this.applicationForm = applicationForm;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(applicationForm, user, role);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ApplicationFormUserRolePrimaryKey other = (ApplicationFormUserRolePrimaryKey) obj;
            return Objects.equal(applicationForm.getId(), other.getApplicationForm().getId()) && Objects.equal(user.getId(), other.getUser().getId())
                    && Objects.equal(role.getId(), other.getRole().getId());
        }

    }

}
