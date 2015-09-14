package com.zuehlke.pgadmissions.domain.definitions;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedAdvertDomicileExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedAgeRangeExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEntityExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEntitySimpleExtractor;
import com.zuehlke.pgadmissions.workflow.selectors.summary.ApplicationByImportedRejectionReasonSelector;
import com.zuehlke.pgadmissions.workflow.selectors.summary.PrismResourceSummarySelector;

import uk.co.alumeni.prism.api.model.advert.EnumDefinition;
import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

public enum PrismImportedEntity implements EnumDefinition<uk.co.alumeni.prism.enums.PrismImportedEntity> {

    // FIXME: mechanism to filter in charts based on department / program of origin
    IMPORTED_ADVERT_DOMICILE(new PrismImportedEntityImportDefinition()
            .withEntityClass(ImportedAdvertDomicile.class),
            new PrismImportedEntityImportInsertDefinition()
                    .withTable("imported_advert_domicile")
                    .withPivotColumn("id")
                    .withColumn("name")
                    .withColumn("currency")
                    .withColumn("enabled")
                    .withExtractor(ImportedAdvertDomicileExtractor.class),
            null, true),
    IMPORTED_AGE_RANGE(new PrismImportedEntityImportDefinition()
            .withEntityClass(ImportedAgeRange.class),
            new PrismImportedEntityImportInsertDefinition()
                    .withTable("imported_age_range")
                    .withPivotColumn("name")
                    .withColumn("lower_bound")
                    .withColumn("upper_bound")
                    .withColumn("enabled")
                    .withExtractor(ImportedAgeRangeExtractor.class),
            new PrismImportedEntityReportDefinition() //
                    .withColumns(new String[] { "application_personal_detail.imported_age_range_id" }),
            true),
    IMPORTED_DISABILITY(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            new PrismImportedEntityReportDefinition() //
                    .withColumns(new String[] { "application_personal_detail.imported_disability_id" }),
            true),
    IMPORTED_ETHNICITY(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            new PrismImportedEntityReportDefinition() //
                    .withColumns(new String[] { "application_personal_detail.imported_ethnicity_id" }),
            true),
    IMPORTED_GENDER(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            new PrismImportedEntityReportDefinition() //
                    .withColumns(new String[] { "application_personal_detail.imported_gender_id" }),
            true),
    IMPORTED_OPPORTUNITY_TYPE(getImportedEntitySimpleImportDefinition()
            .withEntityNameClass(PrismOpportunityType.class),
            getImportedEntitySimpleImportInsertDefinition(),
            new PrismImportedEntityReportDefinition() //
                    .withColumns(new String[] { "application_program_detail.imported_opportunity_type_id" }),
            true),
    IMPORTED_REJECTION_REASON(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            new PrismImportedEntityReportDefinition() //
                    .withColumns(new String[] { "application.id" })
                    .withSelector(ApplicationByImportedRejectionReasonSelector.class),
            true),
    IMPORTED_STUDY_OPTION(getImportedEntitySimpleImportDefinition()
            .withEntityNameClass(PrismStudyOption.class),
            getImportedEntitySimpleImportInsertDefinition(),
            new PrismImportedEntityReportDefinition() //
                    .withColumns(new String[] { "application_program_detail.imported_study_option_id" }),
            true),
    IMPORTED_TITLE(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition(),
            null, true);

    private static final List<PrismImportedEntity> prefetchEntities = Lists.newLinkedList();

    private static final List<PrismImportedEntity> resourceReportFilterProperties = Lists.newLinkedList();

    static {
        for (PrismImportedEntity entity : values()) {
            if (entity.isPrefetchImport()) {
                prefetchEntities.add(entity);
            }

            if (entity.getFilterColumns() != null) {
                resourceReportFilterProperties.add(entity);
            }
        }
    }

    private PrismImportedEntityImportDefinition importDefinition;

    private PrismImportedEntityImportInsertDefinition importInsertDefinition;

    private PrismImportedEntityReportDefinition reportDefinition;

    private boolean prefetchImport;

    PrismImportedEntity(PrismImportedEntityImportDefinition importDefinition, PrismImportedEntityImportInsertDefinition importInsertDefinition,
            PrismImportedEntityReportDefinition reportDefinition, boolean prefetchImport) {
        this.importDefinition = importDefinition;
        this.importInsertDefinition = importInsertDefinition;
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

    @Override
    public uk.co.alumeni.prism.enums.PrismImportedEntity getDefinition() {
        return uk.co.alumeni.prism.enums.PrismImportedEntity.valueOf(name());
    }

    public Class<? extends ImportedEntityRequest> getRequestClass() {
        return getDefinition().getRequestClass();
    }
    
    public Class<? extends ImportedEntity<?>> getEntityClass() {
        return importDefinition.getEntityClass();
    }

    public Class<? extends PrismLocalizableDefinition> getEntityClassName() {
        return importDefinition.getEntityNameClass();
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
    
    public String getMappingInsertColumns() {
        return "institution_id, " + importInsertDefinition.getTable() + "_id, code, enabled";
    }

    public String getMappingInsertOnDuplicateKeyUpdate() {
        return "enabled = values(enabled)";
    }

    public String[] getFilterColumns() {
        return reportDefinition == null ? null : reportDefinition.getColumns();
    }

    public Class<? extends PrismResourceSummarySelector> getFilterSelector() {
        return reportDefinition == null ? null : reportDefinition.getSelector();
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

        private Class<? extends ImportedEntity<?>> entityClass;

        private Class<? extends PrismLocalizableDefinition> entityNameClass;

        public Class<? extends ImportedEntity<?>> getEntityClass() {
            return entityClass;
        }

        public Class<? extends PrismLocalizableDefinition> getEntityNameClass() {
            return entityNameClass;
        }
        
        public PrismImportedEntityImportDefinition withEntityClass(Class<? extends ImportedEntity<?>> entityClass) {
            this.entityClass = entityClass;
            return this;
        }

        public PrismImportedEntityImportDefinition withEntityNameClass(Class<? extends PrismLocalizableDefinition> entityNameClass) {
            this.entityNameClass = entityNameClass;
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

    private static class PrismImportedEntityReportDefinition {

        private String[] columns;

        private Class<? extends PrismResourceSummarySelector> selector;

        public String[] getColumns() {
            return columns;
        }

        public Class<? extends PrismResourceSummarySelector> getSelector() {
            return selector;
        }

        public PrismImportedEntityReportDefinition withColumns(String[] columns) {
            this.columns = columns;
            return this;
        }

        public PrismImportedEntityReportDefinition withSelector(Class<? extends PrismResourceSummarySelector> selector) {
            this.selector = selector;
            return this;
        }

    }

}
