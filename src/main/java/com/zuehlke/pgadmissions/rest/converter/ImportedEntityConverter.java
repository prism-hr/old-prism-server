package com.zuehlke.pgadmissions.rest.converter;

import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.StudyOption;
import org.dozer.DozerConverter;
import org.joda.time.DateTime;

import java.util.Date;

public class ImportedEntityConverter extends DozerConverter<ImportedEntity, String> {

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
}
