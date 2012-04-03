package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.enums.LanguageAptitude;

public class LanguageProficiencyTest {
	
	@Test
	public void shouldReturnCorrectjsonString() {

		Language language = new LanguageBuilder().id(1).toLanguage();
		LanguageProficiency languageProficiency = new LanguageProficiency();
		languageProficiency.setAptitude(LanguageAptitude.ELEMENTARY);
		languageProficiency.setLanguage(language);
	
		assertEquals("{\"aptitude\": \"ELEMENTARY\", \"language\": 1}", languageProficiency.getAsJson());
	}
	
}
