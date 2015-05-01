package com.zuehlke.pgadmissions.rest;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.zuehlke.pgadmissions.services.EntityService;

public class EntityJsonDeserializer<T> extends JsonDeserializer<T> {

    private EntityService entityService;

    private Class<T> klazz;

    public EntityJsonDeserializer(EntityService entityService, Class<T> klass) {
        this.entityService = entityService;
        this.klazz = klass;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        Integer id = parser.getIntValue();
        return entityService.getById(klazz, id);
    }

}
