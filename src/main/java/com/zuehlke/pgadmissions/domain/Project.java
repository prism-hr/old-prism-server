package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.AdvertType;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "PROJECT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Project extends Advert {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    @Column(name = "title")
    private String title;

    public Project() {
        super.setAdvertType(AdvertType.PROJECT);
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public Project getProject() {
        return this;
    }

    @Override
    public boolean isEnabled() {
        return state.getId() == PrismState.PROJECT_APPROVED;
    }

    public Project withId(Integer id) {
        setId(id);
        return this;
    }

    @Override
    public String getScopeName() {
        return "project";
    }

    @Override
    public PrismSystem getSystem() {
        return getInstitution().getSystem();
    }

    @Override
    public Institution getInstitution() {
        return getProgram().getInstitution();
    }

}
