package uk.co.alumeni.prism.rest.dto;

import uk.co.alumeni.prism.domain.definitions.*;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class OpportunityQueryDTO {

    private Integer advertId;

    @NotNull
    private PrismResourceContext context;

    private PrismScope resourceScope;

    private Integer resourceId;

    private PrismScope contextScope;

    private PrismOpportunityCategory opportunityCategory;

    private Boolean recommendation;

    private String keyword;

    private List<Integer> locations;

    private Boolean ignoreLocations;

    private List<PrismAdvertIndustry> industries;

    private Boolean ignoreIndustries;

    private List<PrismAdvertFunction> functions;

    private Boolean ignoreFunctions;

    private List<Integer> themes;

    private Boolean ignoreThemes;

    private List<Integer> institutions;

    private Boolean ignoreInstitutions;

    private List<PrismOpportunityType> opportunityTypes;

    private List<PrismStudyOption> studyOptions;

    private PrismDurationUnit salaryInterval;

    private BigDecimal minSalary;

    private BigDecimal maxSalary;

    private Integer minDuration;

    private Integer maxDuration;

    private BigDecimal neLat;

    private BigDecimal neLon;

    private BigDecimal swLat;

    private BigDecimal swLon;

    private String lastSequenceIdentifier;

    @Max(25)
    @Min(1)
    private Integer maxAdverts = 25;

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

    public PrismResourceContext getContext() {
        return context;
    }

    public void setContext(PrismResourceContext context) {
        this.context = context;
    }

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public PrismScope getContextScope() {
        return contextScope;
    }

    public void setContextScope(PrismScope contextScope) {
        this.contextScope = contextScope;
    }

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }

    public Boolean getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(Boolean recommendation) {
        this.recommendation = recommendation;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Integer> getLocations() {
        return locations;
    }

    public void setLocations(List<Integer> locations) {
        this.locations = locations;
    }

    public Boolean getIgnoreLocations() {
        return ignoreLocations;
    }

    public void setIgnoreLocations(Boolean ignoreLocations) {
        this.ignoreLocations = ignoreLocations;
    }

    public List<PrismAdvertIndustry> getIndustries() {
        return industries;
    }

    public void setIndustries(List<PrismAdvertIndustry> industries) {
        this.industries = industries;
    }

    public Boolean getIgnoreIndustries() {
        return ignoreIndustries;
    }

    public void setIgnoreIndustries(Boolean ignoreIndustries) {
        this.ignoreIndustries = ignoreIndustries;
    }

    public List<PrismAdvertFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<PrismAdvertFunction> functions) {
        this.functions = functions;
    }

    public Boolean getIgnoreFunctions() {
        return ignoreFunctions;
    }

    public void setIgnoreFunctions(Boolean ignoreFunctions) {
        this.ignoreFunctions = ignoreFunctions;
    }

    public List<Integer> getThemes() {
        return themes;
    }

    public void setThemes(List<Integer> themes) {
        this.themes = themes;
    }

    public Boolean getIgnoreThemes() {
        return ignoreThemes;
    }

    public void setIgnoreThemes(Boolean ignoreThemes) {
        this.ignoreThemes = ignoreThemes;
    }

    public List<Integer> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<Integer> institutions) {
        this.institutions = institutions;
    }

    public Boolean getIgnoreInstitutions() {
        return ignoreInstitutions;
    }

    public void setIgnoreInstitutions(Boolean ignoreInstitutions) {
        this.ignoreInstitutions = ignoreInstitutions;
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

    public PrismDurationUnit getSalaryInterval() {
        return salaryInterval;
    }

    public void setSalaryInterval(PrismDurationUnit salaryInterval) {
        this.salaryInterval = salaryInterval;
    }

    public BigDecimal getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }

    public BigDecimal getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(BigDecimal maxSalary) {
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

    public Integer getMaxAdverts() {
        return maxAdverts;
    }

    public void setMaxAdverts(Integer maxAdverts) {
        this.maxAdverts = maxAdverts;
    }

    public OpportunityQueryDTO withContext(PrismResourceContext context) {
        this.context = context;
        return this;
    }

    public OpportunityQueryDTO withRecommendation(Boolean recommendation) {
        this.recommendation = recommendation;
        return this;
    }

    public OpportunityQueryDTO withResourceScope(final PrismScope resourceScope) {
        this.resourceScope = resourceScope;
        return this;
    }

    public OpportunityQueryDTO withResourceId(final Integer resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public OpportunityQueryDTO withMaxAdverts(final Integer maxAdverts) {
        this.maxAdverts = maxAdverts;
        return this;
    }

    public OpportunityQueryDTO withIgnoreLocations(Boolean ignoreLocations) {
        this.ignoreLocations = ignoreLocations;
        return this;
    }

    public OpportunityQueryDTO withIgnoreIndustries(Boolean ignoreIndustries) {
        this.ignoreIndustries = ignoreIndustries;
        return this;
    }

    public OpportunityQueryDTO withIgnoreFunctions(Boolean ignoreFunctions) {
        this.ignoreFunctions = ignoreFunctions;
        return this;
    }

    public OpportunityQueryDTO withIgnoreThemes(Boolean ignoreThemes) {
        this.ignoreThemes = ignoreThemes;
        return this;
    }

    public OpportunityQueryDTO withIgnoreInstitutions(Boolean ignoreInstitutions) {
        this.ignoreInstitutions = ignoreInstitutions;
        return this;
    }

}
