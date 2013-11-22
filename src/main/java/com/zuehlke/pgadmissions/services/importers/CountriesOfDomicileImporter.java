package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.adapters.CountryOfDomicileAdapter;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.Domiciles;

@Service
public class CountriesOfDomicileImporter implements Importer {
	
    private final Logger log = LoggerFactory.getLogger(CountriesOfDomicileImporter.class);

	private final JAXBContext context;
	private final URL xmlFileLocation;
	private final DomicileDAO domicileDAO;
	private final ImportService importService;

	public CountriesOfDomicileImporter() throws JAXBException {
	    this(null, null, null);
	}
	
	@Autowired
	public CountriesOfDomicileImporter(DomicileDAO countriesDAO, ImportService importService,
			@Value("${xml.data.import.countriesOfDomicile.url}") URL xmlFileLocation) throws JAXBException {
		domicileDAO = countriesDAO;
		this.importService = importService;
		this.xmlFileLocation = xmlFileLocation;
		context = JAXBContext.newInstance(Domiciles.class);
	}

	@Override
	@Transactional
	public void importData() throws XMLDataImportException {
		log.info("Starting the import from xml file: " + xmlFileLocation);
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Domiciles domiciles = (Domiciles) unmarshaller.unmarshal(xmlFileLocation);
			List<CountryOfDomicileAdapter> importData = createAdapter(domiciles);
			List<Domicile> currentData = domicileDAO.getAllDomiciles();
			List<Domicile> changes = importService.merge(currentData, importData);
			for (Domicile domicile : changes) {
				domicileDAO.save(domicile);
			}
			log.info("Import done. Wrote " + changes.size() + " change(s) to the database.");
		} catch (Exception e) {
			throw new XMLDataImportException("Error during the import of file: " + xmlFileLocation, e);
		}
	}

	private List<CountryOfDomicileAdapter> createAdapter(Domiciles domiciles) {
		List<CountryOfDomicileAdapter> result = new ArrayList<CountryOfDomicileAdapter>(domiciles.getDomicile().size());
		for (com.zuehlke.pgadmissions.referencedata.v2.jaxb.Domiciles.Domicile domicile : domiciles.getDomicile()) {
			result.add(new CountryOfDomicileAdapter(domicile));
		}
		return result;
	}

}
