package com.zuehlke.pgadmissions.rest.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;

public class DisplayPropertyConfigurationDTO extends ArrayList<DisplayPropertyConfigurationDTO.DisplayPropertyConfigurationValueDTO> {

    public static class DisplayPropertyConfigurationValueDTO extends WorkflowConfigurationDTO {

        private PrismDisplayProperty definitionId;

        private String value;

        @Override
        public PrismDisplayProperty getDefinitionId() {
            return definitionId;
        }

        public void setDefinitionId(PrismDisplayProperty definitionId) {
            this.definitionId = definitionId;
        }

        public final String getValue() {
            return value;
        }

        public final void setValue(String value) {
            this.value = value;
        }

    }
}
