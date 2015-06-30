package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_IMPORT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_RESTORE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_PENDING_REACTIVATION;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareIntegerForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareRowsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForInsert;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedInstitutionMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedProgramMapping;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOption;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.DomicileUseDTO;
import com.zuehlke.pgadmissions.dto.imported.ImportedEntityPivotDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedInstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramDTO;
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
    private InstitutionService institutionService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Inject
    private SystemService systemService;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private Mapper mapper;

    @SuppressWarnings("unchecked")
    public <T extends ImportedEntity<?>> T getById(Institution institution, PrismImportedEntity entityId, Integer id) {
        return getById(institution, (Class<T>) entityId.getEntityClass(), id);
    }

    public <T extends ImportedEntity<?>> T getById(Institution institution, Class<T> clazz, Integer id) {
        return (T) entityService.getByProperties(clazz, ImmutableMap.of("institution", institution, "id", id));
    }

    public ImportedEntityFeed getImportedEntityFeedById(Integer id) {
        return entityService.getById(ImportedEntityFeed.class, id);
    }

    public List<ImportedEntityFeed> getImportedEntityFeeds(Integer institution, PrismImportedEntity... exclusions) {
        return importedEntityDAO.getImportedEntityFeeds(institution, exclusions);
    }

    public <T extends ImportedEntity<?>> T getByName(Class<T> entityClass, String name) {
        return importedEntityDAO.getImportedEntityByName(entityClass, name);
    }

    public <T extends ImportedEntity<?>> List<T> getEnabledImportedEntities(Institution institution,
            PrismImportedEntity prismImportedEntity) {
        List<T> entities = importedEntityDAO.getEnabledImportedEntitiesWithMappings(institution, prismImportedEntity);
        if (entities.isEmpty()) {
            entities = importedEntityDAO.getEnabledImportedEntities(institution, prismImportedEntity);
        }
        return entities;
    }

    public List<ImportedInstitution> getEnabledImportedInstitutions(Institution institution, ImportedEntitySimple domicile) {
        List<ImportedInstitution> institutions = importedEntityDAO.getEnabledImportedInstitutionsWithMappings(institution, domicile);
        if (institutions.isEmpty()) {
            institutions = importedEntityDAO.getEnabledImportedInstitutions(institution, domicile);
        }
        return institutions;
    }

    public List<ImportedProgram> getEnabledImportedPrograms(Institution institution, ImportedInstitution importedInstitution) {
        List<ImportedProgram> programs = importedEntityDAO.getEnabledImportedProgramsWithMappings(institution, importedInstitution);
        if (programs.isEmpty()) {
            programs = importedEntityDAO.getEnabledImportedPrograms(institution, importedInstitution);
        }
        return programs;
    }

    public List<ImportedSubjectArea> getEnabledImportedSubjectAreas(Institution institution) {
        List<ImportedSubjectArea> subjectAreas = importedEntityDAO.getEnabledImportedSubjectAreasWithMappings(institution);
        if (subjectAreas.isEmpty()) {
            subjectAreas = importedEntityDAO.getEnabledImportedSubjectAreas(institution);
        }
        return subjectAreas;
    }

    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> V getEnabledImportedEntityMapping(Institution institution, T importedEntity) {
        List<V> mappings = importedEntityDAO.getEnabledImportedEntityMapping(institution, importedEntity);
        List<V> filteredMappings = getFilteredImportedEntityMappings(mappings);
        return filteredMappings.isEmpty() ? null : filteredMappings.get(0);
    }

    public <T extends ImportedEntityMapping<?>> List<T> getEnabledImportedEntityMappings(Institution institution,
            PrismImportedEntity prismImportedEntity) {
        List<T> mappings = importedEntityDAO.getImportedEntityMappings(institution, prismImportedEntity);
        return (List<T>) getFilteredImportedEntityMappings(mappings);
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
            ImportedProgram importedProgram = getById(institution, ImportedProgram.class, importedProgramDTO.getId());
            createImportedProgramMapping(institution, importedProgram);
            return importedProgram;
        }
    }

    // FIXME remove dozer mapper when new classes are available (Jakub)
    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> void mergeImportedEntities(Integer importedEntityFeedId) throws Exception {
        ImportedEntityFeed importedEntityFeed = getImportedEntityFeedById(importedEntityFeedId);
        Institution institution = importedEntityFeed.getInstitution();
        PrismImportedEntity prismImportedEntity = importedEntityFeed.getImportedEntityType();

        List<Object> entityDefinitions = Lists.newArrayList();
        List<Object> mappingDefinitions = readImportedData(prismImportedEntity.getMappingJaxbClass(), prismImportedEntity.getMappingJaxbProperty(),
                prismImportedEntity.getMappingXsdLocation(), importedEntityFeed.getLocation(), importedEntityFeed.getLastImportedTimestamp());
        for (Object mappingDefinition : mappingDefinitions) {
            entityDefinitions.add(mapper.map(mappingDefinition, prismImportedEntity.getEntityJaxbClass()));
        }

        insertImportedEntities(prismImportedEntity, entityDefinitions, false);

        importedEntityDAO.disableImportedEntityMappings(institution, prismImportedEntity);
        entityService.flush();

        List<V> currentMappings = importedEntityDAO.getImportedEntityMappings(institution, prismImportedEntity);
        List<V> currentMappingsFiltered = getFilteredImportedEntityMappings(currentMappings);

        List<String> rows = Lists.newLinkedList();
        for (Object mappingDefinition : mappingDefinitions) {
            List<String> cells = Lists.newLinkedList();
            ImportedEntityPivotDTO pivot = mapper.map(mappingDefinition, prismImportedEntity.getMappingInsertPivotClass());
            V importedEntity = currentMappingsFiltered.get(currentMappingsFiltered.indexOf(pivot));
            if (importedEntity != null) {
                cells.add(prepareIntegerForSqlInsert(institution.getId()));
                cells.add(prepareIntegerForSqlInsert(importedEntity.getId()));
                cells.add(prepareStringForInsert((String) getProperty(mappingDefinition, "code")));
                cells.add(prepareBooleanForSqlInsert(true));
                rows.add(prepareCellsForSqlInsert(cells));
            }
        }

        importedEntityDAO.mergeImportedEntityMappings(prismImportedEntity.getMappingInsertTable(), prismImportedEntity.getMappingInsertColumns(),
                prepareRowsForSqlInsert(rows), prismImportedEntity.getMappingInsertOnDuplicateKeyUpdate());
        entityService.flush();

        importedEntityFeed.setLastImportedTimestamp(new DateTime());
    }

    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> void mergeImportedEntities(DateTime lastImportedTimestamp) throws Exception {
        for (PrismImportedEntity prismImportedEntity : PrismImportedEntity.getEntityimports()) {
            importedEntityDAO.disableImportedEntities(prismImportedEntity);
            entityService.flush();
            List<Object> definitions = readImportedData(prismImportedEntity.getEntityJaxbClass(), prismImportedEntity.getEntityJaxbProperty(),
                    prismImportedEntity.getEntityXsdLocation(), prismImportedEntity.getEntityXmlLocation(), lastImportedTimestamp);
            if (definitions != null) {
                insertImportedEntities(prismImportedEntity, definitions, true);
            }
        }
    }

    public void disableImportedPrograms(Integer institutionId, List<Integer> updates, LocalDate baseline) {
        Institution institution = institutionService.getById(institutionId);
        importedEntityDAO.disableImportedPrograms(institution, updates, baseline);
        importedEntityDAO.disableImportedProgramStudyOptions(institution, updates);
        importedEntityDAO.disableImportedProgramStudyOptionInstances(institution, updates);
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

    public void setLastImportedTimestamp(Integer importedEntityFeedId) {
        ImportedEntityFeed persistentImportedEntityFeed = entityService.getById(ImportedEntityFeed.class, importedEntityFeedId);
        persistentImportedEntityFeed.setLastImportedTimestamp(new DateTime());
    }

    public DomicileUseDTO getMostUsedDomicile(Institution institution) {
        return importedEntityDAO.getMostUsedDomicile(institution);
    }

    public ImportedAgeRange getAgeRange(Institution institution, Integer age) {
        return importedEntityDAO.getAgeRange(institution, age);
    }

    public List<ImportedInstitution> getInstitutionsWithUcasId() {
        return importedEntityDAO.getInstitutionsWithUcasId();
    }

    @SuppressWarnings("unchecked")
    @CacheEvict("importedInstitutionData")
    public List<Object> readImportedData(Class<?> jaxbClass, String jaxbProperty, String xsdLocation, String xmlLocation, DateTime lastImportedTimestamp)
            throws Exception {
        URL fileUrl = new DefaultResourceLoader().getResource(xmlLocation).getURL();
        URLConnection connection = fileUrl.openConnection();
        Long lastModifiedTimestamp = connection.getLastModified();

        if (lastImportedTimestamp == null || lastModifiedTimestamp == 0
                || new LocalDateTime(lastModifiedTimestamp).toDateTime().isAfter(lastImportedTimestamp)) {
            JAXBContext jaxbContext = JAXBContext.newInstance(jaxbClass);

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new DefaultResourceLoader().getResource(xsdLocation).getFile());

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);

            Object unmarshalled = unmarshaller.unmarshal(new DefaultResourceLoader().getResource(xmlLocation).getURL());
            return (List<Object>) PrismReflectionUtils.getProperty(unmarshalled, jaxbProperty);
        }

        return null;
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
            ImportedInstitution importedInstitution = importedEntityDAO.getImportedInstitutionByName(importedInstitutionDTO.getDomicile(),
                    importedInstitutionDTO.getName());
            if (importedInstitution == null) {
                return createImportedInstitution(institution, importedInstitutionDTO);
            } else {
                createImportedInstitutionMapping(institution, importedInstitution);
                return importedInstitution;
            }
        } else {
            ImportedInstitution importedInstitution = getById(institution, ImportedInstitution.class, importedInstitutionDTO.getId());
            createImportedInstitutionMapping(institution, importedInstitution);
            return importedInstitution;
        }
    }

    private ImportedInstitution createImportedInstitution(Institution institution, ImportedInstitutionDTO importedInstitutionDTO) {
        ImportedEntitySimple domicile = getById(institution, ImportedEntitySimple.class, importedInstitutionDTO.getDomicile());
        ImportedInstitution importedInstitution = new ImportedInstitution().withDomicile(domicile).withName(importedInstitutionDTO.getName())
                .withEnabled(false);
        entityService.save(importedInstitution);
        createImportedInstitutionMapping(institution, importedInstitution);
        return importedInstitution;
    }

    private ImportedProgram createImportedProgram(Institution institution, ImportedInstitution importedInstitution, ImportedProgramDTO importedProgramDTO) {
        ImportedEntitySimple qualificationType = getById(institution, ImportedEntitySimple.class, importedProgramDTO.getQualificationType());
        ImportedProgram program = new ImportedProgram().withInstitution(importedInstitution).withQualificationType(qualificationType)
                .withName(importedProgramDTO.getName()).withEnabled(false);
        entityService.save(program);
        createImportedProgramMapping(institution, program);
        return program;
    }

    private void createImportedInstitutionMapping(Institution institution, ImportedInstitution importedInstitution) {
        ImportedInstitutionMapping importedInstitutionMapping = new ImportedInstitutionMapping().withInstitution(institution)
                .withImportedInstitution(importedInstitution).withEnabled(true);
        entityService.getOrCreate(importedInstitutionMapping);
        importedInstitution.getMappings().add(importedInstitutionMapping);
    }

    private void createImportedProgramMapping(Institution institution, ImportedProgram importedProgram) {
        ImportedProgramMapping importedProgramMapping = new ImportedProgramMapping().withInstitution(institution)
                .withImportedProgram(importedProgram).withEnabled(true);
        entityService.getOrCreate(importedProgramMapping);
        importedProgram.getMappings().add(importedProgramMapping);
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

    private <V extends ImportedEntityMapping<?>> List<V> getFilteredImportedEntityMappings(List<V> mappings) {
        Map<ImportedEntity<?>, V> filteredMappings = Maps.newHashMap();
        for (V mapping : mappings) {
            ImportedEntity<?> entity = mapping.getImportedEntity();
            if (!filteredMappings.containsKey(entity)) {
                filteredMappings.put(entity, mapping);
            }
        }
        return Lists.newArrayList(filteredMappings.values());
    }

    private void insertImportedEntities(PrismImportedEntity prismImportedEntity, List<Object> definitions, boolean enable) throws Exception {
        List<String> rows = applicationContext.getBean(prismImportedEntity.getImportInsertExtractor()).extract(prismImportedEntity, definitions, enable);
        importedEntityDAO.mergeImportedEntities(prismImportedEntity.getImportInsertTable(), prismImportedEntity.getImportInsertColumns(),
                prepareRowsForSqlInsert(rows), prismImportedEntity.getImportInsertOnDuplicateKeyUpdate());
        entityService.flush();
    }

}
