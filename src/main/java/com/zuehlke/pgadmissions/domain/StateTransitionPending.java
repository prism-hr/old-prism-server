package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = "STATE_TRANSITION_PENDING", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "state_transition_id" }),
        @UniqueConstraint(columnNames = { "program_id", "state_transition_id" }), @UniqueConstraint(columnNames = { "project_id", "state_transition_id" }),
        @UniqueConstraint(columnNames = { "application_id", "state_transition_id" }) })
public class StateTransitionPending implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "system_id")
    private Institution system;

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
    @JoinColumn(name = "state_transition_id", nullable = false)
    private StateTransition stateTransition;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Institution getSystem() {
        return system;
    }

    public void setSystem(Institution system) {
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

    public StateTransition getStateTransition() {
        return stateTransition;
    }

    public void setStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
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
            PropertyUtils.setProperty(this, resource.getClass().getSimpleName().toLowerCase(), resource);
        } catch (Exception e) {
            new Error(e);
        }
    }

    public StateTransitionPending withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public StateTransitionPending withStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
        return this;
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
        properties.put("stateTransition", stateTransition);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }
    
}