package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.WeightedRelation;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "imported_institution_subject_area", uniqueConstraints = {@UniqueConstraint(columnNames = {"imported_institution_id",
        "imported_subject_area_id", "concentration_factor", "proliferation_factor"})})
public class ImportedInstitutionSubjectArea extends WeightedRelation implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "imported_institution_id", nullable = false)
    private ImportedInstitution institution;

    @ManyToOne
    @JoinColumn(name = "imported_subject_area_id", nullable = false)
    private ImportedSubjectArea subjectArea;

    @Column(name = "concentration_factor", nullable = false)
    private Integer concentrationFactor;

    @Column(name = "proliferation_factor", nullable = false)
    private BigDecimal proliferationFactor;

    @Column(name = "relation_strength", nullable = false)
    private BigDecimal relationStrength;

    @Column(name = "tariff_bands_1_79")
    private Integer tariffBands_1_79;
    @Column(name = "tariff_bands_80_119")
    private Integer tariffBands_80_119;
    @Column(name = "tariff_bands_120_179")
    private Integer tariffBands_120_179;
    @Column(name = "tariff_bands_180_239")
    private Integer tariffBands_180_239;
    @Column(name = "tariff_bands_240_299")
    private Integer tariffBands_240_299;
    @Column(name = "tariff_bands_300_359")
    private Integer tariffBands_300_359;
    @Column(name = "tariff_bands_360_419")
    private Integer tariffBands_360_419;
    @Column(name = "tariff_bands_420_479")
    private Integer tariffBands_420_479;
    @Column(name = "tariff_bands_480_539")
    private Integer tariffBands_480_539;
    @Column(name = "tariff_bands_540_over")
    private Integer tariffBands_540_over;
    @Column(name = "tariff_bands_unknown")
    private Integer tariffBandsUnknown;
    @Column(name = "tariff_bands_not_applicable")
    private Integer tariffBandsNotApplicable;
    @Column(name = "honours_first_class")
    private Integer honoursFirstClass;
    @Column(name = "honours_upper_second_class")
    private Integer honoursUpperSecondClass;
    @Column(name = "honours_lower_second_class")
    private Integer honoursLowerSecondClass;
    @Column(name = "honours_third_class")
    private Integer honoursThirdClass;
    @Column(name = "honours_unclassified")
    private Integer honoursUnclassified;
    @Column(name = "honours_classification_not_applicable")
    private Integer honoursClassificationNotApplicable;
    @Column(name = "honours_not_applicable")
    private Integer honoursNotApplicable;
    @Column(name = "study_full_time")
    private Integer studyFullTime;
    @Column(name = "study_part_time")
    private Integer studyPartTime;
    @Column(name = "course_count")
    private Integer courseCount;
    @Column(name = "fpe")
    private Integer fpe;


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

    public ImportedInstitution getInstitution() {
        return institution;
    }

    public void setInstitution(ImportedInstitution institution) {
        this.institution = institution;
    }

    public ImportedSubjectArea getSubjectArea() {
        return subjectArea;
    }

    public void setSubjectArea(ImportedSubjectArea subjectArea) {
        this.subjectArea = subjectArea;
    }

    public Integer getConcentrationFactor() {
        return concentrationFactor;
    }

    public void setConcentrationFactor(Integer concentrationFactor) {
        this.concentrationFactor = concentrationFactor;
    }

    public BigDecimal getProliferationFactor() {
        return proliferationFactor;
    }

    public void setProliferationFactor(BigDecimal proliferationFactor) {
        this.proliferationFactor = proliferationFactor;
    }

    @Override
    public BigDecimal getRelationStrength() {
        return relationStrength;
    }

    @Override
    public void setRelationStrength(BigDecimal relationStrength) {
        this.relationStrength = relationStrength;
    }

    public Integer getTariffBands_1_79() {
        return tariffBands_1_79;
    }

    public void setTariffBands_1_79(Integer tariffBands_1_79) {
        this.tariffBands_1_79 = tariffBands_1_79;
    }

    public Integer getTariffBands_80_119() {
        return tariffBands_80_119;
    }

    public void setTariffBands_80_119(Integer tariffBands_80_119) {
        this.tariffBands_80_119 = tariffBands_80_119;
    }

    public Integer getTariffBands_120_179() {
        return tariffBands_120_179;
    }

    public void setTariffBands_120_179(Integer tariffBands_120_179) {
        this.tariffBands_120_179 = tariffBands_120_179;
    }

    public Integer getTariffBands_180_239() {
        return tariffBands_180_239;
    }

    public void setTariffBands_180_239(Integer tariffBands_180_239) {
        this.tariffBands_180_239 = tariffBands_180_239;
    }

    public Integer getTariffBands_240_299() {
        return tariffBands_240_299;
    }

    public void setTariffBands_240_299(Integer tariffBands_240_299) {
        this.tariffBands_240_299 = tariffBands_240_299;
    }

    public Integer getTariffBands_300_359() {
        return tariffBands_300_359;
    }

    public void setTariffBands_300_359(Integer tariffBands_300_359) {
        this.tariffBands_300_359 = tariffBands_300_359;
    }

    public Integer getTariffBands_360_419() {
        return tariffBands_360_419;
    }

    public void setTariffBands_360_419(Integer tariffBands_360_419) {
        this.tariffBands_360_419 = tariffBands_360_419;
    }

    public Integer getTariffBands_420_479() {
        return tariffBands_420_479;
    }

    public void setTariffBands_420_479(Integer tariffBands_420_479) {
        this.tariffBands_420_479 = tariffBands_420_479;
    }

    public Integer getTariffBands_480_539() {
        return tariffBands_480_539;
    }

    public void setTariffBands_480_539(Integer tariffBands_480_539) {
        this.tariffBands_480_539 = tariffBands_480_539;
    }

    public Integer getTariffBands_540_over() {
        return tariffBands_540_over;
    }

    public void setTariffBands_540_over(Integer tariffBands_540_over) {
        this.tariffBands_540_over = tariffBands_540_over;
    }

    public Integer getTariffBandsUnknown() {
        return tariffBandsUnknown;
    }

    public void setTariffBandsUnknown(Integer tariffBandsUnknown) {
        this.tariffBandsUnknown = tariffBandsUnknown;
    }

    public Integer getTariffBandsNotApplicable() {
        return tariffBandsNotApplicable;
    }

    public void setTariffBandsNotApplicable(Integer tariffBandsNotApplicable) {
        this.tariffBandsNotApplicable = tariffBandsNotApplicable;
    }

    public Integer getHonoursFirstClass() {
        return honoursFirstClass;
    }

    public void setHonoursFirstClass(Integer honoursFirstClass) {
        this.honoursFirstClass = honoursFirstClass;
    }

    public Integer getHonoursUpperSecondClass() {
        return honoursUpperSecondClass;
    }

    public void setHonoursUpperSecondClass(Integer honoursUpperSecondClass) {
        this.honoursUpperSecondClass = honoursUpperSecondClass;
    }

    public Integer getHonoursLowerSecondClass() {
        return honoursLowerSecondClass;
    }

    public void setHonoursLowerSecondClass(Integer honoursLowerSecondClass) {
        this.honoursLowerSecondClass = honoursLowerSecondClass;
    }

    public Integer getHonoursThirdClass() {
        return honoursThirdClass;
    }

    public void setHonoursThirdClass(Integer honoursThirdClass) {
        this.honoursThirdClass = honoursThirdClass;
    }

    public Integer getHonoursUnclassified() {
        return honoursUnclassified;
    }

    public void setHonoursUnclassified(Integer honoursUnclassified) {
        this.honoursUnclassified = honoursUnclassified;
    }

    public Integer getHonoursClassificationNotApplicable() {
        return honoursClassificationNotApplicable;
    }

    public void setHonoursClassificationNotApplicable(Integer honoursClassificationNotApplicable) {
        this.honoursClassificationNotApplicable = honoursClassificationNotApplicable;
    }

    public Integer getHonoursNotApplicable() {
        return honoursNotApplicable;
    }

    public void setHonoursNotApplicable(Integer honoursNotApplicable) {
        this.honoursNotApplicable = honoursNotApplicable;
    }

    public Integer getStudyFullTime() {
        return studyFullTime;
    }

    public void setStudyFullTime(Integer studyFullTime) {
        this.studyFullTime = studyFullTime;
    }

    public Integer getStudyPartTime() {
        return studyPartTime;
    }

    public void setStudyPartTime(Integer studyPartTime) {
        this.studyPartTime = studyPartTime;
    }

    public Integer getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Integer courseCount) {
        this.courseCount = courseCount;
    }

    public Integer getFpe() {
        return fpe;
    }

    public void setFpe(Integer fpe) {
        this.fpe = fpe;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("institution", institution).addProperty("subjectArea", subjectArea)
                .addProperty("concentrationFactor", concentrationFactor).addProperty("proliferationFactor", proliferationFactor);
    }

}
