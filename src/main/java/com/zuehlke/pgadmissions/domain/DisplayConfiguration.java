package com.zuehlke.pgadmissions.domain;

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

import com.zuehlke.pgadmissions.domain.definitions.DisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.DisplayPropertyKey;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

@Entity
@Table(name = "DISPLAY_CONFIGURATION", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "locale", "property_category", "property_key" }),
        @UniqueConstraint(columnNames = { "institution_id", "locale", "property_category", "property_key" }),
        @UniqueConstraint(columnNames = { "program_id", "locale", "property_category", "property_key" }) })
public class DisplayConfiguration extends WorkflowResource {

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

    @Column(name = "locale", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;

    @Column(name = "property_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private DisplayPropertyCategory propertyCategory;

    @Column(name = "property_key", nullable = false)
    @Enumerated(EnumType.STRING)
    private DisplayPropertyKey propertyKey;

    @Column(name = "property_value", nullable = false)
    private String propertyValue;

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

    public final PrismLocale getLocale() {
        return locale;
    }

    public final void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    public final DisplayPropertyCategory getPropertyCategory() {
        return propertyCategory;
    }

    public final void setPropertyCategory(DisplayPropertyCategory propertyCategory) {
        this.propertyCategory = propertyCategory;
    }

    public final DisplayPropertyKey getPropertyKey() {
        return propertyKey;
    }

    public final void setPropertyKey(DisplayPropertyKey propertyKey) {
        this.propertyKey = propertyKey;
    }

    public final String getPropertyValue() {
        return propertyValue;
    }

    public final void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
    
    public DisplayConfiguration withSystem(System system) {
        this.system = system;
        return this;
    }

    public DisplayConfiguration withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public DisplayConfiguration withProgram(Program program) {
        this.program = program;
        return this;
    }

    public DisplayConfiguration withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }

    public DisplayConfiguration withPropertyCategory(DisplayPropertyCategory propertyCategory) {
        this.propertyCategory = propertyCategory;
        return this;
    }

    public DisplayConfiguration withPropertyKey(DisplayPropertyKey propertyKey) {
        this.propertyKey = propertyKey;
        return this;
    }

    public DisplayConfiguration withPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("locale", locale).addProperty("propertyCategory", propertyCategory)
                .addProperty("propertyKey", propertyKey);
    }

}
