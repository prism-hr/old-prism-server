package com.zuehlke.pgadmissions.services;

import java.math.BigDecimal;
import java.util.Collections;
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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertCategory;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.ImportedLanguageQualificationType;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.dto.AdvertCategoryImportRowDTO;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.iso.jaxb.CategoryNameType;
import com.zuehlke.pgadmissions.iso.jaxb.CategoryType;
import com.zuehlke.pgadmissions.iso.jaxb.CountryType;
import com.zuehlke.pgadmissions.iso.jaxb.ShortNameType;
import com.zuehlke.pgadmissions.iso.jaxb.SubdivisionLocaleType;
import com.zuehlke.pgadmissions.iso.jaxb.SubdivisionType;
import com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes.LanguageQualificationType;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.ModeOfAttendance;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;
import com.zuehlke.pgadmissions.utils.IntrospectionUtils;

@Service
@Transactional
public class ImportedEntityService {

    private static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd-MMM-yy");

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
        return (T) entityService.getByProperties(clazz, ImmutableMap.of("institution", institution, "id", id));
    }

    public <T extends ImportedEntity> T getImportedEntityByCode(Class<? extends ImportedEntity> entityClass, Institution institution, String code) {
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

    public void mergeImportedProgram(Institution institution, Set<ProgrammeOccurrence> programInstanceDefinitions, LocalDate baseline)
            throws DeduplicationException {
        Programme programDefinition = programInstanceDefinitions.iterator().next().getProgramme();
        Program persistentProgram = mergeProgram(institution, programDefinition);

        for (ProgrammeOccurrence occurrence : programInstanceDefinitions) {
            StudyOption studyOption = mergeStudyOption(institution, occurrence.getModeOfAttendance());

            LocalDate transientStartDate = dateFormatter.parseLocalDate(occurrence.getStartDate());
            LocalDate transientCloseDate = dateFormatter.parseLocalDate(occurrence.getEndDate());

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

    public void mergeImportedInstitution(Institution institution, com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution institutionDefinition)
            throws Exception {
        String domicileCode = institutionDefinition.getDomicile();

        Domicile domicile = entityService.getByProperties(Domicile.class, ImmutableMap.of("code", (Object) domicileCode, "enabled", true));
        ImportedInstitution transientImportedInstitution = new ImportedInstitution().withInstitution(institution).withDomicile(domicile)
                .withCode(institutionDefinition.getCode()).withName(institutionDefinition.getName());

        if (domicile == null) {
            throw new DataImportException("No enabled domicile for Institution " + transientImportedInstitution.getResourceSignature().toString()
                    + ". Code specified was " + domicileCode);
        }

        createOrUpdateImportedEntity(transientImportedInstitution);
    }

    public void mergeImportedLanguageQualificationType(Institution institution, LanguageQualificationType languageQualificationTypeDefinition) throws Exception {
        ImportedLanguageQualificationType transientImportedLanguageQualificationType = new ImportedLanguageQualificationType().withInstitution(institution)
                .withCode(languageQualificationTypeDefinition.getCode()).withName(languageQualificationTypeDefinition.getName())
                .withMinimumOverallScore(convertFloatToBigDecimal(languageQualificationTypeDefinition.getMinimumOverallScore()))
                .withMaximumOverallScore(convertFloatToBigDecimal(languageQualificationTypeDefinition.getMaximumOverallScore()))
                .withMinimumReadingScore(convertFloatToBigDecimal(languageQualificationTypeDefinition.getMinimumReadingScore()))
                .withMaximumReadingScore(convertFloatToBigDecimal(languageQualificationTypeDefinition.getMaximumReadingScore()))
                .withMinimumWritingScore(convertFloatToBigDecimal(languageQualificationTypeDefinition.getMinimumWritingScore()))
                .withMaximumWritingScore(convertFloatToBigDecimal(languageQualificationTypeDefinition.getMaximumWritingScore()))
                .withMinimumSpeakingScore(convertFloatToBigDecimal(languageQualificationTypeDefinition.getMinimumSpeakingScore()))
                .withMaximumSpeakingScore(convertFloatToBigDecimal(languageQualificationTypeDefinition.getMaximumSpeakingScore()))
                .withMinimumListeningScore(convertFloatToBigDecimal(languageQualificationTypeDefinition.getMinimumListeningScore()))
                .withMaximumListeningScore(convertFloatToBigDecimal(languageQualificationTypeDefinition.getMaximumListeningScore()));

        createOrUpdateImportedEntity(transientImportedLanguageQualificationType);
    }

    public void mergeImportedEntity(Class<ImportedEntity> entityClass, Institution institution, Object entityDefinition) throws Exception {
        ImportedEntity transientEntity = entityClass.newInstance();
        transientEntity.setInstitution(institution);
        transientEntity.setCode((String) IntrospectionUtils.getProperty(entityDefinition, "code"));
        transientEntity.setName((String) IntrospectionUtils.getProperty(entityDefinition, "name"));
        createOrUpdateImportedEntity(transientEntity);
    }

    public void disableAllInstitutionDomiciles() {
        importedEntityDAO.disableAllEntities(InstitutionDomicile.class);
        importedEntityDAO.disableAllEntities(InstitutionDomicileRegion.class);
    }

    public void disableAllAdvertCategories() {
        importedEntityDAO.disableAllEntities(AdvertCategory.class);
    }

    public void createOrUpdateAdvertCategory(AdvertCategoryImportRowDTO importRow) throws DeduplicationException {
        AdvertCategory parentCategory = entityService.getById(AdvertCategory.class, importRow.getId() / 10);
        AdvertCategory transientCategory = new AdvertCategory().withId(importRow.getId()).withEnabled(true).withName(importRow.getName())
                .withParentCategory(parentCategory);
        entityService.createOrUpdate(transientCategory);
    }

    public void setLastImportedDate(ImportedEntityFeed detachedImportedEntityFeed) {
        ImportedEntityFeed persistentImportedEntityFeed = entityService.getById(ImportedEntityFeed.class, detachedImportedEntityFeed.getId());
        persistentImportedEntityFeed.setLastImportedDate(new LocalDate());
    }

    public void mergeInstitutionDomicile(CountryType country, Map<String, String> countryCurrencies) {
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
        String currency = countryCurrencies.get(alpha2Code);

        if (Strings.isNullOrEmpty(currency) || status == null || status.equals("exceptionally-reserved") || status.equals("indeterminately-reserved")) {
            return;
        }

        InstitutionDomicile institutionDomicile = new InstitutionDomicile().withId(alpha2Code).withName(countryName).withCurrency(currency).withEnabled(true);
        entityService.merge(institutionDomicile);
        mergeInstitutionDomicileRegions(institutionDomicile, subdivisions, categories);
    }

    private Program mergeProgram(Institution institution, Programme programDefinition) throws DeduplicationException {
        User proxyCreator = institution.getUser();

        String transientTitle = programDefinition.getName();
        Advert transientAdvert = new Advert().withTitle(transientTitle);

        DateTime baseline = new DateTime();
        String programTypeCode = PrismProgramType.findValueFromString(programDefinition.getName()).name();

        boolean transientRequireProjectDefinition = programDefinition.isAtasRegistered();

        ProgramType programType = getImportedEntityByCode(ProgramType.class, institution, programTypeCode);
        Program transientProgram = new Program().withSystem(systemService.getSystem()).withInstitution(institution)
                .withImportedCode(programDefinition.getCode()).withTitle(transientTitle).withRequireProjectDefinition(transientRequireProjectDefinition)
                .withImported(true).withAdvert(transientAdvert).withProgramType(programType).withUser(proxyCreator).withCreatedTimestamp(baseline)
                .withUpdatedTimestamp(baseline);

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

    private ProgramStudyOption mergeProgramStudyOption(ProgramStudyOption transientProgramStudyOption, LocalDate baseline) throws DeduplicationException {
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

    private StudyOption mergeStudyOption(Institution institution, ModeOfAttendance modeOfAttendance) throws DeduplicationException {
        String externalCode = modeOfAttendance.getCode();
        PrismStudyOption internalCode = PrismStudyOption.findValueFromString(externalCode);
        StudyOption studyOption = new StudyOption().withInstitution(institution).withCode(internalCode.name()).withName(externalCode).withEnabled(true);
        return entityService.createOrUpdate(studyOption);
    }

    private void createOrUpdateImportedEntity(ImportedEntity transientImportedEntity) throws DeduplicationException {
        ImportedEntity persistentImportedEntity = entityService.getDuplicateEntity(transientImportedEntity);

        if (persistentImportedEntity == null) {
            transientImportedEntity.setEnabled(true);
            entityService.save(transientImportedEntity);
        } else if (!transientImportedEntity.equals(persistentImportedEntity)) {
            String transientCode = transientImportedEntity.getCode();
            String transientName = transientImportedEntity.getName();

            if (transientCode.equals(persistentImportedEntity.getCode())) {
                ImportedEntity otherPersistentEntity = getSimilarEntityByName(transientImportedEntity);
                if (otherPersistentEntity == null) {
                    persistentImportedEntity.setName(transientName);
                }
            } else {
                ImportedEntity otherPersistentEntity = getSimilarEntityByCode(transientImportedEntity);
                if (otherPersistentEntity == null) {
                    persistentImportedEntity.setCode(transientCode);
                }
            }
            persistentImportedEntity.setEnabled(true);
        }
    }

    private ImportedEntity getSimilarEntityByCode(ImportedEntity transientImportedEntity) {
        Class<? extends ImportedEntity> entityClass = transientImportedEntity.getClass();
        if (transientImportedEntity.getClass().equals(ImportedInstitution.class)) {
            ImportedInstitution transientImportedInstitution = (ImportedInstitution) transientImportedEntity;
            return importedEntityDAO.getImportedInstitutionByCode(transientImportedInstitution.getDomicile(), transientImportedInstitution.getCode());
        }
        return getImportedEntityByCode(entityClass, transientImportedEntity.getInstitution(), transientImportedEntity.getCode());
    }

    private ImportedEntity getSimilarEntityByName(ImportedEntity transientImportedEntity) {
        Class<? extends ImportedEntity> entityClass = transientImportedEntity.getClass();
        if (transientImportedEntity.getClass().equals(ImportedInstitution.class)) {
            ImportedInstitution transientImportedInstitution = (ImportedInstitution) transientImportedEntity;
            return importedEntityDAO.getImportedInstitutionByName(transientImportedInstitution.getDomicile(), transientImportedInstitution.getName());
        }
        return importedEntityDAO.getImportedEntityByName(entityClass, transientImportedEntity.getInstitution(), transientImportedEntity.getName());
    }

    private void executeProgramImportAction(Program program) throws DeduplicationException {
        Action action = actionService.getById(PrismAction.INSTITUTION_IMPORT_PROGRAM);

        User invoker = program.getUser();
        Role invokerRole = roleService.getCreatorRole(program);

        Comment comment = new Comment().withUser(invoker).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false)
                .addAssignedUser(invoker, invokerRole, PrismRoleTransitionType.CREATE);
        actionService.executeSystemAction(program, action, comment);
    }

    private void mergeInstitutionDomicileRegions(InstitutionDomicile domicile, List<SubdivisionType> subdivisions, Map<Short, String> categories) {
        InstitutionDomicileSubdivisionToRegionConverter converter = new InstitutionDomicileSubdivisionToRegionConverter(domicile, null, categories);
        Iterable<InstitutionDomicileRegion> regions = Iterables.concat(Iterables.transform(subdivisions, converter));

        for (InstitutionDomicileRegion region : regions) {
            entityService.merge(region);
        }
    }

    private BigDecimal convertFloatToBigDecimal(Float input) throws Exception {
        return input == null ? null : BigDecimal.valueOf(input);
    }

    private static class InstitutionDomicileSubdivisionToRegionConverter implements Function<SubdivisionType, Iterable<InstitutionDomicileRegion>> {

        private InstitutionDomicile domicile;

        private InstitutionDomicileRegion parentRegion;

        private Map<Short, String> categories;

        public InstitutionDomicileSubdivisionToRegionConverter(InstitutionDomicile domicile, InstitutionDomicileRegion parentRegion,
                Map<Short, String> categories) {
            this.domicile = domicile;
            this.parentRegion = parentRegion;
            this.categories = categories;
        }

        @Override
        public Iterable<InstitutionDomicileRegion> apply(SubdivisionType subdivision) {
            List<SubdivisionLocaleType> locales = subdivision.getSubdivisionLocale();
            String name = locales.get(0).getSubdivisionLocaleName();
            List<String> otherNames = Lists.newLinkedList();
            for (int i = 1; i < locales.size(); i++) {
                otherNames.add(locales.get(i).getSubdivisionLocaleName());
            }
            String otherName = null;
            if (!otherNames.isEmpty()) {
                otherName = Joiner.on(",").join(otherNames);
            }
            String regionType = categories.get(subdivision.getCategoryId());

            InstitutionDomicileRegion currentRegion = new InstitutionDomicileRegion().withId(subdivision.getSubdivisionCode().getValue()).withEnabled(true)
                    .withDomicile(domicile).withParentRegion(parentRegion).withName(name).withOtherName(otherName).withRegionType(regionType);

            if (subdivision.getSubdivision().isEmpty()) {
                return Lists.newArrayList(currentRegion);
            }
            InstitutionDomicileSubdivisionToRegionConverter subConverter = new InstitutionDomicileSubdivisionToRegionConverter(domicile, currentRegion,
                    categories);
            Iterable<InstitutionDomicileRegion> subregions = Iterables.concat(Iterables.transform(subdivision.getSubdivision(), subConverter));
            return Iterables.concat(Collections.singleton(currentRegion), subregions);
        }

    }

}
