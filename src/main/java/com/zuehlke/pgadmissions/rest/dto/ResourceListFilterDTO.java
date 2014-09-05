package com.zuehlke.pgadmissions.rest.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.services.helpers.IntrospectionHelper;

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
    
    private List<PrismState> state;
    
    private List<StringFilterDTO> referrer;
    
    private List<UserRoleFilterDTO> userRole;
    
    private List<DateFilterDTO> confirmedStartDate;
    
    private List<RatingFilterDTO> rating;
    
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

    public final List<PrismState> getState() {
        return state;
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

    public final List<RatingFilterDTO> getRating() {
        return rating;
    }

    public final FilterSortOrder getSortOrder() {
        return sortOrder;
    }

    public final Boolean isSaveAsDefaultFilter() {
        return saveAsDefaultFilter;
    }
    
    public HashMap<String, Object> getFilters() {
        return IntrospectionHelper.getBeanPropertiesMap(this, "urgentOnly", "matchMode", "sortOrder", "saveAsDefaultFilter");
    }
    
    public boolean hasFilter(String filterProperty) {
        return getFilters().containsKey(filterProperty);
    }
    
    public static class StringFilterDTO extends ObjectFilterDTO {
        
        private String filter;

        public final String getFilter() {
            return filter;
        }

        public final void setFilter(String filter) {
            this.filter = filter;
        }

    }

    public static class UserRoleFilterDTO extends StringFilterDTO {
        
        private List<PrismRole> userRoles;

        public final List<PrismRole> getUserRoles() {
            return userRoles;
        }

        public final void setUserRoles(List<PrismRole> userRoles) {
            this.userRoles = userRoles;
        }

    }
    
    public static class StateFilterDTO extends ObjectFilterDTO {
        
        private PrismState filter;

        public final PrismState getFilter() {
            return filter;
        }

        public final void setFilter(PrismState filter) {
            this.filter = filter;
        }

    }
    
    public static class DateFilterDTO extends ObjectFilterDTO {
        
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
        
    }
    
    public static class RatingFilterDTO extends ObjectFilterDTO {
        
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
        
    }

    public static abstract class ObjectFilterDTO {
        
        private Boolean negated;

        public final Boolean isNegated() {
            return negated;
        }

        public final void setNegated(Boolean negated) {
            this.negated = negated;
        }
        
    }
    
}
