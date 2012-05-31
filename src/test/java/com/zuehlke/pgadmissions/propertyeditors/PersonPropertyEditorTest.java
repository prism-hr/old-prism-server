package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.builders.Personuilder;


public class PersonPropertyEditorTest {
	private PersonPropertyEditor editor;

	@Test	
	public void shouldParseAndSetAsValue(){
		editor.setAsText("{\"id\": \"1\",\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\" }");
		Person expected = new Personuilder().id(1).firstname("Mark").lastname("Johnson").email("test@gmail.com").toRegistryUser();
		Person registryUser =   (Person) editor.getValue();
		assertEquals(expected.getFirstname(), registryUser.getFirstname());
		assertEquals(expected.getLastname(), registryUser.getLastname());
		assertEquals(expected.getEmail(), registryUser.getEmail());
	}
	
	@Test	
	public void shouldParseEmptyIdAsNull(){
		editor.setAsText("{\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\"}");		
		Person registryUser =   (Person) editor.getValue();
		assertNull (registryUser.getId());
		
	}
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfAStringNotInTheRightFormat(){			
		editor.setAsText("{email: 'test@gmail.com' }");		
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
		editor.setValue(new Personuilder().firstname("Mark").id(1).lastname("Johnson").email("test@gmail.com").toRegistryUser());
		assertEquals("{\"id\": \"1\",\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\"}", editor.getAsText());
	}
	
	@Before
	public void setup(){
		
		editor = new PersonPropertyEditor();
	}
}
