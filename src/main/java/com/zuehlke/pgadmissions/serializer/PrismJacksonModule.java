package com.zuehlke.pgadmissions.serializer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.services.EntityService;

@SuppressWarnings("serial")
public class PrismJacksonModule extends SimpleModule {

    @Autowired
    private EntityService entityService;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostConstruct
    public void initialize() {
        addDeserializer(Domicile.class, new EntityJsonDeserializer(entityService, Domicile.class));
    }
    
}
