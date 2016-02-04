package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;

public class ActionConditionConverter extends DozerConverter<ResourceCondition, PrismActionCondition> {

    public ActionConditionConverter() {
        super(ResourceCondition.class, PrismActionCondition.class);
    }

    @Override
    public PrismActionCondition convertTo(ResourceCondition source, PrismActionCondition destination) {
        return source.getActionCondition();
    }

    @Override
    public ResourceCondition convertFrom(PrismActionCondition source, ResourceCondition destination) {
        throw new UnsupportedOperationException();
    }

}
