package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.MessengerBuilder;

public class MessengerTest {

	@Test	
	public void shouldReturnCorrectjsonString(){			
		Messenger messenger = new MessengerBuilder().messengerAddress("something").toMessenger();
		assertEquals("{ \"address\": \"something\"}",messenger.getAsJson());
	}
}
