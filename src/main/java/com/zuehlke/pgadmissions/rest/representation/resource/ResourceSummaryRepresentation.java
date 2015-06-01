package com.zuehlke.pgadmissions.rest.representation.resource;

import org.joda.time.LocalDate;

public class ResourceSummaryRepresentation {

    private LocalDate createdDate;

    private Integer programCount;

    private Integer projectCount;

    private ResourceSummaryPlotRepresentation plot;

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getProgramCount() {
        return programCount;
    }

    public void setProgramCount(Integer programCount) {
        this.programCount = programCount;
    }

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    public ResourceSummaryPlotRepresentation getPlot() {
        return plot;
    }

    public void setPlot(ResourceSummaryPlotRepresentation plot) {
        this.plot = plot;
    }
}
