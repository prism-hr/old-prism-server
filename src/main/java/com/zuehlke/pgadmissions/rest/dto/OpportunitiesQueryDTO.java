package com.zuehlke.pgadmissions.rest.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;

public class OpportunitiesQueryDTO {

    @Valid
    private ResourceDTO resource;

    @NotNull
    private PrismMotivationContext context;
    
    private PrismScope contextScope;
    
    private Boolean recommended;

    private PrismOpportunityCategory opportunityCategory;

    private String keyword;

    private List<PrismAdvertIndustry> industries;

    private List<PrismAdvertFunction> functions;

    private List<PrismOpportunityType> opportunityTypes;

    private List<PrismStudyOption> studyOptions;

    private Integer minSalary;

    private Integer maxSalary;

    private Integer minDuration;

    private Integer maxDuration;

    private BigDecimal neLat;

    private BigDecimal neLon;

    private BigDecimal swLat;

    private BigDecimal swLon;

    private String lastSequenceIdentifier;

    public ResourceDTO getResource() {
        return resource;
    }

    public void setResource(ResourceDTO resource) {
        this.resource = resource;
    }

    public PrismMotivationContext getContext() {
        return context;
    }

    public void setContext(PrismMotivationContext context) {
        this.context = context;
    }
    
    public PrismScope getContextScope() {
        return contextScope;
    }

    public void setContextScope(PrismScope contextScope) {
        this.contextScope = contextScope;
    }

    public Boolean getRecommended() {
        return recommended;
    }

    public void setRecommended(Boolean recommended) {
        this.recommended = recommended;
    }

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<PrismAdvertIndustry> getIndustries() {
        return industries;
    }

    public void setIndustries(List<PrismAdvertIndustry> industries) {
        this.industries = industries;
    }

    public List<PrismAdvertFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<PrismAdvertFunction> functions) {
        this.functions = functions;
    }

    public List<PrismOpportunityType> getOpportunityTypes() {
        return opportunityTypes;
    }

    public void setOpportunityTypes(List<PrismOpportunityType> opportunityTypes) {
        this.opportunityTypes = opportunityTypes;
    }

    public List<PrismStudyOption> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<PrismStudyOption> studyOptions) {
        this.studyOptions = studyOptions;
    }

    public Integer getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Integer minSalary) {
        this.minSalary = minSalary;
    }

    public Integer getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Integer maxSalary) {
        this.maxSalary = maxSalary;
    }

    public Integer getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(Integer minDuration) {
        this.minDuration = minDuration;
    }

    public Integer getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Integer maxDuration) {
        this.maxDuration = maxDuration;
    }

    public BigDecimal getNeLat() {
        return neLat;
    }

    public void setNeLat(BigDecimal neLat) {
        this.neLat = neLat;
    }

    public BigDecimal getNeLon() {
        return neLon;
    }

    public void setNeLon(BigDecimal neLon) {
        this.neLon = neLon;
    }

    public BigDecimal getSwLat() {
        return swLat;
    }

    public void setSwLat(BigDecimal swLat) {
        this.swLat = swLat;
    }

    public BigDecimal getSwLon() {
        return swLon;
    }

    public void setSwLon(BigDecimal swLon) {
        this.swLon = swLon;
    }

    public String getLastSequenceIdentifier() {
        return lastSequenceIdentifier;
    }

    public void setLastSequenceIdentifier(String lastSequenceIdentifier) {
        this.lastSequenceIdentifier = lastSequenceIdentifier;
    }

}
