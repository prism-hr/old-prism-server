package com.zuehlke.pgadmissions.rest.converter;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerConverter;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class CommaSeparatedActionsToListConverter extends DozerConverter<String, List<PrismAction>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
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
