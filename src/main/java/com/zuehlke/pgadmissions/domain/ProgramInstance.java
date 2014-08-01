package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = "PROGRAM_INSTANCE", uniqueConstraints = @UniqueConstraint(columnNames = { "program_id", "academic_year", "study_option_id" }))
public class ProgramInstance implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;
    
    @ManyToOne
    @JoinColumn(name = "study_option_id", nullable = false)
    private StudyOption studyOption;

    @Column(name = "deadline", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationDeadline;

    @Column(name = "start_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationStartDate;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "sequence_identifier", nullable = false)
    private String identifier;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public LocalDate getApplicationStartDate() {
        return applicationStartDate;
    }

    public void setApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public StudyOption getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(StudyOption studyOption) {
        this.studyOption = studyOption;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ProgramInstance withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public ProgramInstance withAcademicYear(String academicYear) {
        this.academicYear = academicYear;
        return this;
    }
    
    public ProgramInstance withStudyOption(StudyOption studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public ProgramInstance withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public ProgramInstance withApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
        return this;
    }

    public ProgramInstance withApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
        return this;
    }

    public ProgramInstance withProgram(Program program) {
        this.program = program;
        return this;
    }

    public boolean isDateWithinBounds(LocalDate date) {
        boolean afterStartDate = !date.isBefore(getApplicationStartDate());
        boolean beforeEndDate = date.isBefore(getApplicationDeadline());
        return afterStartDate && beforeEndDate;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier, academicYear, studyOption, enabled, applicationStartDate, applicationDeadline);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProgramInstance other = (ProgramInstance) obj;
        return Objects.equal(this.identifier, other.identifier) //
                && Objects.equal(this.academicYear, other.academicYear)
                && Objects.equal(this.studyOption, other.studyOption)
                && Objects.equal(this.enabled, other.enabled)
                && Objects.equal(this.applicationStartDate, other.applicationStartDate)
                && Objects.equal(this.applicationDeadline, other.applicationDeadline);
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("program", program);
        properties.put("academicYear", academicYear);
        properties.put("studyOption", studyOption);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }

}
