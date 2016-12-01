package uk.co.alumeni.prism.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;

@Component
public class PrismJsonMappingUtils {

    private static final Logger logger = LoggerFactory.getLogger(PrismJsonMappingUtils.class);

    @Inject
    private ObjectMapper objectMapper;

    public <T extends Collection<U>, U> T readCollection(String content, Class<T> collectionClass, Class<U> objectClass) {
        try {
            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(collectionClass, objectClass));
        } catch (Exception e) {
            logger.error("Unable to read json", e);
            return null;
        }
    }

    public <T> T readValue(String value, Class<T> valueClass) {
        try {
            return objectMapper.readValue(value, valueClass);
        } catch (Exception e) {
            logger.error("Unable to read json", e);
            return null;
        }
    }

    public String writeValue(Object value) {
        if (value != null) {
            try {
                return objectMapper.writeValueAsString(value);
            } catch (Exception e) {
                logger.error("Unable to write json", e);
                return null;
            }
        }
        return null;
    }

}
