package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.LanguageProficiency;
import com.zuehlke.pgadmissions.domain.enums.LanguageAptitude;
import com.zuehlke.pgadmissions.services.LanguageService;

@Component
public class LanguageProficiencyJSONPropertyEditor extends PropertyEditorSupport {

	private final LanguageService languageService;
	LanguageProficiencyJSONPropertyEditor(){
		this(null);
	}
	@Autowired
	public LanguageProficiencyJSONPropertyEditor(LanguageService languageService) {
		this.languageService = languageService;
	
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
			LanguageProficiency languageProficiency = new LanguageProficiency();
			languageProficiency.setAptitude(LanguageAptitude.valueOf((String) properties.get("aptitude")));
			languageProficiency.setLanguage(languageService.getLanguageById((Integer) properties.get("language")));

			setValue(languageProficiency);

		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getAsText() {
		if (getValue() == null) {
			return null;
		}
		LanguageProficiency languageProficiency = (LanguageProficiency) getValue();
			
		return "{\"aptitude\": \"" + languageProficiency.getAptitude() + "\", \"language\": " + languageProficiency.getLanguage().getId() + "}";
	}

}
