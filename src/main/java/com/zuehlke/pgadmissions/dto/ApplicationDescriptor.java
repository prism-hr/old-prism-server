package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.utils.MathUtils;

public class ApplicationDescriptor {
	
	private Integer applicationFormId;
	
	private Boolean needsToSeeUrgentFlag;
	
    private Boolean needsToSeeUpdateFlag;
    
    private Integer applicantId;
    
    private String applicantFirstName;
    
    private String applicantFirstName2;
    
    private String applicantFirstName3;
    
    private String applicantLastName;
    
    private String applicantEmail;
    
    private String applicationFormNumber;
    
    private String programTitle;
    
    private String projectTitle;
    
    private String oldProjectTitle;
    
    private BigDecimal applicantAverageRating;
    
    private ApplicationFormStatus applicationFormStatus;
    
	private boolean applicationFormWithdrawnBeforeSubmitted;
    
    private ApplicationFormStatus applicationFormNextStatus;
    
    private List<ActionDefinition> actionDefinitions = Lists.newArrayList();
    
    private Integer applicationFormPersonalStatementId;
    
    private Integer applicationFormCvId;
    
    private Date applicationFormCreatedTimestamp;
    
    private Date applicationFormUpdatedTimestamp;
    
	public ApplicationDescriptor() {
    }

    public Integer getApplicationFormId() {
		return applicationFormId;
	}

	public void setApplicationFormId(Integer applicationFormId) {
		this.applicationFormId = applicationFormId;
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
	
	public String getApplicationFormNumber() {
		return applicationFormNumber;
	}

	public void setApplicationFormNumber(String applicationFormNumber) {
		this.applicationFormNumber = applicationFormNumber;
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
	
	public String getApplicantAverageRating() {
		return MathUtils.formatRating(applicantAverageRating);
	}

	public void setApplicantAverageRating(BigDecimal applicantAverageRating) {
		this.applicantAverageRating = applicantAverageRating;
	}

	public ApplicationFormStatus getApplicationFormStatus() {
		return applicationFormStatus;
	}

	public void setApplicationFormStatus(ApplicationFormStatus applicationFormStatus) {
		this.applicationFormStatus = applicationFormStatus;
	}
	
    public boolean isApplicationFormWithdrawnBeforeSubmitted() {
		return applicationFormWithdrawnBeforeSubmitted;
	}

	public void setApplicationFormWithdrawnBeforeSubmitted(boolean applicationFormWithdrawnBeforeSubmitted) {
		this.applicationFormWithdrawnBeforeSubmitted = applicationFormWithdrawnBeforeSubmitted;
	}

	public ApplicationFormStatus getApplicationFormNextStatus() {
		return applicationFormNextStatus;
	}

	public boolean getApplicationFormSubmitted() {
		return applicationFormStatus != ApplicationFormStatus.UNSUBMITTED;
	}
	
	public void setApplicationFormNextStatus(ApplicationFormStatus applicationFormNextStatus) {
		this.applicationFormNextStatus = applicationFormNextStatus;
	}

	public List<ActionDefinition> getActionDefinitions() {
    	if (actionDefinitions == null) {
    		actionDefinitions = new ArrayList<ActionDefinition>();
    	}
        return actionDefinitions;
    }

	public Integer getApplicationFormPersonalStatementId() {
		return applicationFormPersonalStatementId;
	}

	public void setApplicationFormPersonalStatementId(Integer applicationFormPersonalStatementId) {
		this.applicationFormPersonalStatementId = applicationFormPersonalStatementId;
	}

	public Integer getApplicationFormCvId() {
		return applicationFormCvId;
	}

	public void setApplicationFormCvId(Integer applicationFormCvId) {
		this.applicationFormCvId = applicationFormCvId;
	}

	public Date getApplicationFormCreatedTimestamp() {
		return applicationFormCreatedTimestamp;
	}

	public void setApplicationFormCreatedTimestamp(
			Date applicationFormCreatedTimestamp) {
		this.applicationFormCreatedTimestamp = applicationFormCreatedTimestamp;
	}

	public Date getApplicationFormUpdatedTimestamp() {
		return applicationFormUpdatedTimestamp;
	}

	public void setApplicationFormUpdatedTimestamp(Date applicationFormUpdatedTimestamp) {
		this.applicationFormUpdatedTimestamp = applicationFormUpdatedTimestamp;
	}
	
}