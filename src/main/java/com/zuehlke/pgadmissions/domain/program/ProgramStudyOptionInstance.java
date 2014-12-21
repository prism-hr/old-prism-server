package com.zuehlke.pgadmissions.domain.program;

import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name = "PROGRAM_STUDY_OPTION_INSTANCE", uniqueConstraints = @UniqueConstraint(columnNames = { "program_study_option_id", "academic_year" }) )
public class ProgramStudyOptionInstance implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "program_study_option_id", nullable = false)
    private ProgramStudyOption studyOption;

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

    public final ProgramStudyOption getStudyOption() {
        return studyOption;
    }

    public final void setStudyOption(ProgramStudyOption studyOption) {
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

    public ProgramStudyOptionInstance withStudyOption(ProgramStudyOption studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public ProgramStudyOptionInstance withApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
        return this;
    }

    public ProgramStudyOptionInstance withApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
        return this;
    }

    public ProgramStudyOptionInstance withAcademicYear(String academicYear) {
        this.academicYear = academicYear;
        return this;
    }

    public ProgramStudyOptionInstance withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public ProgramStudyOptionInstance withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("studyOption", studyOption).addProperty("academicYear", academicYear);
    }

}
