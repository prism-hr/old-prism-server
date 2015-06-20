package com.zuehlke.pgadmissions.domain.definitions;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
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
import com.zuehlke.pgadmissions.referencedata.jaxb.data.AgeRanges;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Countries;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Disabilities;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Domiciles;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Ethnicities;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.FundingSources;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Genders;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Institutions;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.LanguageQualificationTypes;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Nationalities;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.OpportunityTypes;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.ProgrammeOccurrences;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Qualifications;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.RejectionReasons;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.SourcesOfInterest;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.StudyOptions;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Titles;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedAgeRangeExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEntityExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEntitySimpleExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedLanguageQualificationTypeExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedProgramExtractor;

public enum PrismImportedEntity {

    PROGRAM(ProgrammeOccurrences.class, "programmeOccurrence", Program.class, null, "xsd/import/data/programmeOccurrence.xsd", false, null, null, false), //
    IMPORTED_AGE_RANGE(AgeRanges.class, "ageRange", ImportedAgeRange.class, "xml/defaultEntities/ageRange.xml", //
            "xsd/import/data/ageRange.xsd", true, //
            new PrismImportedEntityInsertDefinition("imported_age_range", ImportedAgeRangeExtractor.class) //
                    .withColumn("name") //
                    .withColumn("lower_bound") //
                    .withColumn("upper_bound") //
                    .withColumn("enabled"),
            new String[] { "application_personal_detail.age_range_id" }, false), //
    IMPORTED_COUNTRY(Countries.class, "country", ImportedCountry.class, "xml/defaultEntities/country.xml", "xsd/import/data/country.xsd", true, //
            getImportedEntitySimpleInsertDefinition(), new String[] { "application_personal_detail.country_id" }, false), //
    IMPORTED_DISABILITY(Disabilities.class, "disability", ImportedDisability.class, "xml/defaultEntities/disability.xml", "xsd/import/data/disability.xsd",
            true, //
            getImportedEntitySimpleInsertDefinition(), new String[] { "application_personal_detail.disability_id" }, false), //
    IMPORTED_DOMICILE(Domiciles.class, "domicile", ImportedDomicile.class, "xml/defaultEntities/domicile.xml", "xsd/import/data/domicile.xsd", true, //
            getImportedEntitySimpleInsertDefinition(), new String[] { "application_personal_detail.domicile_id" }, false), //
    IMPORTED_ETHNICITY(Ethnicities.class, "ethnicity", ImportedEthnicity.class, "xml/defaultEntities/ethnicity.xml", "xsd/import/data/ethnicity.xsd", true, //
            getImportedEntitySimpleInsertDefinition(), new String[] { "application_personal_detail.ethnicity_id" }, false), //
    IMPORTED_NATIONALITY(Nationalities.class, "nationality", ImportedNationality.class, "xml/defaultEntities/nationality.xml",
            "xsd/import/data/nationality.xsd",
            true, getImportedEntitySimpleInsertDefinition(), new String[] { "application_personal_detail.nationality_id1",
                    "application_personal_detail.nationality_id2" }, false), //
    IMPORTED_QUALIFICATION_TYPE(Qualifications.class, "qualification", ImportedQualificationType.class, "xml/defaultEntities/qualificationType.xml",
            "xsd/import/data/qualificationType.xsd", true, getImportedEntitySimpleInsertDefinition(), null, false), //
    IMPORTED_REFERRAL_SOURCE(SourcesOfInterest.class, "sourceOfInterest", ImportedReferralSource.class, "xml/defaultEntities/referralSource.xml",
            "xsd/import/data/referralSource.xsd", true, getImportedEntitySimpleInsertDefinition(), //
            new String[] { "application_program_detail.referral_source_id" }, false), //
    IMPORTED_FUNDING_SOURCE(FundingSources.class, "fundingSource", ImportedFundingSource.class, "xml/defaultEntities/fundingSource.xml",
            "xsd/import/data/fundingSource.xsd", true, getImportedEntitySimpleInsertDefinition(), null, false), //
    IMPORTED_LANGUAGE_QUALIFICATION_TYPE(LanguageQualificationTypes.class, "languageQualificationType", ImportedLanguageQualificationType.class, //
            "xml/defaultEntities/languageQualificationType.xml", "xsd/import/data/languageQualificationType.xsd", true, //
            new PrismImportedEntityInsertDefinition("imported_language_qualification_type", ImportedLanguageQualificationTypeExtractor.class) //
                    .withColumn("name") //
                    .withColumn("minimum_overall_score") //
                    .withColumn("maximum_overall_score") //
                    .withColumn("minimum_reading_score") //
                    .withColumn("maximum_reading_score") //
                    .withColumn("minimum_writing_score") //
                    .withColumn("maximum_writing_score") //
                    .withColumn("minimum_speaking_score") //
                    .withColumn("maximum_speaking_score") //
                    .withColumn("minimum_listening_score") //
                    .withColumn("maximum_listening_score") //
                    .withColumn("enabled"), null, false), //
    IMPORTED_TITLE(Titles.class, "title", ImportedTitle.class, "xml/defaultEntities/title.xml", "xsd/import/data/title.xsd", true, //
            getImportedEntitySimpleInsertDefinition(), null, false), //
    IMPORTED_GENDER(Genders.class, "gender", ImportedGender.class, "xml/defaultEntities/gender.xml", "xsd/import/data/gender.xsd", true, //
            getImportedEntitySimpleInsertDefinition(), new String[] { "application_personal_detail.gender_id" }, false), //
    IMPORTED_REJECTION_REASON(RejectionReasons.class, "rejectionReason", ImportedRejectionReason.class, "xml/defaultEntities/rejectionReason.xml",
            "xsd/import/data/rejectionReason.xsd", true, getImportedEntitySimpleInsertDefinition(), null, false), //
    IMPORTED_STUDY_OPTION(StudyOptions.class, "studyOption", ImportedStudyOption.class, "xml/defaultEntities/studyOption.xml",
            "xsd/import/data/studyOption.xsd",
            true, getImportedEntitySimpleInsertDefinition(), new String[] { "application_program_detail.study_option_id" }, false), //
    IMPORTED_OPPORTUNITY_TYPE(OpportunityTypes.class, "opportunityType", ImportedOpportunityType.class, "xml/defaultEntities/opportunityType.xml",
            "xsd/import/data/opportunityType.xsd", true, getImportedEntitySimpleInsertDefinition(),
            new String[] { "application_program_detail.opportunity_type_id" }, false), //
    // TODO: add as chart filter
    IMPORTED_INSTITUTION(Institutions.class, "institution", ImportedInstitution.class, "xml/defaultEntities/institution.xml",
            "xsd/import/data/institution.xsd", true, //
            new PrismImportedEntityInsertDefinition("imported_institution", ImportedEntitySimpleExtractor.class) //
                    .withColumn("imported_domicile_id") //
                    .withColumn("name") //
                    .withColumn("ucas_id") //
                    .withColumn("facebook_id") //
                    .withColumn("enabled"), //
            new String[] { "application_qualification.institution_id" }, true),
    // TODO: add as chart filter
    IMPORTED_PROGRAM(null, "program", ImportedProgram.class, "xml/defaultEntities/program.xml", "xsd/imported/program.xsd", true, //
            new PrismImportedEntityInsertDefinition("imported_program", ImportedProgramExtractor.class) //
                    .withColumn("imported_institution_id") //
                    .withColumn("level") //
                    .withColumn("qualification") //
                    .withColumn("name") //
                    .withColumn("homepage") //
                    .withColumn("enabled"), null, true), //
    // TODO: add as chart filter
    IMPORTED_SUBJECT_AREA(null, "subjectArea", ImportedSubjectArea.class, "xml/defaultEntities/subjectArea.xml", "xsd/imported/subjectArea.xsd", true, //
            new PrismImportedEntityInsertDefinition("imported_subject_area", ImportedProgramExtractor.class) //
                    .withColumn("name") //
                    .withColumn("code") //
                    .withColumn("enabled"), null, false);

