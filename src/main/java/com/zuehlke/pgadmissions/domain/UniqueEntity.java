package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

public interface UniqueEntity {

    public ResourceSignature getResourceSignature();

    public class ResourceSignature {

        private final HashMap<String, Object> properties = Maps.newHashMap();

        private final HashMultimap<String, Object> exclusions = HashMultimap.create();

        public HashMap<String, Object> getProperties() {
            return properties;
        }

        public HashMultimap<String, Object> getExclusions() {
            return exclusions;
        }

        public ResourceSignature addProperty(String key, Object value) {
            if (key != null) {
                properties.put(key, value);
            }
            return this;
        }

        public ResourceSignature addExclusion(String key, Object value) {
            if (!(key == null || value == null)) {
                exclusions.put(key, value);
            }
            return this;
        }

        @Override
        public String toString() {
            List<String> readableProperties = Lists.newArrayList();
            for (String property : properties.keySet()) {
                readableProperties.add(property + "=" + propertyToString(properties.get(property)));
            }
            return Joiner.on(", ").join(readableProperties);
        }

        private String propertyToString(Object property) {
            if (property == null) {
                return "null";
            } else if (PropertyUtils.isReadable(property, "id")) {
                PrismReflectionUtils.getProperty(property, "id");
            }
            return property.toString();
        }
    }

}
