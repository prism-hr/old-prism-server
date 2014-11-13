package com.zuehlke.pgadmissions.rest.validation.validator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionConfigurationProperty;
import com.zuehlke.pgadmissions.rest.dto.ActionPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.ActionPropertyConfigurationDTO.ActionPropertyDTO;

@Component
public class ActionPropertyConfigurationValidator extends LocalValidatorFactoryBean implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Application.class.isAssignableFrom(clazz);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        ActionPropertyConfigurationDTO configuration = (ActionPropertyConfigurationDTO) target;
        
        int cumulativeRatingCount = 0;
        BigDecimal cumulativeRatingWeight = new BigDecimal(0.00);
        
        for (ActionPropertyDTO property : configuration.getProperties()) {
            PrismActionConfigurationProperty type = PrismActionConfigurationProperty.getByDisplayName(property.getName());
            
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
            errors.reject(PrismDisplayProperty.SYSTEM_COMMENT_CUSTOM_FORM_WEIGHT_ERROR.name());
        }
    }
    
}
