package com.zuehlke.pgadmissions.utils;

import java.util.Collection;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PrismMappingUtils {

    private static final Logger logger = LoggerFactory.getLogger(PrismMappingUtils.class);

    @Inject
    private ObjectMapper objectMapper;

    public <T extends Collection<U>, U extends Object> T readValue(String content, Class<T> collectionClass, Class<U> objectClass) {
        try {
            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(collectionClass, objectClass));
        } catch (Exception e) {
            logger.error("Unable to read json", e);
            return null;
        }
    }

    public String writeValue(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            logger.error("Unable to write json", e);
            return null;
        }
    }

}
