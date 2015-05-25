package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.PrismFilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

import java.util.Collections;
import java.util.List;

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
        if(properties == null){
            return Collections.emptyList();
        }
        return properties;
    }

    public void setProperties(List<ResourceReportFilterPropertyDTO> getFilters) {
        this.properties = getFilters;
    }

    public static class ResourceReportFilterPropertyDTO {

        private PrismImportedEntity entityType;

        private Integer entityId;

        public PrismImportedEntity getEntityType() {
            return entityType;
        }

        public void setEntityType(PrismImportedEntity entityType) {
            this.entityType = entityType;
        }

        public Integer getEntityId() {
            return entityId;
        }

        public void setEntityId(Integer entityId) {
            this.entityId = entityId;
        }

    }

}
