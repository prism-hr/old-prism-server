package com.zuehlke.pgadmissions.services.importers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;

public class CountriesOfBirthImporterTest {
	
	private CountriesOfBirthImporter importer;
	private CountriesDAO countriesDAO;
	private ImportService importService;
	
	@SuppressWarnings("unchecked")
	@Test
    public void testImportData() throws XMLDataImportException {
		List<Country> countries = new ArrayList<Country>();
		countries.add(new CountryBuilder().code("UK").enabled(true).name("United Kingdom").build());
		countries.add(new CountryBuilder().code("PL").enabled(true).name("Poland").build());
		EasyMock.expect(countriesDAO.getAllCountries()).andReturn(countries);
		List<Country> changes = countries.subList(0, 1);
		EasyMock.expect(importService.merge(EasyMock.same(countries), EasyMock.anyObject(List.class))).andReturn(changes);
		countriesDAO.save(EasyMock.same(changes.get(0)));
		EasyMock.replay(countriesDAO, importService);
		importer.importData();
		EasyMock.verify(countriesDAO, importService);
    }
    
    @Before
	public void setUp() throws MalformedURLException, JAXBException {
		countriesDAO = EasyMock.createMock(CountriesDAO.class);
		importService = EasyMock.createMock(ImportService.class);
		URL xmlFileLocation = new File("src/test/resources/reference_data/countriesOfBirth.xml").toURI().toURL();
		importer = new CountriesOfBirthImporter(countriesDAO, importService, xmlFileLocation);
    }

}
