package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.ImportedEntityInstitution;

public class ImportedEntityConverter extends DozerConverter<ImportedEntityInstitution, Integer> {

    public ImportedEntityConverter() {
        super(ImportedEntityInstitution.class, Integer.class);
    }

    @Override
    public Integer convertTo(ImportedEntityInstitution source, Integer destination) {
        return source != null ? source.getId() : null;
    }

    @Override
    public ImportedEntityInstitution convertFrom(Integer source, ImportedEntityInstitution destination) {
        throw new UnsupportedOperationException();
    }

}
