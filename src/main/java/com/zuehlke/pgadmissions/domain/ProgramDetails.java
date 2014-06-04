package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "APPLICATION_PROGRAM_DETAIL")
public class ProgramDetails {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "program_study_option_id", nullable = false)
    private StudyOption studyOption;

    @Column(name = "start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate startDate;

    @OneToOne
    @JoinColumn(name = "referral_source_id", nullable = false)
    private SourcesOfInterest sourceOfInterest;

    @Transient
    private String sourceOfInterestText;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "programme_detail_id")
    @Valid
    private List<SuggestedSupervisor> suggestedSupervisors = new ArrayList<SuggestedSupervisor>();

    @OneToOne(mappedBy = "programDetails")
    private Application application;

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

    public ProgramDetails withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public ProgramDetails withStudyOption(StudyOption studyOption) {
        this.studyOption = studyOption;
        return this;
    }
    
    public ProgramDetails withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }
    
    public ProgramDetails withSourceOfInterest(SourcesOfInterest sourceOfInterest) {
        this.sourceOfInterest = sourceOfInterest;
        return this;
    }
    
    public ProgramDetails withSourceOfInterestText(String sourceOfInterestText) {
        this.sourceOfInterestText = sourceOfInterestText;
        return this;
    }
    
    public ProgramDetails withSuggestedSupervisors(SuggestedSupervisor... suggestedSupervisors) {
        this.suggestedSupervisors.addAll(Arrays.asList(suggestedSupervisors));
        return this;
    }
    
    public ProgramDetails withApplication(Application application) {
        this.application = application;
        return this;
    }
    
}
