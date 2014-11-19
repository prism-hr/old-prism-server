package com.zuehlke.pgadmissions.domain.workflow;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismCustomQuestionType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;

@Entity
@Table(name = "ACTION_CUSTOM_QUESTION_CONFIGURATION", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "system_id", "program_type", "locale", "action_custom_question_definition_id", "version", "display_index" }),
        @UniqueConstraint(columnNames = { "institution_id", "program_type", "action_custom_question_definition_id", "version", "display_index" }),
        @UniqueConstraint(columnNames = { "program_id", "action_custom_question_definition_id", "version", "display_index" }) })
public class ActionCustomQuestionConfiguration extends WorkflowConfigurationVersioned {

    @Id
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
    @JoinColumn(name = "action_custom_question_definition_id", nullable = false)
    private ActionCustomQuestionDefinition actionCustomQuestionDefinition;

    @Column(name = "version")
    private Integer version;

    @Column(name = "custom_question_type")
    @Enumerated(EnumType.STRING)
    private PrismCustomQuestionType customQuestionType;

    @Column(name = "display_name", nullable = false)
    private String name;

    @Column(name = "display_editable", nullable = false)
    private Boolean editable;

    @Column(name = "display_index", nullable = false)
    private Integer index;

    @Column(name = "display_label", nullable = false)
    private String label;

    @Column(name = "display_description")
    private String description;

    @Column(name = "display_placeholder")
    private String placeholder;

    @Column(name = "display_options")
    private String options;

    @Column(name = "display_required", nullable = false)
    private Boolean required;

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
    public Program getProgram() {
        return program;
    }

    @Override
    public void setProgram(Program program) {
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

    public final ActionCustomQuestionDefinition getActionCustomQuestionDefinition() {
        return actionCustomQuestionDefinition;
    }

    public final void setActionCustomQuestionDefinition(ActionCustomQuestionDefinition actionCustomQuestionDefinition) {
        this.actionCustomQuestionDefinition = actionCustomQuestionDefinition;
    }

    @Override
    public final Integer getVersion() {
        return version;
    }

    @Override
    public final void setVersion(Integer version) {
        this.version = version;
    }

    public final PrismCustomQuestionType getCustomQuestionType() {
        return customQuestionType;
    }

    public final void setCustomQuestionType(PrismCustomQuestionType customQuestionType) {
        this.customQuestionType = customQuestionType;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final Boolean getEditable() {
        return editable;
    }

    public final void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public final Integer getIndex() {
        return index;
    }

    public final void setIndex(Integer index) {
        this.index = index;
    }

    public final String getLabel() {
        return label;
    }

    public final void setLabel(String label) {
        this.label = label;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final String getPlaceholder() {
        return placeholder;
    }

    public final void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public final String getOptions() {
        return options;
    }

    public final void setOptions(String options) {
        this.options = options;
    }

    public final Boolean getRequired() {
        return required;
    }

    public final void setRequired(Boolean required) {
        this.required = required;
    }

    public final String getValidation() {
        return validation;
    }

    public final void setValidation(String validation) {
        this.validation = validation;
    }

    public final BigDecimal getWeighting() {
        return weighting;
    }

    public final void setWeighting(BigDecimal weighting) {
        this.weighting = weighting;
    }
    
    @Override
    public final Boolean getActive() {
        return active;
    }

    @Override
    public final void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public final Boolean getSystemDefault() {
        return systemDefault;
    }

    @Override
    public final void setSystemDefault(Boolean systemDefault) {
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

    public ActionCustomQuestionConfiguration withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }

    public ActionCustomQuestionConfiguration withProgramType(PrismProgramType programType) {
        this.programType = programType;
        return this;
    }

    public ActionCustomQuestionConfiguration withActionCustomQuestionDefinition(ActionCustomQuestionDefinition actionCustomQuestionDefinition) {
        this.actionCustomQuestionDefinition = actionCustomQuestionDefinition;
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
        this.name = name;
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

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("actionCustomQuestionDefinition", actionCustomQuestionDefinition).addProperty("version", version)
                .addProperty("index", index);
    }

}
