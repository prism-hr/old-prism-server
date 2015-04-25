package com.zuehlke.pgadmissions.domain.program;

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
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.ResourceParentAttribute;

@Entity
@Table(name = "RESOURCE_LOCATION", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "location" }),
        @UniqueConstraint(columnNames = { "program_id", "location" }), @UniqueConstraint(columnNames = { "project_id", "location" }) })
public class ResourceLocation extends ResourceParentAttribute {

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

    @Column(name = "location", nullable = false)
    private String location;

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

    public final String getLocation() {
        return location;
    }

    public final void setLocation(String location) {
        this.location = location;
    }

    public ResourceLocation withLocation(String location) {
        this.location = location;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getResourceParent());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ResourceLocation other = (ResourceLocation) object;
        return Objects.equal(getResourceParent(), other.getResourceParent()) && Objects.equal(location, other.getLocation());
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("location", location);
    }

}
