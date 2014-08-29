package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.ModeOfAttendance;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;

@Service
@Transactional
public class ImportedEntityService {

    private static DateTimeFormatter datetFormatter = DateTimeFormat.forPattern("dd-MMM-yy");

    @Autowired
    private ImportedEntityDAO importedEntityDAO;
    
    @Autowired
    private ActionService actionService;

    @Autowired
    private EntityService entityService;
    
    @Autowired
    private RoleService roleService;

    @Autowired
    private SystemService systemService;

    @SuppressWarnings("unchecked")
    public <T extends ImportedEntity> T getById(Class<? extends ImportedEntity> clazz, Institution institution, Integer id) {
        T entity = (T) entityService.getByProperties(clazz, ImmutableMap.of("institution", institution, "id", id));
        return entity;
    }
    
    public <T extends ImportedEntity> T getByCode(Class<? extends ImportedEntity> clazz, Institution institution, String code) {
        return importedEntityDAO.getByCode(clazz, institution, code);
    }
    
    public ImportedEntity getByName(Class<ImportedEntity> entityClass, Institution institution, String name) {
        return importedEntityDAO.getByName(entityClass, institution, name);
    }

    public ImportedEntityFeed getOrCreateImportedEntityFeed(Institution institution, PrismImportedEntity importedEntityType, String location) {
        return getOrCreateImportedEntityFeed(institution, importedEntityType, location, null, null);
    }
    
    public ImportedEntityFeed getOrCreateImportedEntityFeed(Institution institution, PrismImportedEntity importedEntityType, String location, String username,
            String password) {
        ImportedEntityFeed transientImportedEntityFeed = new ImportedEntityFeed().withImportedEntityType(importedEntityType).withLocation(location)
                .withUserName(username).withPassword(password).withInstitution(institution);
        return entityService.getOrCreate(transientImportedEntityFeed);
    }

    public List<ImportedEntityFeed> getImportedEntityFeedsToImport() {
        return importedEntityDAO.getImportedEntityFeedsToImport();
    }

    public void disableAllEntities(Class<? extends ImportedEntity> entityClass, Institution institution) {
        importedEntityDAO.disableAllEntities(entityClass, institution);
    }

    public void disableAllImportedPrograms(Institution institution, LocalDate baseline) {
        importedEntityDAO.disableAllImportedPrograms(institution, baseline);
        importedEntityDAO.disableAllImportedProgramStudyOptions(institution);
        importedEntityDAO.disableAllImportedProgramStudyOptionInstances(institution);
    }
    
