package com.zuehlke.pgadmissions.mapping.helpers;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.services.ImportedEntityService;

@Component
public class ImportedProgramTransformer implements
        ImportedEntityTransformer<uk.co.alumeni.prism.api.model.imported.request.ImportedProgramRequest, ImportedProgram> {

    @Inject
    private ImportedEntityService importedEntityService;

    @Override
    public void transform(uk.co.alumeni.prism.api.model.imported.request.ImportedProgramRequest concreteSource, ImportedProgram concreteTarget) {
        concreteTarget.setInstitution((ImportedInstitution) importedEntityService.getById(ImportedInstitution.class, concreteSource.getInstitution()));

        Integer qualificationType = concreteSource.getQualificationType();
        concreteTarget.setQualificationType(qualificationType == null ? null : importedEntityService.getById(ImportedEntitySimple.class, qualificationType));

        concreteTarget.setLevel(concreteSource.getLevel());
        concreteTarget.setQualification(concreteSource.getQualification());
        concreteTarget.setHomepage(concreteSource.getHomepage());
    }

}
