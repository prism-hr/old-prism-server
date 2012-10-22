package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.DisabilityDAO;
import com.zuehlke.pgadmissions.referencedata.jaxb.Disabilities;

@Service
public class DisabilitiesImporter implements Importer {
	
	private final JAXBContext context;
	
	@Value("${xml.data.import.disabilities.url}")
	private URL xmlFileLocation;
	
	@Autowired
	private DisabilityDAO disabilityDAO;
	
	public DisabilitiesImporter() throws JAXBException {
		context = JAXBContext.newInstance(Disabilities.class);
	}

	@Override
	public void importData() throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Disabilities disabilities = (Disabilities) unmarshaller.unmarshal(xmlFileLocation);
	}

}
