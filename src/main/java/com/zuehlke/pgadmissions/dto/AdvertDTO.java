package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class AdvertDTO {

    private Integer id;
    private String title;
    private String description;
    private Integer studyDuration;
    private String funding;
    private String programCode;
	private Date closingDate;
	private RegisteredUser primarySupervisor;
	private Integer projectId = null;
    private RegisteredUser secondarySupervisor = null;
    private Boolean selected = false;
    private Integer ranking = 0;
    
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
    
    public RegisteredUser getPrimarySupervisor() {
        return primarySupervisor;
    }
    
    public void setPrimarySupervisor(RegisteredUser primarySupervisor) {
        this.primarySupervisor = primarySupervisor;
    }
    
    public Integer getProjectId() {
        return projectId;
    }
    
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    
    public RegisteredUser getSecondarySupervisor() {
        return secondarySupervisor;
    }
    
    public void setSecondarySupervisor(RegisteredUser secondarySupervisor) {
        this.secondarySupervisor = secondarySupervisor;
    }
    
    public Boolean getSelected() {
        return selected;
    }
    
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
    
    public Integer getRanking() {
        return ranking;
    }
    
    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

}
