package com.zuehlke.pgadmissions.rest.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
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
    private PrismActionCondition actionCondition;

    @NotNull
    private List<PrismOpportunityCategory> opportunityCategories;

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

    public final Integer getResourceId() {
        return resourceId;
    }

    public final void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public final PrismAction getActionId() {
        return actionId;
    }

    public final void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public final Integer[] getInstitutions() {
        return institutions;
    }

    public final void setInstitutions(Integer[] institutions) {
        this.institutions = institutions;
    }

    public final Integer[] getDepartments() {
        return departments;
    }

    public final void setDepartments(Integer[] departments) {
        this.departments = departments;
    }

    public final Integer[] getPrograms() {
        return programs;
    }

    public final void setPrograms(Integer[] programs) {
        this.programs = programs;
    }

    public final Integer[] getProjects() {
        return projects;
    }

    public final void setProjects(Integer[] projects) {
        this.projects = projects;
    }

    public List<PrismOpportunityCategory> getOpportunityCategories() {
        return opportunityCategories;
    }

    public void setOpportunityCategories(List<PrismOpportunityCategory> opportunityCategories) {
        this.opportunityCategories = opportunityCategories;
    }

    public PrismActionCondition getActionCondition() {
        return actionCondition;
    }

    public void setActionCondition(PrismActionCondition actionCondition) {
        this.actionCondition = actionCondition;
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

    public final Integer getMinFee() {
        return minFee;
    }

    public final void setMinFee(Integer minFee) {
        this.minFee = minFee;
    }

    public final Integer getMaxFee() {
        return maxFee;
    }

    public final void setMaxFee(Integer maxFee) {
        this.maxFee = maxFee;
    }

    public final Integer getMinSalary() {
        return minSalary;
    }

    public final void setMinSalary(Integer minSalary) {
        this.minSalary = minSalary;
    }

    public final Integer getMaxSalary() {
        return maxSalary;
    }

    public final void setMaxSalary(Integer maxSalary) {
        this.maxSalary = maxSalary;
    }

    public final Integer getMinDuration() {
        return minDuration;
    }

    public final void setMinDuration(Integer minDuration) {
        this.minDuration = minDuration;
    }

    public final Integer getMaxDuration() {
        return maxDuration;
    }

    public final void setMaxDuration(Integer maxDuration) {
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

    public final String getLastSequenceIdentifier() {
        return lastSequenceIdentifier;
    }

    public final void setLastSequenceIdentifier(String lastSequenceIdentifier) {
        this.lastSequenceIdentifier = lastSequenceIdentifier;
    }

    public boolean isResourceAction() {
        return !(resourceId == null || actionId == null);
    }

    public boolean isNarrowed() {
        return !(institutions == null && departments == null && programs == null && projects == null);
    }

    public OpportunitiesQueryDTO withOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        opportunityCategories = Lists.newArrayList(opportunityCategory);
        return this;
    }

    public Integer[] getResources(PrismScope resourceScope) {
        if (ResourceParent.class.isAssignableFrom(resourceScope.getResourceClass())) {
            return (Integer[]) PrismReflectionUtils.getProperty(this, resourceScope.getLowerCamelName() + "s");
        }
        return null;
    }

}
