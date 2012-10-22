package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.referencedata.jaxb.Institutions;

@Service
public class InstitutionsImporter implements Importer {
	
	private final JAXBContext context;
	
	@Value("${xml.data.import.institutions.url}")
	private URL xmlFileLocation;
	
	public InstitutionsImporter() throws JAXBException {
		context = JAXBContext.newInstance(Institutions.class);
	}

	@Override
	public void importData() throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Institutions institutions = (Institutions) unmarshaller.unmarshal(xmlFileLocation);
	}

}
