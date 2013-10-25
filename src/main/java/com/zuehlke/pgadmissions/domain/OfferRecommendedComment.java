package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.validators.ATASConstraint;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "OFFER_RECOMMENDED_COMMENT")
public class OfferRecommendedComment extends Comment {

    private static final long serialVersionUID = 2184172372328153404L;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    @Column(name = "project_title")
    private String projectTitle;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 2000)
    @ATASConstraint
    @Column(name = "project_abstract")
    private String projectAbstract;

    @Temporal(value = TemporalType.DATE)
    @Column(name = "recommended_start_date")
    private Date recommendedStartDate;

    @Column(name = "recommended_conditions_available")
    private Boolean recommendedConditionsAvailable;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 1000)
    @Column(name = "recommended_conditions")
    private String recommendedConditions;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type")
    private CommentType type;

    @Transient
    private List<Supervisor> supervisors = new ArrayList<Supervisor>();

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectAbstract() {
        return projectAbstract;
    }

    public void setProjectAbstract(String projectAbstract) {
        this.projectAbstract = projectAbstract;
    }

    public Date getRecommendedStartDate() {
        return recommendedStartDate;
    }

    public void setRecommendedStartDate(Date recommendedStartDate) {
        this.recommendedStartDate = recommendedStartDate;
    }

    public Boolean getRecommendedConditionsAvailable() {
        return recommendedConditionsAvailable;
    }

    public void setRecommendedConditionsAvailable(Boolean recommendedConditionsAvailable) {
        this.recommendedConditionsAvailable = recommendedConditionsAvailable;
    }

    public String getRecommendedConditions() {
        return recommendedConditions;
    }

    public void setRecommendedConditions(String recommendedConditions) {
        this.recommendedConditions = recommendedConditions;
    }

    public CommentType getType() {
        return type;
    }

    public void setType(CommentType type) {
        this.type = type;
    }

    public List<Supervisor> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(List<Supervisor> supervisors) {
        this.supervisors = supervisors;
    }

}
