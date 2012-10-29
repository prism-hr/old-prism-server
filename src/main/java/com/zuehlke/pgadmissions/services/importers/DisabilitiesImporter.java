package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.DisabilityDAO;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.adapters.DisabilityAdapter;
import com.zuehlke.pgadmissions.referencedata.jaxb.Disabilities;
import com.zuehlke.pgadmissions.referencedata.jaxb.Disabilities.Disability;

@Service
public class DisabilitiesImporter implements Importer {
	
	private static final Logger log = Logger.getLogger(DisabilitiesImporter.class);

	private final JAXBContext context;

	private final URL xmlFileLocation;
	private final DisabilityDAO disabilityDAO;
	private final ImportService importService;

	@Autowired
	public DisabilitiesImporter(DisabilityDAO disabilityDAO, ImportService importService,
			@Value("${xml.data.import.disabilities.url}") URL xmlFileLocation) throws JAXBException {
		this.disabilityDAO = disabilityDAO;
		this.importService = importService;
		this.xmlFileLocation = xmlFileLocation;
		context = JAXBContext.newInstance(Disabilities.class);
	}

	@Override
	@Transactional
	public void importData() throws XMLDataImportException {
		log.info("Starting the import from xml file: " + xmlFileLocation);
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Disabilities disabilities = (Disabilities) unmarshaller.unmarshal(xmlFileLocation);
			List<DisabilityAdapter> importData = createAdapter(disabilities);
			List<com.zuehlke.pgadmissions.domain.Disability> currentData = disabilityDAO.getAllDisabilities();
			List<com.zuehlke.pgadmissions.domain.Disability> changes = importService.merge(currentData, importData);
			for (com.zuehlke.pgadmissions.domain.Disability disability : changes) {
				disabilityDAO.save(disability);
			}
			log.info("Import done. Wrote " + changes.size() + " change(s) to the database.");
		} catch (Throwable e) {
			throw new XMLDataImportException("Error during the import of file: " + xmlFileLocation, e);
		}
	}

	private List<DisabilityAdapter> createAdapter(Disabilities disabilities) {
		List<DisabilityAdapter> result = new ArrayList<DisabilityAdapter>(disabilities.getDisability().size());
		for (Disability disability : disabilities.getDisability()) {
			result.add(new DisabilityAdapter(disability));
		}
		return result;
	}

}
