package com.zuehlke.pgadmissions.rest.dto;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class ResourceListFilterDTO {

    private Boolean urgentOnly;
    
    private Boolean updatedOnly;
    
    private FilterMatchMode matchMode;
    
    private List<StringFilterDTO> institution;

    private List<StringFilterDTO> program;
    
    private List<StringFilterDTO> project;
    
    private List<DateFilterDTO> createdDate;
    
    private List<DateFilterDTO> submittedDate;
    
    private List<DateFilterDTO> updatedDate;
    
    private List<DateFilterDTO> closingDate;
    
    private List<PrismState> state;
    
    private List<StringFilterDTO> referrer;
    
    private List<StringFilterDTO> supervisor;
    
    private List<RatingFilterDTO> rating;

    public final Boolean isUrgentOnly() {
        return urgentOnly;
    }
    
    public final FilterMatchMode getMatchMode() {
        return matchMode;
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
    
    public final List<DateFilterDTO> getCreatedDate() {
        return createdDate;
    }

    public final List<DateFilterDTO> getSubmittedDate() {
        return submittedDate;
    }

    public final List<DateFilterDTO> getUpdatedDate() {
        return updatedDate;
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

    public final List<StringFilterDTO> getSupervisor() {
        return supervisor;
    }

    public final List<RatingFilterDTO> getRating() {
        return rating;
    }

    public class StringFilterDTO {
        
        private Boolean containing;
        
        private String filter;

        public final Boolean getContaining() {
            return containing;
        }

        public final void setContaining(Boolean containing) {
            this.containing = containing;
        }

        public final String getFilter() {
            return filter;
        }

        public final void setFilter(String filter) {
            this.filter = filter;
        }

    }
    
    public static class DateFilterDTO {
        
        private DateTime rangeStart;
        
        private DateTime rangeClose;

        public final DateTime getRangeStart() {
            return rangeStart;
        }

        public final void setRangeStart(DateTime rangeStart) {
            this.rangeStart = rangeStart;
        }

        public final DateTime getRangeClose() {
            return rangeClose;
        }

        public final void setRangeClose(DateTime rangeClose) {
            this.rangeClose = rangeClose;
        }
        
    }
    
    public static class RatingFilterDTO {
        
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
    
    public HashMap<String, Object> getFilters() {
        HashMap<String, Object> filters = Maps.newHashMap();
        for (Field field : this.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            if (Arrays.asList("urgentOnly", "updatedOnly", "matchMode").contains(fieldName)) {
                try {
                    filters.put(fieldName, field.get(this));
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }
        return filters;
    }

}
