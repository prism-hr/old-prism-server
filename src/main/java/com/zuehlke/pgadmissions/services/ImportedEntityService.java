package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.getSystemOpportunityType;
import static com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption.getSystemStudyOption;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_IMPORT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_RESTORE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_PENDING_REACTIVATION;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.floatToBigDecimal;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static org.apache.commons.lang.StringEscapeUtils.escapeSql;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import com.zuehlke.pgadmissions.domain.imported.AgeRange;
import com.zuehlke.pgadmissions.domain.imported.Domicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
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
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes.LanguageQualificationType;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.ModeOfAttendance;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;
import com.zuehlke.pgadmissions.rest.dto.application.ImportedInstitutionDTO;

@Service
@Transactional
public class ImportedEntityService {

    private static final Logger logger = LoggerFactory.getLogger(ImportedEntityService.class);

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
    private InstitutionService institutionService;

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

    public <T extends ImportedEntity> T getById(Institution institution, Class<T> clazz, Integer id) {
        return (T) entityService.getByProperties(clazz, ImmutableMap.of("institution", institution, "id", id));
    }

    public ImportedEntityFeed getImportedEntityFeedById(Integer id) {
        return entityService.getById(ImportedEntityFeed.class, id);
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

    public List<ImportedEntityFeed> getInstitutionImportedEntityFeeds(Integer institution, PrismImportedEntity... exclusions) {
        return importedEntityDAO.getImportedEntityFeeds(institution, exclusions);
    }

    public void setInstitutionImportedEntityFeeds(Institution institution) {
        for (PrismImportedEntity prismImportedEntity : PrismImportedEntity.values()) {
            String defaultLocation = prismImportedEntity.getDefaultLocation();
            if (defaultLocation != null) {
                entityService.getOrCreate(new ImportedEntityFeed().withInstitution(institution).withImportedEntityType(prismImportedEntity)
                        .withLocation(prismImportedEntity.getDefaultLocation()));
            }
        }
    }

    public void disableImportedPrograms(Integer institutionId, List<Integer> updates, LocalDate baseline) {
        Institution institution = institutionService.getById(institutionId);
        importedEntityDAO.disableImportedPrograms(institution, updates, baseline);
        importedEntityDAO.disableImportedProgramStudyOptions(institution, updates);
        importedEntityDAO.disableImportedProgramStudyOptionInstances(institution, updates);
    }

    public void mergeImportedAgeRanges(Integer importedEntityFeedId, List<com.zuehlke.pgadmissions.referencedata.jaxb.AgeRanges.AgeRange> definitions)
            throws Exception {
        ImportedEntityFeed importedEntityFeed = getImportedEntityFeedById(importedEntityFeedId);
        Institution institution = importedEntityFeed.getInstitution();
        PrismImportedEntity importedEntityType = importedEntityFeed.getImportedEntityType();

        try {
            importedEntityDAO.disableImportedEntities(importedEntityType.getEntityClass(), institution);
            entityService.flush();

            List<String> rows = Lists.newLinkedList();
            for (com.zuehlke.pgadmissions.referencedata.jaxb.AgeRanges.AgeRange definition : definitions) {
                List<String> cells = Lists.newLinkedList();
                cells.add(prepareCellForInsert(institution.getId().toString()));
                cells.add(prepareCellForInsert(definition.getCode()));
                cells.add(prepareCellForInsert(definition.getName()));
                cells.add(prepareCellForInsert(definition.getLowerBound().toString()));

                BigInteger upperBound = definition.getUpperBound();
                cells.add(upperBound == null ? "null" : prepareCellForInsert(upperBound.toString()));

                cells.add(prepareCellForInsert(new Integer(1).toString()));

                String row = prepareCellsForInsert(cells);
                rows.add(row);
            }

            importedEntityDAO.mergeImportedEntities(importedEntityType.getDatabaseTable(), importedEntityType.getDatabaseColumns(), prepareRowsForInsert(rows));
            entityService.flush();

            importedEntityFeed.setLastImportedTimestamp(new DateTime());
        } catch (Exception e) {
            logger.error("Error importing " + importedEntityType.name() + " for institution " + institution.getCode(), e);
        }
    }

    public void mergeImportedEntities(Integer importedEntityFeedId, List<Object> definitions) throws Exception {
        ImportedEntityFeed importedEntityFeed = getImportedEntityFeedById(importedEntityFeedId);
        Institution institution = importedEntityFeed.getInstitution();
        PrismImportedEntity importedEntityType = importedEntityFeed.getImportedEntityType();

        try {
            importedEntityDAO.disableImportedEntities(importedEntityType.getEntityClass(), institution);
            entityService.flush();

            List<String> rows = Lists.newLinkedList();
            for (Object definition : definitions) {
                List<String> cells = Lists.newLinkedList();
                cells.add(prepareCellForInsert(institution.getId().toString()));
                cells.add(prepareCellForInsert(importedEntityType.toString()));
                cells.add(prepareCellForInsert((String) getProperty(definition, "code")));
                cells.add(prepareCellForInsert((String) getProperty(definition, "name")));
                cells.add(prepareCellForInsert(new Integer(1).toString()));

                String row = prepareCellsForInsert(cells);
                rows.add(row);
            }

            importedEntityDAO.mergeImportedEntities(importedEntityType.getDatabaseTable(), importedEntityType.getDatabaseColumns(), prepareRowsForInsert(rows));
            entityService.flush();

            importedEntityFeed.setLastImportedTimestamp(new DateTime());
        } catch (Exception e) {
            logger.error("Error importing " + importedEntityType.name() + " for institution " + institution.getCode(), e);
        }
    }

    public void mergeImportedInstitutions(Integer importedEntityFeedId, List<com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution> definitions)
            throws Exception {
        ImportedEntityFeed importedEntityFeed = getImportedEntityFeedById(importedEntityFeedId);
        Institution institution = importedEntityFeed.getInstitution();
        PrismImportedEntity importedEntityType = importedEntityFeed.getImportedEntityType();

        Map<String, String> domicilesByCode = Maps.newHashMap();
        List<Domicile> domiciles = getEnabledImportedEntities(institution, Domicile.class);
        for (Domicile domicile : domiciles) {
            domicilesByCode.put(domicile.getCode(), domicile.getId().toString());
        }

        try {
            importedEntityDAO.disableImportedInstitutions(institution);
            entityService.flush();

            List<String> rows = Lists.newLinkedList();
            for (com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution definition : definitions) {
                List<String> cells = Lists.newLinkedList();
                cells.add(prepareCellForInsert(institution.getId().toString()));
                cells.add(prepareCellForInsert(domicilesByCode.get(definition.getDomicile())));
                cells.add(prepareCellForInsert(definition.getCode()));
                cells.add(prepareCellForInsert(definition.getName()));
                cells.add(prepareCellForInsert(new Integer(1).toString()));
                cells.add(prepareCellForInsert(new Integer(0).toString()));

                String row = prepareCellsForInsert(cells);
                rows.add(row);
            }

            importedEntityDAO.mergeImportedEntities(importedEntityType.getDatabaseTable(), importedEntityType.getDatabaseColumns(), prepareRowsForInsert(rows));
            entityService.flush();

            importedEntityFeed.setLastImportedTimestamp(new DateTime());
        } catch (Exception e) {
            logger.error("Error importing " + importedEntityType.name() + " for institution " + institution.getCode(), e);
        }
    }

    public void mergeImportedLanguageQualifications(Integer importedEntityFeedId, List<LanguageQualificationType> definitions) throws Exception {
        ImportedEntityFeed importedEntityFeed = getImportedEntityFeedById(importedEntityFeedId);
        Institution institution = importedEntityFeed.getInstitution();
        PrismImportedEntity importedEntityType = importedEntityFeed.getImportedEntityType();

        try {
            importedEntityDAO.disableImportedEntities(importedEntityType.getEntityClass(), institution);
            entityService.flush();

            List<String> rows = Lists.newLinkedList();
            for (LanguageQualificationType definition : definitions) {
                List<String> cells = Lists.newLinkedList();
                cells.add(prepareCellForInsert(institution.getId().toString()));
                cells.add(prepareCellForInsert(definition.getCode()));
                cells.add(prepareCellForInsert(definition.getName()));
                cells.add(prepareDecimalForInsert(definition.getMinimumOverallScore()));
                cells.add(prepareDecimalForInsert(definition.getMaximumOverallScore()));
                cells.add(prepareDecimalForInsert(definition.getMinimumReadingScore()));
                cells.add(prepareDecimalForInsert(definition.getMaximumReadingScore()));
                cells.add(prepareDecimalForInsert(definition.getMinimumWritingScore()));
                cells.add(prepareDecimalForInsert(definition.getMaximumWritingScore()));
                cells.add(prepareDecimalForInsert(definition.getMinimumSpeakingScore()));
                cells.add(prepareDecimalForInsert(definition.getMaximumSpeakingScore()));
                cells.add(prepareDecimalForInsert(definition.getMinimumListeningScore()));
                cells.add(prepareDecimalForInsert(definition.getMaximumListeningScore()));
                cells.add(prepareCellForInsert(new Integer(1).toString()));

                String row = prepareCellsForInsert(cells);
                rows.add(row);
            }

            importedEntityDAO.mergeImportedEntities(importedEntityType.getDatabaseTable(), importedEntityType.getDatabaseColumns(), prepareRowsForInsert(rows));
            entityService.flush();

            importedEntityFeed.setLastImportedTimestamp(new DateTime());
        } catch (Exception e) {
            logger.error("Error importing " + importedEntityType.name() + " for institution " + institution.getCode(), e);
        }
    }

    public Integer mergeImportedProgram(Integer institutionId, Set<ProgrammeOccurrence> programInstanceDefinitions, LocalDate baseline, DateTime baselineTime)
            throws Exception {
        Institution institution = institutionService.getById(institutionId);
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

    public void setLastImportedTimestamp(Integer importedEntityFeedId) {
        ImportedEntityFeed persistentImportedEntityFeed = entityService.getById(ImportedEntityFeed.class, importedEntityFeedId);
        persistentImportedEntityFeed.setLastImportedTimestamp(new DateTime());
    }

    public DomicileUseDTO getMostUsedDomicile(Institution institution) {
        return importedEntityDAO.getMostUsedDomicile(institution);
    }

    public AgeRange getAgeRange(Institution institution, Integer age) {
        return importedEntityDAO.getAgeRange(institution, age);
    }

    private Program mergeProgram(Institution institution, Programme programDefinition, LocalDate baseline) throws DeduplicationException {
        User proxyCreator = institution.getUser();

        String transientTitle = programDefinition.getName();
        String transientTitleClean = transientTitle.replace("\n", "").replace("\r", "").replace("\t", "").replaceAll(" +", " ");

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
        Comment lastImportComment = commentService.getLatestComment(program, INSTITUTION_CREATE_PROGRAM, INSTITUTION_IMPORT_PROGRAM);
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

    private static String prepareRowsForInsert(List<String> sequence) {
        return Joiner.on(", ").join(sequence);
    }

    private static String prepareCellsForInsert(List<String> cells) {
        return "(" + prepareRowsForInsert(cells) + ")";
    }

    private static String prepareCellForInsert(String string) {
        return string == null ? "null" : "'" + escapeSql(string.replace("\n", "").replace("\r", "").replace("\t", "").replaceAll(" +", " ")) + "'";
    }

    private static String prepareDecimalForInsert(Float decimal) {
        return decimal == null ? "null" : "'" + escapeSql(floatToBigDecimal(decimal, 2).toPlainString()) + "'";
    }

}
