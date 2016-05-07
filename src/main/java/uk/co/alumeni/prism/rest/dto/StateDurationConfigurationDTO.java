package uk.co.alumeni.prism.rest.dto;

import java.util.ArrayList;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition;
import uk.co.alumeni.prism.rest.dto.StateDurationConfigurationDTO.StateDurationConfigurationValueDTO;

public class StateDurationConfigurationDTO extends ArrayList<StateDurationConfigurationValueDTO> {

    private static final long serialVersionUID = -6908980030567163970L;

    public static class StateDurationConfigurationValueDTO extends WorkflowConfigurationDTO {

        private PrismStateDurationDefinition definitionId;

        private Integer duration;

        @Override
        public PrismStateDurationDefinition getDefinitionId() {
            return definitionId;
        }

        public void setDefinitionId(PrismStateDurationDefinition definitionId) {
            this.definitionId = definitionId;
        }

        public final Integer getDuration() {
            return duration;
        }

        public final void setDuration(Integer duration) {
            this.duration = duration;
        }

        public StateDurationConfigurationValueDTO withDefinitionId(PrismStateDurationDefinition definitionId) {
            this.definitionId = definitionId;
            return this;
        }

        public StateDurationConfigurationDTO.StateDurationConfigurationValueDTO withDuration(Integer duration) {
            this.duration = duration;
            return this;
        }

    }

}
