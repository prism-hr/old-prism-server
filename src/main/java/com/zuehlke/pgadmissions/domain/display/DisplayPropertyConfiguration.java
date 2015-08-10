package com.zuehlke.pgadmissions.domain.display;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;

@Entity
@Table(name = "display_property_configuration", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "system_id", "opportunity_type", "display_property_definition_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "opportunity_type", "display_property_definition_id" }),
        @UniqueConstraint(columnNames = { "department_id", "opportunity_type", "display_property_definition_id" }),
        @UniqueConstraint(columnNames = { "program_id", "display_property_definition_id" }),
        @UniqueConstraint(columnNames = { "project_id", "display_property_definition_id" }) })
public class DisplayPropertyConfiguration extends WorkflowConfiguration<DisplayPropertyDefinition> {

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
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "opportunity_type")
    @Enumerated(EnumType.STRING)
    private PrismOpportunityType opportunityType;

    @ManyToOne
    @JoinColumn(name = "display_property_definition_id", nullable = false)
    private DisplayPropertyDefinition definition;

    @Lob
    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "system_default", nullable = false)
    private Boolean systemDefault;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public System getSystem() {
        return system;
    }

    @Override
    public void setSystem(System system) {
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
    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    @Override
    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    @Override
    public DisplayPropertyDefinition getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(DisplayPropertyDefinition definition) {
        this.definition = definition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Boolean getSystemDefault() {
        return systemDefault;
    }

    @Override
    public void setSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
    }

    public DisplayPropertyConfiguration withResource(Resource<?> resource) {
        setResource(resource);
        return this;
    }

    public DisplayPropertyConfiguration withOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public DisplayPropertyConfiguration withDefinition(DisplayPropertyDefinition definition) {
        this.definition = definition;
        return this;
    }

    public DisplayPropertyConfiguration withValue(String value) {
        this.value = value;
        return this;
    }

    public DisplayPropertyConfiguration withSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
        return this;
    }

}
