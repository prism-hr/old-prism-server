package com.zuehlke.pgadmissions.timers;

import java.net.Authenticator;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;

@Service
public class XMLDataImportTask {

    private final Logger log = LoggerFactory.getLogger(XMLDataImportTask.class);

    @Autowired
    private EntityImportService entityImportService;

    @Autowired
    private MailSendingService mailService;

    @Autowired
    private RoleService roleService;

    @Scheduled(cron = "${xml.data.import.cron}")
    public void importData() {
        for (ImportedEntityFeed importedEntityFeed : entityImportService.getImportedEntityFeeds()) {
            String maxRedirects = null;
            try {
                maxRedirects = System.getProperty("http.maxRedirects");
                System.setProperty("http.maxRedirects", "5");

                entityImportService.importEntities(importedEntityFeed);
            } catch (XMLDataImportException e) {
                log.error("Error importing reference data.", e);
                String message = e.getMessage();
                Throwable cause = e.getCause();
                if (cause != null) {
                    message += "\n" + cause.toString();
                }

                mailService.sendImportErrorMessage(message);

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
