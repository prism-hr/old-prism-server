package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class OpportunitiesQueryDTO {

    @NotNull
    private List<PrismOpportunityCategory> opportunityCategories;

    private String keyword;

    private List<PrismAdvertIndustry> industries;

    private List<PrismAdvertFunction> functions;

    private List<String> themes;

    private List<PrismOpportunityType> opportunityTypes;

    private List<PrismStudyOption> studyOptions;

    private List<PrismActionCondition> actionConditions;

    private Integer minFee;

    private Integer maxFee;

    private Integer minSalary;

    private Integer maxSalary;

    private Integer minDuration;

    private Integer maxDuration;

    private Integer[] institutions;

    private Integer[] departments;

    private Integer[] programs;

    private Integer[] projects;

    private BigDecimal neLat;

    private BigDecimal neLon;

    private BigDecimal swLat;

    private BigDecimal swLon;

    private String lastSequenceIdentifier;

    private Integer resourceId;

    private PrismAction actionId;

    public List<PrismOpportunityCategory> getOpportunityCategories() {
        return opportunityCategories;
    }

    public void setOpportunityCategories(List<PrismOpportunityCategory> opportunityCategories) {
        this.opportunityCategories = opportunityCategories;
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

    public List<PrismActionCondition> getActionConditions() {
        return actionConditions;
    }

    public void setActionConditions(List<PrismActionCondition> actionConditions) {
        this.actionConditions = actionConditions;
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

    public boolean isResourceAction() {
        return !(resourceId == null || actionId == null);
    }

}
