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

import com.zuehlke.pgadmissions.dao.QualificationInstitutionReferenceDAO;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.adapters.QualificationInstitutionReferenceAdapter;
import com.zuehlke.pgadmissions.referencedata.jaxb.Institutions;
import com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution;

@Service
public class InstitutionsImporter implements Importer {
	
    private static final Logger log = Logger.getLogger(InstitutionsImporter.class);
    
	private final JAXBContext context;
	
	@Value("${xml.data.import.institutions.url}")
	private URL xmlFileLocation;
	
	@Autowired
	private ImportService importService;
	
	private final QualificationInstitutionReferenceDAO qualificationInstitutionReferenceDAO;
	
	public InstitutionsImporter() throws JAXBException {
	    this(null, null, null);
	}
	
	@Autowired
	public InstitutionsImporter(ImportService importService, @Value("${xml.data.import.institutions.url}") URL xmlFileLocation, 
	        QualificationInstitutionReferenceDAO dao) throws JAXBException {
	    this.importService = importService;
	    this.xmlFileLocation = xmlFileLocation;
	    this.qualificationInstitutionReferenceDAO = dao;
		context = JAXBContext.newInstance(Institutions.class);
	}

	@Override
	@Transactional
	public void importData() throws XMLDataImportException {
		try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Institutions institutions = (Institutions) unmarshaller.unmarshal(xmlFileLocation);
            List<QualificationInstitutionReferenceAdapter> importData = createAdapter(institutions);
            
            List<com.zuehlke.pgadmissions.domain.QualificationInstitutionReference> currentData = qualificationInstitutionReferenceDAO.getAllInstitutions();
            List<com.zuehlke.pgadmissions.domain.QualificationInstitutionReference> changes = importService.merge(currentData, importData);
            for (com.zuehlke.pgadmissions.domain.QualificationInstitutionReference institution : changes) {
                qualificationInstitutionReferenceDAO.save(institution);
            }
            log.info("Import done. Wrote " + changes.size() + " change(s) to the database.");
		} catch (Exception e) {
			throw new XMLDataImportException("Error during the import of file: " + xmlFileLocation, e);
		}
	}

	private List<QualificationInstitutionReferenceAdapter> createAdapter(Institutions institutions) {
        List<QualificationInstitutionReferenceAdapter> result = new ArrayList<QualificationInstitutionReferenceAdapter>(institutions.getInstitution().size());
        for (Institution inst : institutions.getInstitution()) {
            result.add(new QualificationInstitutionReferenceAdapter(inst));
        }
        return result;
    }
}
