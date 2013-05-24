package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class DurationOfStudyPropertyEditor extends PropertyEditorSupport {

    public static final Integer ERROR_VALUE_FOR_DURATION_OF_STUDY = -1;

    @Override
    public void setAsText(String jsonString) throws IllegalArgumentException {

        if (jsonString == null || StringUtils.isBlank(jsonString)) {
            setValue(null);
            return;
        }

        Integer result = ERROR_VALUE_FOR_DURATION_OF_STUDY;

        java.util.Map durationOfStudyMap = new Gson().fromJson(jsonString, java.util.Map.class);
        String durationOfStudyAsString = (String) durationOfStudyMap.get("value");
        String durationOfStudyUnitAsString = (String) durationOfStudyMap.get("unit");

        if (StringUtils.isNotBlank(durationOfStudyAsString) && StringUtils.isNotBlank(durationOfStudyUnitAsString)) {
            Double durationOfStudyAsDouble = Double.valueOf(durationOfStudyAsString);
            if (!durationOfStudyAsDouble.isNaN() && (Math.floor(durationOfStudyAsDouble) == durationOfStudyAsDouble)) {
                Integer durationOfStudyInMonths = Integer.valueOf(durationOfStudyAsString);
                if (durationOfStudyUnitAsString.equals("Years")) {
                    durationOfStudyInMonths = durationOfStudyInMonths * 12;
                }
                result = durationOfStudyInMonths;
            }
        }

        setValue(result);
    }

}
