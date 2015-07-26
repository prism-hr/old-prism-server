package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.imported.*;
import com.zuehlke.pgadmissions.domain.imported.mapping.*;
import com.zuehlke.pgadmissions.mapping.helpers.*;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramImportDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedSubjectAreaImportDTO;
import com.zuehlke.pgadmissions.services.helpers.extractors.*;
import org.apache.commons.lang3.ObjectUtils;
import uk.co.alumeni.prism.api.model.advert.EnumDefinition;
import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.CaseFormat.*;

public enum PrismImportedEntity implements EnumDefinition<uk.co.alumeni.prism.enums.PrismImportedEntity> {

    IMPORTED_ADVERT_DOMICILE(new PrismImportedEntityImportDefinition()
            .withEntityClass(ImportedAdvertDomicile.class)
            .withTransformerClass(ImportedAdvertDomicileTransformer.class),
            new PrismImportedEntityImportInsertDefinition()
                    .withTable("imported_advert_domicile")
                    .withPivotColumn("id")
                    .withColumn("name")
                    .withColumn("currency")
                    .withColumn("enabled")
                    .withExtractor(ImportedAdvertDomicileExtractor.class),
            new PrismImportedEntityMappingInsertDefinition()
                    .withMappingClass(ImportedAdvertDomicileMapping.class)
                    .withTable("imported_advert_domicile_mapping"),
            null, true),
    IMPORTED_AGE_RANGE(new PrismImportedEntityImportDefinition()
            .withEntityClass(ImportedAgeRange.class)
            .withTransformerClass(ImportedAgeRangeTransformer.class),
            new PrismImportedEntityImportInsertDefinition()
                    .withTable("imported_age_range")
                    .withPivotColumn("name")
                    .withColumn("lower_bound")
                    .withColumn("upper_bound")
                    .withColumn("enabled")
                    .withExtractor(ImportedAgeRangeExtractor.class),
            new PrismImportedEntityMappingInsertDefinition()
                    .withMappingClass(ImportedAgeRangeMapping.class)
                    .withTable("imported_age_range_mapping"),
            new String[] { "application_personal_detail.age_range_id" }, true),
    IMPORTED_COUNTRY(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            getImportedEntitySimpleMappingInsertDefinition(),
            new String[] { "application_personal_detail.country_id" }, true),
    IMPORTED_DISABILITY(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            getImportedEntitySimpleMappingInsertDefinition(),
            new String[] { "application_personal_detail.disability_id" }, true),
    IMPORTED_DOMICILE(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            getImportedEntitySimpleMappingInsertDefinition(),
            new String[] { "application_personal_detail.domicile_id" }, true),
    IMPORTED_ETHNICITY(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            getImportedEntitySimpleMappingInsertDefinition(),
            new String[] { "application_personal_detail.ethnicity_id" }, true),
    IMPORTED_FUNDING_SOURCE(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            getImportedEntitySimpleMappingInsertDefinition(),
            null, true),
    IMPORTED_GENDER(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            getImportedEntitySimpleMappingInsertDefinition(),
            new String[] { "application_personal_detail.gender_id" }, true),
    // TODO: add as chart filter
    IMPORTED_INSTITUTION(new PrismImportedEntityImportDefinition()
            .withEntityClass(ImportedInstitution.class)
            .withTransformerClass(ImportedInstitutionTransformer.class),
            new PrismImportedEntityImportInsertDefinition()
                    .withTable("imported_institution")
                    .withPivotColumn("imported_domicile_id")
                    .withPivotColumn("name")
                    .withColumn("ucas_id")
                    .withColumn("facebook_id")
                    .withColumn("enabled")
                    .withExtractor(ImportedInstitutionExtractor.class),
            new PrismImportedEntityMappingInsertDefinition()
                    .withMappingClass(ImportedInstitutionMapping.class)
                    .withTable("imported_institution_mapping"),
            new String[] { "application_qualification.institution_id" }, false),
    IMPORTED_LANGUAGE_QUALIFICATION_TYPE(new PrismImportedEntityImportDefinition()
            .withEntityClass(ImportedLanguageQualificationType.class)
            .withTransformerClass(ImportedLanguageQualificationTypeTransformer.class),
            new PrismImportedEntityImportInsertDefinition()
                    .withTable("imported_language_qualification_type")
                    .withPivotColumn("name")
                    .withColumn("minimum_overall_score")
                    .withColumn("maximum_overall_score")
                    .withColumn("minimum_reading_score")
                    .withColumn("maximum_reading_score")
                    .withColumn("minimum_writing_score")
                    .withColumn("maximum_writing_score")
                    .withColumn("minimum_speaking_score")
                    .withColumn("maximum_speaking_score")
                    .withColumn("minimum_listening_score")
                    .withColumn("maximum_listening_score")
                    .withColumn("enabled")
                    .withExtractor(ImportedLanguageQualificationTypeExtractor.class),
            new PrismImportedEntityMappingInsertDefinition()
                    .withMappingClass(ImportedLanguageQualificationTypeMapping.class)
                    .withTable("imported_language_qualification_type_mapping"),
            null, true),
    IMPORTED_NATIONALITY(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            getImportedEntitySimpleMappingInsertDefinition(),
            new String[] { "application_personal_detail.nationality_id1", "application_personal_detail.nationality_id2" }, true),
    IMPORTED_OPPORTUNITY_TYPE(getImportedEntitySimpleImportDefinition()
            .withEntityNameClass(PrismOpportunityType.class),
            getImportedEntitySimpleImportInsertDefinition(),
            getImportedEntitySimpleMappingInsertDefinition(),
            new String[] { "application_program_detail.opportunity_type_id" }, true),
    @SuppressWarnings("unchecked")
    // TODO: add as chart filter
    IMPORTED_SUBJECT_AREA(new PrismImportedEntityImportDefinition()
            .withEntityClass(ImportedSubjectArea.class)
            .withSystemRequestClass(ImportedSubjectAreaImportDTO.class)
            .withTransformerClass(ImportedSubjectAreaTransformer.class),
            new PrismImportedEntityImportInsertDefinition()
                    .withTable("imported_subject_area")
                    .withColumn("id")
                    .withColumn("jacs_code")
                    .withColumn("jacs_code_old")
                    .withPivotColumn("name")
                    .withColumn("description")
                    .withColumn("ucas_subject")
                    .withColumn("parent_imported_subject_area_id")
                    .withColumn("enabled")
                    .withExtractor((Class<? extends ImportedEntityExtractor<?>>) ImportedSubjectAreaExtractor.class),
            new PrismImportedEntityMappingInsertDefinition()
                    .withMappingClass(ImportedSubjectAreaMapping.class)
                    .withTable("imported_subject_area_mapping"),
            null, true),
    // TODO: add as chart filter
    IMPORTED_PROGRAM(new PrismImportedEntityImportDefinition()
            .withEntityClass(ImportedProgram.class)
            .withSystemRequestClass(ImportedProgramImportDTO.class)
            .withTransformerClass(ImportedProgramTransformer.class),
            new PrismImportedEntityImportInsertDefinition()
                    .withTable("imported_program")
                    .withPivotColumn("imported_institution_id")
                    .withColumn("imported_qualification_type_id")
                    .withColumn("level")
                    .withColumn("qualification")
                    .withPivotColumn("name")
                    .withColumn("code")
                    .withColumn("enabled")
                    .withExtractor(ImportedProgramExtractor.class),
            new PrismImportedEntityMappingInsertDefinition()
                    .withMappingClass(ImportedProgramMapping.class)
                    .withTable("imported_program_mapping"),
            null, false),
    IMPORTED_QUALIFICATION_TYPE(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            getImportedEntitySimpleMappingInsertDefinition(),
            null, true),
    IMPORTED_REFERRAL_SOURCE(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            getImportedEntitySimpleMappingInsertDefinition(),
            new String[] { "application_program_detail.referral_source_id" }, true),
    IMPORTED_REJECTION_REASON(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            getImportedEntitySimpleMappingInsertDefinition(),
            null, true),
    IMPORTED_STUDY_OPTION(getImportedEntitySimpleImportDefinition()
            .withEntityNameClass(PrismStudyOption.class),
            getImportedEntitySimpleImportInsertDefinition(),
            getImportedEntitySimpleMappingInsertDefinition(),
            new String[] { "application_program_detail.study_option_id" }, true),
    IMPORTED_TITLE(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            getImportedEntitySimpleMappingInsertDefinition(),
            null, true);

