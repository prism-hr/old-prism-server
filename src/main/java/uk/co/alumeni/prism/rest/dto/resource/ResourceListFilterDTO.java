package uk.co.alumeni.prism.rest.dto.resource;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;

import uk.co.alumeni.prism.domain.definitions.PrismFilterMatchMode;
import uk.co.alumeni.prism.domain.definitions.PrismFilterSortOrder;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.dto.ResourceIdentityDTO;

public class ResourceListFilterDTO {

    private PrismRoleCategory roleCategory;

    private List<PrismAction> actionIds;

    private PrismActionEnhancement[] actionEnhancements;

    private List<Integer> resourceIds;
    
    private List<Integer> excludedResourceIds;

    private ResourceIdentityDTO parentResource;

    private PrismOpportunityCategory opportunityCategory;

    private List<PrismOpportunityType> opportunityTypes;

    private List<ResourceListFilterTagDTO> themes;

    private Boolean themesApplied;

    private List<ResourceListFilterTagDTO> secondaryThemes;

    private Boolean secondaryThemesApplied;

    private List<ResourceListFilterTagDTO> locations;

    private Boolean locationsApplied;

    private List<ResourceListFilterTagDTO> secondaryLocations;

    private Boolean secondaryLocationsApplied;

    private PrismFilterMatchMode matchMode;

    private PrismFilterSortOrder sortOrder;

    private String valueString;

    private Boolean urgentOnly;

    private Boolean updateOnly;

    private List<ResourceListFilterConstraintDTO> constraints;

    public PrismRoleCategory getRoleCategory() {
        return roleCategory;
    }

    public void setRoleCategory(PrismRoleCategory roleCategory) {
        this.roleCategory = roleCategory;
    }

    public List<PrismAction> getActionIds() {
        return actionIds;
    }

    public void setActionIds(List<PrismAction> actionIds) {
        this.actionIds = actionIds;
    }

    public PrismActionEnhancement[] getActionEnhancements() {
        return actionEnhancements;
    }

    public void setActionEnhancements(PrismActionEnhancement[] actionEnhancements) {
        this.actionEnhancements = actionEnhancements;
    }

