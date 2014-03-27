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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "APPLICATION_FORM_PROGRAM_DETAIL")
public class ProgramDetails implements Serializable, FormSectionObject {

    private static final long serialVersionUID = -5997103825068065955L;

    @Id
    @GeneratedValue
    protected Integer id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_option_id")
    private StudyOption studyOption;

	@Temporal(value = TemporalType.DATE)
	@Column(name = "start_date")
	private Date startDate;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sources_of_interest_id")
    private SourcesOfInterest sourceOfInterest;
	
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
	@Column (name = "sources_of_interest_text")
	private String sourceOfInterestText;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "programme_detail_id")
	@Valid
	private List<SuggestedSupervisor> suggestedSupervisors = new ArrayList<SuggestedSupervisor>();
    
    @OneToOne(mappedBy = "programmeDetails", fetch = FetchType.LAZY)
    private ApplicationForm application;

    @Transient
    private boolean acceptedTerms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StudyOption getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(StudyOption studyOption) {
        this.studyOption = studyOption;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public SourcesOfInterest getSourceOfInterest() {
        return sourceOfInterest;
    }

    public void setSourceOfInterest(SourcesOfInterest sourceOfInterest) {
        this.sourceOfInterest = sourceOfInterest;
    }

    public String getSourceOfInterestText() {
        return sourceOfInterestText;
    }

    public void setSourceOfInterestText(String sourceOfInterest) {
        this.sourceOfInterestText = sourceOfInterest;
    }

    public List<SuggestedSupervisor> getSuggestedSupervisors() {
        return suggestedSupervisors;
    }

    public void setSuggestedSupervisors(List<SuggestedSupervisor> suggestedSupervisors) {
        this.suggestedSupervisors = suggestedSupervisors;
    }
    
    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }
    
}
