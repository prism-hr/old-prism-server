package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.utils.PrismConstants.MAX_BATCH_INSERT_SIZE;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareIntegerForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareRowsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitutionSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitutionSubjectAreaDTO;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgramSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedInstitutionMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedProgramMapping;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOption;
import com.zuehlke.pgadmissions.dto.DomicileUseDTO;
import com.zuehlke.pgadmissions.dto.ImportedSubjectAreaDTO;
import com.zuehlke.pgadmissions.dto.ImportedSubjectAreaIndexDTO;
import com.zuehlke.pgadmissions.dto.ImportedSubjectAreaWithWeightingDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedInstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramImportDTO;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEntityExtractor;
import com.zuehlke.pgadmissions.utils.PrismConstants;

@Service
@Transactional
public class ImportedEntityService {

    @Inject
    private ImportedEntityDAO importedEntityDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private EntityService entityService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Inject
    private ApplicationContext applicationContext;

    public <T extends ImportedEntity<?, ?>> T getById(Class<T> clazz, Integer id) {
        return entityService.getById(clazz, id);
    }

    public <T extends ImportedEntity<?, ?>> T getByName(Class<T> entityClass, String name) {
        return importedEntityDAO.getByName(entityClass, name);
    }

    public <T extends ImportedEntity<?, ?>> List<T> getEnabledImportedEntities(Institution institution,
            PrismImportedEntity prismImportedEntity) {
        List<T> entities = importedEntityDAO.getEnabledImportedEntitiesWithMappings(institution, prismImportedEntity);
        if (entities.isEmpty()) {
            entities = importedEntityDAO.getEnabledImportedEntities(prismImportedEntity);
        }
        return entities;
    }

    public List<ImportedInstitution> getEnabledImportedInstitutions(Institution institution, ImportedEntitySimple domicile) {
        List<ImportedInstitution> institutions = importedEntityDAO.getEnabledImportedInstitutionsWithMappings(institution, domicile);
        if (institutions.isEmpty()) {
            institutions = importedEntityDAO.getEnabledImportedInstitutions(domicile);
        }
        return institutions;
    }

    public List<ImportedProgram> getEnabledImportedPrograms(Institution institution, ImportedInstitution importedInstitution) {
        List<ImportedProgram> programs = importedEntityDAO.getEnabledImportedProgramsWithMappings(institution, importedInstitution);
        if (programs.isEmpty()) {
            programs = importedEntityDAO.getEnabledImportedPrograms(importedInstitution);
        }
        return programs;
    }

    public Map<Integer, Integer> getImportedInstitutionsByUcasId() {
        Map<Integer, Integer> references = Maps.newHashMap();
        List<ImportedInstitution> institutions = importedEntityDAO.getInstitutionsWithUcasId();
        for (ImportedInstitution institution : institutions) {
            references.put(institution.getUcasId(), institution.getId());
        }
        return references;
    }

    public <T extends ImportedEntity<?, U>, U extends ImportedEntityMapping<T>> U getEnabledImportedEntityMapping(Institution institution, T importedEntity) {
        List<U> mappings = importedEntityDAO.getEnabledImportedEntityMapping(institution, importedEntity);
        List<U> filteredMappings = getFilteredImportedEntityMappings(mappings);
        return filteredMappings.isEmpty() ? null : filteredMappings.get(0);
    }

    public <T extends ImportedEntityMapping<?>> List<T> getEnabledImportedEntityMappings(Institution institution,
            PrismImportedEntity prismImportedEntity) {
        List<T> mappings = importedEntityDAO.getImportedEntityMappings(institution, prismImportedEntity);
        return getFilteredImportedEntityMappings(mappings);
    }

