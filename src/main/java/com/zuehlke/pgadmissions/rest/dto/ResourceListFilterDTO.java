package com.zuehlke.pgadmissions.rest.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.services.helpers.IntrospectionHelper;

@SuppressWarnings("unchecked")
public class ResourceListFilterDTO {

    private Boolean urgentOnly;
    
    private FilterMatchMode matchMode;
    
    private List<StringFilterDTO> user;
    
    private List<StringFilterDTO> code;
    
    private List<StringFilterDTO> institution;

    private List<StringFilterDTO> program;
    
    private List<StringFilterDTO> project;
    
    private List<DateFilterDTO> createdTimestamp;
    
    private List<DateFilterDTO> submittedTimetamp;
    
    private List<DateFilterDTO> updatedTimestamp;
    
    private List<DateFilterDTO> dueDate;
    
    private List<DateFilterDTO> closingDate;
    
    private List<StateGroupFilterDTO> stateGroup;
    
    private List<StringFilterDTO> referrer;
    
    private List<UserRoleFilterDTO> userRole;
    
    private List<DateFilterDTO> confirmedStartDate;
    
    private List<DecimalFilterDTO> rating;
    
    private FilterSortOrder sortOrder;
    
    private Boolean saveAsDefaultFilter;

    public final Boolean isUrgentOnly() {
        return urgentOnly;
    }

    public final FilterMatchMode getMatchMode() {
        return matchMode;
    }
    
    public final List<StringFilterDTO> getUser() {
        return user;
    }

    public final List<StringFilterDTO> getCode() {
        return code;
    }

    public final List<StringFilterDTO> getInstitution() {
        return institution;
    }

    public final List<StringFilterDTO> getProgram() {
        return program;
    }

    public final List<StringFilterDTO> getProject() {
        return project;
    }
    
    public final List<DateFilterDTO> getCreatedTimestamp() {
        return createdTimestamp;
    }

    public final List<DateFilterDTO> getSubmittedTimestamp() {
        return submittedTimetamp;
    }

    public final List<DateFilterDTO> getUpdatedTimestamp() {
        return updatedTimestamp;
    }
    
    public final List<DateFilterDTO> getDueDate() {
        return dueDate;
    }

    public final List<DateFilterDTO> getClosingDate() {
        return closingDate;
    }

    public final List<StateGroupFilterDTO> getStateGroup() {
        return stateGroup;
    }

    public final List<StringFilterDTO> getReferrer() {
        return referrer;
    }

    public final List<UserRoleFilterDTO> getSupervisor() {
        return userRole;
    }

    public final List<DateFilterDTO> getConfirmedStartDate() {
        return confirmedStartDate;
    }

    public final List<DecimalFilterDTO> getRating() {
        return rating;
    }

    public final FilterSortOrder getSortOrder() {
        return sortOrder;
    }

    public final Boolean isSaveAsDefaultFilter() {
        return saveAsDefaultFilter;
    }
    
    public ResourceListFilterDTO withUrgentOnly(Boolean urgentOnly) {
        this.urgentOnly = urgentOnly;
        return this;
    }
    
    public ResourceListFilterDTO withMatchMode(FilterMatchMode matchMode) {
        this.matchMode = matchMode;
        return this;
    }

    public void addStringFilter(FilterProperty property, String term, Boolean negated) {
        List<StringFilterDTO> constraints = (List<StringFilterDTO>) IntrospectionHelper.getProperty(this, property.getPropertyName());
        constraints.add(new StringFilterDTO().withTerm(term).withNegated(negated));
    }
    
    public void addStateGroupFilter(FilterProperty property, PrismStateGroup stateGroup, Boolean negated) {
        List<StateGroupFilterDTO> constraints = (List<StateGroupFilterDTO>) IntrospectionHelper.getProperty(this, property.getPropertyName());
        constraints.add(new StateGroupFilterDTO().withStateGroup(stateGroup).withNegated(negated));
    }
    
    public void addDateFilter(FilterProperty property, LocalDate rangeStart, LocalDate rangeClose, Boolean negated) {
        List<DateFilterDTO> constraints = (List<DateFilterDTO>) IntrospectionHelper.getProperty(this, property.getPropertyName());
        constraints.add(new DateFilterDTO().withRangeStart(rangeStart).withRangeClose(rangeClose).withNegated(negated));
    }
    
    public void addDecimalFilter(FilterProperty property, BigDecimal rangeStart, BigDecimal rangeClose, Boolean negated) {
        List<DecimalFilterDTO> constraints = (List<DecimalFilterDTO>) IntrospectionHelper.getProperty(this, property.getPropertyName());
        constraints.add(new DecimalFilterDTO().withRangeStart(rangeStart).withRangeClose(rangeClose).withNegated(negated));
    }
    
