package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.services.LanguageService;

public class LanguagePropertyEditorTest {

	private LanguageService languageServiceMock;
	private LanguagePropertyEditor editor;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		Language language = new LanguageBuilder().id(1).toLanguage();
		EasyMock.expect(languageServiceMock.getLanguageById(1)).andReturn(language);
		EasyMock.replay(languageServiceMock);
		
		editor.setAsText("1");
		assertEquals(language, editor.getValue());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfIdNotInteger(){			
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
		editor.setValue(new LanguageBuilder().toLanguage());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnIdAsString(){			
		editor.setValue(new LanguageBuilder().id(5).toLanguage());
		assertEquals("5", editor.getAsText());
	}
	
	@Before
	public void setup(){
		languageServiceMock = EasyMock.createMock(LanguageService.class);
		editor = new LanguagePropertyEditor(languageServiceMock);
	}
}