    public ImportedProgram getOrCreateImportedProgram(Institution institution, ImportedProgramDTO importedProgramDTO) {
        Integer importedProgramId = importedProgramDTO.getId();
        if (importedProgramId == null) {
            ImportedInstitution importedInstitution = getOrCreateImportedInstitution(institution, importedProgramDTO.getInstitution());
            ImportedProgram importedProgram = importedEntityDAO.getImportedProgramByName(importedInstitution, importedProgramDTO.getName());
            if (importedProgram == null) {
                return createImportedProgram(institution, importedInstitution, importedProgramDTO);
            } else {
                createImportedProgramMapping(institution, importedProgram);
                return importedProgram;
            }
        } else {
            ImportedProgram importedProgram = getById(ImportedProgram.class, importedProgramDTO.getId());
            createImportedProgramMapping(institution, importedProgram);
            return importedProgram;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ImportedEntity<?, U>, U extends ImportedEntityMapping<T>, V extends ImportedEntityRequest> void mergeImportedEntities(
            Institution institution, PrismImportedEntity prismImportedEntity, List<V> definitions) {
        insertImportedEntities(prismImportedEntity, definitions, false);

        importedEntityDAO.disableImportedEntityMappings(institution, prismImportedEntity);
        entityService.flush();

        Map<Integer, T> currentMappingsLookup = Maps.newHashMap();
        List<U> currentMappings = importedEntityDAO.getImportedEntityMappings(institution, prismImportedEntity);
        if (currentMappings.isEmpty()) {
            for (ImportedEntity<?, ?> entity : importedEntityDAO.getImportedEntities(prismImportedEntity)) {
                currentMappingsLookup.put(entity.hashCode(), (T) entity);
            }
        } else {
            for (U currentMapping : currentMappings) {
                T entity = currentMapping.getImportedEntity();
                currentMappingsLookup.put(entity.hashCode(), entity);
            }
        }

        List<List<V>> definitionBatches = Lists.partition(definitions, MAX_BATCH_INSERT_SIZE);
        for (List<V> definitionBatch : definitionBatches) {
            List<String> rows = Lists.newLinkedList();
            for (V mappingDefinition : definitionBatch) {
                List<String> cells = Lists.newLinkedList();
                T entity = currentMappingsLookup.get(mappingDefinition.hashCode());
                if (entity != null) {
                    cells.add(prepareIntegerForSqlInsert(institution.getId()));

                    Object entityId = entity.getId();
                    if (entityId.getClass().equals(Integer.class)) {
                        cells.add(prepareIntegerForSqlInsert((Integer) entity.getId()));
                    } else {
                        cells.add(prepareStringForSqlInsert((String) entity.getId()));
                    }

                    cells.add(prepareStringForSqlInsert(mappingDefinition.getCode()));
                    cells.add(prepareBooleanForSqlInsert(true));
                    rows.add(prepareCellsForSqlInsert(cells));
                }
            }

            importedEntityDAO.executeBulkMerge(prismImportedEntity.getMappingInsertTable(), prismImportedEntity.getMappingInsertColumns(),
                    prepareRowsForSqlInsert(rows), prismImportedEntity.getMappingInsertOnDuplicateKeyUpdate());
            entityService.flush();
        }
    }

    public <T extends ImportedEntityRequest> void mergeImportedEntities(PrismImportedEntity prismImportedEntity, List<T> representations) {
        importedEntityDAO.disableImportedEntities(prismImportedEntity);
        entityService.flush();
        insertImportedEntities(prismImportedEntity, representations, true);
    }

    public void disableImportedPrograms(Integer institutionId, List<Integer> updates, LocalDate baseline) {
        Institution institution = institutionService.getById(institutionId);
        importedEntityDAO.disableImportedPrograms(institution, updates, baseline);
        importedEntityDAO.disableImportedProgramStudyOptionInstances(institution, updates);
        importedEntityDAO.disableImportedProgramStudyOptions(institution, updates);
    }

    // public Integer mergeImportedProgram(Integer institutionId,
    // Set<ProgrammeOccurrence> programInstanceDefinitions, LocalDate baseline,
    // DateTime baselineTime)
    // throws Exception {
    // Institution institution = institutionService.getById(institutionId);
    // Programme programDefinition =
    // programInstanceDefinitions.iterator().next().getProgramme();
    // Program persistentProgram = mergeProgram(institution, programDefinition,
    // baseline);
    //
    // for (ProgrammeOccurrence occurrence : programInstanceDefinitions) {
    // ImportedStudyOption studyOption = mergeStudyOption(institution,
    // occurrence.getModeOfAttendance());
    //
    // LocalDate transientStartDate =
    // DATE_FORMAT.parseLocalDate(occurrence.getStartDate());
    // LocalDate transientCloseDate =
    // DATE_FORMAT.parseLocalDate(occurrence.getEndDate());
    //
    // if (transientCloseDate.isAfter(baseline)) {
    // ResourceStudyOption transientProgramStudyOption = new
    // ResourceStudyOption().withResource(persistentProgram).withStudyOption(studyOption)
    // .withApplicationStartDate(transientStartDate).withApplicationCloseDate(transientCloseDate);
    //
    // ResourceStudyOption persistentProgramStudyOption =
    // mergeProgramStudyOption(transientProgramStudyOption, baseline);
    // persistentProgram.getStudyOptions().add(persistentProgramStudyOption);
    //
    // ResourceStudyOptionInstance transientProgramStudyOptionInstance = new
    // ResourceStudyOptionInstance()
    // .withStudyOption(persistentProgramStudyOption).withApplicationStartDate(transientStartDate)
    // .withApplicationCloseDate(transientCloseDate).withAcademicYear(Integer.toString(transientStartDate.getYear()))
    // .withIdentifier(occurrence.getIdentifier());
    //
    // ResourceStudyOptionInstance persistentProgramStudyOptionInstance =
    // entityService.createOrUpdate(transientProgramStudyOptionInstance);
    // persistentProgramStudyOption.getStudyOptionInstances().add(persistentProgramStudyOptionInstance);
    // }
    // }
    //
    // executeProgramImportAction(persistentProgram, baselineTime);
    // return persistentProgram.getId();
    // }

    public DomicileUseDTO getMostUsedDomicile(Institution institution) {
        return importedEntityDAO.getMostUsedDomicile(institution);
    }

    public ImportedAgeRange getAgeRange(Institution institution, Integer age) {
        return importedEntityDAO.getAgeRange(institution, age);
    }

    public List<ImportedInstitution> getInstitutionsWithUcasId() {
        return importedEntityDAO.getInstitutionsWithUcasId();
    }

    public List<ImportedProgram> getImportedPrograms(String searchTerm) {
        return importedEntityDAO.getImportedPrograms(searchTerm);
    }

    public void mergeImportedProgramSubjectAreas(List<ImportedProgramImportDTO> programDefinitions) {
        List<String> inserts = getImportedSubjectAreaInserts(programDefinitions);
        if (!inserts.isEmpty()) {
            importedEntityDAO.disableImportedEntityRelations(ImportedProgramSubjectArea.class);
            entityService.flush();
            for (List<String> values : Lists.partition(inserts, MAX_BATCH_INSERT_SIZE)) {
                importedEntityDAO.executeBulkMerge("imported_program_subject_area",
                        "imported_program_id, imported_subject_area_id, relation_strength, enabled",
                        Joiner.on(", ").join(values), "enabled = 1");
            }

            importedEntityDAO.disableImportedEntityRelations(ImportedInstitutionSubjectArea.class);

            List<List<ImportedInstitutionSubjectAreaDTO>> importedInstitutionSubjectAreaInsertDefinitions = Lists.partition(
                    importedEntityDAO.getImportedInstitutionSubjectAreas(), PrismConstants.MAX_BATCH_INSERT_SIZE);
            for (List<ImportedInstitutionSubjectAreaDTO> importedInstitutionSubjectAreaInserts : importedInstitutionSubjectAreaInsertDefinitions) {
                List<String> importedInstitutionSubjectAreaValues = Lists.newArrayListWithExpectedSize(importedInstitutionSubjectAreaInsertDefinitions.size());
                for (ImportedInstitutionSubjectAreaDTO importedInstitutionSubjectAreaInsert : importedInstitutionSubjectAreaInserts) {
                    importedInstitutionSubjectAreaValues.add(importedInstitutionSubjectAreaInsert.getInsertDefinition());
                }
                importedEntityDAO.executeBulkMerge("imported_institution_subject_area",
                        "imported_institution_id, imported_subject_area_id, relation_strength, enabled",
                        Joiner.on(", ").join(importedInstitutionSubjectAreaValues), "enabled = 1");
            }
        }
    }

    // private Program mergeProgram(Institution institution, Programme
    // programDefinition, LocalDate baseline) throws DeduplicationException {
    // User proxyCreator = institution.getUser();
    // String transientTitle =
    // prepareStringForInsert(programDefinition.getName());
    //
    // PrismOpportunityType prismOpportunityType =
    // PrismOpportunityType.findValueFromString(programDefinition.getName());
    // prismOpportunityType = prismOpportunityType == null ?
    // getSystemOpportunityType() : prismOpportunityType;
    //
    // boolean transientRequireProjectDefinition =
    // programDefinition.isAtasRegistered();
    //
    // DateTime baselineDateTime = new DateTime();
    // ImportedOpportunityType opportunityType =
    // getByCode(ImportedOpportunityType.class, institution,
    // prismOpportunityType.name());
    // Department department = departmentService.getOrCreateDepartment(new
    // Department().withInstitution(institution).withTitle(
    // programDefinition.getDepartment()));
    //
    // Program transientProgram = new
    // Program().withSystem(systemService.getSystem()).withInstitution(institution).withDepartment(department)
    // .withImportedCode(programDefinition.getCode()).withTitle(transientTitle).withRequireProjectDefinition(transientRequireProjectDefinition)
    // .withOpportunityType(opportunityType).withUser(proxyCreator).withCreatedTimestamp(baselineDateTime).withUpdatedTimestamp(baselineDateTime)
    // .withUpdatedTimestampSitemap(baselineDateTime);
    //
    // Program persistentProgram =
    // entityService.getDuplicateEntity(transientProgram);
    // if (persistentProgram == null) {
    // Advert transientAdvert = new Advert().withTitle(transientTitle);
    // transientAdvert.setAddress(advertService.createAddressCopy(institution.getAdvert().getAddress()));
    // transientProgram.setAdvert(transientAdvert);
    // entityService.save(transientProgram);
    // return transientProgram;
    // } else {
    // persistentProgram.setDepartment(department);
    // persistentProgram.setTitle(transientTitle);
    // persistentProgram.setRequireProjectDefinition(transientRequireProjectDefinition);
    // return persistentProgram;
    // }
    // }

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

    // // TODO: store study option mapping
    // private ImportedStudyOption mergeStudyOption(Institution institution,
    // ModeOfAttendance modeOfAttendance) throws DeduplicationException {
    // String externalCode = modeOfAttendance.getCode();
    // PrismStudyOption prismStudyOption =
    // PrismStudyOption.findValueFromString(externalCode);
    // prismStudyOption = prismStudyOption == null ? getSystemStudyOption() :
    // prismStudyOption;
    // ImportedStudyOption studyOption = new
    // ImportedStudyOption().withName(prismStudyOption.name()).withEnabled(true);
    // studyOption.setType(PrismImportedEntity.IMPORTED_STUDY_OPTION);
    // return entityService.createOrUpdate(studyOption);
    // }

    private ImportedInstitution getOrCreateImportedInstitution(Institution institution, ImportedInstitutionDTO importedInstitutionDTO) {
        Integer importedInstitutionId = importedInstitutionDTO.getId();
        if (importedInstitutionId == null) {
            ImportedInstitution importedInstitution = importedEntityDAO.getImportedInstitutionByName(importedInstitutionDTO.getDomicile().getId(),
                    importedInstitutionDTO.getName());
            if (importedInstitution == null) {
                return createImportedInstitution(institution, importedInstitutionDTO);
            } else {
                createImportedInstitutionMapping(institution, importedInstitution);
                return importedInstitution;
            }
        } else {
            ImportedInstitution importedInstitution = getById(ImportedInstitution.class, importedInstitutionDTO.getId());
            createImportedInstitutionMapping(institution, importedInstitution);
            return importedInstitution;
        }
    }

    private ImportedInstitution createImportedInstitution(Institution institution, ImportedInstitutionDTO importedInstitutionDTO) {
        ImportedEntitySimple domicile = getById(ImportedEntitySimple.class, importedInstitutionDTO.getDomicile().getId());
        ImportedInstitution importedInstitution = new ImportedInstitution().withDomicile(domicile).withName(importedInstitutionDTO.getName())
                .withEnabled(false);
        entityService.save(importedInstitution);
        createImportedInstitutionMapping(institution, importedInstitution);
        return importedInstitution;
    }

    private ImportedProgram createImportedProgram(Institution institution, ImportedInstitution importedInstitution, ImportedProgramDTO importedProgramDTO) {
        ImportedEntitySimple qualificationType = getById(ImportedEntitySimple.class, importedProgramDTO.getQualificationType().getId());
        ImportedProgram program = new ImportedProgram().withInstitution(importedInstitution).withQualificationType(qualificationType)
                .withName(importedProgramDTO.getName()).withEnabled(false);
        entityService.save(program);
        createImportedProgramMapping(institution, program);
        return program;
    }

    private void createImportedInstitutionMapping(Institution institution, ImportedInstitution importedInstitution) {
        ImportedInstitutionMapping importedInstitutionMapping = new ImportedInstitutionMapping().withInstitution(institution)
                .withImportedInstitution(importedInstitution).withEnabled(true).withImportedTimestamp(DateTime.now());
        entityService.getOrCreate(importedInstitutionMapping);
        importedInstitution.getMappings().add(importedInstitutionMapping);
    }

    private void createImportedProgramMapping(Institution institution, ImportedProgram importedProgram) {
        ImportedProgramMapping importedProgramMapping = new ImportedProgramMapping().withInstitution(institution)
                .withImportedProgram(importedProgram).withEnabled(true).withImportedTimestamp(new DateTime());
        entityService.getOrCreate(importedProgramMapping);
        importedProgram.getMappings().add(importedProgramMapping);
    }

    // TODO generalize for API creation
    // private void executeProgramImportAction(Program program, DateTime
    // baselineTime) throws Exception {
    // Comment lastImportComment = commentService.getLatestComment(program,
    // INSTITUTION_CREATE_PROGRAM, INSTITUTION_IMPORT_PROGRAM);
    // PrismAction actionId = lastImportComment == null ?
    // INSTITUTION_CREATE_PROGRAM : INSTITUTION_IMPORT_PROGRAM;
    //
    // User invoker = program.getUser();
    // Role invokerRole = roleService.getCreatorRole(program);
    //
    // State state = program.getState();
    // State transitionState = null;
    // if (state == null) {
    // transitionState = stateService.getById(PROGRAM_APPROVED);
    // } else {
    // PrismState stateId = state.getId();
    // if (stateId.equals(PROGRAM_APPROVED)) {
    // transitionState = state;
    // } else if (stateId.equals(PROGRAM_DISABLED_PENDING_REACTIVATION)) {
    // actionId = PROGRAM_RESTORE;
    // }
    // }
    //
    // Action action = actionService.getById(actionId);
    // Comment comment = new
    // Comment().withUser(invoker).withCreatedTimestamp(baselineTime).withAction(action).withDeclinedResponse(false)
    // .withTransitionState(transitionState).addAssignedUser(invoker,
    // invokerRole, PrismRoleTransitionType.CREATE);
    // actionService.executeAction(program, action, comment);
    // }

    private <V extends ImportedEntityMapping<?>> List<V> getFilteredImportedEntityMappings(List<V> mappings) {
        Map<ImportedEntity<?, ?>, V> filteredMappings = Maps.newHashMap();
        for (V mapping : mappings) {
            ImportedEntity<?, ?> entity = mapping.getImportedEntity();
            if (!filteredMappings.containsKey(entity)) {
                filteredMappings.put(entity, mapping);
            }
        }
        return Lists.newArrayList(filteredMappings.values());
    }

    @SuppressWarnings("unchecked")
    private <T extends ImportedEntityRequest> void insertImportedEntities(PrismImportedEntity prismImportedEntity, List<T> definitions, boolean enable) {
        List<List<T>> definitionBatches = Lists.partition(definitions, MAX_BATCH_INSERT_SIZE);
        for (List<T> definitionBatch : definitionBatches) {
            ImportedEntityExtractor<T> extractor = (ImportedEntityExtractor<T>) applicationContext.getBean(prismImportedEntity.getImportInsertExtractor());
            List<String> rows = extractor.extract(prismImportedEntity, definitionBatch, enable);
            if (!rows.isEmpty()) {
                importedEntityDAO.executeBulkMerge(prismImportedEntity.getImportInsertTable(), prismImportedEntity.getImportInsertColumns(),
                        prepareRowsForSqlInsert(rows), prismImportedEntity.getImportInsertOnDuplicateKeyUpdate());
                entityService.flush();
            }
        }
    }

    private <T extends ImportedEntityRequest> List<String> getImportedSubjectAreaInserts(List<ImportedProgramImportDTO> programDefinitions) {
        HashMultimap<Integer, ImportedSubjectAreaWithWeightingDTO> insertDefinitions = HashMultimap.create();

        Map<Integer, Integer> programIndex = getImportedUcasPrograms();
        ImportedSubjectAreaIndexDTO subjectAreaIndex = getImportedSubjectAreas();
        HashMultimap<Integer, ImportedSubjectAreaDTO> parentImportedSubjectAreaIndex = getParentImportedSubjectAreas();
        for (ImportedProgramImportDTO programDefinition : programDefinitions) {
            Integer program = programIndex.get(programDefinition.hashCode());
            Integer weight = programDefinition.getWeight();

            for (String jacsCode : programDefinition.getJacsCodes()) {
                ImportedSubjectAreaDTO subjectArea = subjectAreaIndex.getByJacsCode(jacsCode);
                subjectArea = subjectArea == null ? subjectAreaIndex.getByJacsCodeOld(jacsCode) : subjectArea;
                if (subjectArea != null) {
                    insertDefinitions.put(program, new ImportedSubjectAreaWithWeightingDTO(subjectArea.getId(), jacsCode, weight));
                }
            }

            for (Integer ucasSubject : programDefinition.getUcasSubjects()) {
                for (ImportedSubjectAreaDTO subjectArea : subjectAreaIndex.getByUcasSubject(ucasSubject)) {
                    insertDefinitions.put(program, new ImportedSubjectAreaWithWeightingDTO(subjectArea.getId(), subjectArea.getJacsCode(), weight));
                }
            }

            for (ImportedSubjectAreaWithWeightingDTO subjectArea : insertDefinitions.get(program)) {
                for (ImportedSubjectAreaDTO parentSubjectArea : parentImportedSubjectAreaIndex.get(subjectArea.getId())) {
                    insertDefinitions.put(program, new ImportedSubjectAreaWithWeightingDTO(parentSubjectArea.getId(), parentSubjectArea.getJacsCode(), weight));
                }
            }
        }

        Integer maxProgramSubjectAreaConnectionCount = 0;
        for (Integer program : insertDefinitions.keySet()) {
            Integer programSubjectAreaConnectionCount = insertDefinitions.get(program).size();
            maxProgramSubjectAreaConnectionCount = maxProgramSubjectAreaConnectionCount < programSubjectAreaConnectionCount ? programSubjectAreaConnectionCount
                    : maxProgramSubjectAreaConnectionCount;
        }

        List<String> inserts = Lists.newArrayListWithExpectedSize(insertDefinitions.size());
        for (Integer program : insertDefinitions.keySet()) {
            inserts.add(getImportedProgramSubjectAreaInsertStatement(program, insertDefinitions.get(program), maxProgramSubjectAreaConnectionCount));
        }

        return inserts;
    }

    private Map<Integer, Integer> getImportedUcasPrograms() {
        Map<Integer, Integer> index = Maps.newHashMap();
        List<com.zuehlke.pgadmissions.dto.ImportedProgramDTO> programs = importedEntityDAO.getImportedUcasPrograms();
        for (com.zuehlke.pgadmissions.dto.ImportedProgramDTO program : programs) {
            index.put(program.hashCode(), program.getId());
        }
        return index;
    }

    private ImportedSubjectAreaIndexDTO getImportedSubjectAreas() {
        ImportedSubjectAreaIndexDTO index = new ImportedSubjectAreaIndexDTO();
        List<ImportedSubjectAreaDTO> subjectAreas = importedEntityDAO.getImportedSubjectAreas();
        for (ImportedSubjectAreaDTO subjectArea : subjectAreas) {

            for (String jacsCode : subjectArea.getJacsCode().split("\\|")) {
                index.addJacsCode(jacsCode, subjectArea);
            }

            String jacsCodesOld = subjectArea.getJacsCodeOld();
            if (jacsCodesOld != null) {
                for (String jacsCodeOld : jacsCodesOld.split("\\|")) {
                    index.addJacsCodeOld(jacsCodeOld, subjectArea);
                }
            }

            index.addUcasSubject(subjectArea.getUcasSubject(), subjectArea);
        }
        return index;
    }

    private HashMultimap<Integer, ImportedSubjectAreaDTO> getParentImportedSubjectAreas() {
        HashMultimap<Integer, ImportedSubjectAreaDTO> index = HashMultimap.create();
        for (ImportedSubjectArea child : importedEntityDAO.getChildImportedSubjectAreas()) {
            ImportedSubjectArea parent = child.getParent();
            indexParentImportedSubjectArea(index, child.getId(), parent);
        }
        return index;
    }

    private void indexParentImportedSubjectArea(HashMultimap<Integer, ImportedSubjectAreaDTO> index, Integer child, ImportedSubjectArea parent) {
        index.put(child, new ImportedSubjectAreaDTO().withId(parent.getId()).withJacsCode(parent.getJacsCode()));
        ImportedSubjectArea grandParent = parent.getParent();
        if (grandParent != null) {
            indexParentImportedSubjectArea(index, child, grandParent);
        }
    }

    public String getImportedProgramSubjectAreaInsertStatement(Integer program, Set<ImportedSubjectAreaWithWeightingDTO> subjectAreas,
            Integer maxProgramSubjectAreaConnectionCount) {
        List<String> values = Lists.newArrayList();
        Integer weightModifier = (maxProgramSubjectAreaConnectionCount + 1 - subjectAreas.size());
        for (ImportedSubjectAreaWithWeightingDTO subjectArea : subjectAreas) {
            values.add("(" + program + ", " + subjectArea.getId().toString() + ", "
                    + new Integer(weightModifier * subjectArea.getSpecificity() * subjectArea.getWeight()).toString() + ")");
        }
        return Joiner.on(", ").join(values);
    }

}
