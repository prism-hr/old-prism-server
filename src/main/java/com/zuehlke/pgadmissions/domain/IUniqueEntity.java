package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.HashMultimap;

public interface IUniqueEntity {

    public ResourceSignature getResourceSignature();

    public class ResourceSignature {

        private List<HashMap<String, Object>> properties;

        private HashMultimap<String, Object> exclusions;

        public ResourceSignature(List<HashMap<String, Object>> propertiesWrapper) {
            this(propertiesWrapper, HashMultimap.<String, Object> create());
        }

        public ResourceSignature(List<HashMap<String, Object>> properties, HashMultimap<String, Object> exclusions) {
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
