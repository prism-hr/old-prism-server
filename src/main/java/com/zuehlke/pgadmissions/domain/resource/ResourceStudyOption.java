package com.zuehlke.pgadmissions.domain.resource;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "resource_study_option", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "study_option_id" }),
        @UniqueConstraint(columnNames = { "program_id", "study_option_id" }), @UniqueConstraint(columnNames = { "project_id", "study_option_id" }) })
public class ResourceStudyOption extends ResourceParentAttribute {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_id", insertable = false, updatable = false)
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "program_id", insertable = false, updatable = false)
    private Program program;

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "study_option_id", nullable = false)
    private StudyOption studyOption;

    @Column(name = "application_start_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationStartDate;

    @Column(name = "application_close_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationCloseDate;

    @OneToMany(mappedBy = "studyOption")
    private Set<ResourceStudyOptionInstance> studyOptionInstances = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Institution getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    public StudyOption getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(StudyOption studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getApplicationStartDate() {
        return applicationStartDate;
    }

    public void setApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public LocalDate getApplicationCloseDate() {
        return applicationCloseDate;
    }

    public void setApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    public Set<ResourceStudyOptionInstance> getStudyOptionInstances() {
        return studyOptionInstances;
    }

    public ResourceStudyOption withResource(ResourceParent resource) {
        setResource(resource);
        return this;
    }

    public ResourceStudyOption withStudyOption(StudyOption studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public ResourceStudyOption withApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
        return this;
    }

    public ResourceStudyOption withApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getResource(), studyOption);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ResourceStudyOption other = (ResourceStudyOption) object;
        return Objects.equal(getResource(), other.getResource()) && Objects.equal(studyOption, other.getStudyOption());
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("studyOption", studyOption);
    }

}
