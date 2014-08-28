package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Entity
@Table(name = "PROGRAM_STUDY_OPTION", uniqueConstraints = @UniqueConstraint(columnNames = { "program_id", "study_option_id" }))
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProgramStudyOption implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne
    @JoinColumn(name = "study_option_id", nullable = false)
    private StudyOption studyOption;

    @Column(name = "application_start_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationStartDate;

    @Column(name = "application_close_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationCloseDate;
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToMany(mappedBy = "programStudyOption")
    private Set<ProgramStudyOptionInstance> studyOptionInstances = Sets.newHashSet();

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final Program getProgram() {
        return program;
    }

    public final void setProgram(Program program) {
        this.program = program;
    }

    public final StudyOption getStudyOption() {
        return studyOption;
    }

    public final void setStudyOption(StudyOption studyOption) {
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

    public final Boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public final Set<ProgramStudyOptionInstance> getStudyOptionInstances() {
        return studyOptionInstances;
    }

    public ProgramStudyOption withProgram(Program program) {
        this.program = program;
        return this;
    }

    public ProgramStudyOption withStudyOption(StudyOption studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public ProgramStudyOption withApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
        return this;
    }

    public ProgramStudyOption withApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
        return this;
    }
    
    public ProgramStudyOption withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("program", program);
        properties.put("studyOption", studyOption);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }

}
