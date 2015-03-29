package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ImportedEntityServiceHelperInstitution implements AbstractServiceHelper {

	@Value("${context.environment}")
	private String contextEnvironment;

	@Autowired
	private ImportedEntityService importedEntityService;

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private NotificationService notificationService;

	public void execute() throws DeduplicationException, InstantiationException, IllegalAccessException, IOException, JAXBException, SAXException,
	        BeansException, WorkflowEngineException, IntegrationException {
		institutionService.populateDefaultImportedEntityFeeds();
		for (ImportedEntityFeed importedEntityFeed : importedEntityService.getImportedEntityFeeds()) {
			String maxRedirects = null;
			try {
				maxRedirects = System.getProperty("http.maxRedirects");
				System.setProperty("http.maxRedirects", "5");
				importEntities(importedEntityFeed);
			} catch (DataImportException e) {
				String errorMessage = e.getMessage();
				Throwable cause = e.getCause();
				if (cause != null) {
					errorMessage += "\n" + cause.toString();
				}
				notificationService.sendDataImportErrorNotifications(importedEntityFeed.getInstitution(), errorMessage);
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

	private void importEntities(ImportedEntityFeed importedEntityFeed) throws IOException, JAXBException, SAXException, DataImportException,
	        DeduplicationException, InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IntegrationException {
		Institution institution = importedEntityFeed.getInstitution();
		if (contextEnvironment.equals("prod") || !institutionService.hasAuthenticatedFeeds(institution)) {
			List unmarshalled = unmarshalEntities(importedEntityFeed);
			if (unmarshalled != null) {
				Integer importedEntityFeedId = importedEntityFeed.getId();
				Class<?> importedEntityClass = (Class<?>) importedEntityFeed.getImportedEntityType().getEntityClass();
				if (importedEntityClass.equals(Program.class)) {
					importedEntityService.mergeImportedPrograms(importedEntityFeedId, institution, (List<ProgrammeOccurrence>) unmarshalled);
				} else if (importedEntityClass.equals(ImportedInstitution.class)) {
					importedEntityService.mergeImportedInstitutions(importedEntityFeedId, institution,
					        (List<com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution>) unmarshalled);
				} else if (importedEntityClass.equals(ImportedLanguageQualificationType.class)) {
					importedEntityService.mergeImportedLanguageQualificationTypes(importedEntityFeedId, institution,
					        (List<com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes.LanguageQualificationType>) unmarshalled);
				} else {
					importedEntityService.mergeImportedEntities(importedEntityFeedId, institution, (Class<ImportedEntity>) importedEntityClass,
					        (List<Object>) unmarshalled);
				}
			}
		}
	}

	private List<Object> unmarshalEntities(final ImportedEntityFeed importedEntityFeed) throws IOException, JAXBException, SAXException {
		try {
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(importedEntityFeed.getUsername(), importedEntityFeed.getPassword().toCharArray());
				}
			});

			DateTime lastImportedTimestamp = importedEntityFeed.getLastImportedTimestamp();
			PrismImportedEntity importedEntityType = importedEntityFeed.getImportedEntityType();

			URL fileUrl = new DefaultResourceLoader().getResource(importedEntityFeed.getLocation()).getURL();
			URLConnection connection = fileUrl.openConnection();
			Long lastModifiedTimestamp = connection.getLastModified();

			if (lastImportedTimestamp == null || lastModifiedTimestamp == 0
			        || new LocalDateTime(lastModifiedTimestamp).toDateTime().isAfter(lastImportedTimestamp)) {
				return readImportedData(importedEntityType, fileUrl);
			}

			return null;
		} finally {
			Authenticator.setDefault(null);
		}
	}

	@CacheEvict("institutionStaticData")
	private List<Object> readImportedData(PrismImportedEntity importedEntityType, URL fileUrl) throws JAXBException, SAXException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(importedEntityType.getJaxbClass());

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(new DefaultResourceLoader().getResource(importedEntityType.getSchemaLocation()).getFile());

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setSchema(schema);

		try {
			Object unmarshalled = unmarshaller.unmarshal(fileUrl);
			return (List<Object>) PrismReflectionUtils.getProperty(unmarshalled, importedEntityType.getJaxbPropertyName());
		} catch (Exception e) {
			return null;
		}
	}

}
