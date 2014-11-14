package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class WorkflowPropertyConfigurationDTO {

    @NotNull
    private Boolean enabled;
    
    @Min(0)
    @Max(999)
    private Integer minimum;

    @Min(0)
    @Max(999)
    private Integer maximum;

    public final Boolean getEnabled() {
        return enabled;
    }

    public final void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public final Integer getMinimum() {
        return minimum;
    }

    public final void setMinimum(Integer minimum) {
        this.minimum = minimum;
    }

    public final Integer getMaximum() {
        return maximum;
    }

    public final void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }
    
}
