package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.LanguageDAO;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;

public class LanguageServiceTest {
	@Test
	public void shouldGetAllLanguagesFromDAO() {
		LanguageDAO languageDAOMock = EasyMock.createMock(LanguageDAO.class);
		LanguageService service = new LanguageService(languageDAOMock);
		Language country1 = new LanguageBuilder().id(1).build();
		Language country2 = new LanguageBuilder().id(2).build();
		EasyMock.expect(languageDAOMock.getAllLanguages()).andReturn(Arrays.asList(country1, country2));
		EasyMock.replay(languageDAOMock);

		List<Language> languages = service.getAllLanguages();
		assertEquals(2, languages.size());
		assertEquals(country1, languages.get(0));
		assertEquals(country2, languages.get(1));

	}

	@Test
	public void shouldGetLanguageFromDAO() {
		LanguageDAO languageDAOMock = EasyMock.createMock(LanguageDAO.class);
		LanguageService service = new LanguageService(languageDAOMock);
		Language langugage = new LanguageBuilder().id(1).build();
		EasyMock.expect(languageDAOMock.getLanguageById(1)).andReturn(langugage);
		EasyMock.replay(languageDAOMock);

		Language fetchedLanguage = service.getLanguageById(1);
		assertEquals(langugage, fetchedLanguage);

	}
}
