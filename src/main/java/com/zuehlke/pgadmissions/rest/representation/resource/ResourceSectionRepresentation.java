package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.LinkedHashSet;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocalizableDefinition;

public class ResourceSectionRepresentation implements PrismLocalizableDefinition {

    private PrismDisplayPropertyDefinition displayProperty;

    private PrismDisplayPropertyDefinition incompleteExplanation;

    private LinkedHashSet<ResourceSectionRepresentation> subsections;

    public boolean isRequired() {
        return incompleteExplanation != null;
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return displayProperty;
    }

    public PrismDisplayPropertyDefinition getIncompleteExplanation() {
        return incompleteExplanation;
    }

    public LinkedHashSet<ResourceSectionRepresentation> getSubsections() {
        return subsections;
    }

    public ResourceSectionRepresentation withDisplayProperty(PrismDisplayPropertyDefinition displayProperty) {
        this.displayProperty = displayProperty;
        return this;
    }

    public ResourceSectionRepresentation withIncompleteExplanation(PrismDisplayPropertyDefinition incompleteExplanation) {
        this.incompleteExplanation = incompleteExplanation;
        return this;
    }

    public ResourceSectionRepresentation withSubsections(LinkedHashSet<ResourceSectionRepresentation> subsections) {
        this.subsections = subsections;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(displayProperty);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ResourceSectionRepresentation other = (ResourceSectionRepresentation) object;
        return Objects.equal(displayProperty, other.getDisplayProperty());
    }

}
