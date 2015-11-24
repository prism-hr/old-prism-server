package uk.co.alumeni.prism.domain.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.domain.workflow.WorkflowResourceExecution;

import java.lang.*;

@Entity
@Table(name = "resource_condition", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "action_condition" }),
        @UniqueConstraint(columnNames = { "institution_id", "action_condition" }), @UniqueConstraint(columnNames = { "department_id", "action_condition" }),
        @UniqueConstraint(columnNames = { "program_id", "action_condition" }), @UniqueConstraint(columnNames = { "project_id", "action_condition" }),
        @UniqueConstraint(columnNames = { "application_id", "action_condition" }) })
public class ResourceCondition extends WorkflowResourceExecution {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id", insertable = false, updatable = false)
    private uk.co.alumeni.prism.domain.resource.System system;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", insertable = false, updatable = false)
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", insertable = false, updatable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", insertable = false, updatable = false)
    private Application application;

    @Column(name = "action_condition", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismActionCondition actionCondition;

    @Column(name = "internal_mode", nullable = false)
    private Boolean internalMode;

    @Column(name = "external_mode", nullable = false)
    private Boolean externalMode;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public uk.co.alumeni.prism.domain.resource.System getSystem() {
        return system;
    }

    @Override
    public void setSystem(uk.co.alumeni.prism.domain.resource.System system) {
        this.system = system;
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
    public Department getDepartment() {
        return department;
    }

    @Override
    public void setDepartment(Department department) {
        this.department = department;
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
    public Application getApplication() {
        return application;
    }

    @Override
    public void setApplication(Application application) {
        this.application = application;
    }

    public PrismActionCondition getActionCondition() {
        return actionCondition;
    }

    public void setActionCondition(PrismActionCondition actionCondition) {
        this.actionCondition = actionCondition;
    }

    public Boolean getInternalMode() {
        return internalMode;
    }

    public void setInternalMode(Boolean internalMode) {
        this.internalMode = internalMode;
    }

    public Boolean getExternalMode() {
        return externalMode;
    }

    public void setExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
    }

    public ResourceCondition withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public ResourceCondition withActionCondition(PrismActionCondition actionCondition) {
        this.actionCondition = actionCondition;
        return this;
    }

    public ResourceCondition withInternalMode(Boolean internalMode) {
        this.internalMode = internalMode;
        return this;
    }

    public ResourceCondition withExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getResource(), actionCondition);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ResourceCondition other = (ResourceCondition) object;
        return Objects.equal(getResource(), other.getResource()) && Objects.equal(actionCondition, other.getActionCondition());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("actionCondition", actionCondition);
    }

}
