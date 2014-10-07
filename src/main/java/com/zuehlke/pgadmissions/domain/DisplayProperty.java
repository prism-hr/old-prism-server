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

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

@Entity
@Table(name = "DISPLAY_PROPERTY", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "locale", "property_category", "property_key" }),
        @UniqueConstraint(columnNames = { "institution_id", "property_category", "property_key" }),
        @UniqueConstraint(columnNames = { "program_id", "property_category", "property_key" }) })
public class DisplayProperty extends WorkflowResource {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "system_id")
    private System system;

    @Column(name = "locale", nullable = false)
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
    
    @Column(name = "property_default", nullable = false)
    private Boolean propertyDefault;

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

    public final Boolean getPropertyDefault() {
        return propertyDefault;
    }

    public final void setPropertyDefault(Boolean propertyDefault) {
        this.propertyDefault = propertyDefault;
    }

    public DisplayProperty withResource(Resource resource) {
        setResource(resource);
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
    
    public DisplayProperty withPropertyDefault(Boolean propertyDefault) {
        this.propertyDefault = propertyDefault;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        ResourceSignature signature = super.getResourceSignature();
        if (system != null) {
            signature.addProperty("locale", locale);
        }
        return signature.addProperty("propertyCategory", displayCategory).addProperty("propertyIndex", propertyIndex);
    }

}
