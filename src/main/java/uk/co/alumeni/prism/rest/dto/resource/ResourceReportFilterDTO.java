package uk.co.alumeni.prism.rest.dto.resource;

import uk.co.alumeni.prism.domain.definitions.PrismFilterEntity;

import java.util.List;

public class ResourceReportFilterDTO {

    private List<ResourceReportFilterPropertyDTO> properties;

    public List<ResourceReportFilterPropertyDTO> getProperties() {
        return properties;
    }

    public void setProperties(List<ResourceReportFilterPropertyDTO> getFilters) {
        this.properties = getFilters;
    }

    public static class ResourceReportFilterPropertyDTO {

        private String entityId;

        private PrismFilterEntity entityType;

        public String getEntityId() {
            return entityId;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public PrismFilterEntity getEntityType() {
            return entityType;
        }

        public void setEntityType(PrismFilterEntity entityType) {
            this.entityType = entityType;
        }

    }

}