    public List<Integer> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Integer> resourceIds) {
        this.resourceIds = resourceIds;
    }
    
    public List<Integer> getExcludedResourceIds() {
        return excludedResourceIds;
    }

    public void setExcludedResourceIds(List<Integer> excludedResourceIds) {
        this.excludedResourceIds = excludedResourceIds;
    }

    public ResourceIdentityDTO getParentResource() {
        return parentResource;
    }

    public void setParentResource(ResourceIdentityDTO parentResource) {
        this.parentResource = parentResource;
    }

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }

    public List<PrismOpportunityType> getOpportunityTypes() {
        return opportunityTypes;
    }

    public void setOpportunityTypes(List<PrismOpportunityType> opportunityTypes) {
        this.opportunityTypes = opportunityTypes;
    }

    public List<ResourceListFilterTagDTO> getThemes() {
        return themes;
    }

    public void setThemes(List<ResourceListFilterTagDTO> themes) {
        this.themes = themes;
    }

    public Boolean getThemesApplied() {
        return themesApplied;
    }

    public void setThemesApplied(Boolean themesApplied) {
        this.themesApplied = themesApplied;
    }

    public List<ResourceListFilterTagDTO> getSecondaryThemes() {
        return secondaryThemes;
    }

    public void setSecondaryThemes(List<ResourceListFilterTagDTO> secondaryThemes) {
        this.secondaryThemes = secondaryThemes;
    }

    public Boolean getSecondaryThemesApplied() {
        return secondaryThemesApplied;
    }

    public void setSecondaryThemesApplied(Boolean secondaryThemesApplied) {
        this.secondaryThemesApplied = secondaryThemesApplied;
    }

    public List<ResourceListFilterTagDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<ResourceListFilterTagDTO> locations) {
        this.locations = locations;
    }

    public Boolean getLocationsApplied() {
        return locationsApplied;
    }

    public void setLocationsApplied(Boolean locationsApplied) {
        this.locationsApplied = locationsApplied;
    }

    public List<ResourceListFilterTagDTO> getSecondaryLocations() {
        return secondaryLocations;
    }

    public void setSecondaryLocations(List<ResourceListFilterTagDTO> secondaryLocations) {
        this.secondaryLocations = secondaryLocations;
    }

    public Boolean getSecondaryLocationsApplied() {
        return secondaryLocationsApplied;
    }

    public void setSecondaryLocationsApplied(Boolean secondaryLocationsApplied) {
        this.secondaryLocationsApplied = secondaryLocationsApplied;
    }

    public final PrismFilterMatchMode getMatchMode() {
        return matchMode;
    }

    public final void setMatchMode(PrismFilterMatchMode matchMode) {
        this.matchMode = matchMode;
    }

    public final PrismFilterSortOrder getSortOrder() {
        return sortOrder;
    }

    public final void setSortOrder(PrismFilterSortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public final String getValueString() {
        return valueString;
    }

    public final void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public final Boolean getUrgentOnly() {
        return urgentOnly;
    }

    public void setUrgentOnly(Boolean urgentOnly) {
        this.urgentOnly = urgentOnly;
    }

    public Boolean getUpdateOnly() {
        return updateOnly;
    }

    public void setUpdateOnly(Boolean updateOnly) {
        this.updateOnly = updateOnly;
    }

    public final List<ResourceListFilterConstraintDTO> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ResourceListFilterConstraintDTO> constraints) {
        this.constraints = constraints;
    }

    public ResourceListFilterDTO withResourceIds(List<Integer> resourceIds) {
        this.resourceIds = resourceIds;
        return this;
    }

    public ResourceListFilterDTO withExcludedResourceIds(List<Integer> excludedResourceIds) {
        this.excludedResourceIds = excludedResourceIds;
        return this;
    }
    
    public ResourceListFilterDTO withParentResource(ResourceIdentityDTO parentResource) {
        this.parentResource = parentResource;
        return this;
    }

    public ResourceListFilterDTO withRoleCategory(PrismRoleCategory roleCategory) {
        this.roleCategory = roleCategory;
        return this;
    }

    public ResourceListFilterDTO withActionIds(List<PrismAction> actionIds) {
        this.actionIds = actionIds;
        return this;
    }

    public ResourceListFilterDTO withActionEnhancements(PrismActionEnhancement... actionEnhancements) {
        this.actionEnhancements = actionEnhancements;
        return this;
    }

    public ResourceListFilterDTO withOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
        return this;
    }

    public ResourceListFilterDTO withThemes(List<ResourceListFilterTagDTO> themes) {
        this.themes = themes;
        return this;
    }

    public ResourceListFilterDTO withThemesApplied(Boolean themesApplied) {
        this.themesApplied = themesApplied;
        return this;
    }

    public ResourceListFilterDTO withSecondaryThemes(List<ResourceListFilterTagDTO> secondaryThemes) {
        this.secondaryThemes = secondaryThemes;
        return this;
    }

    public ResourceListFilterDTO withSecondaryThemesApplied(Boolean secondaryThemesApplied) {
        this.secondaryThemesApplied = secondaryThemesApplied;
        return this;
    }

    public ResourceListFilterDTO withLocations(List<ResourceListFilterTagDTO> locations) {
        this.locations = locations;
        return this;
    }

    public ResourceListFilterDTO withLocationsApplied(Boolean locationsApplied) {
        this.locationsApplied = locationsApplied;
        return this;
    }

    public ResourceListFilterDTO withSecondaryLocations(List<ResourceListFilterTagDTO> secondaryLocations) {
        this.secondaryLocations = secondaryLocations;
        return this;
    }

    public ResourceListFilterDTO withSecondaryLocationsApplied(Boolean secondaryLocationsApplied) {
        this.secondaryLocationsApplied = secondaryLocationsApplied;
        return this;
    }

    public ResourceListFilterDTO withUrgentOnly(Boolean urgentOnly) {
        this.urgentOnly = urgentOnly;
        return this;
    }

    public ResourceListFilterDTO withUpdateOnly(Boolean updateOnly) {
        this.updateOnly = updateOnly;
        return this;
    }

    public ResourceListFilterDTO withMatchMode(PrismFilterMatchMode matchMode) {
        this.matchMode = matchMode;
        return this;
    }

    public ResourceListFilterDTO withSortOrder(PrismFilterSortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public ResourceListFilterDTO withValueString(String valueString) {
        setValueString(valueString);
        return this;
    }

    public ResourceListFilterDTO withConstraints(List<ResourceListFilterConstraintDTO> constraints) {
        this.constraints = constraints;
        return this;
    }

    public void addConstraint(ResourceListFilterConstraintDTO constraint) {
        constraints.add(constraint);
    }

    public boolean isUrgentOnly() {
        return BooleanUtils.toBoolean(urgentOnly);
    }

    public boolean isUpdateOnly() {
        return BooleanUtils.toBoolean(updateOnly);
    }

    public boolean hasConstraints() {
        return constraints != null && !constraints.isEmpty();
    }

    public boolean hasBasicFilter() {
        return hasConstraints() && getValueString() != null;
    }

}
