package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.zuehlke.pgadmissions.domain.enums.CommentPropertyType;
import com.zuehlke.pgadmissions.validators.ATASConstraint;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "APPROVAL_COMMENT")
public class ApprovalComment extends Comment {

    private static final long serialVersionUID = 9120577563568889651L;

    public String getProjectTitle() {
        return super.getCommentProperty(CommentPropertyType.PROJECTTITLE).getValueText();
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectAbstract() {
    	return super.getCommentProperty(CommentPropertyType.PROJECTABSTRACT).getValueText();
    }

    public void setProjectAbstract(String projectAbstract) {
        this.projectAbstract = projectAbstract;
    }

    public Date getRecommendedStartDate() {
    	return super.getCommentProperty(CommentPropertyType.RECOMMENDEDSTARTDATE).getValueDatetime();
    }

    public void setRecommendedStartDate(Date recommendedStartDate) {
        this.recommendedStartDate = recommendedStartDate;
    }

    public Boolean getRecommendedConditionsAvailable() {
    	return super.getCommentProperty(CommentPropertyType.RECOMMENDEDOFFERCONDITIONSAVAILABLE).getValueBoolean();
    }

    public void setRecommendedConditionsAvailable(Boolean recommendedConditionsAvailable) {
        this.recommendedConditionsAvailable = recommendedConditionsAvailable;
    }

    public String getRecommendedConditions() {
    	return super.getCommentProperty(CommentPropertyType.RECOMMENDEDOFFERCONDITIONS).getValueText();
    }

    public void setRecommendedConditions(String recommendedConditions) {
        this.recommendedConditions = recommendedConditions;
    }

    public Supervisor getSupervisor() {
        return super.getCommentProperty(CommentPropertyType.PRIMARYSUPERVISOR).getValueInteger();
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public Supervisor getSecondarySupervisor() {
        return secondarySupervisor;
    }

    public void setSecondarySupervisor(Supervisor secondarySupervisor) {
        this.secondarySupervisor = secondarySupervisor;
    }

}