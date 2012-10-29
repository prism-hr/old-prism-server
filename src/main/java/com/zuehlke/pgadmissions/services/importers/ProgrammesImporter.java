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

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.adapters.PrismProgrammeAdapter;
import com.zuehlke.pgadmissions.referencedata.jaxb.Programmes;
import com.zuehlke.pgadmissions.referencedata.jaxb.Programmes.Programme;

@Service
public class ProgrammesImporter implements Importer {
	
	private static final Logger log = Logger.getLogger(ProgrammesImporter.class);
	
	private final JAXBContext context;
	private final URL xmlFileLocation;
	private final ProgramInstanceDAO programDAO;
	private final ImportService importService;
	
	@Autowired
	public ProgrammesImporter(ProgramInstanceDAO programDAO, ImportService importService,
			@Value("${xml.data.import.prismProgrammes.url}") URL xmlFileLocation) throws JAXBException {
		this.programDAO = programDAO;
		this.importService = importService;
		this.xmlFileLocation = xmlFileLocation;
		context = JAXBContext.newInstance(Programmes.class);
	}

	@Override
	@Transactional
	public void importData() throws XMLDataImportException {
		log.info("Starting the import from xml file: " + xmlFileLocation);
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Programmes programmes = (Programmes) unmarshaller.unmarshal(xmlFileLocation);
			List<PrismProgrammeAdapter> importData = createAdapter(programmes);
			List<ProgramInstance> currentData = programDAO.getAllProgramInstances();
			List<ProgramInstance> changes = importService.merge(currentData, importData);
			for (ProgramInstance programInstance : changes) {
				programDAO.save(programInstance);
			}
			log.info("Import done. Wrote " + changes.size() + " change(s) to the database.");
		} catch (Throwable e) {
			throw new XMLDataImportException("Error during the import of file: " + xmlFileLocation, e);
		}
	}

	private List<PrismProgrammeAdapter> createAdapter(Programmes programmes) {
		List<PrismProgrammeAdapter> result = new ArrayList<PrismProgrammeAdapter>(programmes.getProgramme().size());
		for (Programme programme : programmes.getProgramme()) {
			result.add(new PrismProgrammeAdapter(programme));
		}
		return result;
	}


}
