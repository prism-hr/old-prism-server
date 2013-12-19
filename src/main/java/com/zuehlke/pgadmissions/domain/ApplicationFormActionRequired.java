package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;

@Entity(name = "APPLICATION_FORM_ACTION_REQUIRED")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplicationFormActionRequired implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "action_id")
    @Enumerated(EnumType.STRING)
    private ApplicationFormAction action;

    @Column(name = "deadline_timestamp")
    @Temporal(value = TemporalType.DATE)
    private Date deadlineTimestamp;

    @Column(name = "bind_deadline_to_due_date")
    private Boolean bindDeadlineToDueDate = false;

    @Column(name = "raises_urgent_flag")
    private Boolean raisesUrgentFlag = false;
    
    @Column(name = "assigned_timestamp")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date assignedTimestamp = new Date();

    @ManyToOne
    @JoinColumn(name = "application_form_user_role_id", nullable = false, updatable = false, insertable = false)
    private ApplicationFormUserRole applicationFormUserRole;

    public ApplicationFormActionRequired() {
    }

    public ApplicationFormActionRequired(ApplicationFormAction action, Date deadlineTimestamp, Boolean bindDeadlineToDueDate, Boolean raisesUrgentFlag) {
        this.action = action;
        this.deadlineTimestamp = deadlineTimestamp;
        this.bindDeadlineToDueDate = bindDeadlineToDueDate;
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ApplicationFormAction getAction() {
        return action;
    }

    public void setAction(ApplicationFormAction action) {
        this.action = action;
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

    public ApplicationFormUserRole getApplicationFormUserRole() {
        return applicationFormUserRole;
    }

    public void setApplicationFormUserRole(ApplicationFormUserRole applicationFormUserRole) {
        this.applicationFormUserRole = applicationFormUserRole;
    }

}