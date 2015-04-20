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

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;

@Entity
@Table(name = "DISPLAY_PROPERTY_CONFIGURATION", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "system_id", "locale", "advert_type", "display_property_definition_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "advert_type", "display_property_definition_id" }),
        @UniqueConstraint(columnNames = { "program_id", "display_property_definition_id" }) })
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

    @Column(name = "locale")
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;

    @Column(name = "advert_type")
    @Enumerated(EnumType.STRING)
    private PrismAdvertType advertType;

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
    public final PrismLocale getLocale() {
        return locale;
    }

    @Override
    public final void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    @Override
    public final PrismAdvertType getAdvertType() {
        return advertType;
    }

    @Override
    public final void setAdvertType(PrismAdvertType advertType) {
        this.advertType = advertType;
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

    public DisplayPropertyConfiguration withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }

    public DisplayPropertyConfiguration withAdvertType(PrismAdvertType advertType) {
        this.advertType = advertType;
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
