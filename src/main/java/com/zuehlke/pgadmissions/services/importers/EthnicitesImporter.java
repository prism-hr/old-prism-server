package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.referencedata.jaxb.Ethnicities;

@Service
public class EthnicitesImporter implements Importer {
	
	private final JAXBContext context;
	
	@Value("${xml.data.import.ethnicities.url}")
	private URL xmlFileLocation;
	
	public EthnicitesImporter() throws JAXBException {
		context = JAXBContext.newInstance(Ethnicities.class);
	}

	@Override
	public void importData() throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Ethnicities ethnicities = (Ethnicities) unmarshaller.unmarshal(xmlFileLocation);
	}

}
