package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.EthnicityDAO;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.jaxb.Ethnicities;

@Service
public class EthnicitesImporter implements Importer {
	
	private final JAXBContext context;
	
	@Value("${xml.data.import.ethnicities.url}")
	private URL xmlFileLocation;
	
	@Autowired
	private EthnicityDAO ethnicityDAO;
	@Autowired
	private ImportService importService;
	
	public EthnicitesImporter() throws JAXBException {
		context = JAXBContext.newInstance(Ethnicities.class);
	}

	@Override
	public void importData() throws XMLDataImportException {
		try {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Ethnicities ethnicities = (Ethnicities) unmarshaller.unmarshal(xmlFileLocation);
	} catch (Throwable e) {
		throw new XMLDataImportException("Error during the import of file: " + xmlFileLocation, e);
	}
	}

}
