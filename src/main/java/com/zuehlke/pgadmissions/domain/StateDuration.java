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
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = "state_duration", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "state_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "state_id" }), @UniqueConstraint(columnNames = { "program_id", "state_id" }) })
public class StateDuration extends WorkflowResourceConfiguration {

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
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(name = "day_duration", nullable = false)
    private Integer duration;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    
    @Override
    public System getSystem() {
        return system;
    }

    @Override
    public void setSystem(System system) {
        this.system = system;
    }

    @Override
    public Institution getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties1 = Maps.newHashMap();
        properties1.put("system", system);
        properties1.put("state", state);
        propertiesWrapper.add(properties1);
        HashMap<String, Object> properties2 = Maps.newHashMap();
        properties2.put("institution", institution);
        properties2.put("state", state);
        propertiesWrapper.add(properties2);
        HashMap<String, Object> properties3 = Maps.newHashMap();
        properties3.put("program", program);
        properties3.put("state", state);
        propertiesWrapper.add(properties3);
        return new ResourceSignature(propertiesWrapper);
    }

}
