package com.zuehlke.pgadmissions.domain.advert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.UniqueEntity;

import uk.co.alumeni.prism.api.model.resource.ResourceInstanceDefinition;

@Entity
@Table(name = "advert_study_option_instance", uniqueConstraints = @UniqueConstraint(columnNames = { "advert_study_option_id", "business_year" }))
public class AdvertStudyOptionInstance implements UniqueEntity, ResourceInstanceDefinition {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_study_option_id", nullable = false)
    private AdvertStudyOption studyOption;

    @Column(name = "application_start_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationStartDate;

    @Column(name = "application_close_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationCloseDate;

    @Column(name = "business_year", nullable = false)
    private String businessYear;

    @Column(name = "sequence_identifier", nullable = false)
    private String identifier;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final AdvertStudyOption getStudyOption() {
        return studyOption;
    }

    public final void setStudyOption(AdvertStudyOption studyOption) {
        this.studyOption = studyOption;
    }

    @Override
    public final LocalDate getApplicationStartDate() {
        return applicationStartDate;
    }

    @Override
    public final void setApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    @Override
    public final LocalDate getApplicationCloseDate() {
        return applicationCloseDate;
    }

    @Override
    public final void setApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    @Override
    public final String getBusinessYear() {
        return businessYear;
    }

    @Override
    public final void setBusinessYear(String businessYear) {
        this.businessYear = businessYear;
    }

    @Override
    public final String getIdentifier() {
        return identifier;
    }

    @Override
    public final void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public AdvertStudyOptionInstance withStudyOption(AdvertStudyOption studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public AdvertStudyOptionInstance withApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
        return this;
    }

    public AdvertStudyOptionInstance withApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
        return this;
    }

    public AdvertStudyOptionInstance withAcademicYear(String academicYear) {
        this.businessYear = academicYear;
        return this;
    }

    public AdvertStudyOptionInstance withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("studyOption", studyOption).addProperty("businessYear", businessYear);
    }

}
