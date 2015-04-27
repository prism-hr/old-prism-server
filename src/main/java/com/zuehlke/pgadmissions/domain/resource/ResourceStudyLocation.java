package com.zuehlke.pgadmissions.domain.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;

@Entity
@Table(name = "RESOURCE_STUDY_LOCATION", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "study_location" }),
        @UniqueConstraint(columnNames = { "program_id", "study_location" }), @UniqueConstraint(columnNames = { "project_id", "study_location" }) })
public class ResourceStudyLocation extends ResourceParentAttribute {

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

    @Column(name = "study_location", nullable = false)
    private String studyLocation;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
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
    public final Program getProgram() {
        return program;
    }

    @Override
    public final void setProgram(Program program) {
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

    public final String getStudyLocation() {
        return studyLocation;
    }

    public final void setStudyLocation(String studyLocation) {
        this.studyLocation = studyLocation;
    }

    public ResourceStudyLocation withResource(ResourceParent resource) {
        setResource(resource);
        return this;
    }

    public ResourceStudyLocation withStudyLocation(String studyLocation) {
        this.studyLocation = studyLocation;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getResource(), studyLocation);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ResourceStudyLocation other = (ResourceStudyLocation) object;
        return Objects.equal(getResource(), other.getResource()) && Objects.equal(studyLocation, other.getStudyLocation());
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("studyLocation", studyLocation);
    }

}
