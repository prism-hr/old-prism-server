package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.referencedata.jaxb.Domiciles;

@Service
public class CountriesOfDomicileImporter implements Importer {
	
	private final JAXBContext context;	
	
	@Value("${xml.data.import.countriesOfDomicile.url}")
	private URL xmlFileLocation;
	
	@Autowired
	private DomicileDAO domicileDAO;
	@Autowired
	private ImportService importService;
	
	public CountriesOfDomicileImporter() throws JAXBException {
		context = JAXBContext.newInstance(Domiciles.class);
	}

	@Override
	public void importData() throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Domiciles domiciles = (Domiciles) unmarshaller.unmarshal(xmlFileLocation);
	}

}
