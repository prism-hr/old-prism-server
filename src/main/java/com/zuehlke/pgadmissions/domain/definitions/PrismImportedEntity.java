package com.zuehlke.pgadmissions.domain.definitions;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.List;

import com.amazonaws.auth.policy.Resource;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedCountry;
import com.zuehlke.pgadmissions.domain.imported.ImportedDisability;
import com.zuehlke.pgadmissions.domain.imported.ImportedDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEthnicity;
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
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedAgeRangeMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntitySimpleMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedInstitutionMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedLanguageQualificationTypeMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedProgramMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedSubjectAreaMapping;
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
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Programs;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Qualifications;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.RejectionReasons;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.SourcesOfInterest;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.StudyOptions;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.SubjectAreas;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Titles;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedAgeRangeExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEntityExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEntitySimpleExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedLanguageQualificationTypeExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedProgramExtractor;

public enum PrismImportedEntity {

    // TODO: generalise resource importer
    PROGRAM(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(ProgrammeOccurrences.class) //
            .withJaxbProperty("programmeOccurrence") //
            .withEntityClass(Program.class) //
            .withXsdLocation("xsd/import/data/programmeOccurrence.xsd"), //
            null, null, null, false), //
    IMPORTED_AGE_RANGE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(AgeRanges.class) //
            .withJaxbProperty("ageRange") //
            .withEntityClass(ImportedAgeRange.class) //
            .withXsdLocation("xsd/import/data/ageRange.xsd") //
            .withXmlLocation("xml/defaultEntities/ageRange.xml"), //
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.AgeRanges.class) //
                    .withJaxbProperty("ageRange") //
                    .withMappingClass(ImportedAgeRangeMapping.class) //
                    .withXsdLocation("xsd/import/mapping/ageRange.xsd"), //
            new PrismImportedEntityInsertDefinition() //
                    .withTable("imported_age_range")
                    .withColumn("name") //
                    .withColumn("lower_bound") //
                    .withColumn("upper_bound") //
                    .withColumn("enabled") //
                    .withExtractor(ImportedAgeRangeExtractor.class),
            new String[] { "application_personal_detail.age_range_id" }, false), //
    IMPORTED_COUNTRY(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Countries.class) //
            .withJaxbProperty("country") //
            .withEntityClass(ImportedCountry.class) //
            .withXsdLocation("xsd/import/data/country.xsd") //
            .withXmlLocation("xml/defaultEntities/country.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Countries.class) //
                    .withJaxbProperty("country") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/country.xsd"), //
            getImportedEntitySimpleInsertDefinition(), //
            new String[] { "application_personal_detail.country_id" }, false), //
    IMPORTED_DISABILITY(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Disabilities.class) //
            .withJaxbProperty("disability") //
            .withEntityClass(ImportedDisability.class) //
            .withXsdLocation("xsd/import/data/disability.xsd") //
            .withXmlLocation("xml/defaultEntities/disability.xml"), //
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Disabilities.class) //
                    .withJaxbProperty("disabilities") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/disability.xsd"), //
            getImportedEntitySimpleInsertDefinition(), //
            new String[] { "application_personal_detail.disability_id" }, false), //
    IMPORTED_DOMICILE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Domiciles.class) //
            .withJaxbProperty("domicile") //
            .withEntityClass(ImportedDomicile.class) //
            .withXsdLocation("xsd/import/data/domicile.xsd") //
            .withXmlLocation("xml/defaultEntities/domicile.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Domiciles.class) //
                    .withJaxbProperty("domicile") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/domicile.xsd"), //
            getImportedEntitySimpleInsertDefinition(), //
            new String[] { "application_personal_detail.domicile_id" }, false), //
    IMPORTED_ETHNICITY(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Ethnicities.class) //
            .withJaxbProperty("ethnicity") //
            .withEntityClass(ImportedEthnicity.class) //
            .withXsdLocation("xsd/import/data/ethnicity.xsd") //
            .withXmlLocation("xml/defaultEntities/ethnicity.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Ethnicities.class) //
                    .withJaxbProperty("ethnicity") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/ethnicity.xsd"), //
            getImportedEntitySimpleInsertDefinition(), //
            new String[] { "application_personal_detail.ethnicity_id" }, false), //
    IMPORTED_FUNDING_SOURCE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(FundingSources.class) //
            .withJaxbProperty("fundingSource") //
            .withEntityClass(ImportedReferralSource.class) //
            .withXsdLocation("xsd/import/data/fundingSource.xsd") //
            .withXmlLocation("xml/defaultEntities/fundingSource.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.FundingSources.class) //
                    .withJaxbProperty("fundingSource") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/fundingSource.xsd"), //
            getImportedEntitySimpleInsertDefinition(), null, false), //
    IMPORTED_GENDER(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Genders.class) //
            .withJaxbProperty("gender") //
            .withEntityClass(ImportedGender.class) //
            .withXsdLocation("xsd/import/data/gender.xsd") //
            .withXmlLocation("xml/defaultEntities/gender.xml"), //
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Genders.class) //
                    .withJaxbProperty("gender") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/gender.xsd"), //
            getImportedEntitySimpleInsertDefinition(), //
            new String[] { "application_personal_detail.gender_id" }, false), //
    // TODO: add as chart filter
    IMPORTED_INSTITUTION(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Institutions.class) //
            .withJaxbProperty("institution") //
            .withEntityClass(ImportedInstitution.class) //
            .withXsdLocation("xsd/import/data/institution.xsd") //
            .withXmlLocation("xml/defaultEntities/institution.xml"), //
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Institutions.class) //
                    .withJaxbProperty("institution") //
                    .withMappingClass(ImportedInstitutionMapping.class) //
                    .withXsdLocation("xsd/import/mapping/institution.xsd"), //
            new PrismImportedEntityInsertDefinition() //
                    .withTable("imported_institution") //
                    .withColumn("imported_domicile_id") //
                    .withColumn("name") //
                    .withColumn("ucas_id") //
                    .withColumn("facebook_id") //
                    .withColumn("enabled")
                    .withExtractor(ImportedEntitySimpleExtractor.class), //
            new String[] { "application_qualification.institution_id" }, true),
    IMPORTED_LANGUAGE_QUALIFICATION_TYPE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(LanguageQualificationTypes.class) //
            .withJaxbProperty("languageQualificationType") //
            .withEntityClass(ImportedLanguageQualificationType.class) //
            .withXsdLocation("xsd/import/data/languageQualificationType.xsd") //
            .withXmlLocation("xml/defaultEntities/languageQualificationType.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.LanguageQualificationTypes.class) //
                    .withJaxbProperty("languageQualificationType") //
                    .withMappingClass(ImportedLanguageQualificationTypeMapping.class) //
                    .withXsdLocation("xsd/import/mapping/languageQualificationType.xsd"), //
            new PrismImportedEntityInsertDefinition() //
                    .withTable("imported_language_qualification_type") //
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
                    .withColumn("enabled") //
                    .withExtractor(ImportedLanguageQualificationTypeExtractor.class), //
            null, false), //
    IMPORTED_NATIONALITY(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Nationalities.class) //
            .withJaxbProperty("nationality") //
            .withEntityClass(ImportedNationality.class) //
            .withXsdLocation("xsd/import/data/nationality.xsd") //
            .withXmlLocation("xml/defaultEntities/nationality.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Nationalities.class) //
                    .withJaxbProperty("nationality") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/nationality.xsd"), //
            getImportedEntitySimpleInsertDefinition(), //
            new String[] { "application_personal_detail.nationality_id1", "application_personal_detail.nationality_id2" }, false), //
    IMPORTED_OPPORTUNITY_TYPE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(OpportunityTypes.class) //
            .withJaxbProperty("opportunityType") //
            .withEntityClass(ImportedOpportunityType.class) //
            .withXsdLocation("xsd/import/data/opportunityType.xsd") //
            .withXmlLocation("xml/defaultEntities/opportunityType.xml"), //
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.OpportunityTypes.class) //
                    .withJaxbProperty("opportunityType") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/opportunityType.xsd"), //
            getImportedEntitySimpleInsertDefinition(),
            new String[] { "application_program_detail.opportunity_type_id" }, false), //
    // TODO: add as chart filter
    IMPORTED_PROGRAM(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Programs.class) //
            .withJaxbProperty("program") //
            .withEntityClass(ImportedProgram.class) //
            .withXsdLocation("xsd/imported/program.xsd") //
            .withXmlLocation("xml/defaultEntities/program.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Programs.class) //
                    .withJaxbProperty("program") //
                    .withMappingClass(ImportedProgramMapping.class) //
                    .withXsdLocation("xsd/import/mapping/program.xsd"), //
            new PrismImportedEntityInsertDefinition() //
                    .withTable("imported_program") //
                    .withColumn("imported_institution_id") //
                    .withColumn("level") //
                    .withColumn("qualification") //
                    .withColumn("name") //
                    .withColumn("homepage") //
                    .withColumn("enabled") //
                    .withExtractor(ImportedProgramExtractor.class), //
            null, true), //
    IMPORTED_QUALIFICATION_TYPE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Qualifications.class) //
            .withJaxbProperty("qualification") //
            .withEntityClass(ImportedQualificationType.class) //
            .withXsdLocation("xsd/import/data/qualificationType.xsd") //
            .withXmlLocation("xml/defaultEntities/qualificationType.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Qualifications.class) //
                    .withJaxbProperty("qualification") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/qualificationType.xsd"), //
            getImportedEntitySimpleInsertDefinition(), //
            null, false), //
    IMPORTED_REFERRAL_SOURCE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(SourcesOfInterest.class) //
            .withJaxbProperty("sourceOfInterest") //
            .withEntityClass(ImportedReferralSource.class) //
            .withXsdLocation("xsd/import/data/referralSource.xsd") //
            .withXmlLocation("xml/defaultEntities/referralSource.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.SourcesOfInterest.class) //
                    .withJaxbProperty("sourceOfInterest") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/referralSource.xsd"), //
            getImportedEntitySimpleInsertDefinition(), //
            new String[] { "application_program_detail.referral_source_id" }, false), //
    IMPORTED_REJECTION_REASON(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(RejectionReasons.class) //
            .withJaxbProperty("rejectionReason") //
            .withEntityClass(ImportedRejectionReason.class) //
            .withXsdLocation("xsd/import/data/rejectionReason.xsd") //
            .withXmlLocation("xml/defaultEntities/rejectionReason.xml"), //
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.RejectionReasons.class) //
                    .withJaxbProperty("rejectionReason") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/rejectionReason.xsd"), //
            getImportedEntitySimpleInsertDefinition(), //
            null, false), //
    IMPORTED_STUDY_OPTION(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(StudyOptions.class) //
            .withJaxbProperty("studyOption") //
            .withEntityClass(ImportedStudyOption.class) //
            .withXsdLocation("xsd/import/data/studyOption.xsd") //
            .withXmlLocation("xml/defaultEntities/studyOption.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.StudyOptions.class) //
                    .withJaxbProperty("studyOption") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/studyOption.xsd"), //
            getImportedEntitySimpleInsertDefinition(), //
            new String[] { "application_program_detail.study_option_id" }, false), //
    // TODO: add as chart filter
    IMPORTED_SUBJECT_AREA(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(SubjectAreas.class) //
            .withJaxbProperty("subjectArea") //
            .withEntityClass(ImportedSubjectArea.class) //
            .withXsdLocation("xsd/imported/subjectArea.xsd") //
            .withXmlLocation("xml/defaultEntities/subjectArea.xml"), //
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.SubjectAreas.class) //
                    .withJaxbProperty("subjectArea") //
                    .withMappingClass(ImportedSubjectAreaMapping.class) //
                    .withXsdLocation("xsd/import/mapping/subjectArea.xsd"), //
            new PrismImportedEntityInsertDefinition() //
                    .withTable("imported_subject_area") //
                    .withColumn("name") //
                    .withColumn("code") //
                    .withColumn("enabled") //
                    .withExtractor(ImportedProgramExtractor.class), //
            null, false), //
    IMPORTED_TITLE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Titles.class) //
            .withJaxbProperty("title") //
            .withEntityClass(ImportedTitle.class) //
            .withXsdLocation("xsd/import/data/title.xsd") //
            .withXmlLocation("xml/defaultEntities/title.xml"), //
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Titles.class) //
                    .withJaxbProperty("title") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/title.xsd"), //
            getImportedEntitySimpleInsertDefinition(), //
            null, false); //

    private PrismImportedEntityImportDefinition importDefinition;

    private PrismImportedEntityMappingDefinition mappingDefinition;

    private PrismImportedEntityInsertDefinition insertDefinition;

    private String[] reportDefinition;

    private boolean supportsUserDefinedInput;

    private static final List<PrismImportedEntity> entityImports = Lists.newLinkedList();

    private static final List<PrismImportedEntity> resourceImports = Lists.newLinkedList();

    private static final List<PrismImportedEntity> resourceReportFilterProperties = Lists.newLinkedList();

    static {
        for (PrismImportedEntity entity : values()) {
            if (ImportedEntity.class.isAssignableFrom(entity.getEntityClass())) {
                entityImports.add(entity);
            }
            else if (Resource.class.isAssignableFrom(entity.getEntityClass())) {
                resourceImports.add(entity);
            }

            if (entity.getReportDefinition() != null) {
                resourceReportFilterProperties.add(entity);
            }
        }
    }

    private PrismImportedEntity(PrismImportedEntityImportDefinition importDefinition, PrismImportedEntityMappingDefinition mappingDefinition,
            PrismImportedEntityInsertDefinition insertDefinition, String[] reportDefinition, boolean supportsUserDefinedInput) {
        this.importDefinition = importDefinition;
        this.mappingDefinition = mappingDefinition;
        this.insertDefinition = insertDefinition;
        this.reportDefinition = reportDefinition;
        this.supportsUserDefinedInput = supportsUserDefinedInput;
    }

    public Class<?> getEntityJaxbClass() {
        return importDefinition.getJaxbClass();
    }

    public String getEntityJaxbProperty() {
        return importDefinition.getJaxbProperty();
    }

    public Class<?> getEntityClass() {
        return importDefinition.getEntityClass();
    }

    public final String getEntityXsdLocation() {
        return importDefinition.getXsdLocation();
    }

    public String getEntityXmlLocation() {
        return importDefinition.getXmlLocation();
    }

    public Class<?> getMappingJaxbClass() {
        return mappingDefinition.getJaxbClass();
    }

    public String getMappingJaxbProperty() {
        return mappingDefinition.getJaxbProperty();
    }
    
    public Class<? extends ImportedEntityMapping> getMappingEntityClass() {
        return mappingDefinition.getMappingClass();
    }
    
    public final String getMappingXsdLocation() {
        return mappingDefinition.getXsdLocation();
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

    public static List<PrismImportedEntity> getEntityimports() {
        return entityImports;
    }

    public static List<PrismImportedEntity> getResourceimports() {
        return resourceImports;
    }

    public static List<PrismImportedEntity> getResourceReportFilterProperties() {
        return resourceReportFilterProperties;
    }

    public String getLowerCamelName() {
        return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
    }

    private static class PrismImportedEntityImportDefinition {

        private Class<?> jaxbClass;

        private String jaxbProperty;

        private Class<?> entityClass;

        private String xsdLocation;

        private String xmlLocation;

        public Class<?> getJaxbClass() {
            return jaxbClass;
        }

        public String getJaxbProperty() {
            return jaxbProperty;
        }

        public Class<?> getEntityClass() {
            return entityClass;
        }

        public String getXsdLocation() {
            return xsdLocation;
        }

        public String getXmlLocation() {
            return xmlLocation;
        }

        public PrismImportedEntityImportDefinition withJaxbClass(Class<?> jaxbClass) {
            this.jaxbClass = jaxbClass;
            return this;
        }

        public PrismImportedEntityImportDefinition withJaxbProperty(String jaxbProperty) {
            this.jaxbProperty = jaxbProperty;
            return this;
        }

        public PrismImportedEntityImportDefinition withEntityClass(Class<?> entityClass) {
            this.entityClass = entityClass;
            return this;
        }

        public PrismImportedEntityImportDefinition withXsdLocation(String xsdLocation) {
            this.xsdLocation = xsdLocation;
            return this;
        }

        public PrismImportedEntityImportDefinition withXmlLocation(String xmlLocation) {
            this.xmlLocation = xmlLocation;
            return this;
        }

    }

    private static class PrismImportedEntityMappingDefinition {

        private Class<?> jaxbClass;

        private String jaxbProperty;

        private Class<? extends ImportedEntityMapping> mappingClass;

        private String xsdLocation;

        public Class<?> getJaxbClass() {
            return jaxbClass;
        }

        public String getJaxbProperty() {
            return jaxbProperty;
        }

        public Class<? extends ImportedEntityMapping> getMappingClass() {
            return mappingClass;
        }

        public String getXsdLocation() {
            return xsdLocation;
        }

        public PrismImportedEntityMappingDefinition withJaxbClass(Class<?> jaxbClass) {
            this.jaxbClass = jaxbClass;
            return this;
        }

        public PrismImportedEntityMappingDefinition withJaxbProperty(String jaxbProperty) {
            this.jaxbProperty = jaxbProperty;
            return this;
        }

        public PrismImportedEntityMappingDefinition withXsdLocation(String xsdLocation) {
            this.xsdLocation = xsdLocation;
            return this;
        }

        public PrismImportedEntityMappingDefinition withMappingClass(Class<? extends ImportedEntityMapping> mappingClass) {
            this.mappingClass = mappingClass;
            return this;
        }

    }

    private static class PrismImportedEntityInsertDefinition {

        private String table;

        private List<String> columns = Lists.newLinkedList();

        private Class<? extends ImportedEntityExtractor> extractor;

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

        public PrismImportedEntityInsertDefinition withTable(String table) {
            this.table = table;
            return this;
        }

        public PrismImportedEntityInsertDefinition withColumn(String column) {
            columns.add(column);
            return this;
        }

        public PrismImportedEntityInsertDefinition withExtractor(Class<? extends ImportedEntityExtractor> extractor) {
            this.extractor = extractor;
            return this;
        }

    }

    private static PrismImportedEntityInsertDefinition getImportedEntitySimpleInsertDefinition() {
        return new PrismImportedEntityInsertDefinition() //
                .withTable("imported_entity") //
                .withColumn("imported_entity_type") //
                .withColumn("name") //
                .withColumn("enabled") //
                .withExtractor(ImportedEntitySimpleExtractor.class);
    }

}
