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

import com.zuehlke.pgadmissions.dao.LanguageDAO;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.adapters.NationalityAdapter;
import com.zuehlke.pgadmissions.referencedata.jaxb.Nationalities;
import com.zuehlke.pgadmissions.referencedata.jaxb.Nationalities.Nationality;

@Service
public class NationalitiesImporter implements Importer {
	
	private static final Logger log = Logger.getLogger(NationalitiesImporter.class);
	
	private final JAXBContext context;
	
	private final URL xmlFileLocation;

	private final LanguageDAO languageDAO;

	private final ImportService importService;
	
	@Autowired
	public NationalitiesImporter(LanguageDAO languageDAO, ImportService importService,
			@Value("${xml.data.import.nationalities.url}") URL xmlFileLocation) throws JAXBException {
		this.languageDAO = languageDAO;
		this.importService = importService;
		this.xmlFileLocation = xmlFileLocation;
		context = JAXBContext.newInstance(Nationalities.class);
	}

	@Override
	public void importData() throws XMLDataImportException {
		log.info("Starting the import from xml file: " + xmlFileLocation);
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Nationalities nationalities = (Nationalities) unmarshaller.unmarshal(xmlFileLocation);
			List<NationalityAdapter> importData = createAdapter(nationalities);
			List<Language> currentData = languageDAO.getAllLanguages();
			List<Language> changes = importService.merge(currentData, importData);
			for (Language language : changes) {
				languageDAO.save(language);
			}
			log.info("Import done. Wrote " + changes.size() + " change(s) to the database.");
		} catch (Throwable e) {
			throw new XMLDataImportException("Error during the import of file: " + xmlFileLocation, e);
		}
	}

	private List<NationalityAdapter> createAdapter(Nationalities nationalities) {
		List<NationalityAdapter> result = new ArrayList<NationalityAdapter>(nationalities.getNationality().size());
		for (Nationality nationality : nationalities.getNationality()) {
			result.add(new NationalityAdapter(nationality));
		}
		return result;
	}

}
