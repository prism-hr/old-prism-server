package com.zuehlke.pgadmissions.domain.definitions;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedCountry;
import com.zuehlke.pgadmissions.domain.imported.ImportedDisability;
import com.zuehlke.pgadmissions.domain.imported.ImportedDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedEthnicity;
import com.zuehlke.pgadmissions.domain.imported.ImportedFundingSource;
import com.zuehlke.pgadmissions.domain.imported.ImportedGender;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.imported.ImportedNationality;
import com.zuehlke.pgadmissions.domain.imported.ImportedOpportunityType;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedQualificationType;
import com.zuehlke.pgadmissions.domain.imported.ImportedReferralSource;
import com.zuehlke.pgadmissions.domain.imported.ImportedRejectionReason;
import com.zuehlke.pgadmissions.domain.imported.ImportedStudyOption;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.ImportedTitle;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.referencedata.jaxb.AgeRanges;
import com.zuehlke.pgadmissions.referencedata.jaxb.Countries;
import com.zuehlke.pgadmissions.referencedata.jaxb.Disabilities;
import com.zuehlke.pgadmissions.referencedata.jaxb.Domiciles;
import com.zuehlke.pgadmissions.referencedata.jaxb.Ethnicities;
import com.zuehlke.pgadmissions.referencedata.jaxb.FundingSources;
import com.zuehlke.pgadmissions.referencedata.jaxb.Genders;
import com.zuehlke.pgadmissions.referencedata.jaxb.Institutions;
import com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes;
import com.zuehlke.pgadmissions.referencedata.jaxb.Nationalities;
import com.zuehlke.pgadmissions.referencedata.jaxb.OpportunityTypes;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences;
import com.zuehlke.pgadmissions.referencedata.jaxb.Qualifications;
import com.zuehlke.pgadmissions.referencedata.jaxb.RejectionReasons;
import com.zuehlke.pgadmissions.referencedata.jaxb.SourcesOfInterest;
import com.zuehlke.pgadmissions.referencedata.jaxb.StudyOptions;
import com.zuehlke.pgadmissions.referencedata.jaxb.Titles;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedAgeRangeExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedCountryExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedDisabilityExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedDomicileExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEntityExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEthnicityExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedFundingSourceExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedGenderExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedInstitutionExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedLanguageQualificationTypeExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedNationalityExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedOpportunityTypeExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedQualificationTypeExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedReferralSourceExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedRejectionReasonExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedStudyOptionExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedTitleExtractor;

public enum PrismImportedEntity {

