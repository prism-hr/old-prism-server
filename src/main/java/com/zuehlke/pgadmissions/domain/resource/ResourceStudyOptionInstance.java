package com.zuehlke.pgadmissions.domain.resource;

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

@Entity
@Table(name = "resource_study_option_instance", uniqueConstraints = @UniqueConstraint(columnNames = { "program_study_option_id", "academic_year" }) )
public class ResourceStudyOptionInstance implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "program_study_option_id", nullable = false)
    private ResourceStudyOption studyOption;

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

    public final ResourceStudyOption getStudyOption() {
        return studyOption;
    }

    public final void setStudyOption(ResourceStudyOption studyOption) {
        this.studyOption = studyOption;
    }

    public final LocalDate getApplicationStartDate() {
        return applicationStartDate;
    }

    public final void setApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public final LocalDate getApplicationCloseDate() {
        return applicationCloseDate;
    }

    public final void setApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    public final String getBusinessYear() {
        return businessYear;
    }

    public final void setBusinessYear(String businessYear) {
        this.businessYear = businessYear;
    }

    public final String getIdentifier() {
        return identifier;
    }

    public final void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ResourceStudyOptionInstance withStudyOption(ResourceStudyOption studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public ResourceStudyOptionInstance withApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
        return this;
    }

    public ResourceStudyOptionInstance withApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
        return this;
    }

    public ResourceStudyOptionInstance withAcademicYear(String academicYear) {
        this.businessYear = academicYear;
        return this;
    }

    public ResourceStudyOptionInstance withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("studyOption", studyOption).addProperty("academicYear", businessYear);
    }

}
