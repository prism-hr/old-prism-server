package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.system.System;
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
        LocalDate baseline = new LocalDate();
        System system = systemService.getSystem();
        LocalDate lastImportDate = system.getLastDataImportDate();
        if (lastImportDate == null || lastImportDate.isBefore(baseline)) {
            importInstitutionDomiciles();
            importEntities();
            systemService.setLastDataImportDate(baseline);
        }
    }

    private void importInstitutionDomiciles() throws Exception {
        try {
            List<String> definitions = Lists.newArrayList();
            List<InstitutionDomiciles.InstitutionDomicile> institutionDomicileDefinitions = unmarshal();
            for (InstitutionDomiciles.InstitutionDomicile institutionDomicileDefinition : institutionDomicileDefinitions) {
                definitions.add(institutionService.mergeInstitutionDomicile(institutionDomicileDefinition));
            }
            institutionService.disableInstitutionDomiciles(definitions);
        } catch (Exception e) {
            throw new DataImportException("Error during the import of file: " + institutionDomicileImportLocation, e);
        }
    }

    private void importEntities() {
        for (PrismImportedEntity prisImportedEntity : PrismImportedEntity.values()) {
            if (prisImportedEntity.isSystemImport()) {
                importedEntityService.mergeImportedEntities(prisImportedEntity);
            }
        }
    }

    public List<InstitutionDomicile> unmarshal() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(InstitutionDomiciles.class);
        DefaultResourceLoader loader = new DefaultResourceLoader();

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(loader.getResource(institutionDomicileSchemaLocation).getURL());

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(schema);

        InstitutionDomiciles unmarshalled = (InstitutionDomiciles) unmarshaller.unmarshal(loader.getResource(institutionDomicileImportLocation).getURL());
        return unmarshalled.getInstitutionDomicile();
    }

}