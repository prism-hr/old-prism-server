package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;

public class LanguageMappingTest extends AutomaticRollbackTestCase {
	
	@Test
	public void shouldSaveAndLoadLanguage(){
		Language language = new LanguageBuilder().name("ZZZZZZ").code("ZZ").enabled(true).build();
		sessionFactory.getCurrentSession().save(language);
		assertNotNull(language.getId());
		Integer id = language.getId();
		Language	 reloadedLanguage = (Language) sessionFactory.getCurrentSession().get(Language.class, id);
		assertSame(language, reloadedLanguage);
		
		flushAndClearSession();
		
		reloadedLanguage = (Language) sessionFactory.getCurrentSession().get(Language.class, id);
		assertNotSame(language, reloadedLanguage);
		assertEquals(language.getId(), reloadedLanguage.getId());
		assertEquals("ZZZZZZ", reloadedLanguage.getName());
	}
}
