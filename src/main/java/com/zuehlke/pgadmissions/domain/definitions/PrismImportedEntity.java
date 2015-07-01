package com.zuehlke.pgadmissions.domain.definitions;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.List;
import java.util.Set;

import com.amazonaws.auth.policy.Resource;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedAgeRangeMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntitySimpleMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedInstitutionMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedLanguageQualificationTypeMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedProgramMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedSubjectAreaMapping;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.dto.imported.ImportedEntityPivotDTO;
import com.zuehlke.pgadmissions.dto.imported.ImportedInstitutionPivotDTO;
import com.zuehlke.pgadmissions.dto.imported.ImportedProgramPivotDTO;
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
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Programs;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Qualifications;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.RejectionReasons;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.SourcesOfInterest;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.StudyOptions;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.SubjectAreas;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.Titles;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedAgeRangeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedInstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedLanguageQualificationTypeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedProgramRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedSubjectAreaRepresentation;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedAgeRangeExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEntityExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEntitySimpleExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedLanguageQualificationTypeExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedProgramExtractor;

public enum PrismImportedEntity {

    // TODO: generalise resource importer
    INSTITUTION(new PrismImportedEntityImportDefinition() //
            .withImportClass(uk.co.alumeni.prism.api.model.imported.ImportedInstitution)
            .withEntityClass(Institution.class) //
            .withXsdLocation("xsd/import/resource/institution.xsd"), //
            null, null, null, null, null, false, false), //
    // TODO: add as chart filter
    // TODO: generalise resource importer
    PROGRAM(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.resource.Programs.class) //
            .withJaxbProperty("program") //
            .withEntityClass(Program.class) //
            .withXsdLocation("xsd/import/resource/program.xsd"), //
            null, null, null, null, null, false, false), //
    // TODO: add as chart filter
    // TODO: generalise resource importer
    PROJECT(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.resource.Projects.class) //
            .withJaxbProperty("project") //
            .withEntityClass(Project.class) //
            .withXsdLocation("xsd/import/resource/project.xsd"), //
            null, null, null, null, null, false, false), //
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
            new PrismImportedEntityImportInsertDefinition() //
                    .withTable("imported_age_range")
                    .withPivotColumn("name") //
                    .withColumn("lower_bound") //
                    .withColumn("upper_bound") //
                    .withColumn("enabled") //
                    .withExtractor(ImportedAgeRangeExtractor.class), //
            new PrismImportedEntityMappingInsertDefinition() //
                    .withTable("imported_age_range_mapping") //
                    .withPivotClass(ImportedEntityPivotDTO.class), //
            ImportedAgeRangeRepresentation.class, //
            new String[] { "application_personal_detail.age_range_id" }, true, false), //
    IMPORTED_COUNTRY(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Countries.class) //
            .withJaxbProperty("country") //
            .withEntityClass(ImportedEntitySimple.class) //
            .withXsdLocation("xsd/import/data/country.xsd") //
            .withXmlLocation("xml/defaultEntities/country.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Countries.class) //
                    .withJaxbProperty("country") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/country.xsd"), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_personal_detail.country_id" }, true, false), //
    IMPORTED_DISABILITY(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Disabilities.class) //
            .withJaxbProperty("disability") //
            .withEntityClass(ImportedEntitySimple.class) //
            .withXsdLocation("xsd/import/data/disability.xsd") //
            .withXmlLocation("xml/defaultEntities/disability.xml"), //
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Disabilities.class) //
                    .withJaxbProperty("disabilities") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/disability.xsd"), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_personal_detail.disability_id" }, true, false), //
    IMPORTED_DOMICILE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Domiciles.class) //
            .withJaxbProperty("domicile") //
            .withEntityClass(ImportedEntitySimple.class) //
            .withXsdLocation("xsd/import/data/domicile.xsd") //
            .withXmlLocation("xml/defaultEntities/domicile.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Domiciles.class) //
                    .withJaxbProperty("domicile") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/domicile.xsd"), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_personal_detail.domicile_id" }, true, false), //
    IMPORTED_ETHNICITY(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Ethnicities.class) //
            .withJaxbProperty("ethnicity") //
            .withEntityClass(ImportedEntitySimple.class) //
            .withXsdLocation("xsd/import/data/ethnicity.xsd") //
            .withXmlLocation("xml/defaultEntities/ethnicity.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Ethnicities.class) //
                    .withJaxbProperty("ethnicity") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/ethnicity.xsd"), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_personal_detail.ethnicity_id" }, true, false), //
    IMPORTED_FUNDING_SOURCE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(FundingSources.class) //
            .withJaxbProperty("fundingSource") //
            .withEntityClass(ImportedEntitySimple.class) //
            .withXsdLocation("xsd/import/data/fundingSource.xsd") //
            .withXmlLocation("xml/defaultEntities/fundingSource.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.FundingSources.class) //
                    .withJaxbProperty("fundingSource") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/fundingSource.xsd"), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            null, true, false), //
    IMPORTED_GENDER(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Genders.class) //
            .withJaxbProperty("gender") //
            .withEntityClass(ImportedEntitySimple.class) //
            .withXsdLocation("xsd/import/data/gender.xsd") //
            .withXmlLocation("xml/defaultEntities/gender.xml"), //
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Genders.class) //
                    .withJaxbProperty("gender") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/gender.xsd"), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_personal_detail.gender_id" }, true, false), //
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
            new PrismImportedEntityImportInsertDefinition() //
                    .withTable("imported_institution") //
                    .withPivotColumn("imported_domicile_id") //
                    .withPivotColumn("name") //
                    .withColumn("ucas_id") //
                    .withColumn("facebook_id") //
                    .withColumn("enabled")
                    .withExtractor(ImportedEntitySimpleExtractor.class), //
            new PrismImportedEntityMappingInsertDefinition() //
                    .withTable("imported_institution_mapping") //
                    .withPivotClass(ImportedInstitutionPivotDTO.class),
            ImportedInstitutionRepresentation.class, //
            new String[] { "application_qualification.institution_id" }, false, true),
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
            new PrismImportedEntityImportInsertDefinition() //
                    .withTable("imported_language_qualification_type") //
                    .withPivotColumn("name") //
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
            new PrismImportedEntityMappingInsertDefinition() //
                    .withTable("imported_language_qualification_type_mapping") //
                    .withPivotClass(ImportedEntityPivotDTO.class), //
            ImportedLanguageQualificationTypeRepresentation.class, //
            null, true, false), //
    IMPORTED_NATIONALITY(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Nationalities.class) //
            .withJaxbProperty("nationality") //
            .withEntityClass(ImportedEntitySimple.class) //
            .withXsdLocation("xsd/import/data/nationality.xsd") //
            .withXmlLocation("xml/defaultEntities/nationality.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Nationalities.class) //
                    .withJaxbProperty("nationality") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/nationality.xsd"), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_personal_detail.nationality_id1", "application_personal_detail.nationality_id2" }, true, false), //
    IMPORTED_OPPORTUNITY_TYPE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(OpportunityTypes.class) //
            .withJaxbProperty("opportunityType") //
            .withEntityClass(ImportedEntitySimple.class) //
            .withXsdLocation("xsd/import/data/opportunityType.xsd") //
            .withXmlLocation("xml/defaultEntities/opportunityType.xml"), //
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.OpportunityTypes.class) //
                    .withJaxbProperty("opportunityType") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/opportunityType.xsd"), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_program_detail.opportunity_type_id" }, true, false), //
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
            new PrismImportedEntityImportInsertDefinition() //
                    .withTable("imported_program") //
                    .withPivotColumn("imported_institution_id") //
                    .withColumn("level") //
                    .withColumn("qualification") //
                    .withPivotColumn("name") //
                    .withColumn("homepage") //
                    .withColumn("enabled") //
                    .withExtractor(ImportedProgramExtractor.class), //
            new PrismImportedEntityMappingInsertDefinition() //
                    .withTable("imported_program_mapping") //
                    .withPivotClass(ImportedProgramPivotDTO.class), //
            ImportedProgramRepresentation.class, //
            null, false, true), //
    IMPORTED_QUALIFICATION_TYPE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Qualifications.class) //
            .withJaxbProperty("qualification") //
            .withEntityClass(ImportedEntitySimple.class) //
            .withXsdLocation("xsd/import/data/qualificationType.xsd") //
            .withXmlLocation("xml/defaultEntities/qualificationType.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Qualifications.class) //
                    .withJaxbProperty("qualification") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/qualificationType.xsd"), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            null, true, false), //
    IMPORTED_REFERRAL_SOURCE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(SourcesOfInterest.class) //
            .withJaxbProperty("sourceOfInterest") //
            .withEntityClass(ImportedEntitySimple.class) //
            .withXsdLocation("xsd/import/data/referralSource.xsd") //
            .withXmlLocation("xml/defaultEntities/referralSource.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.SourcesOfInterest.class) //
                    .withJaxbProperty("sourceOfInterest") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/referralSource.xsd"), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_program_detail.referral_source_id" }, true, false), //
    IMPORTED_REJECTION_REASON(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(RejectionReasons.class) //
            .withJaxbProperty("rejectionReason") //
            .withEntityClass(ImportedEntitySimple.class) //
            .withXsdLocation("xsd/import/data/rejectionReason.xsd") //
            .withXmlLocation("xml/defaultEntities/rejectionReason.xml"), //
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.RejectionReasons.class) //
                    .withJaxbProperty("rejectionReason") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/rejectionReason.xsd"), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            null, true, false), //
    IMPORTED_STUDY_OPTION(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(StudyOptions.class) //
            .withJaxbProperty("studyOption") //
            .withEntityClass(ImportedEntitySimple.class) //
            .withXsdLocation("xsd/import/data/studyOption.xsd") //
            .withXmlLocation("xml/defaultEntities/studyOption.xml"),
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.StudyOptions.class) //
                    .withJaxbProperty("studyOption") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/studyOption.xsd"), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_program_detail.study_option_id" }, true, false), //
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
            new PrismImportedEntityImportInsertDefinition() //
                    .withTable("imported_subject_area") //
                    .withPivotColumn("name") //
                    .withColumn("code") //
                    .withColumn("enabled") //
                    .withExtractor(ImportedProgramExtractor.class), //
            new PrismImportedEntityMappingInsertDefinition() //
                    .withTable("imported_subject_area_mapping") //
                    .withPivotClass(ImportedEntityPivotDTO.class),
            ImportedSubjectAreaRepresentation.class,
            null, true, false), //
    IMPORTED_TITLE(new PrismImportedEntityImportDefinition() //
            .withJaxbClass(Titles.class) //
            .withJaxbProperty("title") //
            .withEntityClass(ImportedEntitySimple.class) //
            .withXsdLocation("xsd/import/data/title.xsd") //
            .withXmlLocation("xml/defaultEntities/title.xml"), //
            new PrismImportedEntityMappingDefinition() //
                    .withJaxbClass(com.zuehlke.pgadmissions.referencedata.jaxb.mapping.Titles.class) //
                    .withJaxbProperty("title") //
                    .withMappingClass(ImportedEntitySimpleMapping.class) //
                    .withXsdLocation("xsd/import/mapping/title.xsd"), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            null, true, false); //

    private PrismImportedEntityImportDefinition importDefinition;

    private PrismImportedEntityMappingDefinition mappingDefinition;

    private PrismImportedEntityImportInsertDefinition importInsertDefinition;

    private PrismImportedEntityMappingInsertDefinition mappingInsertDefinition;

    private Class<? extends ImportedEntitySimpleRepresentation> representationClass;

    private String[] reportDefinition;

    private boolean prefetchImport;

    private boolean extensibleImport;

    private static final List<PrismImportedEntity> entityImports = Lists.newLinkedList();

    private static final List<PrismImportedEntity> resourceImports = Lists.newLinkedList();

    private static final List<PrismImportedEntity> prefetchImports = Lists.newLinkedList();

    private static final List<PrismImportedEntity> resourceReportFilterProperties = Lists.newLinkedList();

    static {
        for (PrismImportedEntity entity : values()) {
            if (isEntityImport(entity)) {
                entityImports.add(entity);
            }

            if (isResourceImport(entity)) {
                resourceImports.add(entity);
            }

            if (isPrefetchImport(entity)) {
                prefetchImports.add(entity);
            }

            if (entity.getReportDefinition() != null) {
                resourceReportFilterProperties.add(entity);
            }
        }
    }

    private PrismImportedEntity(PrismImportedEntityImportDefinition importDefinition, PrismImportedEntityMappingDefinition mappingDefinition,
            PrismImportedEntityImportInsertDefinition importInsertDefinition, PrismImportedEntityMappingInsertDefinition mappingInsertDefinition,
            Class<? extends ImportedEntitySimpleRepresentation> representationClass, String[] reportDefinition, boolean prefetchImport, boolean extensibleImport) {
        this.importDefinition = importDefinition;
        this.mappingDefinition = mappingDefinition;
        this.importInsertDefinition = importInsertDefinition;
        this.mappingInsertDefinition = mappingInsertDefinition;
        this.representationClass = representationClass;
        this.reportDefinition = reportDefinition;
        this.prefetchImport = prefetchImport;
        this.extensibleImport = extensibleImport;
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

    public Class<? extends ImportedEntityMapping<?>> getMappingEntityClass() {
        return mappingDefinition.getMappingClass();
    }

    public final String getMappingXsdLocation() {
        return mappingDefinition.getXsdLocation();
    }

    public String getImportInsertTable() {
        return importInsertDefinition.getTable();
    }

    public String getImportInsertColumns() {
        return importInsertDefinition.getColumns();
    }

    public Class<? extends ImportedEntityExtractor> getImportInsertExtractor() {
        return importInsertDefinition.getExtractor();
    }

    public String getImportInsertOnDuplicateKeyUpdate() {
        return importInsertDefinition.getOnDuplicateKeyUpdate();
    }

    public String getMappingInsertTable() {
        return mappingInsertDefinition.getTable();
    }

    public String getMappingInsertColumns() {
        return "institution_id, " + importInsertDefinition.getTable() + "_id, code, enabled";
    }

    public String getMappingInsertOnDuplicateKeyUpdate() {
        return "enabled = values(enabled)";
    }

    public Class<? extends ImportedEntityPivotDTO> getMappingInsertPivotClass() {
        return mappingInsertDefinition.getPivotClass();
    }

    public Class<? extends ImportedEntitySimpleRepresentation> getRepresentationClass() {
        return representationClass;
    }

    public String[] getReportDefinition() {
        return reportDefinition;
    }

    public boolean isPrefetchImport() {
        return prefetchImport;
    }

    public boolean isExtensibleImport() {
        return extensibleImport;
    }

    public static List<PrismImportedEntity> getEntityimports() {
        return entityImports;
    }

    public static List<PrismImportedEntity> getResourceimports() {
        return resourceImports;
    }

    public static List<PrismImportedEntity> getPrefetchimports() {
        return prefetchImports;
    }

    public static List<PrismImportedEntity> getResourceReportFilterProperties() {
        return resourceReportFilterProperties;
    }

    public String getLowerCamelName() {
        return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
    }

    public String getEntityClassLowerCamelName() {
        return UPPER_CAMEL.to(LOWER_CAMEL, getEntityClass().getSimpleName());
    }

    public static boolean isEntityImport(PrismImportedEntity entity) {
        return ImportedEntity.class.isAssignableFrom(entity.getEntityClass());
    }

    public static boolean isResourceImport(PrismImportedEntity entity) {
        return Resource.class.isAssignableFrom(entity.getEntityClass());
    }

    public static boolean isPrefetchImport(PrismImportedEntity entity) {
        return entity.isPrefetchImport();
    }

    private static class PrismImportedEntityImportDefinition {

        private Class<?> importClass;

        private Class<?> entityClass;

        public Class<?> getImportClass() {
            return importClass;
        }

        public Class<?> getEntityClass() {
            return entityClass;
        }
        
        public PrismImportedEntityImportDefinition withImportClass(Class<?> importClass) {
            this.importClass = importClass;
            return this;
        }
        
        public PrismImportedEntityImportDefinition withEntityClass(Class<?> entityClass) {
            this.entityClass = entityClass;
            return this;
        }

    }

    private static class PrismImportedEntityMappingDefinition {

        private Class<?> jaxbClass;

        private String jaxbProperty;

        private Class<? extends ImportedEntityMapping<?>> mappingClass;

        private String xsdLocation;

        public Class<?> getJaxbClass() {
            return jaxbClass;
        }

        public String getJaxbProperty() {
            return jaxbProperty;
        }

        public Class<? extends ImportedEntityMapping<?>> getMappingClass() {
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

        public PrismImportedEntityMappingDefinition withMappingClass(Class<? extends ImportedEntityMapping<?>> mappingClass) {
            this.mappingClass = mappingClass;
            return this;
        }

        public PrismImportedEntityMappingDefinition withXsdLocation(String xsdLocation) {
            this.xsdLocation = xsdLocation;
            return this;
        }

    }

    private static class PrismImportedEntityImportInsertDefinition {

        private String table;

        private Set<String> columns = Sets.newLinkedHashSet();

        private Set<String> pivotColumns = Sets.newLinkedHashSet();

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
                if (!pivotColumns.contains(column)) {
                    updates.add(column + " = values(" + column + ")");
                }
            }
            return Joiner.on(", ").join(updates);
        }

        public Class<? extends ImportedEntityExtractor> getExtractor() {
            return extractor;
        }

        public PrismImportedEntityImportInsertDefinition withTable(String table) {
            this.table = table;
            return this;
        }

        public PrismImportedEntityImportInsertDefinition withColumn(String name) {
            columns.add(name);
            return this;
        }

        public PrismImportedEntityImportInsertDefinition withPivotColumn(String name) {
            columns.add(name);
            pivotColumns.add(name);
            return this;
        }

        public PrismImportedEntityImportInsertDefinition withExtractor(Class<? extends ImportedEntityExtractor> extractor) {
            this.extractor = extractor;
            return this;
        }

    }

    private static class PrismImportedEntityMappingInsertDefinition {

        private String table;

        private Class<? extends ImportedEntityPivotDTO> pivotClass;

        public String getTable() {
            return table;
        }

        public Class<? extends ImportedEntityPivotDTO> getPivotClass() {
            return pivotClass;
        }

        public PrismImportedEntityMappingInsertDefinition withTable(String table) {
            this.table = table;
            return this;
        }

        public PrismImportedEntityMappingInsertDefinition withPivotClass(Class<? extends ImportedEntityPivotDTO> pivotClass) {
            this.pivotClass = pivotClass;
            return this;
        }

    }

    private static PrismImportedEntityImportInsertDefinition getImportedEntitySimpleImportInsertDefinition() {
        return new PrismImportedEntityImportInsertDefinition() //
                .withTable("imported_entity") //
                .withPivotColumn("imported_entity_type") //
                .withPivotColumn("name") //
                .withColumn("enabled") //
                .withExtractor(ImportedEntitySimpleExtractor.class);
    }

    private static PrismImportedEntityMappingInsertDefinition getImportedEntitySimpleMappingInsertDefinition() {
        return new PrismImportedEntityMappingInsertDefinition() //
                .withTable("imported_entity_simple_mapping") //
                .withPivotClass(ImportedEntityPivotDTO.class);
    }

}
