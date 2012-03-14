package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.enums.NationalityType;

public class NationalityMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadNationality() {
		Country country = new CountryBuilder().code("aa").name("aaaaa").toCountry();
		sessionFactory.getCurrentSession().save(country);

		Document document1 = new DocumentBuilder().content("aa".getBytes()).fileName("bob").toDocument();
		Document document2 = new DocumentBuilder().content("bb".getBytes()).fileName("fred").toDocument();
		save(document1, document2);

		flushAndClearSession();

		Nationality nationality = new Nationality();
		nationality.setCountry(country);
		nationality.setPrimary(true);
		nationality.setSupportingDocuments(Arrays.asList(document1, document2));
		nationality.setType(NationalityType.CANDIDATE);
		sessionFactory.getCurrentSession().save(nationality);

		assertNotNull(nationality.getId());

		Nationality reloadedNationality = (Nationality) sessionFactory.getCurrentSession().get(Nationality.class, nationality.getId());
		assertSame(nationality, reloadedNationality);
		assertEquals(nationality, reloadedNationality);
		flushAndClearSession();

		reloadedNationality = (Nationality) sessionFactory.getCurrentSession().get(Nationality.class, nationality.getId());
		assertNotSame(nationality, reloadedNationality);
		assertEquals(nationality, reloadedNationality);
		assertEquals(country, reloadedNationality.getCountry());
		assertEquals(2, reloadedNationality.getSupportingDocuments().size());
		assertTrue(reloadedNationality.getSupportingDocuments().containsAll(Arrays.asList(document1, document2)));
		assertEquals(NationalityType.CANDIDATE, reloadedNationality.getType());
		assertTrue(nationality.isPrimary());
	}
}
