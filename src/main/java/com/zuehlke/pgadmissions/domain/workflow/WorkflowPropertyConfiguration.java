package com.zuehlke.pgadmissions.domain.workflow;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;

@Entity
@Table(name = "WORKFLOW_PROPERTY_CONFIGURATION", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "locale", "program_type", "workflow_property_definition_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "program_type", "workflow_property_definition_id" }),
        @UniqueConstraint(columnNames = { "program_id", "workflow_property_definition_id" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WorkflowPropertyConfiguration extends WorkflowResourceConfiguration {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "system_id")
    private System system;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;
    
    @Column(name = "locale")
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;
    
    @Column(name = "program_type")
    @Enumerated(EnumType.STRING)
    private PrismProgramType programType;
    
    @ManyToOne
    @JoinColumn(name = "workflow_property_definition_id", nullable = false)
    private WorkflowPropertyDefinition workflowPropertyDefinition;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;
    
    @Column(name = "minimum")
    private Integer minimum;
    
    @Column(name = "maximum")
    private Integer maximum;

    @Column(name = "system_default", nullable = false)
    private Boolean systemDefault;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    @Override
    public final System getSystem() {
        return system;
    }

    @Override
    public final void setSystem(System system) {
        this.system = system;
    }

    @Override
    public final Institution getInstitution() {
        return institution;
    }

    @Override
    public final void setInstitution(Institution institution) {
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
    public final PrismLocale getLocale() {
        return locale;
    }

    @Override
    public final void setLocale(PrismLocale locale) {
        this.locale = locale;
    }
    
    @Override
    public final PrismProgramType getProgramType() {
        return programType;
    }

    @Override
    public final void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public final WorkflowPropertyDefinition getWorkflowPropertyDefinition() {
        return workflowPropertyDefinition;
    }

    public final void setWorkflowPropertyDefinition(WorkflowPropertyDefinition workflowPropertyDefinition) {
        this.workflowPropertyDefinition = workflowPropertyDefinition;
    }

    public final Boolean getEnabled() {
        return enabled;
    }

    public final void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public final Integer getMinimum() {
        return minimum;
    }

    public final void setMinimum(Integer minimum) {
        this.minimum = minimum;
    }

    public final Integer getMaximum() {
        return maximum;
    }

    public final void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }
    
    @Override
    public final Boolean getSystemDefault() {
        return systemDefault;
    }

    @Override
    public final void setSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
    }
    
    public WorkflowPropertyConfiguration withResource(Resource resource) {
        setResource(resource);
        return this;
    }
    
    public WorkflowPropertyConfiguration withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }
    
    public WorkflowPropertyConfiguration withProgramType(PrismProgramType programType) {
        this.programType = programType;
        return this;
    }
    
    public WorkflowPropertyConfiguration withWorkflowPropertyDefinition(WorkflowPropertyDefinition workflowPropertyDefinition) {
        this.workflowPropertyDefinition = workflowPropertyDefinition;
        return this;
    }
    
    public WorkflowPropertyConfiguration withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public WorkflowPropertyConfiguration withMinimum(Integer minimum) {
        this.minimum = minimum;
        return this;
    }
    
    public WorkflowPropertyConfiguration withMaximum(Integer maximum) {
        this.maximum = maximum;
        return this;
    }
    
    public WorkflowPropertyConfiguration withSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("workflowPropertyDefinition", workflowPropertyDefinition);
    }

}
