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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.imported.Domicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.imported.ProgramType;
import com.zuehlke.pgadmissions.domain.imported.SimpleImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.dto.InstitutionDomicileImportDTO;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.iso.jaxb.CategoryNameType;
import com.zuehlke.pgadmissions.iso.jaxb.CategoryType;
import com.zuehlke.pgadmissions.iso.jaxb.CountryType;
import com.zuehlke.pgadmissions.iso.jaxb.ShortNameType;
import com.zuehlke.pgadmissions.iso.jaxb.SubdivisionType;
import com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes.LanguageQualificationType;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.ModeOfAttendance;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;
import com.zuehlke.pgadmissions.utils.ConversionUtils;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

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
    private GeocodableLocationService geocodableLocationService;

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

    public List<ImportedEntityFeed> getImportedEntityFeeds() {
        return importedEntityDAO.getImportedEntityFeeds();
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
            throws DeduplicationException, DataImportException, InstantiationException, IllegalAccessException {
        Programme programDefinition = programInstanceDefinitions.iterator().next().getProgramme();
        Program persistentProgram = mergeProgram(institution, programDefinition);

        LocalDate startDate = null;
        LocalDate closeDate = null;
        for (ProgrammeOccurrence occurrence : programInstanceDefinitions) {
            StudyOption studyOption = mergeStudyOption(institution, occurrence.getModeOfAttendance());

            LocalDate transientStartDate = dateFormatter.parseLocalDate(occurrence.getStartDate()).minusYears(1);
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

            startDate = startDate == null || startDate.isBefore(transientStartDate) ? transientStartDate : startDate;
            closeDate = closeDate == null || closeDate.isBefore(transientCloseDate) ? transientCloseDate : closeDate;
        }

        if (startDate.isAfter(baseline) || closeDate.isBefore(baseline)) {
            throw new DataImportException("Attempted to import a program that cannot be used");
        }

        persistentProgram.setEndDate(closeDate);
        executeProgramImportAction(persistentProgram);
    }

    public void mergeImportedInstitution(Institution institution, com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution institutionDefinition)
            throws DataImportException, DeduplicationException {
        String domicileCode = institutionDefinition.getDomicile();

        Domicile domicile = entityService.getByProperties(Domicile.class,
                ImmutableMap.of("institution", (Object) institution, "code", (Object) domicileCode, "enabled", true));

        String institutionNameClean = institutionDefinition.getName().replace("\n", "").replace("\r", "").replace("\t", "");

        ImportedInstitution transientImportedInstitution = new ImportedInstitution().withInstitution(institution).withDomicile(domicile)
                .withCode(institutionDefinition.getCode()).withName(institutionNameClean).withEnabled(true);

        if (domicile == null) {
            throw new DataImportException("No enabled domicile for Institution " + transientImportedInstitution.getResourceSignature().toString()
                    + ". Code specified was " + domicileCode);
        }

        entityService.createOrUpdate(transientImportedInstitution);
    }

    public void mergeImportedLanguageQualificationType(Institution institution, LanguageQualificationType languageQualificationTypeDefinition)
            throws DeduplicationException {
        int precision = 2;
        String languageQualificationTypeNameClean = languageQualificationTypeDefinition.getName().replace("\n", "").replace("\r", "").replace("\t", "");

        ImportedLanguageQualificationType transientImportedLanguageQualificationType = new ImportedLanguageQualificationType().withInstitution(institution)
                .withCode(languageQualificationTypeDefinition.getCode()).withName(languageQualificationTypeNameClean)
                .withMinimumOverallScore(ConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMinimumOverallScore(), precision))
                .withMaximumOverallScore(ConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMaximumOverallScore(), precision))
                .withMinimumReadingScore(ConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMinimumReadingScore(), precision))
                .withMaximumReadingScore(ConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMaximumReadingScore(), precision))
                .withMinimumWritingScore(ConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMinimumWritingScore(), precision))
                .withMaximumWritingScore(ConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMaximumWritingScore(), precision))
                .withMinimumSpeakingScore(ConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMinimumSpeakingScore(), precision))
                .withMaximumSpeakingScore(ConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMaximumSpeakingScore(), precision))
                .withMinimumListeningScore(ConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMinimumListeningScore(), precision))
                .withMaximumListeningScore(ConversionUtils.floatToBigDecimal(languageQualificationTypeDefinition.getMaximumListeningScore(), precision))
                .withEnabled(true);
        entityService.createOrUpdate(transientImportedLanguageQualificationType);
    }

    public <T extends ImportedEntity> void mergeImportedEntity(Class<T> entityClass, Institution institution, Object entityDefinition)
            throws InstantiationException, IllegalAccessException, DeduplicationException {
        SimpleImportedEntity transientEntity = (SimpleImportedEntity) entityClass.newInstance();
        transientEntity.setInstitution(institution);
        transientEntity.setType(PrismImportedEntity.getTypeByClass(entityClass));
        transientEntity.setCode((String) ReflectionUtils.getProperty(entityDefinition, "code"));

        String name = (String) ReflectionUtils.getProperty(entityDefinition, "name");
        String nameClean = name.replace("\n", "").replace("\r", "").replace("\t", "");
        transientEntity.setName(nameClean);

        transientEntity.setEnabled(true);
        entityService.createOrUpdate(transientEntity);
    }

    public void disableAllInstitutionDomiciles() {
        importedEntityDAO.disableAllEntities(InstitutionDomicile.class);
    }

    public void setLastImportedTimestamp(ImportedEntityFeed detachedImportedEntityFeed) {
        ImportedEntityFeed persistentImportedEntityFeed = entityService.getById(ImportedEntityFeed.class, detachedImportedEntityFeed.getId());
        persistentImportedEntityFeed.setLastImportedTimestamp(new DateTime());
    }

    public InstitutionDomicileImportDTO mergeInstitutionDomicile(CountryType country, Map<String, String> countryCurrencies) throws DeduplicationException {
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
            return null;
        }

        InstitutionDomicile transientInstitutionDomicile = new InstitutionDomicile().withId(alpha2Code).withName(countryName).withCurrency(currency)
                .withEnabled(true);
        InstitutionDomicile persistentInstitutionDomicile = geocodableLocationService.getOrCreate(transientInstitutionDomicile);
        return new InstitutionDomicileImportDTO().withDomicile(persistentInstitutionDomicile).withSubdivisions(subdivisions).withCategories(categories);
    }

    public List<Integer> getPendingImportEntityFeeds(Integer institutionId) {
        return importedEntityDAO.getPendingImportedEntityFeeds(institutionId);
    }

    public <T extends ImportedEntity> T getCorrespondingImportedEntity(Institution toInstitution, ImportedEntity fromEntity) {
        return importedEntityDAO.getCorrespondingImportedEntity(toInstitution, fromEntity);
    }

    private Program mergeProgram(Institution institution, Programme programDefinition) throws DeduplicationException {
        User proxyCreator = institution.getUser();

        String transientTitle = programDefinition.getName();
        String transientTitleClean = transientTitle.replace("\n", "").replace("\r", "").replace("\t", "");

        Advert transientAdvert = new Advert().withTitle(transientTitle);

        DateTime baseline = new DateTime();

        PrismProgramType programTypeId = PrismProgramType.findValueFromString(programDefinition.getName());
        programTypeId = programTypeId == null ? institution.getDefaultProgramType() : programTypeId;

        boolean transientRequireProjectDefinition = programDefinition.isAtasRegistered();

        ProgramType programType = getImportedEntityByCode(ProgramType.class, institution, programTypeId.name());
        Program transientProgram = new Program().withSystem(systemService.getSystem()).withInstitution(institution)
                .withImportedCode(programDefinition.getCode()).withTitle(transientTitleClean).withRequireProjectDefinition(transientRequireProjectDefinition)
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
        PrismStudyOption studyOptionId = PrismStudyOption.findValueFromString(externalCode);
        studyOptionId = studyOptionId == null ? institution.getDefaultStudyOption() : studyOptionId;
        StudyOption studyOption = new StudyOption().withInstitution(institution).withCode(studyOptionId.name()).withName(externalCode).withEnabled(true);
        studyOption.setType(PrismImportedEntity.STUDY_OPTION);
        return entityService.createOrUpdate(studyOption);
    }

    private void executeProgramImportAction(Program program) throws DeduplicationException, InstantiationException, IllegalAccessException {
        Action action = actionService.getById(PrismAction.INSTITUTION_IMPORT_PROGRAM);

        User invoker = program.getUser();
        Role invokerRole = roleService.getCreatorRole(program);

        Comment comment = new Comment().withUser(invoker).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false)
                .addAssignedUser(invoker, invokerRole, PrismRoleTransitionType.CREATE);
        actionService.executeAction(program, action, comment);
    }

}
