package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;

import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

@Entity
@Table(name = "THROTTLE")
public class Throttle implements Serializable {

    private static final long serialVersionUID = 6043305345938615563L;

    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    @Column(name = "enabled")
    private Boolean enabled;

    @NumberFormat
    @Min(0)
    @NotNull
    @Column(name = "batch_size")
    private Integer batchSize;

    @NumberFormat
    @Min(0)
    @NotNull
    @Column(name = "processing_delay")
    private Short processingDelay;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "processing_delay_unit")
    private DurationUnitEnum processingDelayUnit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Short getProcessingDelay() {
        return processingDelay;
    }

    public void setProcessingDelay(Short processingDelay) {
        this.processingDelay = processingDelay;
    }

    public DurationUnitEnum getProcessingDelayUnit() {
        return processingDelayUnit;
    }

    public void setProcessingDelayUnit(DurationUnitEnum processingDelayUnit) {
        this.processingDelayUnit = processingDelayUnit;
    }

}
