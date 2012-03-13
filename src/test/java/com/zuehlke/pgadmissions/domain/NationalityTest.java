package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.enums.NationalityType;

public class NationalityTest {

	@Test
	public void shouldotuputCorrectJSONString(){

		Document document1 = new DocumentBuilder().id(1).toDocument();
		Document document2 = new DocumentBuilder().id(2).toDocument();
		Country country = new CountryBuilder().id(1).toCountry();
		Nationality nationality = new Nationality();
		nationality.setType(NationalityType.CANDIDATE);
		nationality.setCountry(country);
		nationality.setPrimary(true);
		nationality.setSupportingDocuments(Arrays.asList(document1, document2));
		assertEquals("{\"type\": \"CANDIDATE\", \"country\": 1, \"supportingDocuments\": [1,2], \"primary\": \"true\"}", nationality.getAsJson());
	}
}
