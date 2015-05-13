package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismFilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

public class ResourceReportFilterDTO {
    
    private PrismFilterMatchMode matchMode;

    private List<ResourceReportFilterPropertyDTO> properties;

    public PrismFilterMatchMode getMatchMode() {
        return matchMode;
    }

    public void setMatchMode(PrismFilterMatchMode matchMode) {
        this.matchMode = matchMode;
    }

    public List<ResourceReportFilterPropertyDTO> getProperties() {
        return properties;
    }

    public void setProperties(List<ResourceReportFilterPropertyDTO> getFilters) {
        this.properties = getFilters;
    }

    public static class ResourceReportFilterPropertyDTO {

        private PrismImportedEntity entityType;

        private Integer entityid;

        public PrismImportedEntity getEntityType() {
            return entityType;
        }

        public void setEntityType(PrismImportedEntity entityType) {
            this.entityType = entityType;
        }

        public Integer getEntityid() {
            return entityid;
        }

        public void setEntityid(Integer entityid) {
            this.entityid = entityid;
        }

    }

}
