package com.zuehlke.pgadmissions.mapping.helpers;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;

@Component
public class ImportedSubjectAreaTransformer implements
        ImportedEntityTransformer<uk.co.alumeni.prism.api.model.imported.request.ImportedSubjectAreaRequest, ImportedSubjectArea> {

    @Override
    public void transform(uk.co.alumeni.prism.api.model.imported.request.ImportedSubjectAreaRequest concreteSource, ImportedSubjectArea concreteTarget) {
        concreteTarget.setJacsCode(concreteSource.getJacsCode());
        concreteTarget.setDescription(concreteSource.getDescription());
    }

}
