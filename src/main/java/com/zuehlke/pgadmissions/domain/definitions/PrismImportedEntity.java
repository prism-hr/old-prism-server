package com.zuehlke.pgadmissions.domain.definitions;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedAgeRangeExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedDomicileExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEntityExtractor;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEntitySimpleExtractor;

import uk.co.alumeni.prism.api.model.advert.EnumDefinition;
import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

public enum PrismImportedEntity implements EnumDefinition<uk.co.alumeni.prism.enums.PrismImportedEntity> {

    IMPORTED_DOMICILE(new PrismImportedEntityImportDefinition()
            .withEntityClass(ImportedDomicile.class),
            new PrismImportedEntityImportInsertDefinition()
                .withTable("imported_domicile")
                .withPivotColumn("id")
                .withColumn("name")
                .withColumn("currency")
                .withColumn("enabled")
                .withExtractor(ImportedDomicileExtractor.class)), //
    IMPORTED_AGE_RANGE(new PrismImportedEntityImportDefinition()
            .withEntityClass(ImportedAgeRange.class),
            new PrismImportedEntityImportInsertDefinition()
                    .withTable("imported_age_range")
                    .withPivotColumn("name")
                    .withColumn("lower_bound")
                    .withColumn("upper_bound")
                    .withColumn("enabled")
                    .withExtractor(ImportedAgeRangeExtractor.class)), //
    IMPORTED_DISABILITY(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition()), //
    IMPORTED_ETHNICITY(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition()), //
    IMPORTED_GENDER(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition()), //
    IMPORTED_OPPORTUNITY_TYPE(getImportedEntitySimpleImportDefinition()
            .withEntityNameClass(PrismOpportunityType.class),
            getImportedEntitySimpleImportInsertDefinition()), //
    IMPORTED_REJECTION_REASON(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition()), //
    IMPORTED_STUDY_OPTION(getImportedEntitySimpleImportDefinition()
            .withEntityNameClass(PrismStudyOption.class),
            getImportedEntitySimpleImportInsertDefinition()), 
    IMPORTED_TITLE(getImportedEntitySimpleImportDefinition(),
            getImportedEntitySimpleImportInsertDefinition());

    private PrismImportedEntityImportDefinition importDefinition;

    private PrismImportedEntityImportInsertDefinition importInsertDefinition;

    PrismImportedEntity(PrismImportedEntityImportDefinition importDefinition, PrismImportedEntityImportInsertDefinition importInsertDefinition) {
        this.importDefinition = importDefinition;
        this.importInsertDefinition = importInsertDefinition;
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

}
