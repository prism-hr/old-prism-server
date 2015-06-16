package com.zuehlke.pgadmissions.services.helpers.extractors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.Domicile;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

@Component
public class ImportedInstitutionExtractor implements ImportedEntityExtractor {

    @Inject
    private ImportedEntityService importedEntityService;

    @Override
    public List<String> extract(Institution institution, PrismImportedEntity prismImportedEntity, List<Object> definitions) throws Exception {
        Map<String, String> domicilesByCode = Maps.newHashMap();
        List<Domicile> domiciles = importedEntityService.getEnabledImportedEntities(institution, Domicile.class);
        for (Domicile domicile : domiciles) {
            domicilesByCode.put(domicile.getCode(), domicile.getId().toString());
        }

        List<String> rows = Lists.newLinkedList();
        for (Object definition : definitions) {
            com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution importedInstitution = (com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution) definition;

            String code = importedInstitution.getCode();
            Integer custom = (code.startsWith("CUST") || code.startsWith("PRISM_")) ? 1 : 0;

            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(institution.getId().toString()));
            cells.add(prepareStringForSqlInsert(domicilesByCode.get(importedInstitution.getDomicile())));
            cells.add(prepareStringForSqlInsert(importedInstitution.getCode()));
            cells.add(prepareStringForSqlInsert(importedInstitution.getName()));
            cells.add(prepareStringForSqlInsert(new Integer(1).toString()));
            cells.add(prepareStringForSqlInsert(new Integer(custom).toString()));
            cells.add(prepareStringForSqlInsert(importedInstitution.getUcasId()));
            cells.add(prepareStringForSqlInsert(importedInstitution.getFacebookId()));
            String row = prepareCellsForSqlInsert(cells);
            rows.add(row);
        }
        return rows;
    }

}
