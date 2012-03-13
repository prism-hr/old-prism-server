package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageProficiency;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.enums.LanguageAptitude;
import com.zuehlke.pgadmissions.services.LanguageService;

public class LanguageProficiencyJSONPropertyEditorTest {
	private LanguageProficiencyJSONPropertyEditor editor;
	private LanguageService languageServiceMock;

	@Test
	public void shouldParseAndSetAsValue() throws ParseException {	
		Language language = new LanguageBuilder().id(1).toLanguage();
	
		EasyMock.expect(languageServiceMock.getLanguageById(1)).andReturn(language);
		EasyMock.replay( languageServiceMock);

		editor.setAsText("{\"aptitude\": \"ELEMENTARY\", \"language\": 1, \"primary\": \"true\"}");

		LanguageProficiency languageProficiency = (LanguageProficiency) editor.getValue();
		assertEquals(language, languageProficiency.getLanguage());	
		assertEquals(LanguageAptitude.ELEMENTARY, languageProficiency.getAptitude());
		assertTrue(languageProficiency.isPrimary());

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfAStringNotInTheRightFormat() {
		
		editor.setAsText("{\"aptitude\": \"ELEMENTARY\", \"language: 1}");


	}

	@Test
	public void shouldReturNullIfStringIsNull() {
		editor.setAsText(null);
		assertNull(editor.getValue());
	}

	@Test
	public void shouldReturNullIfStringIsEmpty() {
		editor.setAsText("");
		assertNull(editor.getValue());
	}

	@Test
	public void shouldReturnNullIfValueIsNull() {
		editor.setValue(null);
		assertNull(editor.getAsText());
	}

	@Test
	public void shouldReturnCorrectjsonString() throws ParseException {

		Language language = new LanguageBuilder().id(1).toLanguage();
		LanguageProficiency languageProficiency = new LanguageProficiency();
		languageProficiency.setAptitude(LanguageAptitude.ELEMENTARY);
		languageProficiency.setLanguage(language);

		editor.setValue(languageProficiency);
		assertEquals("{\"aptitude\": \"ELEMENTARY\", \"language\": 1, \"primary\": \"false\"}", editor.getAsText());
	}

	@Before
	public void setup() {
		
		languageServiceMock = EasyMock.createMock(LanguageService.class);
		editor = new LanguageProficiencyJSONPropertyEditor(languageServiceMock);
	}
}
