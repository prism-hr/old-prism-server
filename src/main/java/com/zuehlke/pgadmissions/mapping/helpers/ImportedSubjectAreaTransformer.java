package com.zuehlke.pgadmissions.mapping.helpers;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;

@Component
public class ImportedSubjectAreaTransformer implements
        ImportedEntityTransformer<uk.co.alumeni.prism.api.model.imported.ImportedSubjectArea, ImportedSubjectArea> {

    @Override
    public void transform(uk.co.alumeni.prism.api.model.imported.ImportedSubjectArea concreteSource, ImportedSubjectArea concreteTarget) {
        concreteTarget.setDescription(concreteSource.getDescription());
    }

}
