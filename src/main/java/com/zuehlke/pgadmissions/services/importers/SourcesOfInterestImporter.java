package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.jaxb.SourcesOfInterest;

@Service
public class SourcesOfInterestImporter implements Importer {

	private final JAXBContext context;

	@Value("${xml.data.import.sourcesOfInterest.url}")
	private URL xmlFileLocation;

	public SourcesOfInterestImporter() throws JAXBException {
		context = JAXBContext.newInstance(SourcesOfInterest.class);
	}

	@Override
	public void importData() throws XMLDataImportException {
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			SourcesOfInterest sourcesOfInterest = (SourcesOfInterest) unmarshaller.unmarshal(xmlFileLocation);
		} catch (Throwable e) {
			throw new XMLDataImportException("Error during the import of file: " + xmlFileLocation, e);
		}
	}

}
