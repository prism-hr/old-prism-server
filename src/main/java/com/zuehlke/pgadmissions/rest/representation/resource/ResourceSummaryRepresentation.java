package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceSummaryRepresentation {

    private List<ResourceCountRepresentation> counts;

    private ResourceSummaryPlotRepresentation plot;

    public List<ResourceCountRepresentation> getCounts() {
        return counts;
    }

    public void setCounts(List<ResourceCountRepresentation> counts) {
        this.counts = counts;
    }

    public ResourceSummaryPlotRepresentation getPlot() {
        return plot;
    }

    public void setPlot(ResourceSummaryPlotRepresentation plot) {
        this.plot = plot;
    }

    public static class ResourceCountRepresentation {

        private PrismScope resourceScope;

        private Integer resourceCount;

        public PrismScope getResourceScope() {
            return resourceScope;
        }

        public void setResourceScope(PrismScope resourceScope) {
            this.resourceScope = resourceScope;
        }

        public Integer getResourceCount() {
            return resourceCount;
        }

        public void setResourceCount(Integer resourceCount) {
            this.resourceCount = resourceCount;
        }

        public ResourceCountRepresentation withResourceScope(PrismScope resourceScope) {
            this.resourceScope = resourceScope;
            return this;
        }

        public ResourceCountRepresentation withResourceCount(Integer resourceCount) {
            this.resourceCount = resourceCount;
            return this;
        }

    }

}
