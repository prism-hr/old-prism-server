package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.jaxb.Programmes;

@Service
public class ProgrammesImporter implements Importer {
	
	private final JAXBContext context;
	
	@Value("${xml.data.import.prismProgrammes.url}")
	private URL xmlFileLocation;
	
	public ProgrammesImporter() throws JAXBException {
		context = JAXBContext.newInstance(Programmes.class);
	}

	@Override
	public void importData() throws XMLDataImportException {
		try {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Programmes programmes = (Programmes) unmarshaller.unmarshal(xmlFileLocation);
		} catch (Throwable e) {
			throw new XMLDataImportException("Error during the import of file: " + xmlFileLocation, e);
		}
	}

}
