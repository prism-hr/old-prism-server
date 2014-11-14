package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes.LanguageQualificationType;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ImportedEntityServiceHelperInstitution extends AbstractServiceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportedEntityServiceHelperInstitution.class);

    @Value("${context.environment}")
    private String contextEnvironment;
    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private NotificationService notificationService;

    public void execute() throws DeduplicationException, InstantiationException, IllegalAccessException, IOException, JAXBException, SAXException {
        institutionService.populateDefaultImportedEntityFeeds();
        for (ImportedEntityFeed importedEntityFeed : importedEntityService.getImportedEntityFeeds()) {
            String maxRedirects = null;
            try {
                maxRedirects = System.getProperty("http.maxRedirects");
                System.setProperty("http.maxRedirects", "5");
                importEntities(importedEntityFeed);
            } catch (DataImportException e) {
                LOGGER.error("Error importing reference data", e);
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

    private void importEntities(ImportedEntityFeed importedEntityFeed) throws IOException, JAXBException, SAXException, DataImportException,
            DeduplicationException, InstantiationException, IllegalAccessException {
        Institution institution = importedEntityFeed.getInstitution();
        List unmarshalled = unmarshalEntities(importedEntityFeed);
        if (unmarshalled != null) {
            Class<ImportedEntity> importedEntityClass = (Class<ImportedEntity>) importedEntityFeed.getImportedEntityType().getEntityClass();
            if ((institution.getUclInstitution() && contextEnvironment.equals("prod")) || !importedEntityFeed.isAuthenticated()) {
                if (importedEntityClass.equals(Program.class)) {
                    mergeImportedPrograms(institution, (List<ProgrammeOccurrence>) unmarshalled);
                } else if (importedEntityClass.equals(ImportedInstitution.class)) {
                    mergeImportedInstitutions(institution, (List<com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution>) unmarshalled);
                } else if (importedEntityClass.equals(ImportedLanguageQualificationType.class)) {
                    mergeImportedLanguageQualificationTypes(institution,
                            (List<com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes.LanguageQualificationType>) unmarshalled);
                } else {
                    mergeImportedEntities(importedEntityClass, institution, (List<Object>) unmarshalled);
                }

                importedEntityService.setLastImportedTimestamp(importedEntityFeed);
            }
        }
    }

    private List<Object> unmarshalEntities(final ImportedEntityFeed importedEntityFeed) throws IOException, JAXBException, SAXException {
        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(importedEntityFeed.getUsername(), importedEntityFeed.getPassword().toCharArray());
                }
            });

            DateTime lastImportedTimestamp = importedEntityFeed.getLastImportedTimestamp();
            PrismImportedEntity importedEntityType = importedEntityFeed.getImportedEntityType();

            URL fileUrl = new DefaultResourceLoader().getResource(importedEntityFeed.getLocation()).getURL();
            URLConnection connection = fileUrl.openConnection();
            Long lastModifiedTimestamp = connection.getLastModified();

            if (lastImportedTimestamp == null || lastModifiedTimestamp == 0
                    || new LocalDateTime(lastModifiedTimestamp).toDateTime().isAfter(lastImportedTimestamp)) {
                JAXBContext jaxbContext = JAXBContext.newInstance(importedEntityType.getJaxbClass());

                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema(new DefaultResourceLoader().getResource(importedEntityType.getSchemaLocation()).getFile());

                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                unmarshaller.setSchema(schema);

                Object unmarshalled = unmarshaller.unmarshal(fileUrl);
                return (List<Object>) ReflectionUtils.getProperty(unmarshalled, importedEntityType.getJaxbPropertyName());
            }

            return null;
        } finally {
            Authenticator.setDefault(null);
        }
    }

    private void mergeImportedPrograms(Institution institution, List<ProgrammeOccurrence> programDefinitions) throws DeduplicationException,
            DataImportException {
        LocalDate baseline = new LocalDate();

        importedEntityService.disableAllImportedPrograms(institution, baseline);
        HashMultimap<String, ProgrammeOccurrence> batchedOccurrences = getBatchedImportedPrograms(programDefinitions);

        for (String programCode : batchedOccurrences.keySet()) {
            Set<ProgrammeOccurrence> occurrencesInBatch = batchedOccurrences.get(programCode);
            importedEntityService.mergeImportedProgram(institution, occurrencesInBatch, baseline);
        }
    }

    private void mergeImportedInstitutions(Institution institution,
            List<com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution> institutionDefinitions) throws DataImportException,
            DeduplicationException {
        importedEntityService.disableAllEntities(ImportedInstitution.class, institution);
        for (com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution transientImportedInstitution : institutionDefinitions) {
            importedEntityService.mergeImportedInstitution(institution, transientImportedInstitution);
        }
    }

    private void mergeImportedLanguageQualificationTypes(Institution institution, List<LanguageQualificationType> languageQualificationTypeDefinitions)
            throws DeduplicationException {
        importedEntityService.disableAllEntities(ImportedLanguageQualificationType.class, institution);
        for (LanguageQualificationType languageQualificationTypeDefinition : languageQualificationTypeDefinitions) {
            importedEntityService.mergeImportedLanguageQualificationType(institution, languageQualificationTypeDefinition);
        }
    }

    private void mergeImportedEntities(Class<ImportedEntity> importedEntityClass, Institution institution, List<Object> entityDefinitions)
            throws InstantiationException, IllegalAccessException, DeduplicationException {
        importedEntityService.disableAllEntities(importedEntityClass, institution);
        for (Object entityDefinition : entityDefinitions) {
            importedEntityService.mergeImportedEntity(importedEntityClass, institution, entityDefinition);
        }
    }

    private HashMultimap<String, ProgrammeOccurrence> getBatchedImportedPrograms(List<ProgrammeOccurrence> importedPrograms) {
        HashMultimap<String, ProgrammeOccurrence> batchedImports = HashMultimap.create();
        for (ProgrammeOccurrence occurrence : importedPrograms) {
            batchedImports.put(occurrence.getProgramme().getCode(), occurrence);
        }
        return batchedImports;
    }

}
