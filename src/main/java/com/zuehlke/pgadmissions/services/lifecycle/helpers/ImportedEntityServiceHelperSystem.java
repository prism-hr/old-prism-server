package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.iso.jaxb.InstitutionDomiciles;
import com.zuehlke.pgadmissions.iso.jaxb.InstitutionDomiciles.InstitutionDomicile;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class ImportedEntityServiceHelperSystem implements AbstractServiceHelper {

    @Value("${import.institutionDomicile.location}")
    private String institutionDomicileImportLocation;

    @Value("${import.institutionDomicile.location.schema}")
    private String institutionDomicileSchemaLocation;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private SystemService systemService;

    @Override
    public void execute() throws Exception {
        DateTime baseline = new DateTime();
        System system = systemService.getSystem();
        DateTime lastDataImportTimestamp = system.getLastDataImportTimestamp();
        if (lastDataImportTimestamp == null || lastDataImportTimestamp.isBefore(baseline.minusDays(1))) {
            importInstitutionDomiciles(lastDataImportTimestamp);
            importedEntityService.mergeImportedEntities(lastDataImportTimestamp);
            systemService.setLastDataImportTimestamp(baseline);
        }
    }

    private void importInstitutionDomiciles(DateTime lastDataImportTimestamp) throws Exception {
        try {
            List<String> definitions = Lists.newArrayList();
            List<Object> institutionDomicileDefinitions = importedEntityService.readImportedData(InstitutionDomiciles.class,
                    "institutionDomicile", institutionDomicileSchemaLocation, institutionDomicileImportLocation, lastDataImportTimestamp);
            for (Object institutionDomicileDefinition : institutionDomicileDefinitions) {
                definitions.add(institutionService.mergeInstitutionDomicile((InstitutionDomicile) institutionDomicileDefinition));
            }
            institutionService.disableInstitutionDomiciles(definitions);
        } catch (Exception e) {
            throw new DataImportException("Error during the import of file: " + institutionDomicileImportLocation, e);
        }
    }

}