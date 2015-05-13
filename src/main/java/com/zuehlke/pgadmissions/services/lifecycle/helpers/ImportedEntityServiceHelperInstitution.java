package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.AgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes.LanguageQualificationType;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ImportedEntityServiceHelperInstitution implements AbstractServiceHelper {

    @Value("${context.environment}")
    private String contextEnvironment;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private NotificationService notificationService;

    public void execute() throws Exception {
        institutionService.populateDefaultImportedEntityFeeds();
        for (ImportedEntityFeed importedEntityFeed : importedEntityService.getImportedEntityFeeds()) {
            String maxRedirects = null;
            try {
                maxRedirects = System.getProperty("http.maxRedirects");
                System.setProperty("http.maxRedirects", "5");
                importEntities(importedEntityFeed);
            } catch (Exception e) {
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

    private void importEntities(ImportedEntityFeed importedEntityFeed) throws Exception {
        Institution institution = importedEntityFeed.getInstitution();
        if (contextEnvironment.equals("prod") || !institutionService.hasAuthenticatedFeeds(institution)) {
            List unmarshalled = unmarshalEntities(importedEntityFeed);
            if (unmarshalled != null) {
                Integer importedEntityFeedId = importedEntityFeed.getId();
                Class<?> importedEntityClass = (Class<?>) importedEntityFeed.getImportedEntityType().getEntityClass();
                if (importedEntityClass.equals(Program.class)) {
                    mergeImportedPrograms(importedEntityFeedId, institution, unmarshalled);
                } else if (importedEntityClass.equals(ImportedInstitution.class)) {
                    mergeImportedInstitutions(importedEntityFeedId, institution, unmarshalled);
                } else if (importedEntityClass.equals(ImportedLanguageQualificationType.class)) {
                    mergeImportedLanguageQualificationTypes(importedEntityFeedId, institution, unmarshalled);
                } else if (importedEntityClass.equals(AgeRange.class)) {
                    mergeImportedAgeRanges(importedEntityFeedId, institution, unmarshalled);
                } else {
                    mergeImportedEntities(importedEntityFeedId, institution, (Class<ImportedEntity>) importedEntityClass,
                            (List<Object>) unmarshalled);
                }
            }
        }
    }

    private List<Object> unmarshalEntities(final ImportedEntityFeed importedEntityFeed) throws Exception {
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
                return readImportedData(importedEntityType, fileUrl);
            }

            return null;
        } finally {
            Authenticator.setDefault(null);
        }
    }

    @CacheEvict("institutionStaticData")
    private List<Object> readImportedData(PrismImportedEntity importedEntityType, URL fileUrl) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(importedEntityType.getJaxbClass());

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new DefaultResourceLoader().getResource(importedEntityType.getSchemaLocation()).getFile());

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(schema);

        Object unmarshalled = unmarshaller.unmarshal(fileUrl);
        return (List<Object>) PrismReflectionUtils.getProperty(unmarshalled, importedEntityType.getJaxbPropertyName());
    }

    private void mergeImportedPrograms(Integer importedEntityFeedId, Institution institution, List<ProgrammeOccurrence> programDefinitions) throws Exception {
        DateTime baselineTime = new DateTime();
        LocalDate baseline = baselineTime.toLocalDate();

        List<Integer> updates = Lists.newArrayList();
        HashMultimap<String, ProgrammeOccurrence> batchedOccurrences = getBatchedImportedPrograms(programDefinitions);
        for (String programCode : batchedOccurrences.keySet()) {
            Set<ProgrammeOccurrence> occurrencesInBatch = batchedOccurrences.get(programCode);
            updates.add(importedEntityService.mergeImportedProgram(institution, occurrencesInBatch, baseline, baselineTime));
        }

        importedEntityService.disableImportedPrograms(institution, updates, baseline);
        importedEntityService.setLastImportedTimestamp(importedEntityFeedId);
    }

    private void mergeImportedInstitutions(Integer importedEntityFeedId, Institution institution,
            List<com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution> institutionDefinitions) throws Exception {
        List<Integer> updates = Lists.newArrayList();
        for (com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution transientImportedInstitution : institutionDefinitions) {
            updates.add(importedEntityService.mergeImportedInstitution(institution, transientImportedInstitution));
        }
        importedEntityService.disableAllInstitutions(institution, updates);
        importedEntityService.setLastImportedTimestamp(importedEntityFeedId);
    }

    private void mergeImportedLanguageQualificationTypes(Integer importedEntityFeedId, Institution institution,
            List<LanguageQualificationType> languageQualificationTypeDefinitions) throws Exception {
        List<Integer> updates = Lists.newArrayList();
        for (LanguageQualificationType languageQualificationTypeDefinition : languageQualificationTypeDefinitions) {
            updates.add(importedEntityService.mergeImportedLanguageQualificationType(institution, languageQualificationTypeDefinition));
        }
        importedEntityService.disableEntities(ImportedLanguageQualificationType.class, institution, updates);
        importedEntityService.setLastImportedTimestamp(importedEntityFeedId);
    }

    private void mergeImportedAgeRanges(Integer importedEntityFeedId, Institution institution,
            List<com.zuehlke.pgadmissions.referencedata.jaxb.AgeRanges.AgeRange> ageRangeDefinitions) throws Exception {
        List<Integer> updates = Lists.newArrayList();
        for (com.zuehlke.pgadmissions.referencedata.jaxb.AgeRanges.AgeRange ageRangeDefinition : ageRangeDefinitions) {
            updates.add(importedEntityService.mergeImportedAgeRange(institution, ageRangeDefinition));
        }
        importedEntityService.disableEntities(AgeRange.class, institution, updates);
        importedEntityService.setLastImportedTimestamp(importedEntityFeedId);
    }

    private void mergeImportedEntities(Integer importedEntityFeedId, Institution institution, Class<ImportedEntity> importedEntityClass,
            List<Object> entityDefinitions) throws Exception {
        List<Integer> updates = Lists.newArrayList();
        for (Object entityDefinition : entityDefinitions) {
            updates.add(importedEntityService.mergeImportedEntity(importedEntityClass, institution, entityDefinition));
        }
        importedEntityService.disableEntities(importedEntityClass, institution, updates);
        importedEntityService.setLastImportedTimestamp(importedEntityFeedId);
    }

    private HashMultimap<String, ProgrammeOccurrence> getBatchedImportedPrograms(List<ProgrammeOccurrence> importedPrograms) {
        HashMultimap<String, ProgrammeOccurrence> batchedImports = HashMultimap.create();
        for (ProgrammeOccurrence occurrence : importedPrograms) {
            batchedImports.put(occurrence.getProgramme().getCode(), occurrence);
        }
        return batchedImports;
    }

}
