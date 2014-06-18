package com.zuehlke.pgadmissions.rest.converter;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.rest.domain.application.ActionRepresentation;
import org.dozer.DozerConverter;

import java.util.ArrayList;
import java.util.List;

public class CommaSeparatedActionsToListConverter extends DozerConverter<String, List<PrismAction>> {

    public CommaSeparatedActionsToListConverter() {
        super(String.class, (Class) List.class);
    }

    @Override
    public List<PrismAction> convertTo(String source, List<PrismAction> destination) {
        ArrayList<String> actionStrings = Lists.newArrayList(source.split(","));
        List<PrismAction> actions = Lists.newArrayListWithExpectedSize(actionStrings.size());
        for (String actionString : actionStrings) {
            String[] actionSplit = actionString.split("\\|");
            actions.add(PrismAction.valueOf(actionSplit[1]));
        }
        return actions;
    }

    @Override
    public String convertFrom(List<PrismAction> source, String destination) {
        throw new UnsupportedOperationException();
    }
}
