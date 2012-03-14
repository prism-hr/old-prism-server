package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.enums.NationalityType;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.DocumentService;

public class NationalityJSONPropertyEditorTest {
	private NationalityJSONPropertyEditor editor;
	private DocumentService documentServiceMock;
	private CountryService countryServiceMock;

	@Test
	public void shouldParseAndSetAsValue() throws ParseException {
		Document document1 = new DocumentBuilder().id(1).toDocument();
		Document document2 = new DocumentBuilder().id(2).toDocument();
		Country country = new CountryBuilder().id(1).toCountry();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document1);
		EasyMock.expect(documentServiceMock.getDocumentById(2)).andReturn(document2);
		EasyMock.expect(countryServiceMock.getCountryById(1)).andReturn(country);
		EasyMock.replay(documentServiceMock, countryServiceMock);

		editor.setAsText("{\"type\": \"CANDIDATE\", \"country\": 1, \"supportingDocuments\": [1,2], \"primary\": \"true\"}");

		Nationality nationality = (Nationality) editor.getValue();
		assertEquals(country, nationality.getCountry());
		assertEquals(2, nationality.getSupportingDocuments().size());
		assertTrue(nationality.getSupportingDocuments().containsAll(Arrays.asList(document1, document2)));
		assertEquals(NationalityType.CANDIDATE, nationality.getType());
		assertTrue(nationality.isPrimary());

	}

	@Test
	public void shouldIgnoreIfNoDocumentsFieldPresent() throws ParseException {
		Country country = new CountryBuilder().id(1).toCountry();
		EasyMock.expect(countryServiceMock.getCountryById(1)).andReturn(country);
		EasyMock.replay(documentServiceMock, countryServiceMock);

		editor.setAsText("{\"type\": \"CANDIDATE\", \"country\": 1, \"primary\": \"true\"}");

		Nationality nationality = (Nationality) editor.getValue();
		assertEquals(country, nationality.getCountry());
		assertTrue(nationality.getSupportingDocuments().isEmpty());
		assertEquals(NationalityType.CANDIDATE, nationality.getType());
		assertTrue(nationality.isPrimary());

	}
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfAStringNotInTheRightFormat() {
		Document document1 = new DocumentBuilder().id(1).toDocument();
		Document document2 = new DocumentBuilder().id(2).toDocument();
	
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document1);
		EasyMock.expect(documentServiceMock.getDocumentById(2)).andReturn(document2);
	
		EasyMock.replay(documentServiceMock);
		editor.setAsText("{\"type\": \"CANDIDATE\", \"bob: 1, \"supportingDocuments\": [1,2]}");

	}

	@Test
	public void shouldReturNullIfStringIsNull() {
		editor.setAsText(null);
		assertNull(editor.getValue());
	}

	@Test
	public void shouldReturNullIfStringIsEmpty() {
		editor.setAsText("");
		assertNull(editor.getValue());
	}

	@Test
	public void shouldReturnNullIfValueIsNull() {
		editor.setValue(null);
		assertNull(editor.getAsText());
	}

	@Test
	public void shouldReturnCorrectjsonString() throws ParseException {

		Document document1 = new DocumentBuilder().id(1).toDocument();
		Document document2 = new DocumentBuilder().id(2).toDocument();
		Country country = new CountryBuilder().id(1).toCountry();
		Nationality nationality = new Nationality();
		nationality.setType(NationalityType.CANDIDATE);
		nationality.setCountry(country);
		nationality.setSupportingDocuments(Arrays.asList(document1, document2));

		editor.setValue(nationality);
		assertEquals("{\"type\": \"CANDIDATE\", \"country\": 1, \"supportingDocuments\": [1,2], \"primary\": \"false\"}", editor.getAsText());
	}

	@Before
	public void setup() {
		documentServiceMock = EasyMock.createMock(DocumentService.class);
		countryServiceMock = EasyMock.createMock(CountryService.class);
		editor = new NationalityJSONPropertyEditor(documentServiceMock, countryServiceMock);
	}
}
