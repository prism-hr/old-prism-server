package com.zuehlke.pgadmissions.domain.display;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;

import javax.persistence.*;

@Entity
@Table(name = "display_property_configuration", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "system_id", "opportunity_type", "display_property_definition_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "opportunity_type", "display_property_definition_id" }),
        @UniqueConstraint(columnNames = { "program_id", "display_property_definition_id" }),
        @UniqueConstraint(columnNames = { "project_id", "display_property_definition_id" }) })
public class DisplayPropertyConfiguration extends WorkflowConfiguration {

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

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "opportunity_type")
    @Enumerated(EnumType.STRING)
    private PrismOpportunityType opportunityType;

    @ManyToOne
    @JoinColumn(name = "display_property_definition_id", nullable = false)
    private DisplayPropertyDefinition displayPropertyDefinition;

    @Lob
    @Column(name = "value", nullable = false)
    private String value;

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
    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public final PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    @Override
    public final void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public final DisplayPropertyDefinition getDisplayPropertyDefinition() {
        return displayPropertyDefinition;
    }

    public final void setDisplayPropertyDefinition(DisplayPropertyDefinition displayPropertyDefinition) {
        this.displayPropertyDefinition = displayPropertyDefinition;
    }

    public final String getValue() {
        return value;
    }

    public final void setValue(String value) {
        this.value = value;
    }

    @Override
    public final Boolean getSystemDefault() {
        return systemDefault;
    }

    @Override
    public final void setSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
    }

    @Override
    public WorkflowDefinition getDefinition() {
        return getDisplayPropertyDefinition();
    }

    public DisplayPropertyConfiguration withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public DisplayPropertyConfiguration withOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public DisplayPropertyConfiguration withDisplayPropertyDefinition(DisplayPropertyDefinition displayPropertyDefinition) {
        this.displayPropertyDefinition = displayPropertyDefinition;
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

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("displayPropertyDefinition", displayPropertyDefinition);
    }

}
