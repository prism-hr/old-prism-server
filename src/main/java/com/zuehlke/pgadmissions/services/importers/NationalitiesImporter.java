package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.referencedata.jaxb.Nationalities;

@Service
public class NationalitiesImporter implements Importer {
	
	private final JAXBContext context;
	
	@Value("${xml.data.import.nationalities.url}")
	private URL xmlFileLocation;
	
	public NationalitiesImporter() throws JAXBException {
		context = JAXBContext.newInstance(Nationalities.class);
	}

	@Override
	public void importData() throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Nationalities nationalities = (Nationalities) unmarshaller.unmarshal(xmlFileLocation);
	}

}
