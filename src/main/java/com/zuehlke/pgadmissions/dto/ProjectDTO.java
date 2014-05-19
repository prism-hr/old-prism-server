package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import javax.validation.constraints.Size;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.validators.ATASConstraint;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class ProjectDTO {

    private Program program;

    private Boolean administratorSpecified;

    private User administrator;

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

    private PrismState state;

    private User primarySupervisor;

    private Boolean secondarySupervisorSpecified;

    private User secondarySupervisor;

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

    public User getAdministrator() {
        return administrator;
    }

    public void setAdministrator(User administrator) {
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
        this.closingDate = closingDate;
    }

    public PrismState getState() {
        return state;
    }

    public void setState(PrismState state) {
        this.state = state;
    }

    public User getPrimarySupervisor() {
        return primarySupervisor;
    }

    public void setPrimarySupervisor(User primarySupervisor) {
        this.primarySupervisor = primarySupervisor;
    }

    public Boolean getSecondarySupervisorSpecified() {
        return secondarySupervisorSpecified;
    }

    public void setSecondarySupervisorSpecified(Boolean secondarySupervisorSpecified) {
        this.secondarySupervisorSpecified = secondarySupervisorSpecified;
    }

    public User getSecondarySupervisor() {
        return secondarySupervisor;
    }

    public void setSecondarySupervisor(User secondarySupervisor) {
        this.secondarySupervisor = secondarySupervisor;
    }

    public Integer getStudyDuration() {
        return studyDuration;
    }

    public void setStudyDuration(Integer studyDuration) {
        this.studyDuration = studyDuration;
    }

}