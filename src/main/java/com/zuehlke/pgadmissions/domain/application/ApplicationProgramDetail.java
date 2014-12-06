package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.LocaleUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.imported.ReferralSource;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;

@Entity
@Table(name = "APPLICATION_PROGRAM_DETAIL")
public class ApplicationProgramDetail extends ApplicationSection {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "programDetail")
    private Application application;

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

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
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

    @Override
    public DateTime getLastEditedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastEditedTimestamp(DateTime lastEditedTimestamp) {
        this.lastUpdatedTimestamp = lastEditedTimestamp;
    }

    public ApplicationProgramDetail withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationProgramDetail withStudyOption(StudyOption studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public ApplicationProgramDetail withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ApplicationProgramDetail withReferralSource(ReferralSource referralSource) {
        this.referralSource = referralSource;
        return this;
    }

    public ApplicationProgramDetail withSourceOfInterestText(String sourceOfInterestText) {
        this.sourceOfInterestText = sourceOfInterestText;
        return this;
    }

    public ApplicationProgramDetail withApplication(Application application) {
        this.application = application;
        return this;
    }

    public String getStartDateDisplay(String dateFormat) {
        return startDate == null ? null : startDate.toString(dateFormat, LocaleUtils.toLocale(application.getLocale().toString()));
    }

    public String getReferralSourceDisplay() {
        return referralSource == null ? null : referralSource.getName();
    }

    public PrismStudyOption getStudyOptionDisplay() {
        return studyOption == null ? null : PrismStudyOption.valueOf(studyOption.getCode());
    }

}
