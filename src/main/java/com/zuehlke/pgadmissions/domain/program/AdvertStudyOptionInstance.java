package com.zuehlke.pgadmissions.domain.program;

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
@Table(name = "ADVERT_STUDY_OPTION_INSTANCE", uniqueConstraints = @UniqueConstraint(columnNames = { "advert_study_option_id", "academic_year" }) )
public class AdvertStudyOptionInstance implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_study_option_id", nullable = false)
    private AdvertStudyOption advertStudyOption;

    @Column(name = "application_start_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationStartDate;

    @Column(name = "application_close_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationCloseDate;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "sequence_identifier", nullable = false)
    private String identifier;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final AdvertStudyOption getAdvertStudyOption() {
        return advertStudyOption;
    }

    public final void setAdvertStudyOption(AdvertStudyOption advertStudyOption) {
        this.advertStudyOption = advertStudyOption;
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

    public final String getAcademicYear() {
        return academicYear;
    }

    public final void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public final String getIdentifier() {
        return identifier;
    }

    public final void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public final Boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public AdvertStudyOptionInstance withStudyOption(AdvertStudyOption studyOption) {
        this.advertStudyOption = studyOption;
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
        this.academicYear = academicYear;
        return this;
    }

    public AdvertStudyOptionInstance withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public AdvertStudyOptionInstance withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("advertStudyOption", advertStudyOption).addProperty("academicYear", academicYear);
    }

}
