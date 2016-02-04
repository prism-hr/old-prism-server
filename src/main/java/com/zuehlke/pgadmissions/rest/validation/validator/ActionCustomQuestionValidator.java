package com.zuehlke.pgadmissions.rest.validation.validator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismCustomQuestionType;
import com.zuehlke.pgadmissions.rest.dto.ActionCustomQuestionConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.ActionCustomQuestionConfigurationDTO.ActionCustomQuestionConfigurationValueDTO;

@Component
public class ActionCustomQuestionValidator extends LocalValidatorFactoryBean implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ActionCustomQuestionConfigurationDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ActionCustomQuestionConfigurationDTO configuration = (ActionCustomQuestionConfigurationDTO) target;

        int cumulativeRatingCount = 0;
        BigDecimal cumulativeRatingWeight = new BigDecimal(0.00);

        for (ActionCustomQuestionConfigurationValueDTO property : configuration) {
            PrismCustomQuestionType type = PrismCustomQuestionType.getByComponentName(property.getComponent());

            BigDecimal weighting = property.getWeighting();
            if (type.name().startsWith("RATING") && weighting == null) {
                throw new Error();
            } else if (!type.name().startsWith("RATING") && weighting != null) {
                throw new Error();
            } else if (weighting != null) {
                cumulativeRatingCount++;
                cumulativeRatingWeight = cumulativeRatingWeight.add(weighting).setScale(2, RoundingMode.HALF_UP);
            }
        }

        if (cumulativeRatingCount > 0 && !cumulativeRatingWeight.equals(new BigDecimal(1.00))) {
            errors.reject(PrismDisplayPropertyDefinition.SYSTEM_COMMENT_CUSTOM_FORM_WEIGHT_ERROR.name());
        }
    }

}
