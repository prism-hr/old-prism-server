package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.CountryService;

public class CountryPropertyEditorTest {

	private CountryService countryServiceMock;
	private CountryPropertyEditor editor;
	private EncryptionHelper encryptionHelperMock;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
		Country country = new CountryBuilder().id(1).build();
		EasyMock.expect(countryServiceMock.getCountryById(1)).andReturn(country);
		EasyMock.replay(countryServiceMock,encryptionHelperMock);
		
		editor.setAsText("bob");
		assertEquals(country, editor.getValue());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfIdNotInteger(){		
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andThrow(new IllegalArgumentException());
		EasyMock.replay(encryptionHelperMock);
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
		editor.setValue(new CountryBuilder().build());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnEncryptedIdAsString(){			
		editor.setValue(new CountryBuilder().id(5).build());
		EasyMock.expect(encryptionHelperMock.encrypt(5)).andReturn("bob");
		EasyMock.replay(encryptionHelperMock);
		assertEquals("bob", editor.getAsText());
	}
	
	@Before
	public void setup(){
		countryServiceMock = EasyMock.createMock(CountryService.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		editor = new CountryPropertyEditor(countryServiceMock,encryptionHelperMock);
	}
}
