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

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismCustomQuestionType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;

@Entity
@Table(name = "ACTION_CUSTOM_QUESTION_CONFIGURATION", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"system_id", "advert_type", "locale", "action_custom_question_definition_id", "version", "display_index"}),
        @UniqueConstraint(columnNames = {"institution_id", "advert_type", "action_custom_question_definition_id", "version", "display_index"}),
        @UniqueConstraint(columnNames = {"program_id", "action_custom_question_definition_id", "version", "display_index"})})
public class ActionCustomQuestionConfiguration extends WorkflowConfigurationVersioned {

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

    @Column(name = "advert_type")
    @Enumerated(EnumType.STRING)
    private PrismAdvertType advertType;

    @ManyToOne
    @JoinColumn(name = "action_custom_question_definition_id", nullable = false)
    private ActionCustomQuestionDefinition actionCustomQuestionDefinition;

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
    public Program getProgram() {
        return program;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public PrismLocale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    @Override
    public PrismAdvertType getAdvertType() {
        return advertType;
    }

    @Override
    public void setAdvertType(PrismAdvertType advertType) {
        this.advertType = advertType;
    }

    public ActionCustomQuestionDefinition getActionCustomQuestionDefinition() {
        return actionCustomQuestionDefinition;
    }

    public void setActionCustomQuestionDefinition(ActionCustomQuestionDefinition actionCustomQuestionDefinition) {
        this.actionCustomQuestionDefinition = actionCustomQuestionDefinition;
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

    @Override
    public WorkflowDefinition getDefinition() {
        return getActionCustomQuestionDefinition();
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

    public ActionCustomQuestionConfiguration withAdvertType(PrismAdvertType advertType) {
        this.advertType = advertType;
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

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("actionCustomQuestionDefinition", actionCustomQuestionDefinition).addProperty("version", version)
                .addProperty("index", index);
    }

}
