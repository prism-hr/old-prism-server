package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = "USER_ROLE")
public class UserRole implements IUniqueResource {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "system_id")
    private System system;

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

    @Column(name = "assigned_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime assignedTimestamp;

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

    public UserRole withScope(PrismResource resource) {
        this.setResource(resource);
        return this;
    }
    
    public UserRole withResourceSignature(PrismResource prismResource, User user, Role role) {
        setResource(prismResource);
        this.user = Preconditions.checkNotNull(user);
        this.role = Preconditions.checkNotNull(role);
        return this;
    }

    public PrismResource getResource() {
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

    public void setResource(PrismResource resource) {
        try {
            PropertyUtils.setSimpleProperty(this, resource.getClass().getSimpleName().toLowerCase(), resource);
        } catch (Exception e) {
            throw new Error("Tried to assign user role to invalid prism resource", e);
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
