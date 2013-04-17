package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name="BADGE")
public class Badge implements Serializable {

    private static final long serialVersionUID = -2878739613823320257L;
    
    @Column(name="project_title")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String projectTitle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")    
    private Program program;

    @Temporal(TemporalType.DATE)
    @Column(name="closing_date")
    private Date closingDate;
    
    @Transient
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 1000)
    private String programmeHomepage;

    @Id
    @GeneratedValue
    private Integer id;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
    
    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public Program getProgram() {
        return program;
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
    
    public String getProgrammeHomepage() {
        return programmeHomepage;
    }
    
    public void setProgrammeHomepage(String programmeHomepage) {
        this.programmeHomepage = programmeHomepage;
    }
}
