package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.referencedata.jaxb.Countries;

@Service
public class CountriesOfBirthImporter implements Importer {
	
	private final JAXBContext context;
	
	@Value("${xml.data.import.countriesOfBirth.url}")
	private URL xmlFileLocation;
	
	public CountriesOfBirthImporter() throws JAXBException {
		context = JAXBContext.newInstance(Countries.class);
	}

	@Override
	public void importData() throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
		Countries countries = (Countries) unmarshaller.unmarshal(xmlFileLocation);
	}

}
