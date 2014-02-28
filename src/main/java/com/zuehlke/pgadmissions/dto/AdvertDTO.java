package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.enums.AdvertType;

public class AdvertDTO {

    private Integer id;
    private String title;
    private String description;
    private Integer studyDuration;
    private String funding;
    private String programCode;
	private Date closingDate;
	private String primarySupervisorFirstName;
	private String primarySupervisorLastName;
	private String primarySupervisorEmail;
    private AdvertType advertType = AdvertType.PROGRAM;
	private Integer projectId = null;
    private String secondarySupervisorFirstName = null;
    private String secondarySupervisorLastName = null;
    private Boolean selected = false;
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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
    
    public String getProgramCode() {
        return programCode;
    }
    
    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }
    
    public Date getClosingDate() {
        return closingDate;
    }
    
    public void setClosingDate(Date closingDate) {
        this.closingDate = closingDate;
    }
    
    public String getPrimarySupervisorFirstName() {
        return primarySupervisorFirstName;
    }
    
    public void setPrimarySupervisorFirstName(String primarySupervisorFirstName) {
        this.primarySupervisorFirstName = primarySupervisorFirstName;
    }
    
    public String getPrimarySupervisorLastName() {
        return primarySupervisorLastName;
    }
    
    public void setPrimarySupervisorLastName(String primarySupervisorLastName) {
        this.primarySupervisorLastName = primarySupervisorLastName;
    }
    
    public String getPrimarySupervisorEmail() {
        return primarySupervisorEmail;
    }
    
    public void setPrimarySupervisorEmail(String primarySupervisorEmail) {
        this.primarySupervisorEmail = primarySupervisorEmail;
    }
    
    public AdvertType getAdvertType() {
        return advertType;
    }

    public void setType(AdvertType advertType) {
        this.advertType = advertType;
    }

    public Integer getProjectId() {
        return projectId;
    }
    
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
        if (projectId != null) {
            setType(AdvertType.PROJECT);
        }
    }
    
    public String getSecondarySupervisorFirstName() {
        return secondarySupervisorFirstName;
    }
    
    public void setSecondarySupervisorFirstName(String secondarySupervisorFirstName) {
        this.secondarySupervisorFirstName = secondarySupervisorFirstName;
    }
    
    public String getSecondarySupervisorLastName() {
        return secondarySupervisorLastName;
    }
    
    public void setSecondarySupervisorLastName(String secondarySupervisorLastName) {
        this.secondarySupervisorLastName = secondarySupervisorLastName;
    }
    
    public Boolean getSelected() {
        return selected;
    }
    
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
    
}
