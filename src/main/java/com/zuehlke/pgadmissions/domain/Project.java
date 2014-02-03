package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "PROJECT")
public class Project implements Serializable {

    private static final long serialVersionUID = 5963260213501162814L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private RegisteredUser author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "closing_date")
    @Temporal(value = TemporalType.DATE)
    private Date closingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_supervisor_id", nullable = false)
    private RegisteredUser primarySupervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secondary_supervisor_id")
    private RegisteredUser secondarySupervisor;

    @Column(name = "disabled")
    private boolean disabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrator_id")
    private RegisteredUser administrator;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    private List<ApplicationForm> applications = new ArrayList<ApplicationForm>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RegisteredUser getAuthor() {
        return author;
    }

    public void setAuthor(RegisteredUser author) {
        this.author = author;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public Date getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Date closingDate) {
        this.closingDate = closingDate;
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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public RegisteredUser getAdministrator() {
        return administrator;
    }

    public void setAdministrator(RegisteredUser administrator) {
        this.administrator = administrator;
    }

    public boolean isAcceptingApplications() {
        return !disabled && advert != null && advert.getActive();
    }

    public List<ApplicationForm> getApplications() {
        return applications;
    }

}
