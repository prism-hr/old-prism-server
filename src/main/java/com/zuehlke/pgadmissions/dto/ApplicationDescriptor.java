package com.zuehlke.pgadmissions.dto;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.ApplicationForm;

public class ApplicationDescriptor {
	
	private ApplicationForm applicationForm;
	
	private Boolean needsToSeeUrgentFlag;
	
    private Boolean needsToSeeUpdateFlag;
    
    private Integer applicantId;
    
    private String applicantFirstName;
    
    private String applicantFirstName2;
    
    private String applicantFirstName3;
    
    private String applicantLastName;
    
    private String applicantEmail;
    
    private String programTitle;
    
    private String projectTitle;
    
    private String oldProjectTitle;
    
    private List<ActionDefinition> actionDefinitions = Lists.newArrayList();
    
    private Date applicationFormUpdatedTimestamp;
    
	public ApplicationDescriptor() {
    }

    public ApplicationForm getApplicationForm() {
		return applicationForm;
	}

	public void setApplicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;
	}

    public Boolean getNeedsToSeeUpdateFlag() {
        return needsToSeeUpdateFlag;
    } 
    
    public Boolean getNeedsToSeeUrgentFlag() {
    	return needsToSeeUrgentFlag;
    }

    public void setNeedsToSeeUrgentFlag(Boolean needsToSeeUrgentFlag) {
    	this.needsToSeeUrgentFlag = needsToSeeUrgentFlag;
    }

    public void setNeedsToSeeUpdateFlag(Boolean needsToSeeUpdateFlag) {
        this.needsToSeeUpdateFlag = needsToSeeUpdateFlag;
    }

	public Integer getApplicantId() {
		return applicantId;
	}

	public void setApplicantId(Integer applicantId) {
		this.applicantId = applicantId;
	}

	public String getApplicantFirstName() {
		return applicantFirstName;
	}

	public void setApplicantFirstName(String applicantFirstName) {
		this.applicantFirstName = applicantFirstName;
	}
	
	public String getApplicantFirstName2() {
		return applicantFirstName2;
	}

	public void setApplicantFirstName2(String applicantFirstName2) {
		this.applicantFirstName2 = applicantFirstName2;
	}

	public String getApplicantFirstName3() {
		return applicantFirstName3;
	}

	public void setApplicantFirstName3(String applicantFirstName3) {
		this.applicantFirstName3 = applicantFirstName3;
	}
	
	public String getConcatenatedApplicantFirstName() {
		String concatenatedFirstName = applicantFirstName;
		if (applicantFirstName2 != null ||
			applicantFirstName3 != null) {
			concatenatedFirstName = concatenatedFirstName + " (";
			if (applicantFirstName2 != null) {
				concatenatedFirstName = concatenatedFirstName + applicantFirstName2;
			}
			if (applicantFirstName2 != null &&
				applicantFirstName3 != null) {
				concatenatedFirstName = concatenatedFirstName + " ";
			}
			if (applicantFirstName3 != null) {
				concatenatedFirstName = concatenatedFirstName + applicantFirstName3;
			}
			concatenatedFirstName = concatenatedFirstName + ")";
		}
		return concatenatedFirstName;
	}

	public String getApplicantLastName() {
		return applicantLastName;
	}

	public void setApplicantLastName(String applicantLastName) {
		this.applicantLastName = applicantLastName;
	}

	public String getApplicantEmail() {
		return applicantEmail;
	}

	public void setApplicantEmail(String applicantEmail) {
		this.applicantEmail = applicantEmail;
	}
	
	public String getProgramTitle() {
		return programTitle;
	}

	public void setProgramTitle(String programTitle) {
		this.programTitle = programTitle;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public String getOldProjectTitle() {
		return oldProjectTitle;
	}

	public void setOldProjectTitle(String oldProjectTitle) {
		this.oldProjectTitle = oldProjectTitle;
	}
	
	public String getActualProjectTitle() {
		if (projectTitle != null) {
			return projectTitle;
		} else if (oldProjectTitle != null) {
			return oldProjectTitle;
		} else {
			return null;
		}
	}
	
    public List<ActionDefinition> getActionDefinitions() {
        return actionDefinitions;
    }

	public Date getApplicationFormUpdatedTimestamp() {
		return applicationFormUpdatedTimestamp;
	}

	public void setApplicationFormUpdatedTimestamp(
			Date applicationFormUpdatedTimestamp) {
		this.applicationFormUpdatedTimestamp = applicationFormUpdatedTimestamp;
	}
	
}