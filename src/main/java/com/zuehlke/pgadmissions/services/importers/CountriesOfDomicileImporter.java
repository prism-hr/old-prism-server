package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.Domicile;

@Service
public class CountriesOfDomicileImporter implements Importer {
	
	private final JAXBContext context;
	
	@Value("${xml.data.import.countriesOfDomicile.url}")
	private URL xmlFileLocation;
	
	public CountriesOfDomicileImporter() throws JAXBException {
		context = JAXBContext.newInstance(Domicile.class);
	}

	@Override
	public void importData() throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Domicile domicile = (Domicile) unmarshaller.unmarshal(xmlFileLocation);
	}

}
