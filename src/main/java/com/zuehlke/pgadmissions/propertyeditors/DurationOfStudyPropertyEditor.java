package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class DurationOfStudyPropertyEditor extends PropertyEditorSupport {

    public static final Integer ERROR_VALUE_FOR_DURATION_OF_STUDY = -1;
    public static final Integer ERROR_UNIT_FOR_DURATION_OF_STUDY = -2;

    @Override
    public void setAsText(String jsonString) {

        if (jsonString == null || StringUtils.isBlank(jsonString)) {
            setValue(null);
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, String> durationOfStudyMap = new Gson().fromJson(jsonString, Map.class);
        String durationOfStudyAsString = durationOfStudyMap.get("value");
        String durationOfStudyUnitAsString = durationOfStudyMap.get("unit");

        
        Integer result = getDurationOfStudyAsIntOrError(durationOfStudyAsString);
        if(!result.equals(ERROR_VALUE_FOR_DURATION_OF_STUDY)){
        	result = getDurationOfStudyInMonthsOrError(result, durationOfStudyUnitAsString);
        }

        setValue(result);
    }

	private Integer getDurationOfStudyInMonthsOrError(Integer duration, String durationOfStudyUnitAsString) {
		if( StringUtils.isNotBlank(durationOfStudyUnitAsString))
		{
			return durationOfStudyUnitAsString.trim().equals("Years") ? duration * 12 : duration; 
		}
		return ERROR_UNIT_FOR_DURATION_OF_STUDY;
	}

	private Integer getDurationOfStudyAsIntOrError(String durationOfStudyAsString) {
		if (StringUtils.isNotBlank(durationOfStudyAsString)  && StringUtils.isNumeric(durationOfStudyAsString.trim())) {
            Double durationOfStudyAsDouble = Double.valueOf(durationOfStudyAsString.trim());
            if (!durationOfStudyAsDouble.isNaN() && (Math.floor(durationOfStudyAsDouble) == durationOfStudyAsDouble)) {
                return durationOfStudyAsDouble.intValue();
            }
        }
		return ERROR_VALUE_FOR_DURATION_OF_STUDY;
	}

}
