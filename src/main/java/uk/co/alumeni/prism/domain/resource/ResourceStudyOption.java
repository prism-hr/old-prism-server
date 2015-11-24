package uk.co.alumeni.prism.domain.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;

import uk.co.alumeni.prism.api.model.resource.ResourceInstanceGroupDefinition;

@Entity
@Table(name = "resource_study_option", uniqueConstraints = { @UniqueConstraint(columnNames = { "program_id", "study_option" }),
        @UniqueConstraint(columnNames = { "project_id", "study_option" }) })
public class ResourceStudyOption extends ResourceOpportunityAttribute implements ResourceInstanceGroupDefinition<PrismStudyOption> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "program_id", insertable = false, updatable = false)
    private Program program;

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @Column(name = "study_option", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismStudyOption studyOption;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public PrismStudyOption getStudyOption() {
        return studyOption;
    }

    @Override
    public void setStudyOption(PrismStudyOption studyOption) {
        this.studyOption = studyOption;
    }

    public ResourceStudyOption withResource(ResourceParent resource) {
        setResource(resource);
        return this;
    }

    public ResourceStudyOption withStudyOption(PrismStudyOption studyOption) {
        this.studyOption = studyOption;
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
    public UniqueEntity.EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("studyOption", studyOption);
    }

}
