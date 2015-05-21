package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.AGE_RANGE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.LANGUAGE_QUALIFICATION_TYPE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.PROGRAM;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes.LanguageQualificationType;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

@Component
@SuppressWarnings({ "unchecked" })
public class ImportedEntityServiceHelperInstitution implements AbstractServiceHelper {

    private static final Logger logger = LoggerFactory.getLogger(ImportedEntityServiceHelperInstitution.class);

    @Value("${context.environment}")
    private String contextEnvironment;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private NotificationService notificationService;

    public void execute() throws Exception {
        List<Integer> institutions = institutionService.getApprovedInstitutions();
        for (Integer institution : institutions) {
            execute(institution);
        }
    }

    public void execute(Integer institution, PrismImportedEntity... exclusions) throws Exception {
        List<ImportedEntityFeed> importedEntityFeeds = importedEntityService.getInstitutionImportedEntityFeeds(institution, exclusions);
        for (ImportedEntityFeed importedEntityFeed : importedEntityFeeds) {
            List<?> unmarshalledEntities = unmarshallEntities(importedEntityFeed);
            if (unmarshalledEntities != null) {
                Integer importedEntityFeedId = importedEntityFeed.getId();
                PrismImportedEntity prismImportedEntity = importedEntityFeed.getImportedEntityType();
                if (prismImportedEntity.equals(AGE_RANGE)) {
                    importedEntityService.mergeImportedAgeRanges(importedEntityFeedId,
                            (List<com.zuehlke.pgadmissions.referencedata.jaxb.AgeRanges.AgeRange>) unmarshalledEntities);
                } else if (prismImportedEntity.equals(INSTITUTION)) {
                    importedEntityService.mergeImportedInstitutions(importedEntityFeedId,
                            (List<com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution>) unmarshalledEntities);
                } else if (prismImportedEntity.equals(LANGUAGE_QUALIFICATION_TYPE)) {
                    importedEntityService.mergeImportedLanguageQualifications(importedEntityFeedId, (List<LanguageQualificationType>) unmarshalledEntities);
                } else if (prismImportedEntity.equals(PROGRAM)) {
                    mergeImportedPrograms(institution, importedEntityFeedId, (List<ProgrammeOccurrence>) unmarshalledEntities);
                } else {
                    importedEntityService.mergeImportedEntities(importedEntityFeedId, (List<Object>) unmarshalledEntities);
                }
            }
        }
    }

    private List<?> unmarshallEntities(ImportedEntityFeed importedEntityFeed) {
        String maxRedirects = null;
        List<?> unmarshalledEntities = Lists.newArrayList();
        try {
            maxRedirects = System.getProperty("http.maxRedirects");
            System.setProperty("http.maxRedirects", "5");
            if (contextEnvironment.equals("prod") || !institutionService.hasAuthenticatedFeeds(importedEntityFeed.getInstitution())) {
                unmarshalledEntities = unmarshalEntities(importedEntityFeed);
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            Throwable cause = e.getCause();
            if (cause != null) {
                errorMessage += "\n" + cause.toString();
            }
            logger.error("Error importing " + importedEntityFeed.getImportedEntityType().name() + " for " + importedEntityFeed.getInstitution().getCode(), e);
            notificationService.sendDataImportErrorNotifications(importedEntityFeed.getInstitution(), errorMessage);
        } finally {
            Authenticator.setDefault(null);
            if (maxRedirects != null) {
                System.setProperty("http.maxRedirects", maxRedirects);
            } else {
                System.clearProperty("http.maxRedirects");
            }
        }
        return unmarshalledEntities;
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

    private void mergeImportedPrograms(Integer institutionId, Integer importedEntityFeedId, List<ProgrammeOccurrence> definitions) throws Exception {
        DateTime baselineTime = new DateTime();
        LocalDate baseline = baselineTime.toLocalDate();

        List<Integer> updates = Lists.newArrayList();
        HashMultimap<String, ProgrammeOccurrence> batchedOccurrences = getBatchedImportedPrograms(definitions);
        for (String programCode : batchedOccurrences.keySet()) {
            Set<ProgrammeOccurrence> occurrencesInBatch = batchedOccurrences.get(programCode);
            updates.add(importedEntityService.mergeImportedProgram(institutionId, occurrencesInBatch, baseline, baselineTime));
        }

        if (!updates.isEmpty()) {
            importedEntityService.disableImportedPrograms(institutionId, updates, baseline);
        }

        importedEntityService.setLastImportedTimestamp(importedEntityFeedId);
    }

    private HashMultimap<String, ProgrammeOccurrence> getBatchedImportedPrograms(List<ProgrammeOccurrence> imports) {
        HashMultimap<String, ProgrammeOccurrence> batchedImports = HashMultimap.create();
        for (ProgrammeOccurrence occurrence : imports) {
            batchedImports.put(occurrence.getProgramme().getCode(), occurrence);
        }
        return batchedImports;
    }

}
