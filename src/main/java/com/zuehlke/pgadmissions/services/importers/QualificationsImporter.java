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

import com.zuehlke.pgadmissions.dao.QualificationTypeDAO;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.adapters.QualificationAdapter;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.Qualifications;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.Qualifications.Qualification;

@Service
public class QualificationsImporter implements Importer {
	
    private final Logger log = LoggerFactory.getLogger(QualificationsImporter.class);
	
	private final JAXBContext context;
	
	private final URL xmlFileLocation;
	
	private final QualificationTypeDAO qualificationDAO;
	private final ImportService importService;
	
	public QualificationsImporter() throws JAXBException {
	    this(null, null, null);
	}
	
	@Autowired
	public QualificationsImporter(QualificationTypeDAO qualificationDAO, ImportService importService,
			@Value("${xml.data.import.qualifications.url}") URL xmlFileLocation) throws JAXBException {
		this.qualificationDAO = qualificationDAO;
		this.importService = importService;
		this.xmlFileLocation = xmlFileLocation;
		context = JAXBContext.newInstance(Qualifications.class);
	}

	@Override
	@Transactional
	public void importData() throws XMLDataImportException {
		log.info("Starting the import from xml file: " + xmlFileLocation);
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Qualifications qualifications = (Qualifications) unmarshaller.unmarshal(xmlFileLocation);
			List<QualificationAdapter> importData = createAdapter(qualifications);
			List<QualificationType> currentData = qualificationDAO.getAllQualificationTypes();
			List<QualificationType> changes = importService.merge(currentData, importData);
			for (QualificationType qualification : changes) {
				qualificationDAO.save(qualification);
			}
			log.info("Import done. Wrote " + changes.size() + " change(s) to the database.");
		} catch (Exception e) {
			throw new XMLDataImportException("Error during the import of file: " + xmlFileLocation, e);
		}
	}

	private List<QualificationAdapter> createAdapter(Qualifications qualifications) {
		List<QualificationAdapter> result = new ArrayList<QualificationAdapter>(qualifications.getQualification().size());
		for (Qualification qualification : qualifications.getQualification()) {
			result.add(new QualificationAdapter(qualification));
		}
		return result;
	}

    @Override
    public Class<?> getImportedType() {
        return QualificationType.class;
    }

}
