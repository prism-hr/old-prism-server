package com.zuehlke.pgadmissions.rest.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertContext;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

public class OpportunitiesQueryDTO {

    private Integer resourceId;

    private PrismAction actionId;

    private Integer[] institutions;

    private Integer[] departments;

    private Integer[] programs;

    private Integer[] projects;

    @NotNull
    private PrismAdvertContext context;

    private PrismOpportunityCategory opportunityCategory;

    private PrismScope scope;

    private String keyword;

    private List<PrismAdvertIndustry> industries;

    private List<PrismAdvertFunction> functions;

    private List<String> themes;

    private List<PrismOpportunityType> opportunityTypes;

    private List<PrismStudyOption> studyOptions;

    private Integer minFee;

    private Integer maxFee;

    private Integer minSalary;

    private Integer maxSalary;

    private Integer minDuration;

    private Integer maxDuration;

    private BigDecimal neLat;

    private BigDecimal neLon;

    private BigDecimal swLat;

    private BigDecimal swLon;

    private String lastSequenceIdentifier;

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public Integer[] getInstitutions() {
        return institutions;
    }

    public void setInstitutions(Integer[] institutions) {
        this.institutions = institutions;
    }

    public Integer[] getDepartments() {
        return departments;
    }

    public void setDepartments(Integer[] departments) {
        this.departments = departments;
    }

    public Integer[] getPrograms() {
        return programs;
    }

    public void setPrograms(Integer[] programs) {
        this.programs = programs;
    }

    public Integer[] getProjects() {
        return projects;
    }

    public void setProjects(Integer[] projects) {
        this.projects = projects;
    }

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }

    public PrismScope getScope() {
        return scope;
    }

    public void setScope(PrismScope scope) {
        this.scope = scope;
    }

    public PrismAdvertContext getContext() {
        return context;
    }

    public void setContext(PrismAdvertContext context) {
        this.context = context;
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

    public List<String> getThemes() {
        return themes;
    }

    public void setThemes(List<String> themes) {
        this.themes = themes;
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

    public Integer getMinFee() {
        return minFee;
    }

    public void setMinFee(Integer minFee) {
        this.minFee = minFee;
    }

    public Integer getMaxFee() {
        return maxFee;
    }

    public void setMaxFee(Integer maxFee) {
        this.maxFee = maxFee;
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

    public boolean isResourceAction() {
        return !(resourceId == null || actionId == null);
    }

    public boolean isNarrowed() {
        return !(institutions == null && departments == null && programs == null && projects == null);
    }

    public Integer[] getResources(PrismScope resourceScope) {
        if (ResourceParent.class.isAssignableFrom(resourceScope.getResourceClass())) {
            return (Integer[]) PrismReflectionUtils.getProperty(this, resourceScope.getLowerCamelName() + "s");
        }
        return null;   
    }

}
