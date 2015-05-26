package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.Set;

import com.google.common.base.Objects;

public class ResourceSummaryPlotRepresentation {

    private Set<ResourceSummaryPlotConstraintRepresentation> constraint;

    private ResourceSummaryPlotDataRepresentation data;

    public Set<ResourceSummaryPlotConstraintRepresentation> getConstraint() {
        return constraint;
    }

    public ResourceSummaryPlotDataRepresentation getData() {
        return data;
    }

    public ResourceSummaryPlotRepresentation withConstraint(Set<ResourceSummaryPlotConstraintRepresentation> constraint) {
        this.constraint = constraint;
        return this;
    }

    public ResourceSummaryPlotRepresentation withData(ResourceSummaryPlotDataRepresentation data) {
        this.data = data;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(constraint);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ResourceSummaryPlotRepresentation other = (ResourceSummaryPlotRepresentation) object;
        return Objects.equal(constraint, other.getConstraint());
    }

}
