package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "APPLICATION_FORM_ACTION_REQUIRED")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplicationFormActionRequired implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "action_id")
    private String action;

    @Column(name = "deadline_timestamp")
    @Temporal(value = TemporalType.DATE)
    private Date deadlineTimestamp;

    @Column(name = "bind_deadline_to_due_date")
    private Boolean bindDeadlineToDueDate = false;

    public ApplicationFormActionRequired() {
    }

    public ApplicationFormActionRequired(String action, Date deadlineTimestamp, Boolean bindDeadlineToDueDate) {
        this.action = action;
        this.deadlineTimestamp = deadlineTimestamp;
        this.bindDeadlineToDueDate = bindDeadlineToDueDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getDeadlineTimestamp() {
        return deadlineTimestamp;
    }

    public void setDeadlineTimestamp(Date deadlineTimestamp) {
        this.deadlineTimestamp = deadlineTimestamp;
    }

    public Boolean getBindDeadlineToDueDate() {
        return bindDeadlineToDueDate;
    }

    public void setBindDeadlineToDueDate(Boolean bindDeadlineToDueDate) {
        this.bindDeadlineToDueDate = bindDeadlineToDueDate;
    }

}