    public void addUserRoleFilter(FilterProperty property, List<PrismRole> roles, Boolean negated) {
        List<UserRoleFilterDTO> constraints = (List<UserRoleFilterDTO>) IntrospectionHelper.getProperty(this, property.getPropertyName());
        constraints.add(new UserRoleFilterDTO().withRoles(roles).withNegated(negated));
    }    
    
    public ResourceListFilterDTO withSortOrder(FilterSortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }
    
    public HashMap<String, Object> getFilterConstraints() {
        return IntrospectionHelper.getBeanPropertiesMap(this, "urgentOnly", "matchMode", "sortOrder", "saveAsDefaultFilter");
    }
    
    public boolean hasFilter(FilterProperty filterProperty) {
        return getFilterConstraints().containsKey(filterProperty.getPropertyName());
    }
    
    public static class StringFilterDTO extends AbstractFilterDTO {
        
        private String string;

        public final String getString() {
            return string;
        }

        public final void setString(String string) {
            this.string = string;
        }
        
        public StringFilterDTO withTerm(String string) {
            this.string = string;
            return this;
        }
        
        public StringFilterDTO withNegated(Boolean negated) {
            setNegated(negated);
            return this;
        }

    }

    public static class UserRoleFilterDTO extends StringFilterDTO {
        
        private List<PrismRole> roles;

        public final List<PrismRole> getRoles() {
            return roles;
        }

        public final void setRoles(List<PrismRole> roles) {
            this.roles = roles;
        }
        
        public UserRoleFilterDTO withRoles(List<PrismRole> roles) {
            this.roles = roles;
            return this;
        }
        
        public StringFilterDTO withTerm(String string) {
            setString(string);
            return this;
        }
        
        public UserRoleFilterDTO withNegated(Boolean negated) {
            setNegated(negated);
            return this;
        }
        
    }
    
    public static class StateGroupFilterDTO extends AbstractFilterDTO {
        
        private PrismStateGroup stateGroup;

        public final PrismStateGroup getStateGroup() {
            return stateGroup;
        }

        public final void setStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
        }
        
        public StateGroupFilterDTO withStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
            return this;
        }

        public StateGroupFilterDTO withNegated(Boolean negated) {
            setNegated(negated);
            return this;
        }

    }
    
    public static class DateFilterDTO extends AbstractFilterDTO {
        
        private LocalDate rangeStart;
        
        private LocalDate rangeClose;

        public final LocalDate getRangeStart() {
            return rangeStart;
        }

        public final void setRangeStart(LocalDate rangeStart) {
            this.rangeStart = rangeStart;
        }

        public final LocalDate getRangeClose() {
            return rangeClose;
        }

        public final void setRangeClose(LocalDate rangeClose) {
            this.rangeClose = rangeClose;
        }
        
        public DateTime getRangeStartAsDateTime() {
            return rangeStart.toDateTimeAtStartOfDay();
        }
        
        public DateTime getRangeCloseAsDateTime() {
            return rangeClose.plusDays(1).toDateTimeAtStartOfDay().minusSeconds(1);
        }
        
        public DateFilterDTO withRangeStart(LocalDate rangeStart) {
            this.rangeStart = rangeStart;
            return this;
        }
        
        public DateFilterDTO withRangeClose(LocalDate rangeClose) {
            this.rangeClose = rangeClose;
            return this;
        }
        
        public DateFilterDTO withNegated(Boolean negated) {
            setNegated(negated);
            return this;
        }
        
    }
    
    public static class DecimalFilterDTO extends AbstractFilterDTO {
        
        private BigDecimal rangeStart;
        
        private BigDecimal rangeClose;

        public final BigDecimal getRangeStart() {
            return rangeStart;
        }

        public final void setRangeStart(BigDecimal rangeStart) {
            this.rangeStart = rangeStart;
        }

        public final BigDecimal getRangeClose() {
            return rangeClose;
        }

        public final void setRangeClose(BigDecimal rangeClose) {
            this.rangeClose = rangeClose;
        }
        
        public DecimalFilterDTO withRangeStart(BigDecimal rangeStart) {
            this.rangeStart = rangeStart;
            return this;
        }
        
        public DecimalFilterDTO withRangeClose(BigDecimal rangeClose) {
            this.rangeClose = rangeClose;
            return this;
        }
        
        public DecimalFilterDTO withNegated(Boolean negated) {
            setNegated(negated);
            return this;
        }
        
    }

    public static abstract class AbstractFilterDTO {
        
        private Boolean negated;

        public final Boolean isNegated() {
            return negated;
        }

        public final void setNegated(Boolean negated) {
            this.negated = negated;
        }
        
    }
    
}
