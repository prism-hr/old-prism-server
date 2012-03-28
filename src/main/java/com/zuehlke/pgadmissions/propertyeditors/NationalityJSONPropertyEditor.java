package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.enums.NationalityType;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.DocumentService;

@Component
public class NationalityJSONPropertyEditor extends PropertyEditorSupport {

	private final DocumentService documentService;
	private final CountryService countryService;

	NationalityJSONPropertyEditor() {
		this(null, null);
	}

	@Autowired
	public NationalityJSONPropertyEditor(DocumentService documentService, CountryService countryService) {
		this.documentService = documentService;
		this.countryService = countryService;
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
			Nationality nationality = new Nationality();
			nationality.setType(NationalityType.valueOf((String) properties.get("type")));
			nationality.setCountry(countryService.getCountryById((Integer) properties.get("country")));
			if (properties.get("supportingDocuments") != null) {
				for (Integer id : (Iterable<Integer>) properties.get("supportingDocuments")) {
					nationality.getSupportingDocuments().add(documentService.getDocumentById(id));
				}
			}
			setValue(nationality);

		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getAsText() {
		if (getValue() == null) {
			return null;
		}
		Nationality nationality = (Nationality) getValue();

		return nationality.getAsJson();
	}
}
