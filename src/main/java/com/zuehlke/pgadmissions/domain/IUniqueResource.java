package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.HashMultimap;

public interface IUniqueResource {
    
    public static String UNIQUE_CREATION_ERROR = "Tried to create a resource that has an invalid unique signature";
    
    public static String UNIQUE_IDENTIFICATION_ERROR = "Tried to identify a resource that has an invalid unique signature";
    
    public UniqueResourceSignature getUniqueResourceSignature();

    public class UniqueResourceSignature {
        
        private List<HashMap<String, Object>> properties;
        
        private HashMultimap<String, Object> exclusions;
        
        public UniqueResourceSignature(List<HashMap<String, Object>> properties, HashMultimap<String, Object> exclusions) {
            this.properties = properties;
            this.exclusions = exclusions;
        }
        
        public List<HashMap<String, Object>> getProperties() {
            return properties;
        }
        
        public void setProperties(List<HashMap<String, Object>> properties) {
            this.properties = properties;
        }
    
        public HashMultimap<String, Object> getExclusions() {
            return exclusions;
        }
    
        public void setExclusions(HashMultimap<String, Object> exclusions) {
            this.exclusions = exclusions;
        }
        
    }
    
}
