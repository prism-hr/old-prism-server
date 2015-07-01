package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;

@SuppressWarnings("rawtypes")
public class ImportedEntityDisplayConverter extends DozerConverter<ImportedEntity, String> {

    public ImportedEntityDisplayConverter() {
        super(ImportedEntity.class, String.class);
    }

    @Override
    public String convertTo(ImportedEntity source, String destination) {
        return source == null ? null : source.getName();
    }

    @Override
    public ImportedEntity convertFrom(String source, ImportedEntity destination) {
        throw new UnsupportedOperationException();
    }

}
