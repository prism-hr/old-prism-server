package com.zuehlke.pgadmissions.services.importers;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.DateTime;
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
import com.google.common.collect.Iterables;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.LanguageQualificationType;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.mail.MailService;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.ModeOfAttendance;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.SystemService;

@Service
public class EntityImportService {

    private static final Logger log = LoggerFactory.getLogger(EntityImportService.class);

    private static DateTimeFormatter dtFormatter = DateTimeFormat.forPattern("dd-MMM-yy");

    @Autowired
    private ImportedEntityDAO importedEntityDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MailService mailSendingService;

    @Autowired
    private RoleService roleService;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void importEntities(ImportedEntityFeed importedEntityFeed) throws XMLDataImportException {
        EntityImportService thisBean = applicationContext.getBean(EntityImportService.class);
        String fileLocation = importedEntityFeed.getLocation();
        log.info("Starting the import from file: " + fileLocation);

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
                    }
                }
            }
        }
    }

    public void mergePrograms(List<ProgrammeOccurrence> programOccurrences, Institution institution) throws XMLDataImportException {
        EntityImportService thisBean = applicationContext.getBean(EntityImportService.class);

        thisBean.disableAllProgramInstances(institution);

        for (ProgrammeOccurrence occurrence : programOccurrences) {
            Programme occurrenceProgram = occurrence.getProgramme();
            ModeOfAttendance modeOfAttendance = occurrence.getModeOfAttendance();

            Program program = thisBean.getOrCreateProgram(occurrenceProgram, institution);
            StudyOption studyOption = thisBean.getOrCreateStudyOption(institution, modeOfAttendance);

            LocalDate startDate = dtFormatter.parseLocalDate(occurrence.getStartDate());
            LocalDate endDate = dtFormatter.parseLocalDate(occurrence.getEndDate());
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
                    throw new XMLDataImportException("Could not merge: " + programInstance + " due to a data integrity problem in the import feed.");
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
        entityService.save(entity);
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

    // TODO: integrate with workflow engine when finished
    @Transactional
    public Program getOrCreateProgram(Programme programme, Institution institution) {
        String prefixedProgramCode = String.format("%010d", institution.getId().toString()) + "-" + programme.getCode();
        Program program = programService.getProgramByCode(prefixedProgramCode);
        if (program == null) {
            PrismProgramType programType = PrismProgramType.findValueFromString(programme.getName());
            program = new Program().withSystem(systemService.getSystem()).withInstitution(institution).withCode(prefixedProgramCode)
                    .withTitle(programme.getName()).withState(new State().withId(PrismState.PROGRAM_APPROVED)).withProgramType(programType).withCreatedTimestamp(new DateTime()).withUpdatedTimestamp(new DateTime());
            entityService.save(program);
        }

        program.setTitle(programme.getName());
        program.setRequireProjectDefinition(programme.isAtasRegistered());
        return program;
    }

    @Transactional
    public StudyOption getOrCreateStudyOption(Institution institution, ModeOfAttendance modeOfAttendance) {
        StudyOption studyOption = entityService.getByProperty(StudyOption.class, "code", modeOfAttendance.getCode());
        if (studyOption == null) {
            studyOption = new StudyOption().withInstitution(institution).withCode(modeOfAttendance.getCode()).withName(modeOfAttendance.getName()).withEnabled(true);
            entityService.save(studyOption);
        }
        return studyOption;
    }

    @Transactional
    public List<ImportedEntityFeed> getImportedEntityFeeds() {
        return importedEntityDAO.getImportedEntityFeeds();
    }

}
