package com.zuehlke.pgadmissions.services.importers;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.LanguageQualificationType;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.ModeOfAttendance;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.SystemService;

@Service
public class EntityImportService {

    private static DateTimeFormatter datetFormatter = DateTimeFormat.forPattern("dd-MMM-yy");

    private static final Logger logger = LoggerFactory.getLogger(EntityImportService.class);

    @Autowired
    private ActionService actionService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SystemService systemService;

    public void importReferenceData() {
        institutionService.populateDefaultImportedEntityFeeds();

        for (ImportedEntityFeed importedEntityFeed : getImportedEntityFeedsToImport()) {
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
            setLastImportedDate(importedEntityFeed);
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
        disableAllEntities(entityClass, institution);
        for (ImportedEntity transientEntity : transientEntities) {
            mergeEntity(entityClass, institution, transientEntity);
        }
    }

    private void mergeProgrammeOcccurences(Institution institution, List<ProgrammeOccurrence> occurrences) throws Exception {
        LocalDate baseline = new LocalDate();
        disableAllImportedPrograms(institution, baseline);

        HashMultimap<String, ProgrammeOccurrence> batchedOccurrences = getBatchedProgramOccurrences(occurrences);

        for (String programCode : batchedOccurrences.keySet()) {
            Set<ProgrammeOccurrence> occurrencesInBatch = batchedOccurrences.get(programCode);
            mergeBatchedProgrammeOccurrences(institution, occurrencesInBatch, baseline);
        }
    }

    @Transactional
    private void mergeEntity(Class<ImportedEntity> entityClass, Institution institution, ImportedEntity transientEntity) {
        ImportedEntity persistentEntity = entityService.getDuplicateEntity(transientEntity);

        if (persistentEntity == null) {
            entityService.save(transientEntity);
        } else {
            String transientCode = transientEntity.getCode();
            String transientName = transientEntity.getName();

            String persistentCode = persistentEntity.getCode();
            String persistentName = persistentEntity.getName();

            if (transientCode == persistentCode && transientName == persistentName) {
                persistentEntity.setEnabled(true);
            } else {
                if (transientName != persistentName) {
                    ImportedEntity otherPersistentEntity = importedEntityService.getByName(entityClass, institution, transientName);
                    if (otherPersistentEntity == null) {
                        persistentEntity.setName(transientName);
                    }
                } else {
                    ImportedEntity otherPersistentEntity = importedEntityService.getByCode(entityClass, institution, transientCode);
                    if (otherPersistentEntity == null) {
                        persistentEntity.setCode(transientCode);
                    }
                }
                persistentEntity.setEnabled(true);
            }
        }
    }

    @Transactional
    private void mergeBatchedProgrammeOccurrences(Institution institution, Set<ProgrammeOccurrence> occurrencesInBatch, LocalDate baseline) {
        Programme programme = occurrencesInBatch.iterator().next().getProgramme();
        Program program = mergeProgram(programme, institution);

        for (ProgrammeOccurrence occurrence : occurrencesInBatch) {
            StudyOption studyOption = mergeStudyOption(institution, program, occurrence.getModeOfAttendance());

            LocalDate transientStartDate = datetFormatter.parseLocalDate(occurrence.getStartDate());
            LocalDate transientCloseDate = datetFormatter.parseLocalDate(occurrence.getEndDate());

            ProgramStudyOption transientProgramStudyOption = new ProgramStudyOption().withProgram(program).withStudyOption(studyOption)
                    .withApplicationStartDate(transientStartDate).withApplicationCloseDate(transientCloseDate)
                    .withEnabled(transientCloseDate.isBefore(baseline) && transientCloseDate.isAfter(baseline));

            ProgramStudyOption persistentProgramStudyOption = mergeProgramStudyOption(program, transientProgramStudyOption, baseline);

            ProgramStudyOptionInstance transientProgramStudyOptionInstance = new ProgramStudyOptionInstance()
                    .withProgramStudyOption(persistentProgramStudyOption).withApplicationStartDate(transientStartDate)
                    .withApplicationCloseDate(transientCloseDate).withAcademicYear(Integer.toString(transientStartDate.getYear()))
                    .withIdentifier(occurrence.getIdentifier()).withEnabled(transientCloseDate.isBefore(baseline) && transientCloseDate.isAfter(baseline));

            mergeImportedStudyOptionInstance(program, transientProgramStudyOptionInstance, baseline);
            executeProgramImportAction(program);
        }
    }

    @Transactional
    private Program mergeProgram(Programme importProgram, Institution institution) throws WorkflowEngineException {
        institution = institutionService.getById(institution.getId());
        User proxyCreator = institution.getUser();

        String title = importProgram.getName();
        Advert transientAdvert = new Advert().withTitle(title);

        DateTime importTimestamp = new DateTime();
        String programTypeCode = PrismProgramType.findValueFromString(importProgram.getName()).name();

        String transientTitle = importProgram.getName();
        boolean transientRequireProjectDefinition = importProgram.isAtasRegistered();

        ProgramType programType = importedEntityService.getByCode(ProgramType.class, institution, programTypeCode);
        Program transientProgram = new Program().withSystem(systemService.getSystem()).withInstitution(institution).withImportedCode(importProgram.getCode())
                .withTitle(transientTitle).withRequireProjectDefinition(transientRequireProjectDefinition)
                .withGroupStartFrequency(PrismProgramType.valueOf(programTypeCode).getGroupStartFrequency()).withImported(true).withAdvert(transientAdvert)
                .withProgramType(programType).withUser(proxyCreator).withCreatedTimestamp(importTimestamp).withUpdatedTimestamp(importTimestamp);

        Program persistentProgram = entityService.getDuplicateEntity(transientProgram);

        if (persistentProgram == null) {
            entityService.save(transientProgram);
            return transientProgram;
        } else {
            persistentProgram.setTitle(transientTitle);
            persistentProgram.setRequireProjectDefinition(transientRequireProjectDefinition);
            persistentProgram.setUpdatedTimestamp(importTimestamp);
            return persistentProgram;
        }
    }

    @Transactional
    private ProgramStudyOption mergeProgramStudyOption(Program program, ProgramStudyOption transientProgramStudyOption, LocalDate baseline) {
        program = programService.getById(program.getId());
        ProgramStudyOption persistentProgramStudyOption = entityService.getDuplicateEntity(transientProgramStudyOption);

        if (persistentProgramStudyOption == null) {
            entityService.save(transientProgramStudyOption);
            return transientProgramStudyOption;
        } else {
            LocalDate transientStartDate = transientProgramStudyOption.getApplicationStartDate();
            LocalDate transientCloseDate = transientProgramStudyOption.getApplicationCloseDate();

            LocalDate persistentStartDate = persistentProgramStudyOption.getApplicationStartDate();
            LocalDate persistentCloseDate = persistentProgramStudyOption.getApplicationCloseDate();

            persistentStartDate = transientStartDate.isBefore(persistentStartDate) ? transientStartDate : persistentStartDate;
            persistentCloseDate = transientCloseDate.isAfter(persistentCloseDate) ? transientCloseDate : persistentCloseDate;

            persistentProgramStudyOption.setApplicationStartDate(persistentStartDate);
            persistentProgramStudyOption.setApplicationCloseDate(persistentCloseDate);

            persistentProgramStudyOption.setEnabled(persistentStartDate.isBefore(baseline) && persistentCloseDate.isAfter(baseline));
            return persistentProgramStudyOption;
        }
    }

    @Transactional
    private void mergeImportedStudyOptionInstance(Program program, ProgramStudyOptionInstance transientProgramStudyOptionInstance, LocalDate baseline) {
        program = programService.getById(program.getId());
        Advert advert = program.getAdvert();

        ProgramStudyOption programStudyOption = transientProgramStudyOptionInstance.getProgramStudyOption();
        programStudyOption = programService.getProgramStudyOptionById(programStudyOption.getId());

        entityService.createOrUpdate(transientProgramStudyOptionInstance);

        LocalDate transientStartDate = transientProgramStudyOptionInstance.getApplicationStartDate();
        LocalDate transientCloseDate = transientProgramStudyOptionInstance.getApplicationCloseDate();

        LocalDate programPublishDate = advert.getPublishDate();

        if (programPublishDate == null || transientStartDate.isBefore(programPublishDate)) {
            advert.setPublishDate(transientStartDate);
        }

        if (transientStartDate.isBefore(baseline) && transientCloseDate.isAfter(baseline)) {
            programStudyOption.setDefaultStartDate(transientCloseDate);
        }
    }

    @Transactional
    private StudyOption mergeStudyOption(Institution institution, Program program, ModeOfAttendance modeOfAttendance) {
        institution = institutionService.getById(institution.getId());
        program = programService.getById(program.getId());
        String externalcode = modeOfAttendance.getCode();
        PrismStudyOption internalCode = PrismStudyOption.findValueFromString(externalcode);
        StudyOption studyOption = new StudyOption().withInstitution(institution).withCode(internalCode.name()).withName(externalcode).withEnabled(true);
        return entityService.createOrUpdate(studyOption);
    }

    @Transactional
    public List<ImportedEntityFeed> getImportedEntityFeedsToImport() {
        return importedEntityService.getImportedEntityFeedsToImport();
    }

    @Transactional
    public void setLastImportedDate(ImportedEntityFeed detachedImportedEntityFeed) {
        ImportedEntityFeed persistentImportedEntityFeed = entityService.getById(ImportedEntityFeed.class, detachedImportedEntityFeed.getId());
        persistentImportedEntityFeed.setLastImportedDate(new LocalDate());
    }

    @Transactional
    private void disableAllImportedPrograms(Institution institution, LocalDate baseline) {
        importedEntityService.disableAllImportedPrograms(institution, baseline);
    }

    @Transactional
    private void disableAllEntities(Class<? extends ImportedEntity> entityClass, Institution institution) {
        importedEntityService.disableAllEntities(entityClass, institution);
    }

    @Transactional
    private void executeProgramImportAction(Program program) {
        Action action = actionService.getById(PrismAction.INSTITUTION_IMPORT_PROGRAM);

        User invoker = program.getUser();
        Role invokerRole = roleService.getCreatorRole(program);

        Comment comment = new Comment().withUser(invoker).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false)
                .withAssignedUser(invoker, invokerRole);
        actionService.executeSystemAction(program, action, comment);
    }

    private HashMultimap<String, ProgrammeOccurrence> getBatchedProgramOccurrences(List<ProgrammeOccurrence> occurrences) {
        HashMultimap<String, ProgrammeOccurrence> batchedImports = HashMultimap.create();
        for (ProgrammeOccurrence occurrence : occurrences) {
            batchedImports.put(occurrence.getProgramme().getCode(), occurrence);
        }
        return batchedImports;
    }

}
