package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "SUPERVISION_CONFIRMATION_COMMENT")
public class SupervisionConfirmationComment extends Comment {

    private static final long serialVersionUID = 9120577563568889651L;

    @OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "supervisor_id")
    private Supervisor supervisor;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    @Column(name = "project_title")
    private String projectTitle;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 2000)
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

    public CommentType getType() {
        return type;
    }

    public void setType(CommentType type) {
        this.type = type;
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

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

}
