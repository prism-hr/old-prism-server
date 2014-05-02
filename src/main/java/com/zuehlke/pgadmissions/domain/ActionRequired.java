package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

@Entity
@Table(name = "ACTION_REQUIRED")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ActionRequired {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "system_id")
    private PrismSystem system;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private ApplicationForm application;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @Column(name = "deadline_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate deadlineDate;

    @Column(name = "bind_deadline_to_due_date")
    private Boolean bindDeadlineToDueDate;

    @Column(name = "raises_urgent_flag")
    private Boolean raisesUrgentFlag = false;

    @Column(name = "assigned_timestamp", insertable = false, updatable = false, nullable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date assignedTimestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrismSystem getSystem() {
        return system;
    }

    public void setSystem(PrismSystem system) {
        this.system = system;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public LocalDate getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDate deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public Boolean getBindDeadlineToDueDate() {
        return bindDeadlineToDueDate;
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

    public void setAssignedTimestamp(Date assignedTimestamp) {
        this.assignedTimestamp = assignedTimestamp;
    }

    public ActionRequired withSystem(PrismSystem system) {
        this.system = system;
        return this;
    }

    public ActionRequired withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public ActionRequired withProgram(Program program) {
        this.program = program;
        return this;
    }

    public ActionRequired withProject(Project project) {
        this.project = project;
        return this;
    }

    public ActionRequired withApplication(ApplicationForm application) {
        this.application = application;
        return this;
    }


    public ActionRequired withRole(Role role) {
        this.role = role;
        return this;
    }

    public ActionRequired withAction(Action action) {
        this.action = action;
        return this;
    }
    
    public ActionRequired withDeadlineDate(LocalDate deadlineDate) {
        this.deadlineDate = deadlineDate;
        return this;
    }
    
    public ActionRequired withBindDeadlineToDueDate(Boolean bindDeadlineToDueDate) {
        this.bindDeadlineToDueDate = bindDeadlineToDueDate;
        return this;
    }
    
    public ActionRequired withRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
        return this;
    }
    
}
