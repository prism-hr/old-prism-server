package com.zuehlke.pgadmissions.timers;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.mail.PrismMailMessageException;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.importers.Importer;

@Service
public class XMLDataImportTask {

    private final Logger log = LoggerFactory.getLogger(XMLDataImportTask.class);

    private final List<Importer> importers;

    private Authenticator authenticator;

    private final String maxRedirects;
    
    private final MailSendingService mailService;
    
    private final UserService userService;

    @Autowired
    public XMLDataImportTask(List<Importer> importers,
            @Value("${xml.data.import.user}") final String user,
            @Value("${xml.data.import.password}") final String password,
            final MailSendingService mailService,
            final UserService userService) {
        this.importers = importers;
		this.mailService = mailService;
		this.userService = userService;
        this.authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password.toCharArray());
            }
        };
        this.maxRedirects = System.getProperty("http.maxRedirects");
    }

    @Scheduled(cron = "${xml.data.import.cron}")
    public void importData() {
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
                try {
                    mailService.sendImportErrorMessage(userService.getUsersInRole(Authority.SUPERADMINISTRATOR), message, new Date());
                } catch (PrismMailMessageException pmme) {
                    log.warn("{}", pmme);
                }
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
