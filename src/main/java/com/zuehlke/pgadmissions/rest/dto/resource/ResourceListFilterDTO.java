package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;

import com.zuehlke.pgadmissions.domain.definitions.PrismFilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.PrismFilterSortOrder;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Resource;

public class ResourceListFilterDTO {

    private Resource enclosingResource;
    
    private PrismAction action;

    private PrismOpportunityCategory opportunityCategory;

    private PrismFilterMatchMode matchMode;

    private PrismFilterSortOrder sortOrder;

    private String valueString;

    private Boolean urgentOnly;

    private Boolean updateOnly;

    private PrismAction actionId;

    private List<ResourceListFilterConstraintDTO> constraints;
    
    public Resource getEnclosingResource() {
        return enclosingResource;
    }

    public void setEnclosingResource(Resource enclosingResource) {
        this.enclosingResource = enclosingResource;
    }

    public PrismAction getAction() {
        return action;
    }

    public void setAction(PrismAction action) {
        this.action = action;
    }

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
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

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public final List<ResourceListFilterConstraintDTO> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ResourceListFilterConstraintDTO> constraints) {
        this.constraints = constraints;
    }

    public ResourceListFilterDTO withEnclosingResource(Resource enclosingResource) {
        this.enclosingResource = enclosingResource;
        return this;
    }
    
    public ResourceListFilterDTO withAction(PrismAction action) {
        this.action = action;
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
