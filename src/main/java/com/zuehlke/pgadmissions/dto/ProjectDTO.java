package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.utils.DateUtils;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class ProjectDTO {

	private Integer id;
	
    private Program program;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    private String title;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 2000)
    private String description;

    private Integer studyDuration;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    private String funding;

    private Boolean closingDateSpecified;

    private Date closingDate;

    private Boolean active;

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStudyDuration() {
        return studyDuration;
    }

    public void setStudyDuration(Integer studyDuration) {
        this.studyDuration = studyDuration;
    }

    public String getFunding() {
        return funding;
    }

    public void setFunding(String funding) {
        this.funding = funding;
    }

    public Boolean getClosingDateSpecified() {
        return closingDateSpecified;
    }

    public void setClosingDateSpecified(Boolean closingDateSpecified) {
        this.closingDateSpecified = closingDateSpecified;
    }

    public Date getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Date closingDate) {
        this.closingDate = closingDate == null? closingDate : DateUtils.truncateToDay(closingDate);
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}