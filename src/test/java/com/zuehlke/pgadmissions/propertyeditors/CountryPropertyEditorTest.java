package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.services.CountryService;

public class CountryPropertyEditorTest {

	private CountryService countryServiceMock;
	private CountryPropertyEditor editor;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		Country country = new CountryBuilder().id(1).toCountry();
		EasyMock.expect(countryServiceMock.getCountryById(1)).andReturn(country);
		EasyMock.replay(countryServiceMock);
		
		editor.setAsText("1");
		assertEquals(country, editor.getValue());
		
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
		editor.setValue(new CountryBuilder().toCountry());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnIdAsString(){			
		editor.setValue(new CountryBuilder().id(5).toCountry());
		assertEquals("5", editor.getAsText());
	}
	
	@Before
	public void setup(){
		countryServiceMock = EasyMock.createMock(CountryService.class);
		editor = new CountryPropertyEditor(countryServiceMock);
	}
}
