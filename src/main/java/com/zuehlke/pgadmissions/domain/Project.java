package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.AdvertType;

@Entity(name = "PROJECT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Project extends Advert implements PrismScope {

    private static final long serialVersionUID = 5963260213501162814L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_supervisor_id", nullable = false)
    private User primarySupervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secondary_supervisor_id")
    private User secondarySupervisor;
    
    public Project() {
        super();
        super.setAdvertType(AdvertType.PROJECT);
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public User getPrimarySupervisor() {
        return primarySupervisor;
    }

    public void setPrimarySupervisor(User primarySupervisor) {
        this.primarySupervisor = primarySupervisor;
    }

    public User getSecondarySupervisor() {
        return secondarySupervisor;
    }

    public void setSecondarySupervisor(User secondarySupervisor) {
        this.secondarySupervisor = secondarySupervisor;
    }
    
    @Override
    public Program getProgram() {
        return program;
    }
    
    @Override
    public Project getProject() {
        return this;
    }
    
}
