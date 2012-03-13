package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.builders.TelephoneBuilder;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;


public class PhoneNumberJSONPropertyEditorTest {
	private PhoneNumberJSONPropertyEditor editor;

	@Test	
	public void shouldParseAndSetAsValue(){
		editor.setAsText("{\"type\": \"MOBILE\", \"number\": \"something\"}");
		Telephone expected = new TelephoneBuilder().telephoneType(PhoneType.MOBILE).telephoneNumber("something").toTelephone();
		Telephone telephone =   (Telephone) editor.getValue();
		assertEquals(expected.getTelephoneType(), telephone.getTelephoneType());
		assertEquals(expected.getTelephoneNumber(), telephone.getTelephoneNumber());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfAStringNotInTheRightFormat(){			
		editor.setAsText("{type: 'MOBILE' number: 'something'}");		
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
	public void shouldReturnCorrectjsonString(){			
		editor.setValue(new TelephoneBuilder().telephoneType(PhoneType.MOBILE).telephoneNumber("something").toTelephone());
		assertEquals("{\"type\": \"MOBILE\", \"number\": \"something\"}", editor.getAsText());
	}
	
	@Before
	public void setup(){
		
		editor = new PhoneNumberJSONPropertyEditor();
	}
}
