package com.zuehlke.pgadmissions.domain.imported;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_LANGUAGE_QUALIFICATION_TYPE;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedLanguageQualificationTypeMapping;

import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;
import uk.co.alumeni.prism.api.model.imported.ImportedLanguageQualificationTypeDefinition;

@Entity
@Table(name = "imported_language_qualification_type")
public class ImportedLanguageQualificationType extends ImportedEntity<Integer, ImportedLanguageQualificationTypeMapping> implements
        ImportedLanguageQualificationTypeDefinition, ImportedEntityResponseDefinition<Integer> {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "minimum_overall_score")
    private BigDecimal minimumOverallScore;

    @Column(name = "maximum_overall_score")
    private BigDecimal maximumOverallScore;

    @Column(name = "minimum_reading_score")
    private BigDecimal minimumReadingScore;

    @Column(name = "maximum_reading_score")
    private BigDecimal maximumReadingScore;

    @Column(name = "minimum_writing_score")
    private BigDecimal minimumWritingScore;

    @Column(name = "maximum_writing_score")
    private BigDecimal maximumWritingScore;

    @Column(name = "minimum_speaking_score")
    private BigDecimal minimumSpeakingScore;

    @Column(name = "maximum_speaking_score")
    private BigDecimal maximumSpeakingScore;

    @Column(name = "minimum_listening_score")
    private BigDecimal minimumListeningScore;

    @Column(name = "maximum_listening_score")
    private BigDecimal maximumListeningScore;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToMany(mappedBy = "importedEntity")
    private Set<ImportedLanguageQualificationTypeMapping> mappings = Sets.newHashSet();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public PrismImportedEntity getType() {
        return IMPORTED_LANGUAGE_QUALIFICATION_TYPE;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public BigDecimal getMinimumOverallScore() {
        return minimumOverallScore;
    }

    @Override
    public void setMinimumOverallScore(BigDecimal minimumOverallScore) {
        this.minimumOverallScore = minimumOverallScore;
    }

    @Override
    public BigDecimal getMaximumOverallScore() {
        return maximumOverallScore;
    }

    @Override
    public void setMaximumOverallScore(BigDecimal maximumOverallScore) {
        this.maximumOverallScore = maximumOverallScore;
    }

    @Override
    public BigDecimal getMinimumReadingScore() {
        return minimumReadingScore;
    }

    @Override
    public void setMinimumReadingScore(BigDecimal minimumReadingScore) {
        this.minimumReadingScore = minimumReadingScore;
    }

    @Override
    public BigDecimal getMaximumReadingScore() {
        return maximumReadingScore;
    }

    @Override
    public void setMaximumReadingScore(BigDecimal maximumReadingScore) {
        this.maximumReadingScore = maximumReadingScore;
    }

    @Override
    public BigDecimal getMinimumWritingScore() {
        return minimumWritingScore;
    }

    @Override
    public void setMinimumWritingScore(BigDecimal minimumWritingScore) {
        this.minimumWritingScore = minimumWritingScore;
    }

    @Override
    public BigDecimal getMaximumWritingScore() {
        return maximumWritingScore;
    }

    @Override
    public void setMaximumWritingScore(BigDecimal maximumWritingScore) {
        this.maximumWritingScore = maximumWritingScore;
    }

    @Override
    public BigDecimal getMinimumSpeakingScore() {
        return minimumSpeakingScore;
    }

    @Override
    public void setMinimumSpeakingScore(BigDecimal minimumSpeakingScore) {
        this.minimumSpeakingScore = minimumSpeakingScore;
    }

    @Override
    public BigDecimal getMaximumSpeakingScore() {
        return maximumSpeakingScore;
    }

    @Override
    public void setMaximumSpeakingScore(BigDecimal maximumSpeakingScore) {
        this.maximumSpeakingScore = maximumSpeakingScore;
    }

    @Override
    public BigDecimal getMinimumListeningScore() {
        return minimumListeningScore;
    }

    @Override
    public void setMinimumListeningScore(BigDecimal minimumListeningScore) {
        this.minimumListeningScore = minimumListeningScore;
    }

    @Override
    public BigDecimal getMaximumListeningScore() {
        return maximumListeningScore;
    }

    @Override
    public void setMaximumListeningScore(BigDecimal maximumListeningScore) {
        this.maximumListeningScore = maximumListeningScore;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Set<ImportedLanguageQualificationTypeMapping> getMappings() {
        return mappings;
    }

    public ImportedLanguageQualificationType withName(String name) {
        this.name = name;
        return this;
    }

    public ImportedLanguageQualificationType withMinimumOverallScore(BigDecimal minimumOverallScore) {
        this.minimumOverallScore = minimumOverallScore;
        return this;
    }

    public ImportedLanguageQualificationType withMaximumOverallScore(BigDecimal maximumOverallScore) {
        this.maximumOverallScore = maximumOverallScore;
        return this;
    }

    public ImportedLanguageQualificationType withMinimumReadingScore(BigDecimal minimumReadingScore) {
        this.minimumReadingScore = minimumReadingScore;
        return this;
    }

    public ImportedLanguageQualificationType withMaximumReadingScore(BigDecimal maximumReadingScore) {
        this.maximumReadingScore = maximumReadingScore;
        return this;
    }

    public ImportedLanguageQualificationType withMinimumWritingScore(BigDecimal minimumWritingScore) {
        this.minimumWritingScore = minimumWritingScore;
        return this;
    }

    public ImportedLanguageQualificationType withMaximumWritingScore(BigDecimal maximumWritingScore) {
        this.maximumWritingScore = maximumWritingScore;
        return this;
    }

    public ImportedLanguageQualificationType withMinimumSpeakingScore(BigDecimal minimumSpeakingScore) {
        this.minimumSpeakingScore = minimumSpeakingScore;
        return this;
    }

    public ImportedLanguageQualificationType withMaximumSpeakingScore(BigDecimal maximumSpeakingScore) {
        this.maximumSpeakingScore = maximumSpeakingScore;
        return this;
    }

    public ImportedLanguageQualificationType withMinimumListeningScore(BigDecimal minimumListeningScore) {
        this.minimumListeningScore = minimumListeningScore;
        return this;
    }

    public ImportedLanguageQualificationType withMaximumListeningScore(BigDecimal maximumListeningScore) {
        this.maximumListeningScore = maximumListeningScore;
        return this;
    }

    public ImportedLanguageQualificationType withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

}
