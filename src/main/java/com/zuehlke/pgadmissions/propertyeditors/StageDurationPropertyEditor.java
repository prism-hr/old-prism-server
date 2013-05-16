package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

@Component
public class StageDurationPropertyEditor extends PropertyEditorSupport {
	
	@SuppressWarnings("unchecked")
	@Override
	public void setAsText(String jsonStirng) throws IllegalArgumentException {
		try {
			if (jsonStirng == null || StringUtils.isBlank(jsonStirng)) {
				setValue(null);
				return;
			}
			ObjectMapper objectMapper = new ObjectMapper();

			Map<String, Object> properties = objectMapper.readValue(jsonStirng,
					Map.class);
			StageDuration stageDuration = new StageDuration();
			stageDuration.setDuration(Integer.parseInt((String)properties.get("duration")));
			stageDuration.setStage(ApplicationFormStatus.valueOf((String) properties.get("stage")));
			stageDuration.setUnit(DurationUnitEnum.valueOf((String) properties.get("unit")));

			setValue(stageDuration);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getAsText() {
		if (getValue() == null) {
			return null;
		}
		StageDuration stageDuration = (StageDuration) getValue();
		return "{\"stage\": \""
				+ stageDuration.getStage() + "\",\"duration\": \""
				+ stageDuration.getDuration() + "\",\"unit\": \""
				+ stageDuration.getUnit() + "\"}";
	}
}
