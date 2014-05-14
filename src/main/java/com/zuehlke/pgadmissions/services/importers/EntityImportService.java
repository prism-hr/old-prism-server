package com.zuehlke.pgadmissions.services.importers;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.enums.ProgramState;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.ModeOfAttendance;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramService;

@Service
public class EntityImportService {

    private static final Logger log = LoggerFactory.getLogger(EntityImportService.class);

    private static DateTimeFormatter dtFormatter = DateTimeFormat.forPattern("dd-MMM-yy");

    @Autowired
    private ImportedEntityDAO importedEntityDAO;

    @Autowired
    private EntityDAO entityDAO;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MailSendingService mailSendingService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void importEntities(ImportedEntityFeed importedEntityFeed) throws XMLDataImportException {
        EntityImportService thisBean = applicationContext.getBean(EntityImportService.class);
        String fileLocation = importedEntityFeed.getLocation();
        log.info("Starting the import from file: " + fileLocation);

        try {
            List unmarshalled = thisBean.unmarshall(importedEntityFeed);

            Class<ImportedEntity> entityClass = (Class<ImportedEntity>) importedEntityFeed.getImportedEntityType().getEntityClass();

            if (entityClass.equals(Program.class)) {
                thisBean.mergePrograms((List<ProgrammeOccurrence>) unmarshalled, importedEntityFeed.getInstitution());
            } else {
                ImportEntityConverter<ImportedEntity> entityConverter = ImportEntityConverter.create(entityClass);

                Iterable<ImportedEntity> newEntities = Iterables.transform(unmarshalled, entityConverter);

                thisBean.mergeImportedEntities(entityClass, newEntities);
            }
        } catch (Exception e) {
            throw new XMLDataImportException("Error during the import of file: " + fileLocation, e);
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

    public void mergeImportedEntities(Class<ImportedEntity> entityClass, Iterable<ImportedEntity> entities) {
        EntityImportService thisBean = applicationContext.getBean(EntityImportService.class);
        thisBean.disableAllEntities(entityClass);
        for (ImportedEntity entity : entities) {
            try {
                thisBean.attemptInsert(entity);
            } catch (ConstraintViolationException e) {
                try {
                    thisBean.attemptUpdateByCode(entityClass, entity);
                } catch (Exception e1) {
                    try {
                        thisBean.attemptUpdateByName(entityClass, entity);
                    } catch (Exception e2) {
                        log.error("Couldn't insert entity", e);
                        log.error("Couldn't update entity by code", e1);
                        log.error("Couldn't update entity by name", e2);
                        mailSendingService.sendImportErrorMessage("Could not merge: " + entity + " due to a data integrity problem in the import feed.");
                    }
                }
            }
        }
    }

    public void mergePrograms(List<ProgrammeOccurrence> programOccurrences, Institution institution) {
        EntityImportService thisBean = applicationContext.getBean(EntityImportService.class);

        thisBean.disableAllProgramInstances(institution);

        for (ProgrammeOccurrence occurrence : programOccurrences) {
            Programme occurrenceProgram = occurrence.getProgramme();
            ModeOfAttendance modeOfAttendance = occurrence.getModeOfAttendance();

            // create new program if does not exist
            Program program = thisBean.getOrCreateProgram(occurrenceProgram, institution);

            // create new study option if does not exist yet
            StudyOption studyOption = thisBean.getOrCreateStudyOption(modeOfAttendance);

            Date startDate = dtFormatter.parseDateTime(occurrence.getStartDate()).toDate();
            Date endDate = dtFormatter.parseDateTime(occurrence.getEndDate()).toDate();
            ProgramInstance programInstance = new ProgramInstance().withProgram(program).withIdentifier(occurrence.getIdentifier())
                    .withAcademicYear(occurrence.getAcademicYear()).withStudyOption(studyOption).withApplicationStartDate(startDate)
                    .withApplicationDeadline(endDate).withEnabled(true);

            try {
                thisBean.attemptInsert(programInstance);
            } catch (ConstraintViolationException e) {
                try {
                    thisBean.attemptUpdate(programInstance);
                } catch (Exception e1) {
                    log.error("Couldn't insert program instance", e);
                    log.error("Couldn't update program instance", e1);
                    mailSendingService.sendImportErrorMessage("Could not merge: " + programInstance + " due to a data integrity problem in the import feed.");
                }
            }
        }
    }

    @Transactional
    public void disableAllEntities(Class<? extends ImportedEntity> entityClass) {
        importedEntityDAO.disableAllEntities(entityClass);
    }

    @Transactional
    public void disableAllProgramInstances(Institution institution) {
        importedEntityDAO.disableAllProgramInstances(institution);
    }

    @Transactional
    public void attemptInsert(Object entity) {
        entityDAO.save(entity);
    }

    @Transactional
    public void attemptUpdateByCode(Class<ImportedEntity> entityClass, ImportedEntity entity) {
        ImportedEntity entityByCode = importedEntityDAO.getByCode(entityClass, entity.getCode());
        entityByCode.setName(entity.getName());
        entityByCode.setEnabled(true);
    }

    @Transactional
    public void attemptUpdateByName(Class<ImportedEntity> entityClass, ImportedEntity entity) {
        ImportedEntity entityByName = importedEntityDAO.getByName(entityClass, entity.getName());
        entityByName.setCode(entity.getCode());
        entityByName.setEnabled(true);
    }

    @Transactional
    public void attemptUpdate(ProgramInstance programInstance) {
        ProgramInstance persistentProgramInstance = programInstanceService.getByProgramAndAcademicYearAndStudyOption(programInstance.getProgram(),
                programInstance.getAcademicYear(), programInstance.getStudyOption());
        persistentProgramInstance.setIdentifier(programInstance.getIdentifier());
        persistentProgramInstance.setApplicationStartDate(programInstance.getApplicationStartDate());
        persistentProgramInstance.setApplicationDeadline(programInstance.getApplicationDeadline());
        persistentProgramInstance.setEnabled(true);
    }

    @Transactional
    public Program getOrCreateProgram(Programme programme, Institution institution) {
        Program program = programService.getProgramByCode(programme.getCode());
        if (program == null) {
            program = new Program().withInstitution(institution).withCode(programme.getCode()).withState(ProgramState.PROGRAM_APPROVED);
            entityDAO.save(program);
        }

        program.setTitle(programme.getName());
        program.setRequireProjectDefinition(programme.isAtasRegistered());
        return program;
    }

    @Transactional
    public StudyOption getOrCreateStudyOption(ModeOfAttendance modeOfAttendance) {
        StudyOption studyOption = entityDAO.getBy(StudyOption.class, "id", modeOfAttendance.getCode());
        if (studyOption == null) {
            studyOption = new StudyOption().withId(modeOfAttendance.getCode()).withDisplayName(modeOfAttendance.getName());
            entityDAO.save(studyOption);
        }
        return studyOption;
    }

    @Transactional
    public List<ImportedEntityFeed> getImportedEntityFeeds() {
        return importedEntityDAO.getImportedEntityFeeds();
    }

}
