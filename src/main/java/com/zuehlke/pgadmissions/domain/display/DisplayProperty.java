package com.zuehlke.pgadmissions.domain.display;

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

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResourceConfiguration;

@Entity
@Table(name = "DISPLAY_PROPERTY", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "program_type", "locale", "property_index" }),
        @UniqueConstraint(columnNames = { "institution_id", "program_type", "property_index" }),
        @UniqueConstraint(columnNames = { "program_id", "property_index" }) })
public class DisplayProperty extends WorkflowResourceConfiguration {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "system_id")
    private System system;

    @Column(name = "program_type")
    @Enumerated(EnumType.STRING)
    private PrismProgramType programType;

    @Column(name = "locale")
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "display_category_id")
    private DisplayCategory displayCategory;

    @Column(name = "property_index", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismDisplayProperty propertyIndex;

    @Column(name = "property_value", nullable = false)
    private String propertyValue;

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
    public final PrismProgramType getProgramType() {
        return programType;
    }

    @Override
    public final void setProgramType(PrismProgramType programType) {
        this.programType = programType;
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

    public final DisplayCategory getDisplayCategory() {
        return displayCategory;
    }

    public final void setDisplayCategory(DisplayCategory displayCategory) {
        this.displayCategory = displayCategory;
    }

    public final PrismDisplayProperty getPropertyIndex() {
        return propertyIndex;
    }

    public final void setPropertyIndex(PrismDisplayProperty propertyIndex) {
        this.propertyIndex = propertyIndex;
    }

    public final String getPropertyValue() {
        return propertyValue;
    }

    public final void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    @Override
    public final Boolean getSystemDefault() {
        return systemDefault;
    }

    @Override
    public final void setSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
    }
    
    public DisplayProperty withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public DisplayProperty withProgramType(PrismProgramType programType) {
        this.programType = programType;
        return this;
    }

    public DisplayProperty withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }

    public DisplayProperty withDisplayCategory(DisplayCategory displayCategory) {
        this.displayCategory = displayCategory;
        return this;
    }

    public DisplayProperty withPropertyIndex(PrismDisplayProperty propertyIndex) {
        this.propertyIndex = propertyIndex;
        return this;
    }

    public DisplayProperty withPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
        return this;
    }

    public DisplayProperty withSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("propertyIndex", propertyIndex);
    }

}
