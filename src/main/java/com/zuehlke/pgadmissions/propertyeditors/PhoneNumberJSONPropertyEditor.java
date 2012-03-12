package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;

@Component
public class PhoneNumberJSONPropertyEditor extends PropertyEditorSupport {
	@SuppressWarnings("unchecked")
	@Override
	public void setAsText(String jsonStirng) throws IllegalArgumentException {
		try {
			if (jsonStirng == null || StringUtils.isBlank(jsonStirng)) {
				setValue(null);
				return;
			}		
			ObjectMapper objectMapper = new ObjectMapper();

			Map<String, Object> properties = objectMapper.readValue(jsonStirng, Map.class);
			Telephone telephone = new Telephone();
			telephone.setTelephoneType(PhoneType.valueOf((String) properties.get("type")));
			telephone.setTelephoneNumber((String) properties.get("number"));
			setValue(telephone);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getAsText() {
		if (getValue() == null ) {
			return null;
		}
			Telephone phone = (Telephone) getValue();
			return "{\"type\": \"" + phone.getTelephoneType() + "\", \"number\": \"" + phone.getTelephoneNumber() + "\"}";
	}
}
