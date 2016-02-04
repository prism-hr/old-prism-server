package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.referencedata.jaxb.SourcesOfInterest.SourceOfInterest;

@Component
public class ImportedReferralSourceExtractor implements ImportedEntityExtractor {

    @Override
    public List<String> extract(Institution institution, PrismImportedEntity prismImportedEntity, List<Object> definitions) throws Exception {
        List<String> rows = Lists.newLinkedList();
        for (Object definition : definitions) {
            SourceOfInterest sourceOfInterest = (SourceOfInterest) definition;

            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(institution.getId().toString()));
            cells.add(prepareStringForSqlInsert(prismImportedEntity.name()));
            cells.add(prepareStringForSqlInsert(sourceOfInterest.getCode()));
            cells.add(prepareStringForSqlInsert(sourceOfInterest.getName()));
            cells.add(prepareStringForSqlInsert(new Integer(1).toString()));

            String row = prepareCellsForSqlInsert(cells);
            rows.add(row);
        }
        return rows;
    }

}
