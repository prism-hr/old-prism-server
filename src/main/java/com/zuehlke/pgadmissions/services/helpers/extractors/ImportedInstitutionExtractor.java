package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

@Component
public class ImportedInstitutionExtractor implements ImportedEntityExtractor {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<Object> definitions) throws Exception {

        List<String> rows = Lists.newLinkedList();
        for (Object definition : definitions) {
            com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution importedInstitution = (com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution) definition;

            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(domicilesByCode.get(importedInstitution.getDomicile())));
            cells.add(prepareStringForSqlInsert(importedInstitution.getCode()));
            cells.add(prepareStringForSqlInsert(importedInstitution.getName()));
            cells.add(prepareStringForSqlInsert(importedInstitution.getUcasId()));
            cells.add(prepareStringForSqlInsert(importedInstitution.getFacebookId()));
            cells.add(prepareStringForSqlInsert(new Integer(1).toString()));
            String row = prepareCellsForSqlInsert(cells);
            rows.add(row);
        }
        return rows;
    }

}
