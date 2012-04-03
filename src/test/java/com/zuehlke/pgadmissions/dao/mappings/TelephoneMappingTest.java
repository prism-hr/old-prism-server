package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.builders.TelephoneBuilder;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;

public class TelephoneMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadTelphone(){
		Telephone telephone = new TelephoneBuilder().telephoneNumber("abc").telephoneType(PhoneType.MOBILE).toTelephone();
		sessionFactory.getCurrentSession().save(telephone);
		assertNotNull(telephone.getId());
		
		Telephone reloadedPhone = (Telephone) sessionFactory.getCurrentSession().get(Telephone.class, telephone.getId());
		assertSame(telephone, reloadedPhone);
		
		flushAndClearSession();
		reloadedPhone = (Telephone) sessionFactory.getCurrentSession().get(Telephone.class, telephone.getId());
		assertNotSame(telephone, reloadedPhone);
		assertEquals(telephone, reloadedPhone);
		assertEquals(telephone.getTelephoneNumber(), reloadedPhone.getTelephoneNumber());
		assertEquals(telephone.getTelephoneType(), reloadedPhone.getTelephoneType());
	}
}
