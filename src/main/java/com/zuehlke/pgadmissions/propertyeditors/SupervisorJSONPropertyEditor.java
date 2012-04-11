package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.AwareStatus;

@Component
public class SupervisorJSONPropertyEditor extends PropertyEditorSupport {
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
			Supervisor supervisor = new Supervisor();
			supervisor.setFirstname((String) properties.get("firstname"));
			supervisor.setLastname((String) properties.get("lastname"));
			supervisor.setEmail((String) properties.get("email"));
			if (StringUtils.isNotBlank((String) properties.get("id"))) {
				supervisor
						.setId(Integer.parseInt((String) properties.get("id")));
			}
			supervisor.setAwareSupervisor(AwareStatus
					.valueOf((String) properties.get("awareSupervisor")));

			setValue(supervisor);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getAsText() {
		if (getValue() == null) {
			return null;
		}
		Supervisor supervisor = (Supervisor) getValue();
		return "{\"id\": \"" + supervisor.getId() + "\",\"firstname\": \""
				+ supervisor.getFirstname() + "\",\"lastname\": \""
				+ supervisor.getLastname() + "\",\"email\": \""
				+ supervisor.getEmail() + "\", \"awareSupervisor\": \""
				+ supervisor.getAwareSupervisor() + "\"}";
	}
}
