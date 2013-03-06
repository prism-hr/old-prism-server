package com.zuehlke.pgadmissions.timers;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.mail.DataImporterMailSender;
import com.zuehlke.pgadmissions.services.importers.Importer;

public class XMLDataImportTask {

    private final Logger log = LoggerFactory.getLogger(XMLDataImportTask.class);

    private final List<Importer> importers;
    private final DataImporterMailSender mailSender;

    private Authenticator authenticator;

    private final String maxRedirects;

    @Autowired
    public XMLDataImportTask(List<Importer> importers, DataImporterMailSender mailSender,
            @Value("${xml.data.import.user}") final String user,
            @Value("${xml.data.import.password}") final String password) {
        this.importers = importers;
        this.mailSender = mailSender;
        this.authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password.toCharArray());
            }
        };
        this.maxRedirects = System.getProperty("http.maxRedirects");
    }

    @Scheduled(cron = "${xml.data.import.cron}")
    public void imoprtData() {
        for (Importer importer : importers) {
            try {
                System.setProperty("http.maxRedirects", "5");
                Authenticator.setDefault(authenticator);
                importer.importData();
            } catch (XMLDataImportException e) {
                log.error("Error importing reference data.", e);
                String message = e.getMessage();
                Throwable cause = e.getCause();
                if (cause != null) {
                    message += "\n" + cause.toString();
                }
                mailSender.sendErrorMessage(message);
            } finally {
                Authenticator.setDefault(null);
                if (maxRedirects != null) {
                    System.setProperty("http.maxRedirects", maxRedirects);
                } else {
                    System.clearProperty("http.maxRedirects");
                }
            }
        }
    }
}
