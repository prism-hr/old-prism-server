package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.referencedata.jaxb.Qualifications.Qualification;

@Service
public class QualificationsImporter implements Importer {
	
	private final JAXBContext context;
	
	@Value("${xml.data.import.qualifications.url}")
	private URL xmlFileLocation;
	
	public QualificationsImporter() throws JAXBException {
		context = JAXBContext.newInstance(Qualification.class);
	}

	@Override
	public void importData() throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Qualification qualification = (Qualification) unmarshaller.unmarshal(xmlFileLocation);
	}

}
