package com.zuehlke.pgadmissions.rest.dto;

import java.util.ArrayList;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO.DisplayPropertyConfigurationValueDTO;

public class DisplayPropertyConfigurationDTO extends ArrayList<DisplayPropertyConfigurationValueDTO> {

    private static final long serialVersionUID = 5008263877226224685L;

    public static class DisplayPropertyConfigurationValueDTO extends WorkflowConfigurationDTO {

        private PrismDisplayPropertyDefinition definitionId;

        private String value;

        @Override
        public PrismDisplayPropertyDefinition getDefinitionId() {
            return definitionId;
        }

        public void setDefinitionId(PrismDisplayPropertyDefinition definitionId) {
            this.definitionId = definitionId;
        }

        public final String getValue() {
            return value;
        }

        public final void setValue(String value) {
            this.value = value;
        }
        
        public DisplayPropertyConfigurationDTO.DisplayPropertyConfigurationValueDTO withDefinitionId(PrismDisplayPropertyDefinition definitionId) {
            this.definitionId = definitionId;
            return this;
        }
        
        public DisplayPropertyConfigurationDTO.DisplayPropertyConfigurationValueDTO withValue(String value) {
            this.value = value;
            return this;
        }

    }
    
}
