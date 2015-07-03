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
import com.zuehlke.pgadmissions.mapping.helpers.ImportedEntityTransformer;
import com.zuehlke.pgadmissions.mapping.helpers.ImportedInstitutionTransformer;
import com.zuehlke.pgadmissions.mapping.helpers.ImportedLanguageQualificationTypeTransformer;
import com.zuehlke.pgadmissions.mapping.helpers.ImportedProgramTransformer;
import com.zuehlke.pgadmissions.mapping.helpers.ImportedSubjectAreaTransformer;
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

    IMPORTED_AGE_RANGE(new PrismImportedEntityImportDefinition() //
            .withImportClass(uk.co.alumeni.prism.api.model.imported.ImportedAgeRange.class) //
            .withEntityClass(ImportedAgeRange.class), //
            new PrismImportedEntityImportInsertDefinition() //
                    .withTable("imported_age_range")
                    .withPivotColumn("name") //
                    .withColumn("lower_bound") //
                    .withColumn("upper_bound") //
                    .withColumn("enabled") //
                    .withExtractor(ImportedAgeRangeExtractor.class), //
            new PrismImportedEntityMappingInsertDefinition() //
                    .withMappingClass(ImportedAgeRangeMapping.class) //
                    .withTable("imported_age_range_mapping"), //
            ImportedAgeRangeRepresentation.class, //
            new String[] { "application_personal_detail.age_range_id" }, true, false), //
    IMPORTED_COUNTRY(getImportedEntitySimpleImportDefinition(), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_personal_detail.country_id" }, true, false), //
    IMPORTED_DISABILITY(getImportedEntitySimpleImportDefinition(), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_personal_detail.disability_id" }, true, false), //
    IMPORTED_DOMICILE(getImportedEntitySimpleImportDefinition(), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_personal_detail.domicile_id" }, true, false), //
    IMPORTED_ETHNICITY(getImportedEntitySimpleImportDefinition(), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_personal_detail.ethnicity_id" }, true, false), //
    IMPORTED_FUNDING_SOURCE(getImportedEntitySimpleImportDefinition(), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            null, true, false), //
    IMPORTED_GENDER(getImportedEntitySimpleImportDefinition(), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_personal_detail.gender_id" }, true, false), //
    // TODO: add as chart filter
    IMPORTED_INSTITUTION(new PrismImportedEntityImportDefinition() //
            .withImportClass(uk.co.alumeni.prism.api.model.imported.ImportedInstitution.class)
            .withEntityClass(ImportedInstitution.class) //
            .withTransformerClass(ImportedInstitutionTransformer.class), //
            new PrismImportedEntityImportInsertDefinition() //
                    .withTable("imported_institution") //
                    .withPivotColumn("imported_domicile_id") //
                    .withPivotColumn("name") //
                    .withColumn("ucas_id") //
                    .withColumn("facebook_id") //
                    .withColumn("enabled")
                    .withExtractor(ImportedEntitySimpleExtractor.class), //
            new PrismImportedEntityMappingInsertDefinition() //
                    .withMappingClass(ImportedInstitutionMapping.class) //
                    .withTable("imported_institution_mapping"),
            ImportedInstitutionRepresentation.class, //
            new String[] { "application_qualification.institution_id" }, false, true),
    IMPORTED_LANGUAGE_QUALIFICATION_TYPE(new PrismImportedEntityImportDefinition() //
            .withImportClass(uk.co.alumeni.prism.api.model.imported.ImportedLanguageQualificationType.class) //
            .withEntityClass(ImportedLanguageQualificationType.class) //
            .withTransformerClass(ImportedLanguageQualificationTypeTransformer.class),
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
                    .withMappingClass(ImportedLanguageQualificationTypeMapping.class) //
                    .withTable("imported_language_qualification_type_mapping"), //
            ImportedLanguageQualificationTypeRepresentation.class, //
            null, true, false), //
    IMPORTED_NATIONALITY(getImportedEntitySimpleImportDefinition(), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_personal_detail.nationality_id1", "application_personal_detail.nationality_id2" }, true, false), //
    IMPORTED_OPPORTUNITY_TYPE(getImportedEntitySimpleImportDefinition(), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_program_detail.opportunity_type_id" }, true, false), //
    // TODO: add as chart filter
    IMPORTED_PROGRAM(new PrismImportedEntityImportDefinition() //
            .withImportClass(uk.co.alumeni.prism.api.model.imported.ImportedProgram.class) //
            .withEntityClass(ImportedProgram.class) //
            .withTransformerClass(ImportedProgramTransformer.class), //
            new PrismImportedEntityImportInsertDefinition() //
                    .withTable("imported_program") //
                    .withPivotColumn("imported_institution_id") //
                    .withColumn("imported_qualification_type_id") //
                    .withColumn("level") //
                    .withColumn("qualification") //
                    .withPivotColumn("name") //
                    .withColumn("homepage") //
                    .withColumn("enabled") //
                    .withExtractor(ImportedProgramExtractor.class), //
            new PrismImportedEntityMappingInsertDefinition() //
                    .withMappingClass(ImportedProgramMapping.class) //
                    .withTable("imported_program_mapping"), //
            ImportedProgramRepresentation.class, //
            null, false, true), //
    IMPORTED_QUALIFICATION_TYPE(getImportedEntitySimpleImportDefinition(), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            null, true, false), //
    IMPORTED_REFERRAL_SOURCE(getImportedEntitySimpleImportDefinition(), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_program_detail.referral_source_id" }, true, false), //
    IMPORTED_REJECTION_REASON(getImportedEntitySimpleImportDefinition(), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            null, true, false), //
    IMPORTED_STUDY_OPTION(getImportedEntitySimpleImportDefinition(), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            new String[] { "application_program_detail.study_option_id" }, true, false), //
    // TODO: add as chart filter
    IMPORTED_SUBJECT_AREA(new PrismImportedEntityImportDefinition() //
            .withImportClass(uk.co.alumeni.prism.api.model.imported.ImportedSubjectArea.class) //
            .withEntityClass(ImportedSubjectArea.class) //
            .withTransformerClass(ImportedSubjectAreaTransformer.class), //
            new PrismImportedEntityImportInsertDefinition() //
                    .withTable("imported_subject_area") //
                    .withPivotColumn("name") //
                    .withColumn("code") //
                    .withColumn("enabled") //
                    .withExtractor(ImportedProgramExtractor.class), //
            new PrismImportedEntityMappingInsertDefinition() //
                    .withMappingClass(ImportedSubjectAreaMapping.class) //
                    .withTable("imported_subject_area_mapping"), //
            ImportedSubjectAreaRepresentation.class,
            null, true, false), //
    IMPORTED_TITLE(getImportedEntitySimpleImportDefinition(), //
            getImportedEntitySimpleImportInsertDefinition(), //
            getImportedEntitySimpleMappingInsertDefinition(), //
            ImportedEntitySimpleRepresentation.class, //
            null, true, false); //

    private PrismImportedEntityImportDefinition importDefinition;

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

    private PrismImportedEntity(PrismImportedEntityImportDefinition importDefinition, PrismImportedEntityImportInsertDefinition importInsertDefinition,
            PrismImportedEntityMappingInsertDefinition mappingInsertDefinition, Class<? extends ImportedEntitySimpleRepresentation> representationClass,
            String[] reportDefinition, boolean prefetchImport, boolean extensibleImport) {
        this.importDefinition = importDefinition;
        this.importInsertDefinition = importInsertDefinition;
        this.mappingInsertDefinition = mappingInsertDefinition;
        this.representationClass = representationClass;
        this.reportDefinition = reportDefinition;
        this.prefetchImport = prefetchImport;
        this.extensibleImport = extensibleImport;
    }

    public Class<? extends uk.co.alumeni.prism.api.model.imported.ImportedEntity> getImportClass() {
        return importDefinition.getImportClass();
    }

    public Class<? extends ImportedEntity<?>> getEntityClass() {
        return importDefinition.getEntityClass();
    }

    public Class<? extends ImportedEntityMapping<?>> getMappingClass() {
        return mappingInsertDefinition.getMappingClass();
    }

    public Class<? extends ImportedEntityTransformer<? extends uk.co.alumeni.prism.api.model.imported.ImportedEntity, ? extends ImportedEntity<?>>> getTransformerClass() {
        return this.importDefinition.getTransformerClass();
    }

    public String getImportInsertTable() {
        return importInsertDefinition.getTable();
    }

    public String getImportInsertColumns() {
        return importInsertDefinition.getColumns();
    }

    public Class<? extends ImportedEntityExtractor<?>> getImportInsertExtractor() {
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

        private Class<? extends uk.co.alumeni.prism.api.model.imported.ImportedEntity> importClass;

        private Class<? extends ImportedEntity<?>> entityClass;

        private Class<? extends ImportedEntityTransformer<? extends uk.co.alumeni.prism.api.model.imported.ImportedEntity, ? extends ImportedEntity<?>>> transformerClass;

        public Class<? extends uk.co.alumeni.prism.api.model.imported.ImportedEntity> getImportClass() {
            return importClass;
        }

        public Class<? extends ImportedEntity<?>> getEntityClass() {
            return entityClass;
        }

        public Class<? extends ImportedEntityTransformer<? extends uk.co.alumeni.prism.api.model.imported.ImportedEntity, ? extends ImportedEntity<?>>> getTransformerClass() {
            return transformerClass;
        }

        public PrismImportedEntityImportDefinition withImportClass(Class<? extends uk.co.alumeni.prism.api.model.imported.ImportedEntity> importClass) {
            this.importClass = importClass;
            return this;
        }

        public PrismImportedEntityImportDefinition withEntityClass(Class<? extends ImportedEntity<?>> entityClass) {
            this.entityClass = entityClass;
            return this;
        }

        public PrismImportedEntityImportDefinition withTransformerClass(
                Class<? extends ImportedEntityTransformer<? extends uk.co.alumeni.prism.api.model.imported.ImportedEntity, ? extends ImportedEntity<?>>> transformerClass) {
            this.transformerClass = transformerClass;
            return this;
        }

    }

    private static class PrismImportedEntityImportInsertDefinition {

        private String table;

        private Set<String> columns = Sets.newLinkedHashSet();

        private Set<String> pivotColumns = Sets.newLinkedHashSet();

        private Class<? extends ImportedEntityExtractor<?>> extractor;

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

        public Class<? extends ImportedEntityExtractor<?>> getExtractor() {
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

        public PrismImportedEntityImportInsertDefinition withExtractor(Class<? extends ImportedEntityExtractor<?>> extractor) {
            this.extractor = extractor;
            return this;
        }

    }

    private static class PrismImportedEntityMappingInsertDefinition {

        private Class<? extends ImportedEntityMapping<?>> mappingClass;

        private String table;

        public Class<? extends ImportedEntityMapping<?>> getMappingClass() {
            return mappingClass;
        }

        public String getTable() {
            return table;
        }

        public PrismImportedEntityMappingInsertDefinition withMappingClass(Class<? extends ImportedEntityMapping<?>> mappingClass) {
            this.mappingClass = mappingClass;
            return this;
        }

        public PrismImportedEntityMappingInsertDefinition withTable(String table) {
            this.table = table;
            return this;
        }

    }

    private static PrismImportedEntityImportDefinition getImportedEntitySimpleImportDefinition() {
        return new PrismImportedEntityImportDefinition() //
                .withImportClass(uk.co.alumeni.prism.api.model.imported.ImportedEntity.class) //
                .withEntityClass(ImportedEntitySimple.class);
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
                .withMappingClass(ImportedEntitySimpleMapping.class) //
                .withTable("imported_entity_simple_mapping");
    }

}
