package com.zuehlke.pgadmissions.domain.resource;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;

import uk.co.alumeni.prism.api.model.resource.ResourceInstanceGroupDefinition;

@Entity
@Table(name = "resource_study_option", uniqueConstraints = { @UniqueConstraint(columnNames = { "program_id", "imported_study_option_id" }),
        @UniqueConstraint(columnNames = { "project_id", "imported_study_option_id" }) })
public class ResourceStudyOption extends ResourceOpportunityAttribute implements
        ResourceInstanceGroupDefinition<ImportedEntitySimple, ResourceStudyOptionInstance> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "program_id", insertable = false, updatable = false)
    private Program program;

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "imported_study_option_id", nullable = false)
    private ImportedEntitySimple studyOption;

    @Column(name = "application_start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationStartDate;

    @Column(name = "application_close_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationCloseDate;

    @OneToMany(mappedBy = "studyOption")
    private Set<ResourceStudyOptionInstance> instances = Sets.newHashSet();

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
    public ImportedEntitySimple getStudyOption() {
        return studyOption;
    }

    @Override
    public void setStudyOption(ImportedEntitySimple studyOption) {
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

    @Override
    public Set<ResourceStudyOptionInstance> getInstances() {
        return instances;
    }

    @Override
    public void setInstances(Set<ResourceStudyOptionInstance> instances) {
        this.instances = instances;
    }

    public ResourceStudyOption withResource(ResourceParent<?> resource) {
        setResource(resource);
        return this;
    }

    public ResourceStudyOption withStudyOption(ImportedEntitySimple studyOption) {
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
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("studyOption", studyOption);
    }

}
