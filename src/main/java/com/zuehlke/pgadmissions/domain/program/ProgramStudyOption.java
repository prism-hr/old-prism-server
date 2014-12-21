package com.zuehlke.pgadmissions.domain.program;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "PROGRAM_STUDY_OPTION", uniqueConstraints = @UniqueConstraint(columnNames = { "program_id", "study_option_id" }))
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

    @OneToMany(mappedBy = "studyOption")
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
        return new ResourceSignature().addProperty("program", program).addProperty("studyOption", studyOption);
    }

}
