package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity(name = "USER_ROLE")
public class UserRole {

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
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesting_user_id")
    private User requestingUser;

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

    public User getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(User requestingUser) {
        this.requestingUser = requestingUser;
    }

    public Date getAssignedTimestamp() {
        return assignedTimestamp;
    }

    public void setAssignedTimestamp(Date assignedTimestamp) {
        this.assignedTimestamp = assignedTimestamp;
    }

    public UserRole withSystem(PrismSystem system) {
        this.system = system;
        return this;
    }

    public UserRole withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public UserRole withProgram(Program program) {
        this.program = program;
        return this;
    }

    public UserRole withProject(Project project) {
        this.project = project;
        return this;
    }

    public UserRole withApplication(ApplicationForm application) {
        this.application = application;
        return this;
    }

    public UserRole withUser(User user) {
        this.user = user;
        return this;
    }

    public UserRole withRole(Role role) {
        this.role = role;
        return this;
    }

    public UserRole withRequestingUser(User user) {
        this.user = user;
        return this;
    }

}
