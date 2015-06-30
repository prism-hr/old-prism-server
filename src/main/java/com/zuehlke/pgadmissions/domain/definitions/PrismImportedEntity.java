package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.imported.*;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.services.helpers.extractors.*;
import uk.co.alumeni.prism.api.model.imported.ImportedEntity;
import uk.co.alumeni.prism.api.model.imported.ImportedProgram;

import java.util.List;
import java.util.Map;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

public enum PrismImportedEntity {

    AGE_RANGE(ImportedEntity.class, "ageRange", AgeRange.class, "xml/defaultEntities/ageRange.xml",
            "xsd/import/ageRange.xsd", "imported_age_range", "institution_id, code, name, lower_bound, upper_bound, enabled", ImportedAgeRangeExtractor.class,
            new String[] { "application_personal_detail.age_range_id" }, false), //
    COUNTRY(ImportedEntity.class, "country", Country.class, "xml/defaultEntities/country.xml", "xsd/import/country.xsd", //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedCountryExtractor.class, //
            new String[] { "application_personal_detail.country_id" }, false), //
    DISABILITY(ImportedEntity.class, "disability", Disability.class, "xml/defaultEntities/disability.xml", "xsd/import/disability.xsd", //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedDisabilityExtractor.class, //
            new String[] { "application_personal_detail.disability_id" }, false), //
    DOMICILE(ImportedEntity.class, "domicile", Domicile.class, "xml/defaultEntities/domicile.xml", "xsd/import/domicile.xsd", //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedDomicileExtractor.class, //
            new String[] { "application_personal_detail.domicile_id" }, false), //
    ETHNICITY(ImportedEntity.class, "ethnicity", Ethnicity.class, "xml/defaultEntities/ethnicity.xml", "xsd/import/ethnicity.xsd", //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedEthnicityExtractor.class, //
            new String[] { "application_personal_detail.ethnicity_id" }, false), //
    NATIONALITY(ImportedEntity.class, "nationality", Nationality.class, "xml/defaultEntities/nationality.xml", "xsd/import/nationality.xsd", //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedNationalityExtractor.class, //
            new String[] { "application_personal_detail.nationality_id1", "application_personal_detail.nationality_id2" }, false), //
    PROGRAM(ImportedProgram.class, "programmeOccurrence", Program.class, null, "xsd/import/program.xsd", null, null, null, null, false), //
    QUALIFICATION_TYPE(ImportedEntity.class, "qualification", QualificationType.class, "xml/defaultEntities/qualificationType.xml",
            "xsd/import/qualificationType.xsd", "imported_entity", "institution_id, imported_entity_type, code, name, enabled", //
            ImportedQualificationTypeExtractor.class, null, false), //
    REFERRAL_SOURCE(ImportedEntity.class, "sourceOfInterest", ReferralSource.class, "xml/defaultEntities/referralSource.xml",
            "xsd/import/referralSource.xsd", "imported_entity", "institution_id, imported_entity_type, code, name, enabled",
            ImportedReferralSourceExtractor.class,
            new String[] { "application_program_detail.referral_source_id" }, false), //
    FUNDING_SOURCE(ImportedEntity.class, "fundingSource", FundingSource.class, "xml/defaultEntities/fundingSource.xml", "xsd/import/fundingSource.xsd",
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedFundingSourceExtractor.class, null, false), //
    LANGUAGE_QUALIFICATION_TYPE(
            ImportedEntity.class,
            "languageQualificationType",
            ImportedLanguageQualificationType.class,
            "xml/defaultEntities/languageQualificationType.xml",
            "xsd/import/languageQualificationType.xsd",
            "imported_language_qualification_type",
            "institution_id, code, name, minimum_overall_score, maximum_overall_score, minimum_reading_score, maximum_reading_score, minimum_writing_score, maximum_writing_score, minimum_speaking_score, maximum_speaking_score, minimum_listening_score, maximum_listening_score, enabled",
            ImportedLanguageQualificationTypeExtractor.class, null, false), //
    TITLE(ImportedEntity.class, "title", Title.class, "xml/defaultEntities/title.xml", "xsd/import/title.xsd", "imported_entity",
            "institution_id, imported_entity_type, code, name, enabled", ImportedTitleExtractor.class, null, false), //
    GENDER(ImportedEntity.class, "gender", Gender.class, "xml/defaultEntities/gender.xml", "xsd/import/gender.xsd", //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedGenderExtractor.class, //
            new String[] { "application_personal_detail.gender_id" }, false), //
    REJECTION_REASON(ImportedEntity.class, "rejectionReason", RejectionReason.class, "xml/defaultEntities/rejectionReason.xml",
            "xsd/import/rejectionReason.xsd", "imported_entity", "institution_id, imported_entity_type, code, name, enabled",
            ImportedRejectionReasonExtractor.class, //
            null, false), //
    STUDY_OPTION(ImportedEntity.class, "studyOption", StudyOption.class, "xml/defaultEntities/studyOption.xml", "xsd/import/studyOption.xsd", //
            "imported_entity", "institution_id, imported_entity_type, code, name, enabled", ImportedStudyOptionExtractor.class, //
            new String[] { "application_program_detail.study_option_id" }, false), //
    OPPORTUNITY_TYPE(ImportedEntity.class, "opportunityType", OpportunityType.class, "xml/defaultEntities/opportunityType.xml",
            "xsd/import/opportunityType.xsd", "imported_entity", "institution_id, imported_entity_type, code, name, enabled",
            ImportedOpportunityTypeExtractor.class, //
            new String[] { "application_program_detail.opportunity_type_id" }, false), //
    INSTITUTION(uk.co.alumeni.prism.api.model.imported.ImportedInstitution.class, "institution", ImportedInstitution.class, "xml/defaultEntities/institution.xml", "xsd/import/institution.xsd",
            "imported_institution", "institution_id, domicile_id, code, name, enabled, custom, ucas_id, facebook_id", ImportedInstitutionExtractor.class, //
            new String[] { "application_qualification.institution_id" }, true);

    private Class<?> apiEntityClass;

    private String jaxbPropertyName;

    private Class<?> entityClass;

    private String defaultLocation;

    private String schemaLocation;

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

    PrismImportedEntity(Class<?> apiEntityClass, String jaxbPropertyName, Class<?> entityClass, String defaultLocation, String schemaLocation,
            String databaseTable, String databaseColumns, Class<? extends ImportedEntityExtractor> databaseImportExtractor, String[] databaseReferenceColumns,
            boolean supportsUserDefinedInput) {
        this.apiEntityClass = apiEntityClass;
        this.jaxbPropertyName = jaxbPropertyName;
        this.entityClass = entityClass;
        this.defaultLocation = defaultLocation;
        this.schemaLocation = schemaLocation;
        this.databaseTable = databaseTable;
        this.databaseColumns = databaseColumns;
        this.databaseImportExtractor = databaseImportExtractor;
        this.databaseReferenceColumns = databaseReferenceColumns;
        this.supportsUserDefinedInput = supportsUserDefinedInput;
    }

    public Class<?> getApiEntityClass() {
        return apiEntityClass;
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
