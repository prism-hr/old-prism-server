package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

@Component
public class SuggestedSupervisorJSONPropertyEditor extends PropertyEditorSupport {

	private final EncryptionHelper encryptionHelper;

	@Autowired
	public SuggestedSupervisorJSONPropertyEditor(EncryptionHelper encryptionHelper) {
		this.encryptionHelper = encryptionHelper;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setAsText(String jsonString) throws IllegalArgumentException {
		try {
			if (jsonString == null || StringUtils.isBlank(jsonString)) {
				setValue(null);
				return;
			}
			ObjectMapper objectMapper = new ObjectMapper();

			Map<String, Object> properties = objectMapper.readValue(jsonString, Map.class);
			SuggestedSupervisor supervisor = new SuggestedSupervisor();
			supervisor.setFirstname(StringUtils.trim((String) properties.get("firstname")));
			supervisor.setLastname(StringUtils.trim((String) properties.get("lastname")));
			supervisor.setEmail(StringUtils.trim((String) properties.get("email")));
			if(properties.get("awareSupervisor") != null && "YES".equals((String) properties.get("awareSupervisor"))){			
				supervisor.setAware(true);
			}			
			if (StringUtils.isNotBlank((String) properties.get("id"))) {
				supervisor.setId(encryptionHelper.decryptToInteger((String) properties.get("id")));
			}
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
		SuggestedSupervisor supervisor = (SuggestedSupervisor) getValue();
		String aware = "NO";
		if(supervisor.isAware()){
			aware = "YES";
		}
		return "{\"id\": \"" + encryptionHelper.encrypt(supervisor.getId()) + "\",\"firstname\": \"" + supervisor.getFirstname() + "\",\"lastname\": \"" + supervisor.getLastname()
				+ "\",\"email\": \"" + supervisor.getEmail() + "\", \"awareSupervisor\": \"" + aware + "\"}";
	}
}
