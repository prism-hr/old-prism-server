package com.zuehlke.pgadmissions.rest.representation.resource;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocalizableDefinition;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceCompletenessEvaluator;

import java.util.Arrays;
import java.util.List;

public class ResourceSectionRepresentation implements PrismLocalizableDefinition {

    private PrismDisplayPropertyDefinition displayProperty;

    private Class<? extends ResourceCompletenessEvaluator<?>> completenessEvaluator;

    private PrismDisplayPropertyDefinition incompleteExplanation;

    private List<ResourceSectionRepresentation> subsections;

    public boolean isRequired() {
        return completenessEvaluator != null;
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return displayProperty;
    }

    public Class<? extends ResourceCompletenessEvaluator<?>> getCompletenessEvaluator() {
        return completenessEvaluator;
    }

    public PrismDisplayPropertyDefinition getIncompleteExplanation() {
        return incompleteExplanation;
    }

    public List<ResourceSectionRepresentation> getSubsections() {
        return subsections;
    }

    public ResourceSectionRepresentation withDisplayProperty(PrismDisplayPropertyDefinition displayProperty) {
        this.displayProperty = displayProperty;
        return this;
    }

    public ResourceSectionRepresentation withCompletenessEvaluator(Class<? extends ResourceCompletenessEvaluator<?>> completenessEvaluator) {
        this.completenessEvaluator = completenessEvaluator;
        return this;
    }

    public ResourceSectionRepresentation withIncompleteExplanation(PrismDisplayPropertyDefinition incompleteExplanation) {
        this.incompleteExplanation = incompleteExplanation;
        return this;
    }

    public ResourceSectionRepresentation withSubsections(ResourceSectionRepresentation... subsections) {
        return withSubsections(Arrays.asList(subsections));
    }

    public ResourceSectionRepresentation withSubsections(List<ResourceSectionRepresentation> subsections) {
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
