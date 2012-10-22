package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.referencedata.jaxb.Qualifications;

@Service
public class QualificationsImporter implements Importer {
	
	private final JAXBContext context;
	
	@Value("${xml.data.import.qualifications.url}")
	private URL xmlFileLocation;
	
	@Autowired
	private QualificationDAO qualificationDAO;
	
	public QualificationsImporter() throws JAXBException {
		context = JAXBContext.newInstance(Qualifications.class);
	}

	@Override
	public void importData() throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Qualifications qualification = (Qualifications) unmarshaller.unmarshal(xmlFileLocation);
	}

}
