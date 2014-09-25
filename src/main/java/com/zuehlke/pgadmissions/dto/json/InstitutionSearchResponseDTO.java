package com.zuehlke.pgadmissions.dto.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstitutionSearchResponseDTO {
    
    private List<Item> items;

    public final List<Item> getItems() {
        return items;
    }

    public final void setItems(List<Item> items) {
        this.items = items;
    }
    
    public static class Item {

        @JsonProperty("metatags")
        private MetaData metaData;
        
        public final MetaData getMetaData() {
            return metaData;
        }

        public final void setMetaData(MetaData metaData) {
            this.metaData = metaData;
        }

        public static class MetaData {
            
            @JsonProperty("og:url")
            private String uri;

            public final String getUri() {
                return uri;
            }

            public final void setUri(String uri) {
                this.uri = uri;
            }
            
        }
        
    }
 
}
