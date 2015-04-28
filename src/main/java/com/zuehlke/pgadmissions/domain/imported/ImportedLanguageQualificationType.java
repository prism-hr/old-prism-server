package com.zuehlke.pgadmissions.domain.imported;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.LANGUAGE_QUALIFICATION_TYPE;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@Table(name = "IMPORTED_LANGUAGE_QUALIFICATION_TYPE", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "code" }) })
public class ImportedLanguageQualificationType extends ImportedEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "code", nullable = false)
    private String code;

    @Lob
    @Column(name = "name", nullable = false)
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

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Institution getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public PrismImportedEntity getType() {
        return LANGUAGE_QUALIFICATION_TYPE;
    }
    
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMinimumOverallScore() {
        return minimumOverallScore;
    }

    public void setMinimumOverallScore(BigDecimal minimumOverallScore) {
        this.minimumOverallScore = minimumOverallScore;
    }

    public BigDecimal getMaximumOverallScore() {
        return maximumOverallScore;
    }

    public void setMaximumOverallScore(BigDecimal maximumOverallScore) {
        this.maximumOverallScore = maximumOverallScore;
    }

    public BigDecimal getMinimumReadingScore() {
        return minimumReadingScore;
    }

    public void setMinimumReadingScore(BigDecimal minimumReadingScore) {
        this.minimumReadingScore = minimumReadingScore;
    }

    public BigDecimal getMaximumReadingScore() {
        return maximumReadingScore;
    }

    public void setMaximumReadingScore(BigDecimal maximumReadingScore) {
        this.maximumReadingScore = maximumReadingScore;
    }

    public BigDecimal getMinimumWritingScore() {
        return minimumWritingScore;
    }

    public void setMinimumWritingScore(BigDecimal minimumWritingScore) {
        this.minimumWritingScore = minimumWritingScore;
    }

    public BigDecimal getMaximumWritingScore() {
        return maximumWritingScore;
    }

    public void setMaximumWritingScore(BigDecimal maximumWritingScore) {
        this.maximumWritingScore = maximumWritingScore;
    }

    public BigDecimal getMinimumSpeakingScore() {
        return minimumSpeakingScore;
    }

    public void setMinimumSpeakingScore(BigDecimal minimumSpeakingScore) {
        this.minimumSpeakingScore = minimumSpeakingScore;
    }

    public BigDecimal getMaximumSpeakingScore() {
        return maximumSpeakingScore;
    }

    public void setMaximumSpeakingScore(BigDecimal maximumSpeakingScore) {
        this.maximumSpeakingScore = maximumSpeakingScore;
    }

    public BigDecimal getMinimumListeningScore() {
        return minimumListeningScore;
    }

    public void setMinimumListeningScore(BigDecimal minimumListeningScore) {
        this.minimumListeningScore = minimumListeningScore;
    }

    public BigDecimal getMaximumListeningScore() {
        return maximumListeningScore;
    }

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

    public ImportedLanguageQualificationType withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public ImportedLanguageQualificationType withCode(String code) {
        this.code = code;
        return this;
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
