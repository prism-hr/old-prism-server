package com.zuehlke.pgadmissions.lifecycle;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.LanguageQualificationType;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.importers.GenericEntityImportConverter;
import com.zuehlke.pgadmissions.services.importers.InstitutionImportConverter;
import com.zuehlke.pgadmissions.services.importers.LanguageQualificationTypeImportConverter;

@Component
public class EntityImportHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(EntityImportHelper.class);
    
    @Autowired
    private EntityService entityService;
    
    @Autowired
    private ImportedEntityService importedEntityService;
    
    @Autowired
    private InstitutionService institutionService;
    
    @Autowired
    private NotificationService notificationService;

    public void importReferenceData() {
        institutionService.populateDefaultImportedEntityFeeds();

        for (ImportedEntityFeed importedEntityFeed : importedEntityService.getImportedEntityFeedsToImport()) {
            String maxRedirects = null;

            try {
                maxRedirects = System.getProperty("http.maxRedirects");
                System.setProperty("http.maxRedirects", "5");

                importReferenceEntities(importedEntityFeed);
            } catch (DataImportException e) {
                logger.error("Error importing reference data.", e);
                String errorMessage = e.getMessage();
                Throwable cause = e.getCause();

                if (cause != null) {
                    errorMessage += "\n" + cause.toString();
                }

                notificationService.sendDataImportErrorNotifications(importedEntityFeed.getInstitution(), errorMessage);
            } finally {
                Authenticator.setDefault(null);
                if (maxRedirects != null) {
                    System.setProperty("http.maxRedirects", maxRedirects);
                } else {
                    System.clearProperty("http.maxRedirects");
                }
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void importReferenceEntities(ImportedEntityFeed importedEntityFeed) throws DataImportException {
        String fileLocation = importedEntityFeed.getLocation();
        logger.info("Starting the import from file: " + fileLocation);

        try {
            importedEntityService.setLastImportedDate(importedEntityFeed);
            List unmarshalled = unmarshall(importedEntityFeed);

            Class<ImportedEntity> entityClass = (Class<ImportedEntity>) importedEntityFeed.getImportedEntityType().getEntityClass();

            Institution institution = importedEntityFeed.getInstitution();
            if (entityClass.equals(Program.class)) {
                mergeProgrammeOcccurences(institution, (List<ProgrammeOccurrence>) unmarshalled);
            } else {
                Function<Object, ? extends ImportedEntity> entityConverter;
                if (entityClass.equals(LanguageQualificationType.class)) {
                    entityConverter = new LanguageQualificationTypeImportConverter(institution);
                } else if (entityClass.equals(ImportedInstitution.class)) {
                    entityConverter = new InstitutionImportConverter(institution, entityService);
                } else {
                    entityConverter = GenericEntityImportConverter.create(institution, entityClass);
                }

                Iterable<ImportedEntity> newEntities = Iterables.transform(unmarshalled, entityConverter);
                mergeEntities(entityClass, importedEntityFeed.getInstitution(), newEntities);
            }

            // TODO: state change to institution ready to use.
        } catch (Exception e) {
            throw new DataImportException("Error during the import of file: " + fileLocation, e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Object> unmarshall(final ImportedEntityFeed importedEntityFeed) throws Exception {
        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(importedEntityFeed.getUsername(), importedEntityFeed.getPassword().toCharArray());
                }
            });

            URL fileUrl = new DefaultResourceLoader().getResource(importedEntityFeed.getLocation()).getURL();
            JAXBContext jaxbContext = JAXBContext.newInstance(importedEntityFeed.getImportedEntityType().getJaxbClass());
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Object unmarshaled = unmarshaller.unmarshal(fileUrl);
            return (List<Object>) PropertyUtils.getSimpleProperty(unmarshaled, importedEntityFeed.getImportedEntityType().getJaxbPropertyName());
        } finally {
            Authenticator.setDefault(null);
        }
    }
    
    private void mergeEntities(Class<ImportedEntity> entityClass, Institution institution, Iterable<ImportedEntity> transientEntities) {
        importedEntityService.disableAllEntities(entityClass, institution);
        for (ImportedEntity transientEntity : transientEntities) {
            importedEntityService.mergeEntity(entityClass, institution, transientEntity);
        }
    }

    private void mergeProgrammeOcccurences(Institution institution, List<ProgrammeOccurrence> occurrences) throws Exception {
        LocalDate baseline = new LocalDate();
        
        importedEntityService.disableAllImportedPrograms(institution, baseline);
        HashMultimap<String, ProgrammeOccurrence> batchedOccurrences = getBatchedProgramOccurrences(occurrences);

        for (String programCode : batchedOccurrences.keySet()) {
            Set<ProgrammeOccurrence> occurrencesInBatch = batchedOccurrences.get(programCode);
            importedEntityService.mergeBatchedProgrammeOccurrences(institution, occurrencesInBatch, baseline);
        }
    }
    
    private HashMultimap<String, ProgrammeOccurrence> getBatchedProgramOccurrences(List<ProgrammeOccurrence> occurrences) {
        HashMultimap<String, ProgrammeOccurrence> batchedImports = HashMultimap.create();
        for (ProgrammeOccurrence occurrence : occurrences) {
            batchedImports.put(occurrence.getProgramme().getCode(), occurrence);
        }
        return batchedImports;
    }
    
}
