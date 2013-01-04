package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.SuggestedSupervisorBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;


public class SuggestedSupervisorJSONPropertyEditorTest {
	
	private SuggestedSupervisorJSONPropertyEditor editor;
	private EncryptionHelper encryptionHelperMock;

	@Test	
	public void shouldParseAndSetAsValue(){
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
		EasyMock.replay(encryptionHelperMock);
		editor.setAsText("{\"id\": \"bob\",\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\" , \"awareSupervisor\": \"YES\"}");
		SuggestedSupervisor expected = new SuggestedSupervisorBuilder().id(1).firstname("Mark").lastname("Johnson").email("test@gmail.com").aware(true).build();
		SuggestedSupervisor suggestedSupervisor =   (SuggestedSupervisor) editor.getValue();
		assertEquals(expected.getFirstname(), suggestedSupervisor.getFirstname());
		assertEquals(expected.getLastname(), suggestedSupervisor.getLastname());
		assertEquals(expected.getEmail(), suggestedSupervisor.getEmail());
		assertEquals(expected.isAware(), suggestedSupervisor.isAware());
	}
	
	@Test	
	public void shouldParseEmptyIdAsNull(){
		editor.setAsText("{\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\" ,  \"awareSupervisor\": \"YES\"}");		
		SuggestedSupervisor suggestedSupervisor =   (SuggestedSupervisor) editor.getValue();
		assertNull (suggestedSupervisor.getId());
		
	}
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfAStringNotInTheRightFormat(){			
		editor.setAsText("{email: 'test@gmail.com' awareSupervisor: 'YES'}");		
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
		EasyMock.expect(encryptionHelperMock.encrypt(1)).andReturn("bob");
		EasyMock.replay(encryptionHelperMock);
		editor.setValue(new SuggestedSupervisorBuilder().firstname("Mark").id(1).lastname("Johnson").email("test@gmail.com").aware(false).build());
		assertEquals("{\"id\": \"bob\",\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\", \"awareSupervisor\": \"NO\"}", editor.getAsText());
	}
	
	@Before
	public void setup(){
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		editor = new SuggestedSupervisorJSONPropertyEditor(encryptionHelperMock);
	}
}
