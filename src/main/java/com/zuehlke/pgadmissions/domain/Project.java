package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.AdvertType;
import com.zuehlke.pgadmissions.domain.enums.ProjectState;

@Entity
@Table(name = "PROJECT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Project extends Advert implements PrismScope {

    private static final long serialVersionUID = 5963260213501162814L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Column(name = "state_id")
    @Enumerated(EnumType.STRING)
    private ProjectState state;

    public Project() {
        super.setAdvertType(AdvertType.PROJECT);
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public ProjectState getState() {
        return state;
    }

    public void setState(ProjectState state) {
        this.state = state;
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
        return state == ProjectState.PROJECT_APPROVED;
    }
    
    public Project withId(Integer id) {
        setId(id);
        return this;
    }

}
