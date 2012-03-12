package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;


public class DatePropertyEditorTest {
	private DatePropertyEditor editor;
	private SimpleDateFormat dateFormat;
	

	@Test	
	public void shouldParseAndSetAsValue() throws ParseException{
		editor.setAsText("02-Feb-2001");
		assertEquals(dateFormat.parse("2001/02/02"), editor.getValue());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfAStringNotInTheRightFormat(){			
		editor.setAsText("bob");			
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
	public void shouldReturnDateAsCorrectString() throws ParseException{			
		editor.setValue(dateFormat.parse("2001/02/02"));
		assertEquals("02-Feb-2001", editor.getAsText());
	}
	
	@Before
	public void setup(){
		dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		editor = new DatePropertyEditor();
	}
}
