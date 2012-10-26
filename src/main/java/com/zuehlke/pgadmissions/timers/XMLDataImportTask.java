package com.zuehlke.pgadmissions.timers;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.mail.DataImporterMailSender;
import com.zuehlke.pgadmissions.services.importers.Importer;

public class XMLDataImportTask {
	
	private static final Logger log = Logger.getLogger(XMLDataImportTask.class);
	
	private final List<Importer> importers;
	private final DataImporterMailSender mailSender;
	
	@Autowired
	public XMLDataImportTask(List<Importer> importers, DataImporterMailSender mailSender) {
		this.importers = importers;
		this.mailSender = mailSender;
	}
	
	
	@Scheduled(cron = "${xml.data.import.cron}")
	public void imoprtData() {
		for (Importer importer : importers) {
			try {
				importer.importData();
			} catch (XMLDataImportException e) {
				log.error("Error importing reference data.", e);
				String message = e.getMessage();
				Throwable cause = e.getCause();
				if(cause != null) {
					message += "\n" + cause.toString();
				}
				mailSender.sendErrorMessage(message);
			}
		}
	}

}
