package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;

@SuppressWarnings("rawtypes")
public class ImportedEntityConverter extends DozerConverter<ImportedEntity, Integer> {

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
