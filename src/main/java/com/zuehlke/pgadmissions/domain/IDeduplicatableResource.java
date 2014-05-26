package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

public interface IDeduplicatableResource {
    
    public UniqueResourceSignature getUniqueResourceSignature();

    public class UniqueResourceSignature {
        
        private List<HashMap<String, Object>> properties;
        
        private HashMap<String, Object> exclusions;
        
        public UniqueResourceSignature(List<HashMap<String, Object>> properties, HashMap<String, Object> exclusions) {
            this.properties = properties;
            this.exclusions = exclusions;
        }
        
        public List<HashMap<String, Object>> getProperties() {
            return properties;
        }
        
        public void setProperties(List<HashMap<String, Object>> properties) {
            this.properties = properties;
        }
    
        public HashMap<String, Object> getExclusions() {
            return exclusions;
        }
    
        public void setExclusions(HashMap<String, Object> exclusions) {
            this.exclusions = exclusions;
        }
        
    }
    
}
