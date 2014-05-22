package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_FORM_PROGRAM_DETAIL")
public class ProgramDetails implements Serializable, FormSectionObject {

    private static final long serialVersionUID = -5997103825068065955L;

    @Id
    @GeneratedValue
    protected Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_option_id")
    private StudyOption studyOption;

    @Column(name = "start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate startDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sources_of_interest_id")
    private SourcesOfInterest sourceOfInterest;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    @Column(name = "sources_of_interest_text")
    private String sourceOfInterestText;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "programme_detail_id")
    @Valid
    private List<SuggestedSupervisor> suggestedSupervisors = new ArrayList<SuggestedSupervisor>();

    @OneToOne(mappedBy = "programDetails", fetch = FetchType.LAZY)
    private Application application;

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
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

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

}
