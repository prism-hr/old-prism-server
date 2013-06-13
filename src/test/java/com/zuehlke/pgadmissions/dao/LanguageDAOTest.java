package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;


public class LanguageDAOTest extends AutomaticRollbackTestCase{

	@Test(expected=NullPointerException.class)
	public void shouldThrowNullPointerException(){
		LanguageDAO languageDAO = new LanguageDAO();
		languageDAO.getAllLanguages();
	}
	
	@Test
	public void shouldGetAllLanguagesInAlhphabeticalOrder() {
		BigInteger numberOfLanguages = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from LANGUAGE").uniqueResult();
		Language language1 = new LanguageBuilder().name("ZZZZZZ").code("ZZ").enabled(true).build();
		Language language2 = new LanguageBuilder().name("AAAAAA").code("AA").enabled(true).build();
		save(language1,  language2);
		flushAndClearSession();
		LanguageDAO languageDAO = new LanguageDAO(sessionFactory);
		List<Language> allLanguages = languageDAO.getAllLanguages();

		assertEquals(numberOfLanguages.intValue() +2, allLanguages.size());
		assertEquals("AAAAAA",allLanguages.get(0).getName());
		assertEquals("ZZZZZZ",allLanguages.get(numberOfLanguages.intValue() +1).getName());
	}
	
	@Test
	public void shouldGetLanguageById(){

		Language language1 = new LanguageBuilder().name("ZZZZZZ").code("ZZ").enabled(true).build();
		Language language2 = new LanguageBuilder().name("AAAAAA").code("AA").enabled(true).build();
		
		save(language1, language2);
		flushAndClearSession();
		Integer id = language1.getId();
		LanguageDAO languageDAO = new LanguageDAO(sessionFactory);
		Language reloadedLanguage = languageDAO.getLanguageById(id);
		assertEquals(language1.getId(), reloadedLanguage.getId());
	}
	
    @Test
    public void shouldGetAllEnabledLanguages() {
        BigInteger numberOfLanguages = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from LANGUAGE WHERE enabled = true").uniqueResult();
        Language language1 = new LanguageBuilder().name("ZZZZZZ").code("ZZ").enabled(true).build();
        Language language2 = new LanguageBuilder().name("AAAAAA").code("AA").enabled(false).build();
        save(language1,  language2);
        flushAndClearSession();
        LanguageDAO languageDAO = new LanguageDAO(sessionFactory);
        List<Language> allLanguages = languageDAO.getAllEnabledLanguages();
        assertEquals(numberOfLanguages.intValue() +1, allLanguages.size());
    }	
}
