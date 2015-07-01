package com.zuehlke.pgadmissions.rest.converter;

import java.util.List;

import org.dozer.DozerConverter;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismCustomQuestionType;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionConfiguration;
import com.zuehlke.pgadmissions.rest.dto.ActionCustomQuestionConfigurationDTO.ActionCustomQuestionConfigurationValueDTO;

public class ActionCustomQuestionConfigurationConverter extends DozerConverter<ActionCustomQuestionConfigurationValueDTO, ActionCustomQuestionConfiguration> {

    public ActionCustomQuestionConfigurationConverter() {
        super(ActionCustomQuestionConfigurationValueDTO.class, ActionCustomQuestionConfiguration.class);
    }

    @Override
    public ActionCustomQuestionConfiguration convertTo(ActionCustomQuestionConfigurationValueDTO source, ActionCustomQuestionConfiguration destination) {
        String name = source.getComponent();
        List<String> options = source.getOptions();
        String validation = source.getValidation();
        return new ActionCustomQuestionConfiguration().withCustomQuestionType(PrismCustomQuestionType.getByComponentName(name)).withName(name)
                .withEditable(source.getEditable()).withIndex(source.getIndex()).withLabel(source.getLabel()).withDescription(source.getDescription())
                .withOptions(options == null ? null : Joiner.on("|").join(options)).withRequired(source.getRequired())
                .withValidation(validation).withWeighting(source.getWeighting()).withPlaceholder(source.getPlaceholder());
    }

    @Override
    public ActionCustomQuestionConfigurationValueDTO convertFrom(ActionCustomQuestionConfiguration source, ActionCustomQuestionConfigurationValueDTO destination) {
        throw new UnsupportedOperationException();
    }

}
