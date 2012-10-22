package com.zuehlke.pgadmissions.timers;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.importers.Importer;

@Service
public class XMLDataImportTask {
	
	@Autowired
	private List<Importer> importers;
	
	@Scheduled(cron = "${xml.data.import.cron}")
	public void imoprtData() {
		for (Importer importer : importers) {
			try {
				importer.importData(); //TODO @Async maybe?
			} catch (JAXBException e) {
				// TODO Auto-generated catch block - send email on fail
				e.printStackTrace();
			}
		}
	}

}
