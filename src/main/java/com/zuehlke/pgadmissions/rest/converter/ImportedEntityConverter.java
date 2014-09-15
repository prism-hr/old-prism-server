package com.zuehlke.pgadmissions.rest.converter;

import com.zuehlke.pgadmissions.domain.ImportedEntity;
import org.dozer.DozerConverter;

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
