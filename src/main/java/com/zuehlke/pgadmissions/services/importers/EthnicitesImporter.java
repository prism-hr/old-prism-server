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

import com.zuehlke.pgadmissions.dao.EthnicityDAO;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.adapters.EthnicityAdapter;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.Ethnicities;

@Service
public class EthnicitesImporter implements Importer {
	
    private final Logger log = LoggerFactory.getLogger(EthnicitesImporter.class);
	
	private final JAXBContext context;
	
	private final URL xmlFileLocation;
	
	private final EthnicityDAO ethnicityDAO;
	private final ImportService importService;
	
	public EthnicitesImporter() throws JAXBException {
	    this(null, null, null);
	}
	
	@Autowired
	public EthnicitesImporter(EthnicityDAO ethnicityDAO, ImportService importService,
			@Value("${xml.data.import.ethnicities.url}") URL xmlFileLocation) throws JAXBException {
		this.ethnicityDAO = ethnicityDAO;
		this.importService = importService;
		this.xmlFileLocation = xmlFileLocation;
		context = JAXBContext.newInstance(Ethnicities.class);
	}

	@Override
	@Transactional
	public void importData() throws XMLDataImportException {
		log.info("Starting the import from xml file: " + xmlFileLocation);
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Ethnicities ethnicities = (Ethnicities) unmarshaller.unmarshal(xmlFileLocation);
			List<EthnicityAdapter> importData = createAdapter(ethnicities);
			List<Ethnicity> currentData = ethnicityDAO.getAllEthnicities();
			List<Ethnicity> changes = importService.merge(currentData, importData);
			for (Ethnicity ethnicity : changes) {
				ethnicityDAO.save(ethnicity);
			}
			log.info("Import done. Wrote " + changes.size() + " change(s) to the database.");
		} catch (Exception e) {
			throw new XMLDataImportException("Error during the import of file: " + xmlFileLocation, e);
		}
	}

	private List<EthnicityAdapter> createAdapter(Ethnicities ethnicities) {
		List<EthnicityAdapter> result = new ArrayList<EthnicityAdapter>(ethnicities.getEthnicity().size());
		for (com.zuehlke.pgadmissions.referencedata.v2.jaxb.Ethnicities.Ethnicity ethnicity : ethnicities.getEthnicity()) {
			result.add(new EthnicityAdapter(ethnicity));
		}
		return result;
	}

    @Override
    public Class<?> getImportedType() {
        return Ethnicity.class;
    }

}
