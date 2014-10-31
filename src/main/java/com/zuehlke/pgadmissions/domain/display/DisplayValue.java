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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResourceConfiguration;

@Entity
@Table(name = "DISPLAY_VALUE", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "locale", "program_type", "display_property_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "program_type", "display_property_id" }),
        @UniqueConstraint(columnNames = { "program_id", "display_property_id" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DisplayValue extends WorkflowResourceConfiguration {

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
    @JoinColumn(name = "display_property_id", nullable = false)
    private DisplayProperty displayProperty;

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
    public final PrismProgramType getProgramType() {
        return programType;
    }

    @Override
    public final void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public final DisplayProperty getDisplayProperty() {
        return displayProperty;
    }

    public final void setDisplayProperty(DisplayProperty displayProperty) {
        this.displayProperty = displayProperty;
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
    
    public DisplayValue withResource(Resource resource) {
        setResource(resource);
        return this;
    }
    
    public DisplayValue withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }
    
    public DisplayValue withProgramType(PrismProgramType programType) {
        this.programType = programType;
        return this;
    }
    
    public DisplayValue withDisplayProperty(DisplayProperty displayProperty) {
        this.displayProperty = displayProperty;
        return this;
    }
    
    public DisplayValue withValue(String value) {
        this.value = value;
        return this;
    }
    
    public DisplayValue withSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("displayProperty", displayProperty);
    }

}
