package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import com.google.common.collect.Lists;

public class DisplayPropertyConfigurationDTO extends WorkflowConfigurationGroupDTO {

    List<DisplayPropertyConfigurationValueDTO> values = Lists.newArrayList();

    @Override
    public final List<DisplayPropertyConfigurationValueDTO> getValues() {
        return values;
    }

    public final void setValues(List<DisplayPropertyConfigurationValueDTO> values) {
        this.values = values;
    }

    public static class DisplayPropertyConfigurationValueDTO extends WorkflowConfigurationDTO {

        private String value;

        public final String getValue() {
            return value;
        }

        public final void setValue(String value) {
            this.value = value;
        }

    }
}
