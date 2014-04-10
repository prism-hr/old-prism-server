package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import javax.validation.constraints.Size;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.enums.AdvertState;
import com.zuehlke.pgadmissions.utils.DateUtils;
import com.zuehlke.pgadmissions.validators.ATASConstraint;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class ProjectDTO {

    private Integer id;

    private Program program;

    private Boolean administratorSpecified;

    private Person administrator;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    private String title;

    @Size(max = 3000, message = "A maximum of 2000 characters are allowed.")
    @ATASConstraint
    private String description;

    private Integer studyDuration;

    @Size(max = 2000, message = "A maximum of 1000 characters are allowed.")
    private String funding;

    private Boolean closingDateSpecified;

    private Date closingDate;

    private AdvertState state;

    private Person primarySupervisor;

    private Boolean secondarySupervisorSpecified;

    private Person secondarySupervisor;

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Boolean getAdministratorSpecified() {
        return administratorSpecified;
    }

    public void setAdministratorSpecified(Boolean administratorSpecified) {
        this.administratorSpecified = administratorSpecified;
    }

    public Person getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Person administrator) {
        this.administrator = administrator;
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
        this.closingDate = closingDate == null ? closingDate : DateUtils.truncateToDay(closingDate);
    }

    public AdvertState getState() {
        return state;
    }

    public void setState(AdvertState state) {
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Person getPrimarySupervisor() {
        return primarySupervisor;
    }

    public void setPrimarySupervisor(Person primarySupervisor) {
        this.primarySupervisor = primarySupervisor;
    }

    public Boolean getSecondarySupervisorSpecified() {
        return secondarySupervisorSpecified;
    }

    public void setSecondarySupervisorSpecified(Boolean secondarySupervisorSpecified) {
        this.secondarySupervisorSpecified = secondarySupervisorSpecified;
    }

    public Person getSecondarySupervisor() {
        return secondarySupervisor;
    }

    public void setSecondarySupervisor(Person secondarySupervisor) {
        this.secondarySupervisor = secondarySupervisor;
    }

    public Integer getStudyDuration() {
        return studyDuration;
    }

    public void setStudyDuration(Integer studyDuration) {
        this.studyDuration = studyDuration;
    }

}