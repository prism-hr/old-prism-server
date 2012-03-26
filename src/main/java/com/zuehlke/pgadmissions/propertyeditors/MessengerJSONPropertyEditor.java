package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Messenger;

@Component
public class MessengerJSONPropertyEditor extends PropertyEditorSupport{
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
			Messenger messenger = new Messenger();
			messenger.setMessengerAddress((String) properties.get("address"));
			setValue(messenger);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getAsText() {
		if (getValue() == null ) {
			return null;
		}
			Messenger messenger = (Messenger) getValue();
			return "{ \"address\": \"" + messenger.getMessengerAddress() + "\"}";
	}
}
