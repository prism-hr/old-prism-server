package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.LanguageService;

public class LanguagePropertyEditorTest {

	private LanguageService languageServiceMock;
	private LanguagePropertyEditor editor;
	private EncryptionHelper encryptionHelperMock;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
		
		Language language = new LanguageBuilder().id(1).build();
		EasyMock.expect(languageServiceMock.getLanguageById(1)).andReturn(language);
		EasyMock.replay(languageServiceMock, encryptionHelperMock);
		
		editor.setAsText("encryptedId");
		assertEquals(language, editor.getValue());
		
	}
	
	@Test(expected=IllegalArgumentException.class)			
	public void shouldThrowIllegalArgumentExceptionIfIdNotInteger(){		
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andThrow(new IllegalArgumentException());
		EasyMock.replay( encryptionHelperMock);
		editor.setAsText("bob");			
	}
	
	@Test	
	public void shouldReturNullIfIdIsNull(){			
		editor.setAsText(null);
		assertNull(editor.getValue());		
	}
	
	@Test	
	public void shouldReturNullIfIdIsEmptyString(){			
		editor.setAsText(" ");
		assertNull(editor.getValue());		
	}
	@Test	
	public void shouldReturnNullIfValueIsNull(){			
		editor.setValue(null);
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnNullIfValueIdIsNull(){			
		editor.setValue(new LanguageBuilder().build());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnEnctyptedIdAsString(){			
		editor.setValue(new LanguageBuilder().id(5).build());
		EasyMock.expect(encryptionHelperMock.encrypt(5)).andReturn("bob");
		EasyMock.replay(encryptionHelperMock);
		assertEquals("bob", editor.getAsText());
	}
	
	@Before
	public void setup(){
		languageServiceMock = EasyMock.createMock(LanguageService.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		editor = new LanguagePropertyEditor(languageServiceMock, encryptionHelperMock);
	}
}
