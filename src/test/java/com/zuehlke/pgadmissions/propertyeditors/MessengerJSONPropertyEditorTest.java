package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.builders.MessengerBuilder;



public class MessengerJSONPropertyEditorTest {
	private MessengerJSONPropertyEditor editor;
	

	@Test	
	public void shouldParseAndSetAsValue() throws ParseException{
		editor.setAsText("{\"address\": \"something\"}");
		Messenger expected = new MessengerBuilder().messengerAddress("something").toMessenger();
		Messenger messenger =   (Messenger) editor.getValue();
		assertEquals(expected.getMessengerAddress(), messenger.getMessengerAddress());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfAStringNotInTheRightFormat(){			
		editor.setAsText("{type: 'skype' number: 'something'}");		
	}
	
	@Test	
	public void shouldReturNullIfStringIsNull(){			
		editor.setAsText(null);
		assertNull(editor.getValue());		
	}
	@Test	
	public void shouldReturNullIfStringIsEmpty(){			
		editor.setAsText("");
		assertNull(editor.getValue());		
	}
	
	@Test	
	public void shouldReturnNullIfValueIsNull(){			
		editor.setValue(null);
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnCorrectjsonString() throws ParseException{			
		editor.setValue(new MessengerBuilder().messengerAddress("something").toMessenger());
		assertEquals("{ \"address\": \"something\"}", editor.getAsText());
	}
	
	@Before
	public void setup(){
		
		editor = new MessengerJSONPropertyEditor();
	}
}