    PROGRAM(ProgrammeOccurrences.class, "programmeOccurrence", Program.class, null, "xsd/import/programmeOccurrence.xsd", false, null, null, null, null, false), //
    IMPORTED_AGE_RANGE(AgeRanges.class, "ageRange", ImportedAgeRange.class, "xml/defaultEntities/ageRange.xml",
            "xsd/import/ageRange.xsd", true, "imported_age_range", "institution_id, code, name, lower_bound, upper_bound, enabled",
            ImportedAgeRangeExtractor.class,
            new String[] { "application_personal_detail.age_range_id" }, false), //
    IMPORTED_COUNTRY(Countries.class, "country", ImportedCountry.class, "xml/defaultEntities/country.xml", "xsd/import/country.xsd", true, //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedCountryExtractor.class, //
            new String[] { "application_personal_detail.country_id" }, false), //
    IMPORTED_DISABILITY(Disabilities.class, "disability", ImportedDisability.class, "xml/defaultEntities/disability.xml", "xsd/import/disability.xsd", true, //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedDisabilityExtractor.class, //
            new String[] { "application_personal_detail.disability_id" }, false), //
    IMPORTED_DOMICILE(Domiciles.class, "domicile", ImportedDomicile.class, "xml/defaultEntities/domicile.xml", "xsd/import/domicile.xsd", true, //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedDomicileExtractor.class, //
            new String[] { "application_personal_detail.domicile_id" }, false), //
    IMPORTED_ETHNICITY(Ethnicities.class, "ethnicity", ImportedEthnicity.class, "xml/defaultEntities/ethnicity.xml", "xsd/import/ethnicity.xsd", true, //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedEthnicityExtractor.class, //
            new String[] { "application_personal_detail.ethnicity_id" }, false), //
    IMPORTED_NATIONALITY(Nationalities.class, "nationality", ImportedNationality.class, "xml/defaultEntities/nationality.xml", "xsd/import/nationality.xsd",
            true, //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedNationalityExtractor.class, //
            new String[] { "application_personal_detail.nationality_id1", "application_personal_detail.nationality_id2" }, false), //
    IMPORTED_QUALIFICATION_TYPE(Qualifications.class, "qualification", ImportedQualificationType.class, "xml/defaultEntities/qualificationType.xml",
            "xsd/import/qualificationType.xsd", true, "imported_entity", "institution_id, imported_entity_type, code, name, enabled", //
            ImportedQualificationTypeExtractor.class, null, false), //
    IMPORTED_REFERRAL_SOURCE(SourcesOfInterest.class, "sourceOfInterest", ImportedReferralSource.class, "xml/defaultEntities/referralSource.xml",
            "xsd/import/referralSource.xsd", true, "imported_entity", "institution_id, imported_entity_type, code, name, enabled",
            ImportedReferralSourceExtractor.class,
            new String[] { "application_program_detail.referral_source_id" }, false), //
    IMPORTED_FUNDING_SOURCE(FundingSources.class, "fundingSource", ImportedFundingSource.class, "xml/defaultEntities/fundingSource.xml",
            "xsd/import/fundingSource.xsd", true,
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedFundingSourceExtractor.class, null, false), //
    IMPORTED_LANGUAGE_QUALIFICATION_TYPE(
            LanguageQualificationTypes.class,
            "languageQualificationType",
            ImportedLanguageQualificationType.class,
            "xml/defaultEntities/languageQualificationType.xml",
            "xsd/import/languageQualificationType.xsd",
            true,
            "imported_language_qualification_type",
            "institution_id, code, name, minimum_overall_score, maximum_overall_score, minimum_reading_score, maximum_reading_score, minimum_writing_score, maximum_writing_score, minimum_speaking_score, maximum_speaking_score, minimum_listening_score, maximum_listening_score, enabled",
            ImportedLanguageQualificationTypeExtractor.class, null, false), //
    IMPORTED_TITLE(Titles.class, "title", ImportedTitle.class, "xml/defaultEntities/title.xml", "xsd/import/title.xsd", true, "imported_entity",
            "institution_id, imported_entity_type, code, name, enabled", ImportedTitleExtractor.class, null, false), //
    IMPORTED_GENDER(Genders.class, "gender", ImportedGender.class, "xml/defaultEntities/gender.xml", "xsd/import/gender.xsd", true, //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedGenderExtractor.class, //
            new String[] { "application_personal_detail.gender_id" }, false), //
    IMPORTED_REJECTION_REASON(RejectionReasons.class, "rejectionReason", ImportedRejectionReason.class, "xml/defaultEntities/rejectionReason.xml",
            "xsd/import/rejectionReason.xsd", true, "imported_entity", "institution_id, imported_entity_type, code, name, enabled",
            ImportedRejectionReasonExtractor.class, //
            null, false), //
    IMPORTED_STUDY_OPTION(StudyOptions.class, "studyOption", ImportedStudyOption.class, "xml/defaultEntities/studyOption.xml", "xsd/import/studyOption.xsd",
            true, //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedStudyOptionExtractor.class, //
            new String[] { "application_program_detail.study_option_id" }, false), //
    IMPORTED_OPPORTUNITY_TYPE(OpportunityTypes.class, "opportunityType", ImportedOpportunityType.class, "xml/defaultEntities/opportunityType.xml",
            "xsd/import/opportunityType.xsd", true, "imported_entity", "institution_id, imported_entity_type, code, name, enabled",
            ImportedOpportunityTypeExtractor.class, //
            new String[] { "application_program_detail.opportunity_type_id" }, false), //
    IMPORTED_INSTITUTION(Institutions.class, "institution", ImportedInstitution.class, "xml/defaultEntities/institution.xml", "xsd/import/institution.xsd",
            true,
            "imported_institution", "institution_id, domicile_id, code, name, ucas_id, facebook_id, enabled, custom", ImportedInstitutionExtractor.class, //
            new String[] { "application_qualification.institution_id" }, true),
    IMPORTED_PROGRAM(null, "program", ImportedProgram.class, "xml/defaultEntities/program.xml", "xsd/imported/program.xsd", true, "imported_program", //
            "imported_institution_id, imported_qualification_type_id, code, level, qualification, name, enabled", //
            null, null, true), //
    IMPORTED_SUBJECT_AREA(null, "subjectArea", ImportedSubjectArea.class, "xml/defaultEntities/subjectArea.xml", "xsd/imported/subjectArea.xsd", true, //
            "imported_subject_area", "code, name, enabled", null, null, false);

