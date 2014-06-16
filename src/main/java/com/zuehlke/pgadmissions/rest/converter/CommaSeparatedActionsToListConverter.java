package com.zuehlke.pgadmissions.rest.converter;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.rest.domain.application.ActionRepresentation;
import org.dozer.DozerConverter;

import java.util.ArrayList;
import java.util.List;

public class CommaSeparatedActionsToListConverter extends DozerConverter<String, List<ActionRepresentation>> {

    public CommaSeparatedActionsToListConverter() {
        super(String.class, (Class) List.class);
    }

    @Override
    public List<ActionRepresentation> convertTo(String source, List<ActionRepresentation> destination) {
        ArrayList<String> actionStrings = Lists.newArrayList(source.split(","));
        List<ActionRepresentation> actions = Lists.newArrayListWithExpectedSize(actionStrings.size());
        for (String actionString : actionStrings) {
            String[] actionSplit = actionString.split("\\|");
            actions.add(new ActionRepresentation(PrismAction.valueOf(actionSplit[1]), Boolean.parseBoolean(actionSplit[0])));
        }
        return actions;
    }

    @Override
    public String convertFrom(List<ActionRepresentation> source, String destination) {
        throw new UnsupportedOperationException();
    }
}
