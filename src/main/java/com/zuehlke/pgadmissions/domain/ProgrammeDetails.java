package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "APPLICATION_FORM_PROGRAMME_DETAIL")
public class ProgrammeDetails implements FormSectionObject, Serializable {

    private static final long serialVersionUID = -5997103825068065955L;

    @Id
    @GeneratedValue
    protected Integer id;
    
	@Transient
	private boolean acceptedTerms;

	@Column(name = "programme_name")
	private String programmeName;

	@Column(name = "study_option")
	private String studyOption;
	
	@Column(name = "study_code")
    private String studyOptionCode;

	@Column(name = "project_name")
	private String projectName;

	@Temporal(value = TemporalType.DATE)
	@Column(name = "start_date")
	private Date startDate;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sources_of_interest_id")
    private SourcesOfInterest sourcesOfInterest;
	
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
	@Column (name = "sources_of_interest_text")
	private String sourcesOfInterestText;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_form_id")
	private ApplicationForm application = null;

	@OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "programme_detail_id")
	@Valid
	private List<SuggestedSupervisor> suggestedSupervisors = new ArrayList<SuggestedSupervisor>();

	public String getProgrammeName() {
		return programmeName;
	}

	public void setProgrammeName(String programmeName) {
		this.programmeName = programmeName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getStudyOption() {
		return studyOption;
	}

	public void setStudyOption(String studyOption) {
		this.studyOption = studyOption;
	}
	
    public String getStudyOptionCode() {
        return studyOptionCode;
    }

    public void setStudyOptionCode(String studyCode) {
        this.studyOptionCode = studyCode;
    }

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public List<SuggestedSupervisor> getSuggestedSupervisors() {
		return suggestedSupervisors;
	}

	public void setSuggestedSupervisors(List<SuggestedSupervisor> supervisors) {
		this.suggestedSupervisors = supervisors;
		if (supervisors != null && !supervisors.isEmpty()) {
			int size = supervisors.size();
			for (int i = size - 1; i >= 0; i--) {
				SuggestedSupervisor supervisor = supervisors.get(i);
				if (supervisor == null) {
					supervisors.remove(i);
				}
			}
		}
	}
	
	public boolean hasSupervisors(){
		return !suggestedSupervisors.isEmpty();
	}

	public boolean isAcceptedTerms() {
		return acceptedTerms;
	}

	public void setAcceptedTerms(boolean acceptedTerms) {
		this.acceptedTerms = acceptedTerms;
	}

    public SourcesOfInterest getSourcesOfInterest() {
        return sourcesOfInterest;
    }

    public void setSourcesOfInterest(SourcesOfInterest sourcesOfInterest) {
        this.sourcesOfInterest = sourcesOfInterest;
    }

    public String getSourcesOfInterestText() {
        return sourcesOfInterestText;
    }

    public void setSourcesOfInterestText(String sourcesOfInterestText) {
        this.sourcesOfInterestText = sourcesOfInterestText;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
