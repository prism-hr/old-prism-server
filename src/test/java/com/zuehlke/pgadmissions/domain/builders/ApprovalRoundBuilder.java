package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Supervisor;

public class ApprovalRoundBuilder {

    private List<Supervisor> supervisors = new ArrayList<Supervisor>();
    
    private String missingQualificationExplanation;

    private Boolean projectDescriptionAvailable;

    private String projectTitle;

    private String projectAbstract;

    private Date recommendedStartDate;

    private Boolean recommendedConditionsAvailable;

    private String recommendedConditions;

    private ApplicationForm application;

    private Integer id;

    private Date createdDate;
    
    private Boolean projectAcceptingApplications;

    public ApprovalRoundBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ApprovalRoundBuilder createdDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public ApprovalRoundBuilder application(ApplicationForm application) {
        this.application = application;
        return this;
    }

    public ApprovalRoundBuilder supervisors(Supervisor... supervisors) {
        this.supervisors.addAll(Arrays.asList(supervisors));
        return this;
    }
    
    public ApprovalRoundBuilder missingQualificationExplanation(String missingQualificationExplanation) {
        this.missingQualificationExplanation = missingQualificationExplanation;
        return this;
    }
    
    public ApprovalRoundBuilder projectDescriptionAvailable(Boolean projectDescriptionAvailable) {
        this.projectDescriptionAvailable = projectDescriptionAvailable;
        return this;
    }

    public ApprovalRoundBuilder projectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
        return this;
    }

    public ApprovalRoundBuilder projectAbstract(String projectAbstract) {
        this.projectAbstract = projectAbstract;
        return this;
    }

    public ApprovalRoundBuilder recommendedStartDate(Date recommendedStartDate) {
        this.recommendedStartDate = recommendedStartDate;
        return this;
    }
    
    public ApprovalRoundBuilder recommendedConditionsAvailable(Boolean recommendedConditionsAvailable) {
        this.recommendedConditionsAvailable = recommendedConditionsAvailable;
        return this;
    }

    public ApprovalRoundBuilder recommendedConditions(String recommendedConditions) {
        this.recommendedConditions = recommendedConditions;
        return this;
    }

    public ApprovalRoundBuilder projectAcceptingApplications(Boolean projectAcceptingApplications) {
    	this.projectAcceptingApplications = projectAcceptingApplications;
    	return this;
    }

    public ApprovalRound build() {
        ApprovalRound approvalRound = new ApprovalRound();
        approvalRound.setApplication(application);
        approvalRound.setCreatedDate(createdDate);
        approvalRound.setSupervisors(supervisors);
        approvalRound.setMissingQualificationExplanation(missingQualificationExplanation);
        approvalRound.setProjectDescriptionAvailable(projectDescriptionAvailable);
        approvalRound.setProjectTitle(projectTitle);
        approvalRound.setProjectAbstract(projectAbstract);
        approvalRound.setRecommendedStartDate(recommendedStartDate);
        approvalRound.setRecommendedConditionsAvailable(recommendedConditionsAvailable);
        approvalRound.setRecommendedConditions(recommendedConditions);
        approvalRound.setProjectAcceptingApplications(projectAcceptingApplications);
        approvalRound.setId(id);
        return approvalRound;
    }
}
