package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.beanutils.PropertyUtils;

@Entity
@Table(name = "state_duration", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "state_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "state_id" }), @UniqueConstraint(columnNames = { "program_id", "state_id" }) })
public class StateDuration {

    @Id
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
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(name = "day_duration", nullable = false)
    private Integer duration;

    public StateDuration() {
    }
    
    public StateDuration(Resource resource, State state, Integer duration) {
        setResource(resource);
        this.state = state;
        this.duration = duration;
    }
    
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
    
    private void setResource(Resource resource) throws Error {
        try {
            PropertyUtils.setSimpleProperty(this, resource.getClass().getSimpleName().toLowerCase(), resource);
        } catch (Exception e) {
            throw new Error("Tried to assign state duration to invalid prism resource", e);
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public StateDuration withSystem(System system) {
        this.system = system;
        return this;
    }
    
    public StateDuration withState(State state) {
        this.state = state;
        return this;
    }
    
    public StateDuration withDuration(Integer duration) {
        this.duration = duration;
        return this;
    }

}
