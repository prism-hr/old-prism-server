package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;

import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.services.EntityService;

public class ImportedEntityConverter extends DozerConverter<ImportedEntity, String> {

    @Autowired
    private EntityService entityService;

    public ImportedEntityConverter() {
        super(ImportedEntity.class, String.class);
    }

    @Override
    public String convertTo(ImportedEntity source, String destination) {
        return source != null ? source.getCode() : null;
    }

    @Override
    public ImportedEntity convertFrom(String source, ImportedEntity destination) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        if(ImportedEntity.class.isAssignableFrom(destinationClass)) {
            ImportedEntity entity = (ImportedEntity) entityService.getByCode(destinationClass, (String) sourceFieldValue);
            return entity;
        }
        return super.convert(existingDestinationFieldValue, sourceFieldValue, destinationClass, sourceClass);
    }
}
