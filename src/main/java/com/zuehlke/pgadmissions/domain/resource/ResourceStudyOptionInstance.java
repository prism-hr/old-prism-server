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

import uk.co.alumeni.prism.api.model.resource.ResourceInstanceDefinition;

import com.zuehlke.pgadmissions.domain.UniqueEntity;

@Entity
@Table(name = "resource_study_option_instance", uniqueConstraints = @UniqueConstraint(columnNames = { "program_study_option_id", "academic_year" }))
public class ResourceStudyOptionInstance implements UniqueEntity, ResourceInstanceDefinition {

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
        return new ResourceSignature().addProperty("studyOption", studyOption).addProperty("businessYear", businessYear);
    }

}
