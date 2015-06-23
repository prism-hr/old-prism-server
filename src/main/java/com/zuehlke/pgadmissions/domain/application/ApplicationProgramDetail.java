package com.zuehlke.pgadmissions.domain.application;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.imported.ImportedOpportunityType;
import com.zuehlke.pgadmissions.domain.imported.ImportedReferralSource;
import com.zuehlke.pgadmissions.domain.imported.ImportedStudyOption;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name = "application_program_detail")
public class ApplicationProgramDetail extends ApplicationSection {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "programDetail")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "imported_opportunity_type_id", nullable = false)
    private ImportedOpportunityType opportunityType;

    @ManyToOne
    @JoinColumn(name = "imported_study_option_id", nullable = false)
    private ImportedStudyOption studyOption;

    @Column(name = "start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate startDate;

    @ManyToOne
    @JoinColumn(name = "imported_referral_source_id", nullable = false)
    private ImportedReferralSource referralSource;

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

    public ImportedOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(ImportedOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public ImportedStudyOption getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(ImportedStudyOption studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public ImportedReferralSource getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(ImportedReferralSource referralSource) {
        this.referralSource = referralSource;
    }

    @Override
    public DateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public ApplicationProgramDetail withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationProgramDetail withOpportunityType(ImportedOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public ApplicationProgramDetail withStudyOption(ImportedStudyOption studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public ApplicationProgramDetail withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ApplicationProgramDetail withReferralSource(ImportedReferralSource referralSource) {
        this.referralSource = referralSource;
        return this;
    }

    public ApplicationProgramDetail withApplication(Application application) {
        this.application = application;
        return this;
    }

    public String getStartDateDisplay(String dateFormat) {
        return startDate == null ? null : startDate.toString(dateFormat);
    }

    public String getReferralSourceDisplay() {
        return referralSource == null ? null : referralSource.getName();
    }

    public PrismStudyOption getStudyOptionDisplay() {
        return studyOption == null ? null : PrismStudyOption.valueOf(studyOption.getName());
    }

}
