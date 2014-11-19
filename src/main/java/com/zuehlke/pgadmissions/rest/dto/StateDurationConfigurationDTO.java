package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import com.google.common.collect.Lists;

public class StateDurationConfigurationDTO extends WorkflowConfigurationGroupDTO {

    private List<StateDurationConfigurationValueDTO> values = Lists.newArrayList();

    @Override
    public final List<StateDurationConfigurationValueDTO> getValues() {
        return values;
    }

    public final void setValues(List<StateDurationConfigurationValueDTO> values) {
        this.values = values;
    }

    public static class StateDurationConfigurationValueDTO extends WorkflowConfigurationDTO {

        private Integer duration;

        public final Integer getDuration() {
            return duration;
        }

        public final void setDuration(Integer duration) {
            this.duration = duration;
        }

    }

}
