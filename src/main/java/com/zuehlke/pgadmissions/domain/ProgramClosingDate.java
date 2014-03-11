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

import com.zuehlke.pgadmissions.utils.DateUtils;

@Entity(name = "PROGRAM_CLOSING_DATES")
public class ProgramClosingDate implements Serializable {

    private static final long serialVersionUID = -1883742652445622591L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false, updatable = false, insertable = false)
    private Program program;

    @Column(name = "closing_date")
    @Temporal(value = TemporalType.DATE)
    private Date closingDate;

    @Column(name = "study_places")
    private Integer studyPlaces;

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
        this.closingDate = DateUtils.truncateToDay(closingDate);
    }

    public Integer getStudyPlaces() {
        return studyPlaces;
    }

    public void setStudyPlaces(Integer studyPlaces) {
        this.studyPlaces = studyPlaces;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
