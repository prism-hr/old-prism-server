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

public class ResourceListFilterDTO {

    private PrismRoleCategory roleCategory;

    private List<PrismAction> actionIds;

    private PrismActionEnhancement[] actionEnhancements;

    private List<Integer> resourceIds;

    private ResourceDTO parentResource;

    private PrismOpportunityCategory opportunityCategory;

    private List<PrismOpportunityType> opportunityTypes;

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

    public ResourceDTO getParentResource() {
        return parentResource;
    }

    public void setParentResource(ResourceDTO parentResource) {
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

    public ResourceListFilterDTO withParentResource(ResourceDTO parentResource) {
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
