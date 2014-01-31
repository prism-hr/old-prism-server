package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.PersonService;

@Component
public class PersonPropertyEditor extends PropertyEditorSupport {

	private EncryptionHelper encryptionHelper;
	private final PersonService personService;

	@Autowired
	public PersonPropertyEditor(EncryptionHelper encryptionHelper, PersonService personService) {
		this.encryptionHelper = encryptionHelper;
		this.personService = personService;
	}

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
			Person registryUser = null;
			if (StringUtils.isNotBlank((String) properties.get("id"))) {
				registryUser = personService.getPersonWithId(encryptionHelper.decryptToInteger((String) properties.get("id")));
			}else{
				registryUser = new Person();	
			}
			
			registryUser.setFirstname(((String) properties.get("firstname")));
			registryUser.setLastname(((String) properties.get("lastname")));
			registryUser.setEmail(((String) properties.get("email")));
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
		String encrypt = person.getId()!=null?encryptionHelper.encrypt(person.getId()):"";
		return "{\"id\": \"" + encrypt + "\",\"firstname\": \"" + person.getFirstname() + "\",\"lastname\": \""
				+ person.getLastname() + "\",\"email\": \"" + person.getEmail() + "\"}";
	}
}
