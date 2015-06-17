package com.zuehlke.pgadmissions.rest;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.zuehlke.pgadmissions.domain.imported.ImportedDomicile;
import com.zuehlke.pgadmissions.services.EntityService;

@SuppressWarnings("serial")
public class PrismJacksonModule extends SimpleModule {

    @Autowired
    private EntityService entityService;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostConstruct
    public void initialize() {
        addDeserializer(ImportedDomicile.class, new EntityJsonDeserializer(entityService, ImportedDomicile.class));
    }
    
}
