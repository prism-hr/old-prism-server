package com.zuehlke.pgadmissions.mapping.helpers;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.services.ImportedEntityService;

@Component
public class ImportedSubjectAreaTransformer implements
        ImportedEntityTransformer<uk.co.alumeni.prism.api.model.imported.request.ImportedSubjectAreaRequest, ImportedSubjectArea> {

    @Inject
    private ImportedEntityService importedEntityService;

    @Override
    public void transform(uk.co.alumeni.prism.api.model.imported.request.ImportedSubjectAreaRequest concreteSource, ImportedSubjectArea concreteTarget) {
        concreteTarget.setJacsCode(concreteSource.getJacsCode());
        concreteTarget.setDescription(concreteSource.getDescription());
        concreteTarget.setParentSubjectArea(importedEntityService.getById(ImportedSubjectArea.class, concreteSource.getParentSubjectArea()));
    }

}
