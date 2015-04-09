package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationDefinition;
import com.zuehlke.pgadmissions.rest.representation.workflow.StateDurationDefinitionRepresentation;

public class StateDurationDefinitionConverter extends DozerConverter<StateDurationDefinition, StateDurationDefinitionRepresentation> {

    public StateDurationDefinitionConverter() {
        super(StateDurationDefinition.class, StateDurationDefinitionRepresentation.class);
    }

    @Override
    public StateDurationDefinitionRepresentation convertTo(StateDurationDefinition source, StateDurationDefinitionRepresentation destination) {
        PrismConfiguration prismConfiguration = PrismConfiguration.STATE_DURATION;
        return source == null ? null : new StateDurationDefinitionRepresentation().withId(source.getId())
                .withMinimumPermitted(prismConfiguration.getMinimumPermitted()).withMaximumPermitted(prismConfiguration.getMaximumPermitted());
    }

    @Override
    public StateDurationDefinition convertFrom(StateDurationDefinitionRepresentation source, StateDurationDefinition destination) {
        throw new UnsupportedOperationException();
    }

}
