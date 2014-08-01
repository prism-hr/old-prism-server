package com.zuehlke.pgadmissions.services.importers;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate.SYSTEM_IMPORT_ERROR_NOTIFICATION;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.LanguageQualificationType;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
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
import com.zuehlke.pgadmissions.services.UserService;

@Service
public class EntityImportService {

    private static final Logger logger = LoggerFactory.getLogger(EntityImportService.class);

    private static DateTimeFormatter dtFormatter = DateTimeFormat.forPattern("dd-MMM-yy");

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ProgramService programService;
    
    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private UserService userService;
    
    @Autowired private NotificationService notificationService;
    
    @Autowired
    private ApplicationContext applicationContext;
    
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
                String message = e.getMessage();
                Throwable cause = e.getCause();
                if (cause != null) {
                    message += "\n" + cause.toString();
                }

                com.zuehlke.pgadmissions.domain.System system = systemService.getSystem();
                for (User user : userService.getUsersForResourceAndRole(system, PrismRole.SYSTEM_ADMINISTRATOR)) {
                    NotificationTemplate importError = notificationService.getById(SYSTEM_IMPORT_ERROR_NOTIFICATION);
                    notificationService.sendNotification(user, system, importError, ImmutableMap.of("errorMessage", message));
                }

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
        EntityImportService thisBean = applicationContext.getBean(EntityImportService.class);
        String fileLocation = importedEntityFeed.getLocation();
        logger.info("Starting the import from file: " + fileLocation);

        try {
            List unmarshalled = thisBean.unmarshall(importedEntityFeed);

            Class<ImportedEntity> entityClass = (Class<ImportedEntity>) importedEntityFeed.getImportedEntityType().getEntityClass();

            Institution institution = importedEntityFeed.getInstitution();
            if (entityClass.equals(Program.class)) {
                thisBean.mergePrograms((List<ProgrammeOccurrence>) unmarshalled, institution);
            } else {
                Function<Object, ? extends ImportedEntity> entityConverter;
                if (entityClass.equals(LanguageQualificationType.class)) {
                    entityConverter = new LanguageQualificationTypeImportConverter(institution);
                } else if (entityClass.equals(ImportedInstitution.class)) {
                    entityConverter = new InstitutionImportConverter(institution, entityService);
                } else {
                    entityConverter = GenericEntityImportConverter.create(entityClass, institution);
                }

                Iterable<ImportedEntity> newEntities = Iterables.transform(unmarshalled, entityConverter);

                thisBean.mergeImportedEntities(entityClass, importedEntityFeed.getInstitution(), newEntities);
            }
            
            setLastImportedDate(importedEntityFeed);
            // TODO: state change to institution ready to use.
        } catch (Exception e) {
            throw new DataImportException("Error during the import of file: " + fileLocation, e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object> unmarshall(final ImportedEntityFeed importedEntityFeed) throws Exception {
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

    public void mergeImportedEntities(Class<ImportedEntity> entityClass, Institution institution, Iterable<ImportedEntity> entities) {
        EntityImportService thisBean = applicationContext.getBean(EntityImportService.class);
        thisBean.disableAllEntities(entityClass, institution);
        for (ImportedEntity entity : entities) {
            try {
                thisBean.attemptInsert(entity);
            } catch (ConstraintViolationException e) {
                try {
                    thisBean.attemptUpdateByCode(entityClass, institution, entity);
                } catch (Exception e1) {
                    try {
                        thisBean.attemptUpdateByName(entityClass, institution, entity);
                    } catch (Exception e2) {
                        logger.error("Couldn't insert entity", e);
                        logger.error("Couldn't update entity by code", e1);
                        logger.error("Couldn't update entity by name", e2);
                    }
                }
            }
        }
    }

    public void mergePrograms(List<ProgrammeOccurrence> programOccurrences, Institution institution) throws DataImportException {
        LocalDate currentDate = new LocalDate();

        EntityImportService thisBean = applicationContext.getBean(EntityImportService.class);
        thisBean.disableAllImportedPrograms(institution);

        for (ProgrammeOccurrence occurrence : programOccurrences) {
            Programme occurrenceProgram = occurrence.getProgramme();
            ModeOfAttendance modeOfAttendance = occurrence.getModeOfAttendance();

            Program program = programService.getOrImportProgram(occurrenceProgram, institution);
            StudyOption studyOption = thisBean.getOrCreateStudyOption(institution, modeOfAttendance);

            LocalDate applicationStartDate = dtFormatter.parseLocalDate(occurrence.getStartDate());
            LocalDate applicationDeadline = dtFormatter.parseLocalDate(occurrence.getEndDate());
            ProgramInstance transientProgramInstance = new ProgramInstance().withProgram(program).withIdentifier(occurrence.getIdentifier())
                    .withAcademicYear(Integer.toString(applicationStartDate.getYear())).withStudyOption(studyOption)
                    .withApplicationStartDate(applicationStartDate).withApplicationDeadline(applicationDeadline)
                    .withEnabled(currentDate.isBefore(applicationDeadline));

            programService.saveProgramInstance(transientProgramInstance);
        }
    }

    @Transactional
    public void setLastImportedDate(ImportedEntityFeed detachedImportedEntityFeed) {
        ImportedEntityFeed persistentImportedEntityFeed = entityService.getById(ImportedEntityFeed.class, detachedImportedEntityFeed.getId());
        persistentImportedEntityFeed.setLastUploadedDate(new LocalDate());
    }
    
    @Transactional
    public void disableAllEntities(Class<? extends ImportedEntity> entityClass, Institution institution) {
        importedEntityService.disableAllEntities(entityClass, institution);
    }

    @Transactional
    public void disableAllImportedPrograms(Institution institution) {
        importedEntityService.disableAllImportedPrograms(institution);
    }

    @Transactional
    public void attemptInsert(Object entity) {
        entityService.save(entity);
    }

    @Transactional
    public void attemptUpdateByCode(Class<ImportedEntity> entityClass, Institution institution, ImportedEntity entity) {
        ImportedEntity entityByCode = importedEntityService.getByCode(entityClass, institution, entity.getCode());
        entityByCode.setName(entity.getName());
        entityByCode.setEnabled(true);
    }

    @Transactional
    public void attemptUpdateByName(Class<ImportedEntity> entityClass, Institution institution, ImportedEntity entity) {
        ImportedEntity entityByName = importedEntityService.getByName(entityClass, institution, entity.getName());
        entityByName.setCode(entity.getCode());
        entityByName.setEnabled(true);
    }

    @Transactional
    public StudyOption getOrCreateStudyOption(Institution institution, ModeOfAttendance modeOfAttendance) {
        StudyOption studyOption = entityService.getByProperty(StudyOption.class, "code", modeOfAttendance.getCode());
        if (studyOption == null) {
            studyOption = new StudyOption().withInstitution(institution).withCode(modeOfAttendance.getCode()).withName(modeOfAttendance.getName())
                    .withEnabled(true);
            entityService.save(studyOption);
        }
        return studyOption;
    }

    @Transactional
    public List<ImportedEntityFeed> getImportedEntityFeedsToImport() {
        return importedEntityService.getImportedEntityFeedsToImport();
    }

}
