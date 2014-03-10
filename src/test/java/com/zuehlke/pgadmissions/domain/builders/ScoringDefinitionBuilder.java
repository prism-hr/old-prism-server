package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

public class ScoringDefinitionBuilder {

    private Integer id;
    private ScoringStage stage;
    private String content;

    public ScoringDefinitionBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ScoringDefinitionBuilder stage(ScoringStage stage) {

        this.stage = stage;
        return this;
    }

    public ScoringDefinitionBuilder content(String content) {
        this.content = content;
        return this;
    }

    public ScoringDefinition build() {
        ScoringDefinition scoringDefinition = new ScoringDefinition();
        scoringDefinition.setId(id);
        scoringDefinition.setStage(stage);
        scoringDefinition.setContent(content);
        return scoringDefinition;
    }
}
