package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.search.annotations.Indexed;

@Entity
@Table(name = "SYSTEM")
@Indexed
public class PrismSystem extends PrismScope {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
    
    @Override
    public String getScopeName() {
        return "system";
    }

    @Override
    public PrismSystem getSystem() {
        return this;
    }

    @Override
    public Institution getInstitution() {
        return null;
    }

    @Override
    public Program getProgram() {
        return null;
    }

    @Override
    public Project getProject() {
        return null;
    }
    
    @Override
    public Application getApplication() {
        return null;
    }

}
