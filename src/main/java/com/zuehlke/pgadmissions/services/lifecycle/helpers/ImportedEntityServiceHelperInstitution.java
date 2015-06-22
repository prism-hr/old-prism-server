package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.isEntityImport;
import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.isResourceImport;

import java.io.InvalidClassException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.NotificationService;

@Component
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

    public void execute(Integer institution, PrismImportedEntity... exclusions) {
        List<ImportedEntityFeed> importedEntityFeeds = importedEntityService.getImportedEntityFeeds(institution, exclusions);
        for (ImportedEntityFeed importedEntityFeed : importedEntityFeeds) {
            try {
                execute(institution, importedEntityFeed);
            } catch (Exception e) {
                processImportException(importedEntityFeed, e);
            }
        }
    }

    // TODO: separate resource import
    private void execute(Integer institution, ImportedEntityFeed importedEntityFeed) throws Exception {
        List<Object> unmarshalledEntities = unmarshallImportedData(importedEntityFeed);
        if (!(unmarshalledEntities == null || unmarshalledEntities.isEmpty())) {
            Integer importedEntityFeedId = importedEntityFeed.getId();
            PrismImportedEntity prismImportedEntity = importedEntityFeed.getImportedEntityType();
            if (isResourceImport(prismImportedEntity)) {
                // mergeImportedPrograms(institution, importedEntityFeedId,
                // unmarshalledEntities);
            } else if (isEntityImport(prismImportedEntity)) {
                importedEntityService.mergeImportedEntities(importedEntityFeedId);
            } else {
                throw new InvalidClassException(prismImportedEntity.getEntityClass().getCanonicalName() + " is not a valid import target");
            }
        }
    }

    private List<Object> unmarshallImportedData(ImportedEntityFeed importedEntityFeed) throws Exception {
        PrismImportedEntity prismImportedEntity = importedEntityFeed.getImportedEntityType();
        return importedEntityService.readImportedData(prismImportedEntity.getEntityJaxbClass(), prismImportedEntity.getEntityJaxbProperty(),
                prismImportedEntity.getEntityXsdLocation(), prismImportedEntity.getEntityXmlLocation(), importedEntityFeed.getLastImportedTimestamp());
    }

    private void processImportException(ImportedEntityFeed importedEntityFeed, Exception e) {
        String errorMessage = e.getMessage();
        Throwable cause = e.getCause();
        if (cause != null) {
            errorMessage += "\n" + cause.toString();
        }
        logger.error("Error importing " + importedEntityFeed.getImportedEntityType().name() + " for " + importedEntityFeed.getInstitution().getCode(), e);
        notificationService.sendDataImportErrorNotifications(importedEntityFeed.getInstitution(), errorMessage);
    }

    // private void mergeImportedPrograms(Integer institutionId, Integer
    // importedEntityFeedId, List<Object> definitions) throws Exception {
    // DateTime baselineTime = new DateTime();
    // LocalDate baseline = baselineTime.toLocalDate();
    //
    // List<Integer> updates = Lists.newArrayList();
    // HashMultimap<String, ProgrammeOccurrence> batchedOccurrences =
    // getBatchedImportedPrograms(definitions);
    // for (String programCode : batchedOccurrences.keySet()) {
    // Set<ProgrammeOccurrence> occurrencesInBatch =
    // batchedOccurrences.get(programCode);
    // updates.add(importedEntityService.mergeImportedProgram(institutionId,
    // occurrencesInBatch, baseline, baselineTime));
    // }
    //
    // if (!updates.isEmpty()) {
    // importedEntityService.disableImportedPrograms(institutionId, updates,
    // baseline);
    // }
    //
    // importedEntityService.setLastImportedTimestamp(importedEntityFeedId);
    // }
    //
    // private HashMultimap<String, ProgrammeOccurrence>
    // getBatchedImportedPrograms(List<Object> definitions) {
    // HashMultimap<String, ProgrammeOccurrence> batchedImports =
    // HashMultimap.create();
    // for (Object definition : definitions) {
    // ProgrammeOccurrence programmeOccurrence = (ProgrammeOccurrence)
    // definition;
    // batchedImports.put(programmeOccurrence.getProgramme().getCode(),
    // programmeOccurrence);
    // }
    // return batchedImports;
    // }

}
