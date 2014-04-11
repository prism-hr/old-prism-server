package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Throttle;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class ThrottleBuilder {

    private Integer id;
    private Boolean enabled;
    private Integer batchSize;
    private Short processingDelay;
    private DurationUnitEnum processingDelayUnit;

    public ThrottleBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ThrottleBuilder enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public ThrottleBuilder batchSize(Integer batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public ThrottleBuilder processingDelay(Short processingDelay) {
        this.processingDelay = processingDelay;
        return this;
    }
    public ThrottleBuilder processingDelayUnit(DurationUnitEnum processingDelayUnit) {
        this.processingDelayUnit = processingDelayUnit;
        return this;
    }

    public Throttle build() {
        Throttle throttle = new Throttle();
        throttle.setId(this.id);
        throttle.setEnabled(this.enabled);
        throttle.setBatchSize(this.batchSize);
        throttle.setProcessingDelay(processingDelay);
        throttle.setProcessingDelayUnit(processingDelayUnit);
        return throttle;
    }

}
