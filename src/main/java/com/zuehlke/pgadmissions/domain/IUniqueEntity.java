package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;

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
        
        @Override
        public String toString() {
            List<String> readablePropertySet = Lists.newArrayList();
            for (HashMap<String, Object> property : properties) {
                List<String> readableProperties = Lists.newArrayList();
                for (String key : property.keySet()) {
                    readableProperties.add(key + "=" + propertyToString(property.get(key)));
                }
                readablePropertySet.add(Joiner.on(", ").join(readableProperties));
            }
            return Joiner.on("; ").join(readablePropertySet);
        }
        
        private String propertyToString(Object property) {
            if (property == null) {
                return "null";
            } else if (PropertyUtils.isReadable(property, "id")) {
                try {
                    return propertyToString(PropertyUtils.getSimpleProperty(property, "id"));
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
            return property.toString();
        }
    }

}
