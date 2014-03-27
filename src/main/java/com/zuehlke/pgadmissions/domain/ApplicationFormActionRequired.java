package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.google.common.base.Objects;

@Entity(name = "APPLICATION_FORM_ACTION_REQUIRED")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplicationFormActionRequired implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    ApplicationFormActionRequiredPrimaryKey id;

    @Column(name = "deadline_timestamp")
    @Temporal(value = TemporalType.DATE)
    private Date deadlineTimestamp;

    @Column(name = "bind_deadline_to_due_date")
    private Boolean bindDeadlineToDueDate = false;

    @Column(name = "raises_urgent_flag")
    private Boolean raisesUrgentFlag = false;

    @Column(name = "assigned_timestamp", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date assignedTimestamp = new Date();

    public ApplicationFormActionRequired() {
    }

    public ApplicationFormActionRequired(ApplicationForm applicationForm, RegisteredUser user, Role role, Action action, Date deadlineTimestamp,
            Boolean bindDeadlineToDueDate, Boolean raisesUrgentFlag) {
        setId(applicationForm, user, role, action);
        this.deadlineTimestamp = deadlineTimestamp;
        this.bindDeadlineToDueDate = bindDeadlineToDueDate;
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public ApplicationFormActionRequiredPrimaryKey getId() {
        return id;
    }

    public void setId(ApplicationForm applicationForm, RegisteredUser user, Role role, Action action) {
        this.id.setApplicationFormUserRolePrimaryKey(applicationForm, user, role);
        this.id.setAction(action);
    }

    public Date getDeadlineTimestamp() {
        return deadlineTimestamp;
    }

    public void setDeadlineTimestamp(Date deadlineTimestamp) {
        this.deadlineTimestamp = deadlineTimestamp;
    }

    public Boolean getBindDeadlineToDueDate() {
        return this.bindDeadlineToDueDate;
    }

    public void setBindDeadlineToDueDate(Boolean bindDeadlineToDueDate) {
        this.bindDeadlineToDueDate = bindDeadlineToDueDate;
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

    @Embeddable
    public static class ApplicationFormActionRequiredPrimaryKey implements Serializable {

        private static final long serialVersionUID = -2595787410331680123L;

        @EmbeddedId
        protected ApplicationFormUserRole.ApplicationFormUserRolePrimaryKey applicationFormUserRolePrimaryKey;

        @Column(name = "action_id")
        protected Action action;

        public ApplicationFormActionRequiredPrimaryKey() {
        }

        public ApplicationFormActionRequiredPrimaryKey(ApplicationForm applicationForm, RegisteredUser user, Role role, Action action) {
            setApplicationFormUserRolePrimaryKey(applicationForm, user, role);
            this.action = action;
        }

        public ApplicationFormUserRole.ApplicationFormUserRolePrimaryKey getApplicationFormUserRolePrimaryKey() {
            return applicationFormUserRolePrimaryKey;
        }

        public void setApplicationFormUserRolePrimaryKey(ApplicationForm applicationForm, RegisteredUser user, Role role) {
            this.applicationFormUserRolePrimaryKey.applicationForm = applicationForm;
            this.applicationFormUserRolePrimaryKey.user = user;
            this.applicationFormUserRolePrimaryKey.role = role;
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(applicationFormUserRolePrimaryKey.applicationForm, applicationFormUserRolePrimaryKey.role,
                    applicationFormUserRolePrimaryKey.role, action);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ApplicationFormActionRequiredPrimaryKey other = (ApplicationFormActionRequiredPrimaryKey) obj;
            return applicationFormUserRolePrimaryKey.equals(other) && Objects.equal(action.getId(), other.getAction().getId());
        }

    }

}