    private Class<?> jaxbClass;

    private String jaxbPropertyName;

    private Class<?> entityClass;

    private String defaultLocation;

    private String schemaLocation;

    private boolean systemImport;

    private String databaseTable;

    private String databaseColumns;

    private Class<? extends ImportedEntityExtractor> databaseImportExtractor;

    private String[] databaseReferenceColumns;

    private boolean supportsUserDefinedInput;

    private static final Map<Class<?>, PrismImportedEntity> byEntityClass = Maps.newHashMap();

    public static final List<PrismImportedEntity> resourceReportFilterProperties = Lists.newLinkedList();

    static {
        for (PrismImportedEntity entity : values()) {
            byEntityClass.put(entity.getEntityClass(), entity);
            if (entity.getDatabaseReferenceColumns() != null) {
                resourceReportFilterProperties.add(entity);
            }
        }
    }

    private PrismImportedEntity(Class<?> jaxbClass, String jaxbPropertyName, Class<?> entityClass, String defaultLocation, String schemaLocation,
            boolean systemImport, String databaseTable, String databaseColumns, Class<? extends ImportedEntityExtractor> databaseImportExtractor,
            String[] databaseReferenceColumns, boolean supportsUserDefinedInput) {
        this.jaxbClass = jaxbClass;
        this.jaxbPropertyName = jaxbPropertyName;
        this.entityClass = entityClass;
        this.defaultLocation = defaultLocation;
        this.schemaLocation = schemaLocation;
        this.systemImport = systemImport;
        this.databaseTable = databaseTable;
        this.databaseColumns = databaseColumns;
        this.databaseImportExtractor = databaseImportExtractor;
        this.databaseReferenceColumns = databaseReferenceColumns;
        this.supportsUserDefinedInput = supportsUserDefinedInput;
    }

    public Class<?> getJaxbClass() {
        return jaxbClass;
    }

    public String getJaxbPropertyName() {
        return jaxbPropertyName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getDefaultLocation() {
        return defaultLocation;
    }

    public final String getSchemaLocation() {
        return schemaLocation;
    }

    public boolean isSystemImport() {
        return systemImport;
    }

    public String getDatabaseTable() {
        return databaseTable;
    }

    public String getDatabaseColumns() {
        return databaseColumns;
    }

    public Class<? extends ImportedEntityExtractor> getDatabaseImportExtractor() {
        return databaseImportExtractor;
    }

    public String[] getDatabaseReferenceColumns() {
        return databaseReferenceColumns;
    }

    public boolean isSupportsUserDefinedInput() {
        return supportsUserDefinedInput;
    }

    public static final PrismImportedEntity getByEntityClass(Class<?> entityClass) {
        return byEntityClass.get(entityClass);
    }

    public static List<PrismImportedEntity> getResourceReportFilterProperties() {
        return resourceReportFilterProperties;
    }

    public String getLowerCamelName() {
        return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
    }

    public static void main(String[] args) {
        for (PrismImportedEntity prismImportedEntity : PrismImportedEntity.values()) {
            System.out.println(prismImportedEntity.name());

        }
    }
}
