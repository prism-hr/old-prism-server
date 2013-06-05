package com.zuehlke.pgadmissions.services.importers;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.adapters.PrismProgrammeAdapter;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;

@Service
public class ProgrammesImporter implements Importer {
	
    private final Logger log = LoggerFactory.getLogger(ProgrammesImporter.class);
	
	private final JAXBContext context;
	private final URL xmlFileLocation;
	private final ProgramInstanceDAO programInstanceDAO;
	private final ProgramDAO programDao;
	private final ImportService importService;

	private final String user;
	private final String password;

	public ProgrammesImporter() throws JAXBException {
	    this(null, null, null, null, null, null);
	}
	
	@Autowired
	public ProgrammesImporter(ProgramInstanceDAO programDAO, ProgramDAO programDao, ImportService importService,
			@Value("${xml.data.import.prismProgrammes.url}") URL xmlFileLocation, @Value("${xml.data.import.prismProgrammes.user}") String user,
			@Value("${xml.data.import.prismProgrammes.password}") String password) throws JAXBException {
		this.programInstanceDAO = programDAO;
		this.programDao = programDao;
		this.importService = importService;
		this.xmlFileLocation = xmlFileLocation;
		this.user = user;
		this.password = password;
		context = JAXBContext.newInstance(ProgrammeOccurrences.class);
	}

	@Override
	@Transactional
	public void importData() throws XMLDataImportException {
		log.info("Starting the import from xml file: " + xmlFileLocation);
		try {
			ProgrammeOccurrences programmes = unmarshallXML();
			List<PrismProgrammeAdapter> importData = createAdapter(programmes);
			List<ProgramInstance> currentData = programInstanceDAO.getAllProgramInstances();
			List<ProgramInstance> changes = importService.merge(currentData, importData);
			
			for (ProgramInstance programInstance : changes) {
				programInstanceDAO.save(programInstance);
				if (programInstance.getProgram().getId() == null) {
					programDao.save(programInstance.getProgram());
				}
			}

			// Update the require ATAS flag in our PRISM domain object
			for (ProgrammeOccurrence programmeOccurence : programmes.getProgrammeOccurrence()) {
			    Programme programme = programmeOccurence.getProgramme();
			    Program prismProgram = programDao.getProgramByCode(programme.getCode());
			    if (prismProgram != null) {
			        prismProgram.setAtasRequired(BooleanUtils.isTrue(programme.isAtasRegistered()));
			        programDao.save(prismProgram);
			    }
			}
			
			log.info("Import done. Wrote " + changes.size() + " change(s) to the database.");
		} catch (Exception e) {
			throw new XMLDataImportException("Error during the import of file: " + xmlFileLocation, e);
		}
	}
	
	private ProgrammeOccurrences unmarshallXML() throws JAXBException {
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Authenticator.setDefault(null);
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password.toCharArray());
			}
		});
		ProgrammeOccurrences programmes = (ProgrammeOccurrences) unmarshaller.unmarshal(xmlFileLocation);
		Authenticator.setDefault(null);
		return programmes;
	}

	private List<PrismProgrammeAdapter> createAdapter(ProgrammeOccurrences programmes) {
		List<PrismProgrammeAdapter> result = new ArrayList<PrismProgrammeAdapter>(programmes.getProgrammeOccurrence().size());
		for (ProgrammeOccurrence programme : programmes.getProgrammeOccurrence()) {
			result.add(new PrismProgrammeAdapter(programme));
		}
		return result;
	}
}
