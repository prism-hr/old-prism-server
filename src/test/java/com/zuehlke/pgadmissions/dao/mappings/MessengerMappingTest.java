package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.builders.MessengerBuilder;;

public class MessengerMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadMessenger(){
		Messenger messenger = new MessengerBuilder().messengerAddress("john17").toMessenger();
		sessionFactory.getCurrentSession().save(messenger);
		assertNotNull(messenger.getId());
		
		Messenger reloadedMessenger = (Messenger) sessionFactory.getCurrentSession().get(Messenger.class, messenger.getId());
		assertSame(messenger, reloadedMessenger);
		
		flushAndClearSession();
		reloadedMessenger = (Messenger) sessionFactory.getCurrentSession().get(Messenger.class, messenger.getId());
		assertNotSame(messenger, reloadedMessenger);
		assertEquals(messenger, reloadedMessenger);
		assertEquals(messenger.getMessengerAddress(), reloadedMessenger.getMessengerAddress());
	}
}
