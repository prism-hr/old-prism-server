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

import com.zuehlke.pgadmissions.dao.SourcesOfInterestDAO;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.adapters.SourceOfInterestAdapter;
import com.zuehlke.pgadmissions.referencedata.jaxb.SourcesOfInterest;
import com.zuehlke.pgadmissions.referencedata.jaxb.SourcesOfInterest.SourceOfInterest;

@Service
public class SourcesOfInterestImporter implements Importer {
	
	private static final Logger log = Logger.getLogger(SourcesOfInterestImporter.class);

	private final JAXBContext context;

	private final URL xmlFileLocation;

	private final SourcesOfInterestDAO sourcesOfInterestDAO;

	private final ImportService importService;

	public SourcesOfInterestImporter() throws JAXBException {
	    this(null, null, null);
	}
	
	@Autowired
	public SourcesOfInterestImporter(SourcesOfInterestDAO sourcesOfInterestDAO, ImportService importService,
			@Value("${xml.data.import.sourcesOfInterest.url}") URL xmlFileLocation) throws JAXBException {
		this.sourcesOfInterestDAO = sourcesOfInterestDAO;
		this.importService = importService;
		this.xmlFileLocation = xmlFileLocation;
		context = JAXBContext.newInstance(SourcesOfInterest.class);
	}

	@Override
	@Transactional
	public void importData() throws XMLDataImportException {
		log.info("Starting the import from xml file: " + xmlFileLocation);
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			SourcesOfInterest sourcesOfInterest = (SourcesOfInterest) unmarshaller.unmarshal(xmlFileLocation);
			List<SourceOfInterestAdapter> importData = createAdapter(sourcesOfInterest);
			List<com.zuehlke.pgadmissions.domain.SourcesOfInterest> currentData = sourcesOfInterestDAO.getAllSourcesOfInterest();
			List<com.zuehlke.pgadmissions.domain.SourcesOfInterest> changes = importService.merge(currentData, importData);
			for (com.zuehlke.pgadmissions.domain.SourcesOfInterest sourceOfInterest : changes) {
				sourcesOfInterestDAO.save(sourceOfInterest);
			}
			log.info("Import done. Wrote " + changes.size() + " change(s) to the database.");
		} catch (Throwable e) {
			throw new XMLDataImportException("Error during the import of file: " + xmlFileLocation, e);
		}
	}

	private List<SourceOfInterestAdapter> createAdapter(SourcesOfInterest sourcesOfInterest) {
		List<SourceOfInterestAdapter> result = new ArrayList<SourceOfInterestAdapter>(sourcesOfInterest.getSourceOfInterest().size());
		for (SourceOfInterest sourceOfInterest : sourcesOfInterest.getSourceOfInterest()) {
			result.add(new SourceOfInterestAdapter(sourceOfInterest));
		}
		return result;
	}


}
