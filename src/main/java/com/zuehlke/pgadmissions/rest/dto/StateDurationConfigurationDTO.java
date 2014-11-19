package com.zuehlke.pgadmissions.rest.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDuration;

public class StateDurationConfigurationDTO extends ArrayList<StateDurationConfigurationDTO.StateDurationConfigurationValueDTO> {

    public static class StateDurationConfigurationValueDTO extends WorkflowConfigurationDTO {

        private PrismStateDuration definitionId;

        private Integer duration;

        @Override
        public PrismStateDuration getDefinitionId() {
            return definitionId;
        }

        public void setDefinitionId(PrismStateDuration definitionId) {
            this.definitionId = definitionId;
        }

        public final Integer getDuration() {
            return duration;
        }

        public final void setDuration(Integer duration) {
            this.duration = duration;
        }

    }

}
