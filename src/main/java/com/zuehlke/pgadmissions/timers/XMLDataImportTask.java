package com.zuehlke.pgadmissions.timers;

import static com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate.SYSTEM_IMPORT_ERROR_NOTIFICATION;

import java.net.Authenticator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.mail.NotificationService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;

@Service
public class XMLDataImportTask {

    private final Logger log = LoggerFactory.getLogger(XMLDataImportTask.class);

    @Autowired
    private EntityImportService entityImportService;

    @Autowired
    private NotificationService mailService;

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

                // TODO get admin recipients
                List<User> recipients = Lists.newArrayList();
                for (User recipient : recipients) {
                     mailService.sendEmailNotification(recipient, null, SYSTEM_IMPORT_ERROR_NOTIFICATION, null, ImmutableMap.of("errorMessage", message));
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
