package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertCategory;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.InstitutionDomicileRegion;
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
import com.zuehlke.pgadmissions.dto.AdvertCategoryImportRowDTO;
import com.zuehlke.pgadmissions.iso.jaxb.CategoryNameType;
import com.zuehlke.pgadmissions.iso.jaxb.CategoryType;
import com.zuehlke.pgadmissions.iso.jaxb.CountryType;
import com.zuehlke.pgadmissions.iso.jaxb.ShortNameType;
import com.zuehlke.pgadmissions.iso.jaxb.SubdivisionType;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.ModeOfAttendance;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;
import com.zuehlke.pgadmissions.services.converters.InstitutionDomicileSubdivisionToRegionConverter;

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

    public ImportedEntityFeed getOrCreateImportedEntityFeed(Institution institution, PrismImportedEntity importedEntityType, String location) throws Exception {
        return getOrCreateImportedEntityFeed(institution, importedEntityType, location, null, null);
    }

    public ImportedEntityFeed getOrCreateImportedEntityFeed(Institution institution, PrismImportedEntity importedEntityType, String location, String username,
            String password) throws Exception {
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

    public void mergeEntity(Class<ImportedEntity> entityClass, Institution institution, ImportedEntity transientEntity) throws Exception {
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

    public void mergeProgrammeOccurrences(Institution institution, Set<ProgrammeOccurrence> occurrencesInBatch, LocalDate baseline) throws Exception {
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

            ProgramStudyOptionInstance transientProgramStudyOptionInstance = new ProgramStudyOptionInstance().withStudyOption(persistentProgramStudyOption)
                    .withApplicationStartDate(transientStartDate).withApplicationCloseDate(transientCloseDate)
                    .withAcademicYear(Integer.toString(transientStartDate.getYear())).withIdentifier(occurrence.getIdentifier())
                    .withEnabled(transientCloseDate.isAfter(baseline));

            ProgramStudyOptionInstance persistentProgramStudyOptionInstance = entityService.createOrUpdate(transientProgramStudyOptionInstance);
            persistentProgramStudyOption.getStudyOptionInstances().add(persistentProgramStudyOptionInstance);

            executeProgramImportAction(persistentProgram);
        }
    }

    public void disableAllInstitutionDomiciles() {
        importedEntityDAO.disableAllEntities(InstitutionDomicile.class);
        importedEntityDAO.disableAllEntities(InstitutionDomicileRegion.class);
    }

    public void disableAllAdvertCategories() {
        importedEntityDAO.disableAllEntities(AdvertCategory.class);
    }

    public void createOrUpdateAdvertCategory(AdvertCategoryImportRowDTO importRow) throws Exception {
        AdvertCategory parentCategory = entityService.getById(AdvertCategory.class, importRow.getId() / 10);
        AdvertCategory transientCategory = new AdvertCategory().withId(importRow.getId()).withEnabled(true).withName(importRow.getName())
                .withParentCategory(parentCategory);
        entityService.createOrUpdate(transientCategory);
    }

    public void setLastImportedDate(ImportedEntityFeed detachedImportedEntityFeed) {
        ImportedEntityFeed persistentImportedEntityFeed = entityService.getById(ImportedEntityFeed.class, detachedImportedEntityFeed.getId());
        persistentImportedEntityFeed.setLastImportedDate(new LocalDate());
    }
    
    public void mergeInstitutionDomicile(CountryType country) {
        String status = null;
        String countryName = null;
        String alpha2Code = null;
        List<SubdivisionType> subdivisions = Lists.newLinkedList();
        Map<Short, String> categories = Maps.newHashMap();
        for (JAXBElement<?> element : country.getAlpha2CodeOrAlpha3CodeOrNumericCode()) {
            String elementName = element.getName().getLocalPart();
            if (elementName.equals("status")) {
                status = (String) element.getValue();
            } else if (elementName.equals("short-name")) {
                ShortNameType shortName = (ShortNameType) element.getValue();
                if (shortName.getLang3Code().equals("eng")) {
                    countryName = shortName.getValue();
                }
            } else if (elementName.equals("alpha-2-code")) {
                alpha2Code = (String) element.getValue();
            } else if (elementName.equals("subdivision")) {
                subdivisions.add((SubdivisionType) element.getValue());
            } else if (elementName.equals("category")) {
                CategoryType category = (CategoryType) element.getValue();
                for (CategoryNameType categoryName : category.getCategoryName()) {
                    if (categoryName.getLang3Code().equals("eng")) {
                        categories.put(category.getId(), categoryName.getValue());
                    }
                }
            }
        }

        if (!(status.equals("exceptionally-reserved") || status.equals("indeterminately-reserved"))) {
            InstitutionDomicile institutionDomicile = new InstitutionDomicile().withId(alpha2Code).withName(countryName).withEnabled(true);
            entityService.merge(institutionDomicile);
            mergeInstitutionDomicileRegions(institutionDomicile, subdivisions, categories);
        }
    }

    private Program mergeProgram(Institution institution, Programme importProgram) throws Exception {
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

    private ProgramStudyOption mergeProgramStudyOption(ProgramStudyOption transientProgramStudyOption, LocalDate baseline) throws Exception {
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

    private StudyOption mergeStudyOption(Institution institution, ModeOfAttendance modeOfAttendance) throws Exception {
        String externalcode = modeOfAttendance.getCode();
        PrismStudyOption internalCode = PrismStudyOption.findValueFromString(externalcode);
        StudyOption studyOption = new StudyOption().withInstitution(institution).withCode(internalCode.name()).withName(externalcode).withEnabled(true);
        return entityService.createOrUpdate(studyOption);
    }

    private void executeProgramImportAction(Program program) throws Exception {
        Action action = actionService.getById(PrismAction.INSTITUTION_IMPORT_PROGRAM);

        User invoker = program.getUser();
        Role invokerRole = roleService.getCreatorRole(program);

        Comment comment = new Comment().withUser(invoker).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false)
                .withAssignedUser(invoker, invokerRole);
        actionService.executeSystemAction(program, action, comment);
    }
    
    private void mergeInstitutionDomicileRegions(InstitutionDomicile domicile, List<SubdivisionType> subdivisions, Map<Short, String> categories) {
        InstitutionDomicileSubdivisionToRegionConverter converter = new InstitutionDomicileSubdivisionToRegionConverter(domicile, null, categories);
        Iterable<InstitutionDomicileRegion> regions = Iterables.concat(Iterables.transform(subdivisions, converter));

        for (InstitutionDomicileRegion region : regions) {
            entityService.merge(region);
        }
    }

}
