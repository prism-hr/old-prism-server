package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Person;

@Component
public class PersonPropertyEditor extends PropertyEditorSupport {
	
	
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
			Person registryUser = new Person();
			registryUser.setFirstname(((String)properties.get("firstname")));
			registryUser.setLastname(((String) properties.get("lastname")));
			registryUser.setEmail(((String) properties.get("email")));
			if (StringUtils.isNotBlank((String) properties.get("id"))) {
				registryUser.setId(Integer.parseInt((String) properties.get("id")));
			}
			setValue(registryUser);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getAsText() {
		if (getValue() == null) {
			return null;
		}
		Person person = (Person) getValue();
		return "{\"id\": \""
				+ person.getId() + "\",\"firstname\": \""
				+ person.getFirstname() + "\",\"lastname\": \""
				+ person.getLastname() + "\",\"email\": \""
				+ person.getEmail() + "\"}";
		
	}
}
