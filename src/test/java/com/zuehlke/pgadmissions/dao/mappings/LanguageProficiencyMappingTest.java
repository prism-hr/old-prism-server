package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageProficiency;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.enums.LanguageAptitude;

public class LanguageProficiencyMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadLanguageProficiency() {
		Language language = new LanguageBuilder().name("aaaaa").toLanguage();
		sessionFactory.getCurrentSession().save(language);


		flushAndClearSession();

		LanguageProficiency proficiency = new LanguageProficiency();
		proficiency.setLanguage(language);
		proficiency.setAptitude(LanguageAptitude.ELEMENTARY);
		sessionFactory.getCurrentSession().save(proficiency);

		assertNotNull(proficiency.getId());

		LanguageProficiency reloadedLanguageProficiency = (LanguageProficiency) sessionFactory.getCurrentSession().get(LanguageProficiency.class, proficiency.getId());
		assertSame(proficiency, reloadedLanguageProficiency);
		assertEquals(proficiency, reloadedLanguageProficiency);
		flushAndClearSession();

		reloadedLanguageProficiency = (LanguageProficiency) sessionFactory.getCurrentSession().get(LanguageProficiency.class, proficiency.getId());
		assertNotSame(proficiency, reloadedLanguageProficiency);
		assertEquals(proficiency, reloadedLanguageProficiency);
		assertEquals(language, reloadedLanguageProficiency.getLanguage());
		assertEquals(LanguageAptitude.ELEMENTARY, reloadedLanguageProficiency.getAptitude());

	}
}
