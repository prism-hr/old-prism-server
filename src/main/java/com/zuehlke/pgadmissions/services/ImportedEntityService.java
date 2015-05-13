package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.getSystemOpportunityType;
import static com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption.getSystemStudyOption;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_IMPORT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_RESTORE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_PENDING_REACTIVATION;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.imported.Domicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.imported.OpportunityType;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOption;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.DomicileUseDTO;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes.LanguageQualificationType;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.ModeOfAttendance;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;
import com.zuehlke.pgadmissions.rest.dto.application.ImportedInstitutionDTO;
import com.zuehlke.pgadmissions.utils.PrismConversionUtils;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

@Service
@Transactional
public class ImportedEntityService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("dd-MMM-yy");

    @Inject
    private ImportedEntityDAO importedEntityDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private CommentService commentService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private EntityService entityService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Inject
    private SystemService systemService;

    @SuppressWarnings("unchecked")
    public <T extends ImportedEntity> T getById(Institution institution, PrismImportedEntity entityId, Integer id) {
        return getById(institution, (Class<T>) entityId.getEntityClass(), id);
    }
    
    public <T extends ImportedEntity> T getById(Institution institution,  Class<T> clazz, Integer id) {
        return (T) entityService.getByProperties(clazz, ImmutableMap.of("institution", institution, "id", id));
    }

    public <T extends ImportedEntity> T getByCode(Class<T> entityClass, Institution institution, String code) {
        return importedEntityDAO.getImportedEntityByCode(entityClass, institution, code);
    }

    public <T extends ImportedEntity> List<T> getEnabledImportedEntities(Institution institution, Class<T> entityClass) {
        return importedEntityDAO.getEnabledImportedEntities(institution, entityClass);
    }

    public List<ImportedInstitution> getEnabledImportedInstitutions(Domicile domicile) {
        return importedEntityDAO.getEnabledImportedInstitutions(domicile);
    }

    public ImportedEntityFeed getOrCreateImportedEntityFeed(Institution institution, PrismImportedEntity importedEntityType, String location)
            throws DeduplicationException {
        return getOrCreateImportedEntityFeed(institution, importedEntityType, location, null, null);
    }

    public ImportedEntityFeed getOrCreateImportedEntityFeed(Institution institution, PrismImportedEntity importedEntityType, String location, String username,
                                                            String password) throws DeduplicationException {
        ImportedEntityFeed transientImportedEntityFeed = new ImportedEntityFeed().withImportedEntityType(importedEntityType).withLocation(location)
                .withUserName(username).withPassword(password).withInstitution(institution);
        return entityService.getOrCreate(transientImportedEntityFeed);
    }

    public List<ImportedEntityFeed> getImportedEntityFeeds() {
        return importedEntityDAO.getImportedEntityFeeds();
    }

    public List<Integer> getPendingImportedEntityFeeds(Integer institutionId) {
        return importedEntityDAO.getPendingImportedEntityFeeds(institutionId);
    }

    public ImportedInstitution getOrCreateImportedInstitution(Institution institution, ImportedInstitutionDTO importedInstitutionDTO) {
        Integer importedInstitutionId = importedInstitutionDTO.getId();

        if (importedInstitutionId == null) {
            ImportedInstitution importedInstitution = importedEntityDAO.getCustomImportedInstitutionByName(importedInstitutionDTO.getDomicile(),
                    importedInstitutionDTO.getName());
            if (importedInstitution == null) {
                return createCustomImportedInstitution(institution, importedInstitutionDTO);
            } else if (BooleanUtils.isTrue(importedInstitution.getEnabled())) {
                return importedInstitution;
            } else {
                return enableCustomImportedInstitution(importedInstitution);
            }
        } else {
            ImportedInstitution importedInstitution = getById(institution, ImportedInstitution.class, importedInstitutionDTO.getId());
            if (BooleanUtils.isTrue(importedInstitution.getEnabled())) {
                return importedInstitution;
            } else if (BooleanUtils.isTrue(importedInstitution.getCustom())) {
                return enableCustomImportedInstitution(importedInstitution);
            }
        }

        throw new Error();
    }

    public void disableEntities(Class<? extends ImportedEntity> entityClass, Institution institution, List<Integer> updates) {
        importedEntityDAO.disableEntities(entityClass, institution, updates);
    }

    public void disableAllInstitutions(Institution institution, List<Integer> updates) {
        importedEntityDAO.disableInstitutions(institution, updates);
    }

    public void disableImportedPrograms(Institution institution, List<Integer> updates, LocalDate baseline) {
        importedEntityDAO.disableImportedPrograms(institution, updates, baseline);
        importedEntityDAO.disableImportedProgramStudyOptions(institution, updates);
        importedEntityDAO.disableImportedProgramStudyOptionInstances(institution, updates);
    }

    public Integer mergeImportedProgram(Institution institution, Set<ProgrammeOccurrence> programInstanceDefinitions, LocalDate baseline, DateTime baselineTime)
            throws Exception {
        Programme programDefinition = programInstanceDefinitions.iterator().next().getProgramme();
        Program persistentProgram = mergeProgram(institution, programDefinition, baseline);

        LocalDate startDate = null;
        LocalDate closeDate = null;
        for (ProgrammeOccurrence occurrence : programInstanceDefinitions) {
            StudyOption studyOption = mergeStudyOption(institution, occurrence.getModeOfAttendance());

            LocalDate transientStartDate = DATE_FORMAT.parseLocalDate(occurrence.getStartDate());
            LocalDate transientCloseDate = DATE_FORMAT.parseLocalDate(occurrence.getEndDate());

            if (transientCloseDate.isAfter(baseline)) {
                ResourceStudyOption transientProgramStudyOption = new ResourceStudyOption().withResource(persistentProgram).withStudyOption(studyOption)
                        .withApplicationStartDate(transientStartDate).withApplicationCloseDate(transientCloseDate);

                ResourceStudyOption persistentProgramStudyOption = mergeProgramStudyOption(transientProgramStudyOption, baseline);
                persistentProgram.getStudyOptions().add(persistentProgramStudyOption);

                ResourceStudyOptionInstance transientProgramStudyOptionInstance = new ResourceStudyOptionInstance()
                        .withStudyOption(persistentProgramStudyOption).withApplicationStartDate(transientStartDate)
                        .withApplicationCloseDate(transientCloseDate).withAcademicYear(Integer.toString(transientStartDate.getYear()))
                        .withIdentifier(occurrence.getIdentifier());

                ResourceStudyOptionInstance persistentProgramStudyOptionInstance = entityService.createOrUpdate(transientProgramStudyOptionInstance);
                persistentProgramStudyOption.getStudyOptionInstances().add(persistentProgramStudyOptionInstance);

                startDate = startDate == null || startDate.isBefore(transientStartDate) ? transientStartDate : startDate;
                closeDate = closeDate == null || closeDate.isBefore(transientCloseDate) ? transientCloseDate : closeDate;
            }
        }

        persistentProgram.setEndDate(closeDate);
        executeProgramImportAction(persistentProgram, baselineTime);
        return persistentProgram.getId();
    }

    public Integer mergeImportedInstitution(Institution institution, com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution institutionDefinition)
            throws DataImportException, DeduplicationException {
        String domicileCode = institutionDefinition.getDomicile();

        Domicile domicile = entityService.getByProperties(Domicile.class, ImmutableMap.of("institution", institution, "code", domicileCode, "enabled", true));

        String institutionNameClean = institutionDefinition.getName().replace("\n", "").replace("\r", "").replace("\t", "");

        ImportedInstitution transientImportedInstitution = new ImportedInstitution().withInstitution(institution).withDomicile(domicile)
                .withCode(institutionDefinition.getCode()).withName(institutionNameClean).withEnabled(true).withCustom(false);

        if (domicile == null) {
            throw new DataImportException("No enabled domicile for Institution " + transientImportedInstitution.getResourceSignature().toString()
                    + ". Code specified was " + domicileCode);
        }

        return entityService.createOrUpdate(transientImportedInstitution).getId();
    }

    public Integer mergeImportedLanguageQualificationType(Institution institution, LanguageQualificationType languageQualificationTypeDefinition)
            throws DeduplicationException {
        int precision = 2;
        String languageQualificationTypeNameClean = languageQualificationTypeDefinition.getName().replace("\n", "").replace("\r", "").replace("\t", "");

        ImportedLanguageQualificationType transientImportedLanguageQualificationType = new ImportedLanguageQualificationType().withInstitution(institution)
                .withCode(languageQualificationTypeDefinition.getCode()).withName(languageQualificationTypeNameClean)
                .withMinimumOverallScore(PrismConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMinimumOverallScore(), precision))
                .withMaximumOverallScore(PrismConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMaximumOverallScore(), precision))
                .withMinimumReadingScore(PrismConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMinimumReadingScore(), precision))
                .withMaximumReadingScore(PrismConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMaximumReadingScore(), precision))
                .withMinimumWritingScore(PrismConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMinimumWritingScore(), precision))
                .withMaximumWritingScore(PrismConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMaximumWritingScore(), precision))
                .withMinimumSpeakingScore(PrismConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMinimumSpeakingScore(), precision))
                .withMaximumSpeakingScore(PrismConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMaximumSpeakingScore(), precision))
                .withMinimumListeningScore(PrismConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMinimumListeningScore(), precision))
                .withMaximumListeningScore(PrismConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMaximumListeningScore(), precision))
                .withEnabled(true);
        return entityService.createOrUpdate(transientImportedLanguageQualificationType).getId();
    }

    public <T extends ImportedEntity> Integer mergeImportedEntity(Class<T> entityClass, Institution institution, Object entityDefinition)
            throws Exception {
        ImportedEntitySimple transientEntity = (ImportedEntitySimple) entityClass.newInstance();
        transientEntity.setInstitution(institution);
        transientEntity.setType(PrismImportedEntity.getByEntityClass(entityClass));
        transientEntity.setCode((String) PrismReflectionUtils.getProperty(entityDefinition, "code"));

        String name = (String) PrismReflectionUtils.getProperty(entityDefinition, "name");
        String nameClean = name.replace("\n", " ").replace("\r", " ").replace("\t", " ").replaceAll(" +", " ");
        transientEntity.setName(nameClean);

        transientEntity.setEnabled(true);
        return entityService.createOrUpdate(transientEntity).getId();
    }

    public void setLastImportedTimestamp(Integer importedEntityFeedId) {
        ImportedEntityFeed persistentImportedEntityFeed = entityService.getById(ImportedEntityFeed.class, importedEntityFeedId);
        persistentImportedEntityFeed.setLastImportedTimestamp(new DateTime());
    }

    public DomicileUseDTO getMostUsedDomicile(Institution institution) {
        return importedEntityDAO.getMostUsedDomicile(institution);
    }

    private Program mergeProgram(Institution institution, Programme programDefinition, LocalDate baseline) throws DeduplicationException {
        User proxyCreator = institution.getUser();

        String transientTitle = programDefinition.getName();
        String transientTitleClean = transientTitle.replace("\n", "").replace("\r", "").replace("\t", "");

        PrismOpportunityType prismOpportunityType = PrismOpportunityType.findValueFromString(programDefinition.getName());
        prismOpportunityType = prismOpportunityType == null ? getSystemOpportunityType() : prismOpportunityType;

        boolean transientRequireProjectDefinition = programDefinition.isAtasRegistered();

        DateTime baselineDateTime = new DateTime();
        OpportunityType opportunityType = getByCode(OpportunityType.class, institution, prismOpportunityType.name());
        Department department = departmentService.getOrCreateDepartment(new Department().withInstitution(institution).withTitle(
                programDefinition.getDepartment()));

        Program transientProgram = new Program().withSystem(systemService.getSystem()).withInstitution(institution).withDepartment(department)
                .withImportedCode(programDefinition.getCode()).withTitle(transientTitleClean).withRequireProjectDefinition(transientRequireProjectDefinition)
                .withImported(true).withOpportunityType(opportunityType).withUser(proxyCreator).withCreatedTimestamp(baselineDateTime)
                .withUpdatedTimestamp(baselineDateTime).withUpdatedTimestampSitemap(baselineDateTime);

        Program persistentProgram = entityService.getDuplicateEntity(transientProgram);
        if (persistentProgram == null) {
            Advert transientAdvert = new Advert().withTitle(transientTitle);
            transientAdvert.setAddress(advertService.createAddressCopy(institution.getAdvert().getAddress()));
            transientProgram.setAdvert(transientAdvert);
            entityService.save(transientProgram);
            return transientProgram;
        } else {
            persistentProgram.setDepartment(department);
            persistentProgram.setTitle(transientTitle);
            persistentProgram.setRequireProjectDefinition(transientRequireProjectDefinition);
            return persistentProgram;
        }
    }

    private ResourceStudyOption mergeProgramStudyOption(ResourceStudyOption transientProgramStudyOption, LocalDate baseline) throws DeduplicationException {
        ResourceStudyOption persistentProgramStudyOption = entityService.getDuplicateEntity(transientProgramStudyOption);

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
            return persistentProgramStudyOption;
        }
    }

    private StudyOption mergeStudyOption(Institution institution, ModeOfAttendance modeOfAttendance) throws DeduplicationException {
        String externalCode = modeOfAttendance.getCode();
        PrismStudyOption prismStudyOption = PrismStudyOption.findValueFromString(externalCode);
        prismStudyOption = prismStudyOption == null ? getSystemStudyOption() : prismStudyOption;
        StudyOption studyOption = new StudyOption().withInstitution(institution).withCode(prismStudyOption.name()).withName(externalCode).withEnabled(true);
        studyOption.setType(PrismImportedEntity.STUDY_OPTION);
        return entityService.createOrUpdate(studyOption);
    }

    private ImportedInstitution createCustomImportedInstitution(Institution institution, ImportedInstitutionDTO importedInstitutionDTO) {
        Domicile domicile = getById(institution, Domicile.class, importedInstitutionDTO.getDomicile());
        ImportedInstitution importedInstitution = new ImportedInstitution().withInstitution(institution).withDomicile(domicile)
                .withName(importedInstitutionDTO.getName()).withEnabled(true).withCustom(true);
        entityService.save(importedInstitution);
        importedInstitution.setCode("CUSTOM_" + Strings.padStart(importedInstitution.getId().toString(), 10, '0'));
        return importedInstitution;
    }

    private ImportedInstitution enableCustomImportedInstitution(ImportedInstitution importedInstitution) {
        importedInstitution.setEnabled(true);
        return importedInstitution;
    }

    private void executeProgramImportAction(Program program, DateTime baselineTime) throws Exception {
        Comment lastImportComment = commentService.getLatestComment(program, INSTITUTION_IMPORT_PROGRAM);
        PrismAction actionId = lastImportComment == null ? INSTITUTION_CREATE_PROGRAM : INSTITUTION_IMPORT_PROGRAM;

        User invoker = program.getUser();
        Role invokerRole = roleService.getCreatorRole(program);

        State state = program.getState();
        State transitionState = null;
        if (state == null) {
            transitionState = stateService.getById(PROGRAM_APPROVED);
        } else {
            PrismState stateId = state.getId();
            if (stateId.equals(PROGRAM_APPROVED)) {
                transitionState = state;
            } else if (stateId.equals(PROGRAM_DISABLED_PENDING_REACTIVATION)) {
                actionId = PROGRAM_RESTORE;
            }
        }

        Action action = actionService.getById(actionId);
        Comment comment = new Comment().withUser(invoker).withCreatedTimestamp(baselineTime).withAction(action).withDeclinedResponse(false)
                .withTransitionState(transitionState).addAssignedUser(invoker, invokerRole, PrismRoleTransitionType.CREATE);
        actionService.executeAction(program, action, comment);
    }
}
