package com.zuehlke.pgadmissions.rest.dto;

import java.util.Collections;
import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

public class ResourceReportFilterDTO {

    private List<ResourceReportFilterPropertyDTO> properties;

    public List<ResourceReportFilterPropertyDTO> getProperties() {
        return properties == null ? Collections.<ResourceReportFilterPropertyDTO> emptyList() : properties;
    }

    public void setProperties(List<ResourceReportFilterPropertyDTO> getFilters) {
        this.properties = getFilters;
    }

    public static class ResourceReportFilterPropertyDTO {

        private Integer entityId;
        
        private PrismImportedEntity entityType;

        public Integer getEntityId() {
            return entityId;
        }

        public void setEntityId(Integer entityId) {
            this.entityId = entityId;
        }
        
        public PrismImportedEntity getEntityType() {
            return entityType;
        }

        public void setEntityType(PrismImportedEntity entityType) {
            this.entityType = entityType;
        }

    }

}
