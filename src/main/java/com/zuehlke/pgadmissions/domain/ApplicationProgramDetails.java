package com.zuehlke.pgadmissions.domain;

import java.util.Arrays;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;

@Entity
@Table(name = "APPLICATION_PROGRAM_DETAIL")
public class ApplicationProgramDetails {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "study_option_id", nullable = false)
    private StudyOption studyOption;

    @Column(name = "start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate startDate;

    @ManyToOne
    @JoinColumn(name = "referral_source_id", nullable = false)
    private ReferralSource referralSource;

    @Transient
    private String sourceOfInterestText;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_program_detail_id", nullable = false)
    private Set<ApplicationSupervisor> supervisors = Sets.newHashSet();
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "APPLICATION_OTHER_PROJECT", joinColumns = @JoinColumn(name = "application_program_detail_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "project_id", nullable = false))
    private Set<OpportunityCategory> categories = Sets.newHashSet();


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

    public ReferralSource getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(ReferralSource referralSource) {
        this.referralSource = referralSource;
    }

    public String getSourceOfInterestText() {
        return sourceOfInterestText;
    }

    public void setSourceOfInterestText(String sourceOfInterest) {
        this.sourceOfInterestText = sourceOfInterest;
    }

    public Set<ApplicationSupervisor> getSupervisors() {
        return supervisors;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ApplicationProgramDetails withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public ApplicationProgramDetails withStudyOption(StudyOption studyOption) {
        this.studyOption = studyOption;
        return this;
    }
    
    public ApplicationProgramDetails withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }
    
    public ApplicationProgramDetails withReferralSource(ReferralSource referralSource) {
        this.referralSource = referralSource;
        return this;
    }
    
    public ApplicationProgramDetails withSourceOfInterestText(String sourceOfInterestText) {
        this.sourceOfInterestText = sourceOfInterestText;
        return this;
    }
    
    public ApplicationProgramDetails withSuggestedSupervisors(ApplicationSupervisor... suggestedSupervisors) {
        this.supervisors.addAll(Arrays.asList(suggestedSupervisors));
        return this;
    }
    
    public ApplicationProgramDetails withApplication(Application application) {
        this.application = application;
        return this;
    }
    
}