    public void mergeEntity(Class<ImportedEntity> entityClass, Institution institution, ImportedEntity transientEntity) {
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
                    ImportedEntity otherPersistentEntity = getByName(entityClass, institution, transientName);
                    if (otherPersistentEntity == null) {
                        persistentEntity.setName(transientName);
                    }
                } else {
                    ImportedEntity otherPersistentEntity = getByCode(entityClass, institution, transientCode);
                    if (otherPersistentEntity == null) {
                        persistentEntity.setCode(transientCode);
                    }
                }
                persistentEntity.setEnabled(true);
            }
        }
    }

    public void mergeBatchedProgrammeOccurrences(Institution institution, Set<ProgrammeOccurrence> occurrencesInBatch, LocalDate baseline) {
        Programme programme = occurrencesInBatch.iterator().next().getProgramme();
        Program persistentProgram = mergeProgram(institution, programme);

        for (ProgrammeOccurrence occurrence : occurrencesInBatch) {
            StudyOption studyOption = mergeStudyOption(institution, occurrence.getModeOfAttendance());

            LocalDate transientStartDate = datetFormatter.parseLocalDate(occurrence.getStartDate());
            LocalDate transientCloseDate = datetFormatter.parseLocalDate(occurrence.getEndDate());

            ProgramStudyOption transientProgramStudyOption = new ProgramStudyOption().withProgram(persistentProgram).withStudyOption(studyOption)
                    .withApplicationStartDate(transientStartDate).withApplicationCloseDate(transientCloseDate)
                    .withEnabled(transientCloseDate.isAfter(baseline));

            ProgramStudyOption persistentProgramStudyOption = mergeProgramStudyOption(transientProgramStudyOption, baseline);
            persistentProgram.getStudyOptions().add(persistentProgramStudyOption);

            ProgramStudyOptionInstance transientProgramStudyOptionInstance = new ProgramStudyOptionInstance()
                    .withStudyOption(persistentProgramStudyOption).withApplicationStartDate(transientStartDate)
                    .withApplicationCloseDate(transientCloseDate).withAcademicYear(Integer.toString(transientStartDate.getYear()))
                    .withIdentifier(occurrence.getIdentifier()).withEnabled(transientCloseDate.isAfter(baseline));

            ProgramStudyOptionInstance persistentProgramStudyOptionInstance = entityService.createOrUpdate(transientProgramStudyOptionInstance);
            persistentProgramStudyOption.getStudyOptionInstances().add(persistentProgramStudyOptionInstance);
            
            executeProgramImportAction(persistentProgram);
        }
    }

    private Program mergeProgram(Institution institution, Programme importProgram) {
        User proxyCreator = institution.getUser();

        String transientTitle = importProgram.getName();
        Advert transientAdvert = new Advert().withTitle(transientTitle);

        DateTime baseline = new DateTime();
        String programTypeCode = PrismProgramType.findValueFromString(importProgram.getName()).name();

        boolean transientRequireProjectDefinition = importProgram.isAtasRegistered();

        ProgramType programType = getByCode(ProgramType.class, institution, programTypeCode);
        Program transientProgram = new Program().withSystem(systemService.getSystem()).withInstitution(institution).withImportedCode(importProgram.getCode())
                .withTitle(transientTitle).withRequireProjectDefinition(transientRequireProjectDefinition).withImported(true).withAdvert(transientAdvert)
                .withProgramType(programType).withUser(proxyCreator).withCreatedTimestamp(baseline).withUpdatedTimestamp(baseline);

        Program persistentProgram = entityService.getDuplicateEntity(transientProgram);

        if (persistentProgram == null) {
            entityService.save(transientProgram);
            return transientProgram;
        } else {
            persistentProgram.setTitle(transientTitle);
            persistentProgram.setRequireProjectDefinition(transientRequireProjectDefinition);
            persistentProgram.setUpdatedTimestamp(baseline);
            return persistentProgram;
        }
    }

    private ProgramStudyOption mergeProgramStudyOption(ProgramStudyOption transientProgramStudyOption, LocalDate baseline) {
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

            persistentProgramStudyOption.setEnabled(persistentCloseDate.isAfter(baseline));
            return persistentProgramStudyOption;
        }
    }
    
    private StudyOption mergeStudyOption(Institution institution, ModeOfAttendance modeOfAttendance) {
        String externalcode = modeOfAttendance.getCode();
        PrismStudyOption internalCode = PrismStudyOption.findValueFromString(externalcode);
        StudyOption studyOption = new StudyOption().withInstitution(institution).withCode(internalCode.name()).withName(externalcode).withEnabled(true);
        return entityService.createOrUpdate(studyOption);
    }

    public void setLastImportedDate(ImportedEntityFeed detachedImportedEntityFeed) {
        ImportedEntityFeed persistentImportedEntityFeed = entityService.getById(ImportedEntityFeed.class, detachedImportedEntityFeed.getId());
        persistentImportedEntityFeed.setLastImportedDate(new LocalDate());
    }

    private void executeProgramImportAction(Program program) {
        Action action = actionService.getById(PrismAction.INSTITUTION_IMPORT_PROGRAM);

        User invoker = program.getUser();
        Role invokerRole = roleService.getCreatorRole(program);

        Comment comment = new Comment().withUser(invoker).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false)
                .withAssignedUser(invoker, invokerRole);
        actionService.executeSystemAction(program, action, comment);
    }

}
