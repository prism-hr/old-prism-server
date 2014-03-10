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
public class Project extends Advert {

    private static final long serialVersionUID = 5963260213501162814L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Column(name = "closing_date")
    @Temporal(value = TemporalType.DATE)
    private Date closingDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrator_id", nullable = false)
    private RegisteredUser administrator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_supervisor_id", nullable = false)
    private RegisteredUser primarySupervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secondary_supervisor_id")
    private RegisteredUser secondarySupervisor;
    
    public Project() {
        super();
        super.setAdvertType(AdvertType.PROJECT);
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Date getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Date closingDate) {
        this.closingDate = closingDate;
    }

    public RegisteredUser getAdministrator() {
        return administrator;
    }

    public void setAdministrator(RegisteredUser administrator) {
        this.administrator = administrator;
    }

    public RegisteredUser getPrimarySupervisor() {
        return primarySupervisor;
    }

    public void setPrimarySupervisor(RegisteredUser primarySupervisor) {
        this.primarySupervisor = primarySupervisor;
    }

    public RegisteredUser getSecondarySupervisor() {
        return secondarySupervisor;
    }

    public void setSecondarySupervisor(RegisteredUser secondarySupervisor) {
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
