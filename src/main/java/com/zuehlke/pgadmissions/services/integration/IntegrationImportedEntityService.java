package com.zuehlke.pgadmissions.services.integration;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;
import com.zuehlke.pgadmissions.services.ImportedEntityService;

@Service
@Transactional
public class IntegrationImportedEntityService {

    @Inject
    private ImportedEntityService importedEntityService;

    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> ImportedEntitySimpleRepresentation getImportedEntityRepresentation(
            Institution institution, T importedEntity, boolean map) {
        ImportedEntitySimpleRepresentation representation = new ImportedEntitySimpleRepresentation().withId(importedEntity.getId()).withName(
                importedEntity.getName());

        if (map) {
            V mapping = importedEntityService.getEnabledImportedEntityMapping(institution, importedEntity);
            representation.setCode(mapping.getCode());
        }

        return representation;
    }

}
