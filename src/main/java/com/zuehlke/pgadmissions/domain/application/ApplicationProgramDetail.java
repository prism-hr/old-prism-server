package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;

@Entity
@Table(name = "application_program_detail")
public class ApplicationProgramDetail extends ApplicationSection {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "programDetail")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "imported_opportunity_type_id")
    private ImportedEntitySimple opportunityType;

    @ManyToOne
    @JoinColumn(name = "imported_study_option_id")
    private ImportedEntitySimple studyOption;

    @Column(name = "start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate startDate;

    @ManyToOne
    @JoinColumn(name = "imported_referral_source_id")
    private ImportedEntitySimple referralSource;

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

    public ImportedEntitySimple getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(ImportedEntitySimple opportunityType) {
        this.opportunityType = opportunityType;
    }

    public ImportedEntitySimple getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(ImportedEntitySimple studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public ImportedEntitySimple getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(ImportedEntitySimple referralSource) {
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

    public ApplicationProgramDetail withOpportunityType(ImportedEntitySimple opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public ApplicationProgramDetail withStudyOption(ImportedEntitySimple studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public ApplicationProgramDetail withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ApplicationProgramDetail withReferralSource(ImportedEntitySimple referralSource) {
        this.referralSource = referralSource;
        return this;
    }

    public ApplicationProgramDetail withApplication(Application application) {
        this.application = application;
        return this;
    }

    public PrismStudyOption getStudyOptionDisplay() {
        return studyOption == null ? null : PrismStudyOption.valueOf(studyOption.getName());
    }

    public String getReferralSourceDisplay() {
        return referralSource == null ? null : referralSource.getName();
    }

}
