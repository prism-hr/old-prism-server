package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
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

    public ApplicationFormActionRequired(Action action, Date deadlineTimestamp, Boolean bindDeadlineToDueDate, Boolean raisesUrgentFlag) {
        this.setId(action);
        this.deadlineTimestamp = deadlineTimestamp;
        this.bindDeadlineToDueDate = bindDeadlineToDueDate;
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public ApplicationFormActionRequiredPrimaryKey getId() {
        return id;
    }

    public void setId(Action action) {
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

        @ManyToOne
        @JoinColumns({
                @JoinColumn(name = "applicationFrom", referencedColumnName = "applicationForm", nullable = false, insertable = false, updatable = false),
                @JoinColumn(name = "user", referencedColumnName = "user", insertable = false, nullable = false, updatable = false),
                @JoinColumn(name = "role", referencedColumnName = "role", insertable = false, nullable = false, updatable = false) })
        protected ApplicationFormUserRole applicationFormUserRole;

        @Column(name = "action_id")
        protected Action action;

        public ApplicationFormActionRequiredPrimaryKey() {
        }

        public ApplicationFormActionRequiredPrimaryKey(ApplicationFormUserRole applicationFormUserRole, Action action) {
            this.applicationFormUserRole = applicationFormUserRole;
            this.action = action;
        }

        public ApplicationFormUserRole getApplicationFormUserRole() {
            return applicationFormUserRole;
        }

        public void setApplicationFormUserRole(ApplicationFormUserRole applicationFormUserRole) {
            this.applicationFormUserRole = applicationFormUserRole;
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(applicationFormUserRole, action);
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
            return applicationFormUserRole.getId().equals(other.getApplicationFormUserRole().getId())
                    && Objects.equal(action.getId(), other.getAction().getId());
        }

    }

}
