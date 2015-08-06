package com.zuehlke.pgadmissions.domain.workflow;

import java.math.BigDecimal;

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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismCustomQuestionType;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.resource.department.Department;

@Entity
@Table(name = "action_custom_question_configuration", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "system_id", "opportunity_type", "action_custom_question_definition_id", "version", "display_index" }),
        @UniqueConstraint(columnNames = { "institution_id", "opportunity_type", "action_custom_question_definition_id", "version", "display_index" }),
        @UniqueConstraint(columnNames = { "department_id", "opportunity_type", "action_custom_question_definition_id", "version", "display_index" }),
        @UniqueConstraint(columnNames = { "program_id", "action_custom_question_definition_id", "version", "display_index" }),
        @UniqueConstraint(columnNames = { "project_id", "action_custom_question_definition_id", "version", "display_index" }) })
public class ActionCustomQuestionConfiguration extends WorkflowConfigurationVersioned<ActionCustomQuestionDefinition> {

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
    @JoinColumn(name = "action_custom_question_definition_id", nullable = false)
    private ActionCustomQuestionDefinition definition;

    @Column(name = "version")
    private Integer version;

    @Column(name = "custom_question_type")
    @Enumerated(EnumType.STRING)
    private PrismCustomQuestionType customQuestionType;

    @Lob
    @Column(name = "display_name", nullable = false)
    private String component;

    @Column(name = "display_editable", nullable = false)
    private Boolean editable;

    @Column(name = "display_index", nullable = false)
    private Integer index;

    @Lob
    @Column(name = "display_label", nullable = false)
    private String label;

    @Lob
    @Column(name = "display_description")
    private String description;

    @Lob
    @Column(name = "display_placeholder")
    private String placeholder;

    @Lob
    @Column(name = "display_options")
    private String options;

    @Column(name = "display_required", nullable = false)
    private Boolean required;

    @Lob
    @Column(name = "display_validation")
    private String validation;

    @Column(name = "display_weighting")
    private BigDecimal weighting;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "system_default", nullable = false)
    private Boolean systemDefault;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
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
    public ActionCustomQuestionDefinition getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(ActionCustomQuestionDefinition Definition) {
        this.definition = Definition;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    public PrismCustomQuestionType getCustomQuestionType() {
        return customQuestionType;
    }

    public void setCustomQuestionType(PrismCustomQuestionType customQuestionType) {
        this.customQuestionType = customQuestionType;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public BigDecimal getWeighting() {
        return weighting;
    }

    public void setWeighting(BigDecimal weighting) {
        this.weighting = weighting;
    }

    @Override
    public Boolean getActive() {
        return active;
    }

    @Override
    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public Boolean getSystemDefault() {
        return systemDefault;
    }

    @Override
    public void setSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
    }

    public ActionCustomQuestionConfiguration withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public ActionCustomQuestionConfiguration withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public ActionCustomQuestionConfiguration withProgram(Program program) {
        this.program = program;
        return this;
    }

    public ActionCustomQuestionConfiguration withOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public ActionCustomQuestionConfiguration withDefinition(ActionCustomQuestionDefinition definition) {
        this.definition = definition;
        return this;
    }

    public ActionCustomQuestionConfiguration withCustomQuestionType(PrismCustomQuestionType customQuestionType) {
        this.customQuestionType = customQuestionType;
        return this;
    }

    public ActionCustomQuestionConfiguration withVersion(Integer version) {
        this.version = version;
        return this;
    }

    public ActionCustomQuestionConfiguration withName(String name) {
        this.component = name;
        return this;
    }

    public ActionCustomQuestionConfiguration withEditable(Boolean editable) {
        this.editable = editable;
        return this;
    }

    public ActionCustomQuestionConfiguration withIndex(Integer index) {
        this.index = index;
        return this;
    }

    public ActionCustomQuestionConfiguration withLabel(String label) {
        this.label = label;
        return this;
    }

    public ActionCustomQuestionConfiguration withDescription(String description) {
        this.description = description;
        return this;
    }

    public ActionCustomQuestionConfiguration withPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public ActionCustomQuestionConfiguration withOptions(String options) {
        this.options = options;
        return this;
    }

    public ActionCustomQuestionConfiguration withRequired(Boolean required) {
        this.required = required;
        return this;
    }

    public ActionCustomQuestionConfiguration withValidation(String validation) {
        this.validation = validation;
        return this;
    }

    public ActionCustomQuestionConfiguration withWeighting(BigDecimal weighting) {
        this.weighting = weighting;
        return this;
    }

    public ActionCustomQuestionConfiguration withActive(Boolean active) {
        this.active = active;
        return this;
    }

    public ActionCustomQuestionConfiguration withSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
        return this;
    }

}
