package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "APPROVAL_ROUND")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApprovalRound implements Serializable {

    private static final long serialVersionUID = 1068777060574638531L;

    @Id
    @GeneratedValue
    private Integer id;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "approval_round_id")
    private List<Supervisor> supervisors = new ArrayList<Supervisor>();

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 500)
    @Column(name = "missing_qualification_explanation")
    private String missingQualificationExplanation;

    @Column(name = "project_description_available")
    private Boolean projectDescriptionAvailable;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_form_id")
    private ApplicationForm application;

    @Column(name = "created_date", insertable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @Column(name = "project_accepting_applications")
    private Boolean projectAcceptingApplications;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    public String getMissingQualificationExplanation() {
        return missingQualificationExplanation;
    }

    public void setMissingQualificationExplanation(String missingQualificationExplanation) {
        this.missingQualificationExplanation = missingQualificationExplanation;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public List<Supervisor> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(List<Supervisor> supervisors) {
        this.supervisors.clear();
        for (Supervisor supervisor : supervisors) {
            if (supervisor != null) {
                this.supervisors.add(supervisor);
            }
        }
    }

    public Supervisor getPrimarySupervisor() {
        for (Supervisor supervisor : supervisors) {
            if (BooleanUtils.isTrue(supervisor.getIsPrimary())) {
                return supervisor;
            }
        }
        return null;
    }

    public Supervisor getSecondarySupervisor() {
        for (Supervisor supervisor : supervisors) {
            if (BooleanUtils.isNotTrue(supervisor.getIsPrimary())) {
                return supervisor;
            }
        }
        return null;
    }

    public Boolean getProjectDescriptionAvailable() {
        return projectDescriptionAvailable;
    }

    public void setProjectDescriptionAvailable(Boolean projectDescriptionAvailable) {
        this.projectDescriptionAvailable = projectDescriptionAvailable;
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

    public boolean hasPrimarySupervisorResponded(){
        Supervisor primarySupervisor = getPrimarySupervisor();
        return primarySupervisor != null && primarySupervisor.hasResponded();
    }

	public Boolean getProjectAcceptingApplications() {
		return projectAcceptingApplications;
	}

	public void setProjectAcceptingApplications(Boolean projectAcceptingApplications) {
		this.projectAcceptingApplications = projectAcceptingApplications;
	}
    
}
