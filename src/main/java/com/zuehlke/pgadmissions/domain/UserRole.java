package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Entity
@Table(name = "USER_ROLE", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "user_id", "role_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "user_id", "role_id" }), @UniqueConstraint(columnNames = { "program_id", "user_id", "role_id" }),
        @UniqueConstraint(columnNames = { "project_id", "user_id", "role_id" }), @UniqueConstraint(columnNames = { "application_id", "user_id", "role_id" }) })
public class UserRole implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id")
    private System system;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "assigned_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime assignedTimestamp;
    
    @OneToMany(mappedBy = "userRole")
    private Set<UserNotification> userNotifications = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
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

    public DateTime getAssignedTimestamp() {
        return assignedTimestamp;
    }

    public void setAssignedTimestamp(DateTime assignedTimestamp) {
        this.assignedTimestamp = assignedTimestamp;
    }

    public Set<UserNotification> getUserNotifications() {
        return userNotifications;
    }

    public UserRole withSystem(System system) {
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
    
    public UserRole withResource(Resource resource) {
        setResource(resource);
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

    public UserRole withAssignedTimestamp(DateTime assignedTimestamp) {
        this.assignedTimestamp = assignedTimestamp;
        return this;
    }

    public Resource getResource() {
        if (system != null) {
            return system;
        } else if (institution != null) {
            return institution;
        } else if (program != null) {
            return program;
        } else if (project != null) {
            return project;
        }
        return application;
    }

    public void setResource(Resource resource) {
        this.system = null;
        this.institution = null;
        this.program = null;
        this.project = null;
        this.application = null;
        try {
            PropertyUtils.setSimpleProperty(this, resource.getClass().getSimpleName().toLowerCase(), resource);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        if (system != null) {
            properties.put("system", system);
        } else if (institution != null) {
            properties.put("institution", institution);
        } else if (program != null) {
            properties.put("program", program);
        } else if (project != null) {
            properties.put("program", project);
        } else if (application != null) {
            properties.put("application", application);
        }
        properties.put("user", user);
        properties.put("role", role);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }

}
