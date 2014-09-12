package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;

import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.services.EntityService;

public class ImportedEntityConverter extends DozerConverter<ImportedEntity, Integer> {

    @Autowired
    private EntityService entityService;

    public ImportedEntityConverter() {
        super(ImportedEntity.class, Integer.class);
    }

    @Override
    public Integer convertTo(ImportedEntity source, Integer destination) {
        return source != null ? source.getId() : null;
    }

    @Override
    public ImportedEntity convertFrom(Integer source, ImportedEntity destination) {
        throw new UnsupportedOperationException();
    }

}