    private static final List<PrismImportedEntity> prefetchEntities = Lists.newLinkedList();
    private static final List<PrismImportedEntity> resourceReportFilterProperties = Lists.newLinkedList();

    static {
        for (PrismImportedEntity entity : values()) {
            if (entity.isPrefetchImport()) {
                prefetchEntities.add(entity);
            }

            if (entity.getReportDefinition() != null) {
                resourceReportFilterProperties.add(entity);
            }
        }
    }

    private PrismImportedEntityImportDefinition importDefinition;

    private PrismImportedEntityImportInsertDefinition importInsertDefinition;

    private PrismImportedEntityMappingInsertDefinition mappingInsertDefinition;

    private String[] reportDefinition;

    private boolean prefetchImport;

    PrismImportedEntity(PrismImportedEntityImportDefinition importDefinition, PrismImportedEntityImportInsertDefinition importInsertDefinition,
            PrismImportedEntityMappingInsertDefinition mappingInsertDefinition, String[] reportDefinition, boolean prefetchImport) {
        this.importDefinition = importDefinition;
        this.importInsertDefinition = importInsertDefinition;
        this.mappingInsertDefinition = mappingInsertDefinition;
        this.reportDefinition = reportDefinition;
        this.prefetchImport = prefetchImport;
    }

