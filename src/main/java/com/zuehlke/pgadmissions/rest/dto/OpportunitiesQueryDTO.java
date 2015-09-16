package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;

public class OpportunitiesQueryDTO {

    private PrismAction actionId;

    private Integer institutionId;

    private Integer departmentId;

    private Integer programId;

    private Integer projectId;

    @NotNull
    private PrismAdvertContext context;

    private PrismOpportunityCategory opportunityCategory;

    private OpportunitiesQueryScopeTab tab;

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

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }

    public OpportunitiesQueryScopeTab getTab() {
        return tab;
    }

    public void setTab(OpportunitiesQueryScopeTab tab) {
        this.tab = tab;
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

    public boolean isNarrowed() {
        return !(institutionId == null && departmentId == null && programId == null && projectId == null);
    }

    public Integer getResourceId() {
        for (PrismScope scope : new PrismScope[] { PROJECT, PROGRAM, DEPARTMENT, INSTITUTION }) {
            Integer resourceId = (Integer) getProperty(this, getResourceIdReference(scope));
            if (resourceId != null) {
                return resourceId;
            }
        }
        return null;
    }

    public void setResourceId(PrismScope resourceScope, Integer resourceId) {
        if (ResourceParent.class.isAssignableFrom(resourceScope.getResourceClass())) {
            setProperty(this, getResourceIdReference(resourceScope), resourceId);
        }
    }

    public PrismScope getResourceScope() {
        for (PrismScope scope : new PrismScope[] { PROJECT, PROGRAM, DEPARTMENT, INSTITUTION }) {
            if (getProperty(this, scope.getLowerCamelName() + "Id") != null) {
                return scope;
            }
        }
        return null;
    }

    private String getResourceIdReference(PrismScope resourceScope) {
        return resourceScope.getLowerCamelName() + "Id";
    }

    public  enum OpportunitiesQueryScopeTab {
        SCOPE_INSTITUTIONS(INSTITUTION),
        SCOPE_DEPARTMENTS(DEPARTMENT),
        MAIN_OPPORTUNITIES(PROGRAM, PROJECT),
        MAIN_DEPARTMENTS(DEPARTMENT);

        private PrismScope[] scopes;

        OpportunitiesQueryScopeTab(PrismScope... scopes) {
            this.scopes = scopes;
        }

        public PrismScope[] getScopes() {
            return scopes;
        }
    }

}
