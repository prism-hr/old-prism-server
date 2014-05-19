package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "USER_ROLE")
public class UserRole {

    @Id
    @GeneratedValue
    private Integer id;

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
    private Application application;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesting_user_id")
    private User requestingUser;

    @Column(name = "assigned_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime assignedTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
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

    public DateTime getAssignedTimestamp() {
        return assignedTimestamp;
    }

    public void setAssignedTimestamp(DateTime assignedTimestamp) {
        this.assignedTimestamp = assignedTimestamp;
    }

    public void setScope(PrismScope scope) {
        try {
            PropertyUtils.setProperty(this, scope.getScopeName(), scope);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public UserRole withApplication(Application application) {
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

    public UserRole withRequestingUser(User requestingUser) {
        this.requestingUser = requestingUser;
        return this;
    }

    public UserRole withAssignedTimestamp(DateTime assignedTimestamp) {
        this.assignedTimestamp = assignedTimestamp;
        return this;
    }
    
    public UserRole withScope(PrismScope scope) {
        this.setScope(scope);
        return this;
    }

}