    public static List<PrismImportedEntity> getPrefetchEntities() {
        return prefetchEntities;
    }

    public static List<PrismImportedEntity> getResourceReportFilterProperties() {
        return resourceReportFilterProperties;
    }

    private static PrismImportedEntityImportDefinition getImportedEntitySimpleImportDefinition() {
        return new PrismImportedEntityImportDefinition()
                .withEntityClass(ImportedEntitySimple.class);
    }

    private static PrismImportedEntityImportInsertDefinition getImportedEntitySimpleImportInsertDefinition() {
        return new PrismImportedEntityImportInsertDefinition()
                .withTable("imported_entity")
                .withPivotColumn("imported_entity_type")
                .withPivotColumn("name")
                .withColumn("enabled")
                .withExtractor(ImportedEntitySimpleExtractor.class);
    }

    private static PrismImportedEntityMappingInsertDefinition getImportedEntitySimpleMappingInsertDefinition() {
        return new PrismImportedEntityMappingInsertDefinition()
                .withMappingClass(ImportedEntitySimpleMapping.class)
                .withTable("imported_entity_mapping");
    }

    @Override
    public uk.co.alumeni.prism.enums.PrismImportedEntity getDefinition() {
        return uk.co.alumeni.prism.enums.PrismImportedEntity.valueOf(name());
    }

    public Class<? extends ImportedEntityRequest> getRequestClass() {
        return getDefinition().getRequestClass();
    }

    @SuppressWarnings("unchecked")
    public Class<? extends ImportedEntityRequest> getSystemRequestClass() {
        return ObjectUtils.firstNonNull(importDefinition.getSystemRequestClass(), getRequestClass());
    }

    public Class<? extends ImportedEntity<?, ?>> getEntityClass() {
        return importDefinition.getEntityClass();
    }

    public Class<? extends PrismLocalizableDefinition> getEntityClassName() {
        return importDefinition.getEntityNameClass();
    }

    public Class<? extends ImportedEntityMapping<?>> getMappingClass() {
        return mappingInsertDefinition.getMappingClass();
    }

    public Class<? extends ImportedEntityTransformer<? extends ImportedEntityRequest, ? extends ImportedEntity<?, ?>>> getTransformerClass() {
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

    public String[] getReportDefinition() {
        return reportDefinition;
    }

    public boolean isPrefetchImport() {
        return prefetchImport;
    }

    public String getLowerCamelName() {
        return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
    }

    public String getUpperCamelName() {
        return UPPER_UNDERSCORE.to(UPPER_CAMEL, name());
    }

    public String getEntityClassUpperCamelName() {
        return getEntityClass().getSimpleName();
    }

    private static class PrismImportedEntityImportDefinition {

        private Class<? extends ImportedEntity<?, ?>> entityClass;

        private Class<? extends PrismLocalizableDefinition> entityNameClass;

        private Class<? extends ImportedEntityRequest> systemRequestClass;

        private Class<? extends ImportedEntityTransformer<? extends ImportedEntityRequest, ? extends ImportedEntity<?, ?>>> transformerClass;

        public Class<? extends ImportedEntity<?, ?>> getEntityClass() {
            return entityClass;
        }

        public Class<? extends PrismLocalizableDefinition> getEntityNameClass() {
            return entityNameClass;
        }

        public Class<? extends ImportedEntityRequest> getSystemRequestClass() {
            return systemRequestClass;
        }

        public Class<? extends ImportedEntityTransformer<? extends ImportedEntityRequest, ? extends ImportedEntity<?, ?>>> getTransformerClass() {
            return transformerClass;
        }

        public PrismImportedEntityImportDefinition withEntityClass(Class<? extends ImportedEntity<?, ?>> entityClass) {
            this.entityClass = entityClass;
            return this;
        }

        public PrismImportedEntityImportDefinition withEntityNameClass(Class<? extends PrismLocalizableDefinition> entityNameClass) {
            this.entityNameClass = entityNameClass;
            return this;
        }

        public PrismImportedEntityImportDefinition withSystemRequestClass(Class<? extends ImportedEntityRequest> systemRequestClass) {
            this.systemRequestClass = systemRequestClass;
            return this;
        }

        public PrismImportedEntityImportDefinition withTransformerClass(
                Class<? extends ImportedEntityTransformer<? extends ImportedEntityRequest, ? extends ImportedEntity<?, ?>>> transformerClass) {
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
            return columns.stream()
                    .filter(column -> !pivotColumns.contains(column))
                    .map(column -> column + " = values(" + column + ")")
                    .collect(Collectors.joining(", "));
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

}