    private Class<?> jaxbClass;

    private String jaxbPropertyName;

    private Class<?> entityClass;

    private String defaultLocation;

    private String schemaLocation;

    private boolean systemImport;

    private PrismImportedEntityInsertDefinition insertDefinition;

    private String[] reportDefinition;

    private boolean supportsUserDefinedInput;

    private static final Map<Class<?>, PrismImportedEntity> byEntityClass = Maps.newHashMap();

    public static final List<PrismImportedEntity> resourceReportFilterProperties = Lists.newLinkedList();

    static {
        for (PrismImportedEntity entity : values()) {
            byEntityClass.put(entity.getEntityClass(), entity);
            if (entity.getReportDefinition() != null) {
                resourceReportFilterProperties.add(entity);
            }
        }
    }

    private PrismImportedEntity(Class<?> jaxbClass, String jaxbPropertyName, Class<?> entityClass, String defaultLocation, String schemaLocation,
            boolean systemImport, PrismImportedEntityInsertDefinition insertDefinition, String[] reportDefinition, boolean supportsUserDefinedInput) {
        this.jaxbClass = jaxbClass;
        this.jaxbPropertyName = jaxbPropertyName;
        this.entityClass = entityClass;
        this.defaultLocation = defaultLocation;
        this.schemaLocation = schemaLocation;
        this.systemImport = systemImport;
        this.insertDefinition = insertDefinition;
        this.reportDefinition = reportDefinition;
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
        return insertDefinition.getTable();
    }

    public String getDatabaseColumns() {
        return insertDefinition.getColumns();
    }

    public Class<? extends ImportedEntityExtractor> getDatabaseImportExtractor() {
        return insertDefinition.getExtractor();
    }

    public String getOnDuplicateKeyUpdate() {
        return insertDefinition.getOnDuplicateKeyUpdate();
    }

    public String[] getReportDefinition() {
        return reportDefinition;
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

    private static class PrismImportedEntityInsertDefinition {

        private String table;

        private List<String> columns = Lists.newLinkedList();

        private Class<? extends ImportedEntityExtractor> extractor;

        public PrismImportedEntityInsertDefinition(String table, Class<? extends ImportedEntityExtractor> extractor) {
            this.table = table;
            this.extractor = extractor;
        }

        public String getTable() {
            return table;
        }

        public String getColumns() {
            return Joiner.on(", ").join(columns);
        }

        public String getOnDuplicateKeyUpdate() {
            List<String> updates = Lists.newLinkedList();
            for (String column : columns) {
                updates.add(column + " = values(" + column + ")");
            }
            return Joiner.on(", ").join(updates);
        }

        public Class<? extends ImportedEntityExtractor> getExtractor() {
            return extractor;
        }

        public PrismImportedEntityInsertDefinition withColumn(String column) {
            columns.add(column);
            return this;
        }

    }

    private static PrismImportedEntityInsertDefinition getImportedEntitySimpleInsertDefinition() {
        return new PrismImportedEntityInsertDefinition("imported_entity", ImportedEntitySimpleExtractor.class) //
                .withColumn("imported_entity_type") //
                .withColumn("name") //
                .withColumn("enabled");
    }

}
