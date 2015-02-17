package com.zuehlke.pgadmissions.rest.converter;

import java.util.Map;

import org.dozer.DozerConverter;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.comment.CommentCustomResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismCustomQuestionType;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentCustomResponseRepresentation;

public class CommentCustomResponseConverter extends DozerConverter<CommentCustomResponse, CommentCustomResponseRepresentation> {

    public CommentCustomResponseConverter() {
        super(CommentCustomResponse.class, CommentCustomResponseRepresentation.class);
    }

    @Override
    public CommentCustomResponseRepresentation convertTo(CommentCustomResponse source, CommentCustomResponseRepresentation destination) {
        destination = new CommentCustomResponseRepresentation().withLabel(source.getActionCustomQuestionConfiguration().getLabel());
        if (source.getActionCustomQuestionConfiguration().getCustomQuestionType() == PrismCustomQuestionType.RATING_WEIGHTED) {
            String[] options = source.getActionCustomQuestionConfiguration().getOptions().split("\\|");
            Map<Integer, Integer> index = ImmutableMap.of(1, 0, 2, 1, 3, 2, 5, 3, 8, 4);
            destination.setPropertyValue(options[index.get(Integer.parseInt(source.getPropertyValue()))]);
        } else {
            destination.setPropertyValue(source.getPropertyValue());
        }
        return destination;
    }

    @Override
    public CommentCustomResponse convertFrom(CommentCustomResponseRepresentation source, CommentCustomResponse destination) {
        throw new UnsupportedOperationException();
    }

}
