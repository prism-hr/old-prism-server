package com.zuehlke.pgadmissions.services.helpers;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.dto.AdvertCategoryImportRowDTO;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.iso.jaxb.CountryCodesType;
import com.zuehlke.pgadmissions.iso.jaxb.CountryType;
import com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes.LanguageQualificationType;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.NotificationService;
import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.System;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@SuppressWarnings({"unchecked", "rawtypes"})
public class ImportedEntityServiceHelper {

    private static final Logger logger = LoggerFactory.getLogger(ImportedEntityServiceHelper.class);

    @Value("${context.environment}")
    private String contextEnvironment;

    @Value("${import.advertCategory.location}")
    private String advertCategoryImportLocation;

    @Value("${import.institutionDomicile.location}")
    private String institutionDomicileImportLocation;

    @Value("${import.countryCurrency.location}")
    private String countryCurrencyImportLocation;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private NotificationService notificationService;

    public void importReferenceData() throws Exception {
        institutionService.populateDefaultImportedEntityFeeds();

        for (ImportedEntityFeed importedEntityFeed : importedEntityService.getImportedEntityFeedsToImport()) {
            String maxRedirects = null;
            if (contextEnvironment.equals("prod") || contextEnvironment.equals("uat") || (!importedEntityFeed.isAuthenticated()
                    && importedEntityFeed.getImportedEntityType() != PrismImportedEntity.INSTITUTION)) {
                try {
                    maxRedirects = System.getProperty("http.maxRedirects");
                    System.setProperty("http.maxRedirects", "5");
                    importEntities(importedEntityFeed);
                } catch (DataImportException e) {
                    logger.error("Error importing reference data.", e);
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
            } else {
                logger.info("Skipped the import from file: " + importedEntityFeed.getLocation());
            }
        }
    }

    public void importEntities(ImportedEntityFeed importedEntityFeed) throws DataImportException {
        String fileLocation = importedEntityFeed.getLocation();
        logger.info("Starting the import from file: " + fileLocation);

        try {
            List unmarshalled = unmarshalEntities(importedEntityFeed);

            Class<ImportedEntity> importedEntityClass = (Class<ImportedEntity>) importedEntityFeed.getImportedEntityType().getEntityClass();

            Institution institution = importedEntityFeed.getInstitution();
            if (importedEntityClass.equals(Program.class)) {
                mergeImportedPrograms(institution, (List<ProgrammeOccurrence>) unmarshalled);
            } else if (importedEntityClass.equals(ImportedInstitution.class)) {
                mergeImportedInstitutions(institution, (List<com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution>) unmarshalled);
            } else if (importedEntityClass.equals(ImportedLanguageQualificationType.class)) {
                mergeImportedLanguageQualificationTypes(institution,
                        (List<com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes.LanguageQualificationType>) unmarshalled);
            } else {
                mergeImportedEntities(importedEntityClass, institution, (List<Object>) unmarshalled);
            }

            importedEntityService.setLastImportedDate(importedEntityFeed);
            // TODO: state change to institution ready to use.
        } catch (Exception e) {
            throw new DataImportException("Error during the import of file: " + fileLocation, e);
        }
    }

    public void importAdvertCategories() throws DataImportException {
        logger.info("Starting the import from file: " + advertCategoryImportLocation);
        try {
            URL fileUrl = new DefaultResourceLoader().getResource(advertCategoryImportLocation).getURL();
            CSVReader reader = new CSVReader(new InputStreamReader(fileUrl.openStream(), Charsets.UTF_8));
            mergeAdvertCategories(reader);
        } catch (Exception e) {
            throw new DataImportException("Error during the import of file: " + advertCategoryImportLocation, e);
        }
    }

    public void importInstitutionDomiciles() throws DataImportException {
        logger.info("Starting the import from file: " + institutionDomicileImportLocation);
        try {
            List<CountryType> unmarshalled = unmarshalInstitutionDomiciles(institutionDomicileImportLocation);
            Map<String, String> countryCurrencies = parseCountryCurrencies(countryCurrencyImportLocation);
            mergeInstitutionDomiciles(unmarshalled, countryCurrencies);
        } catch (Exception e) {
            throw new DataImportException("Error during the import of file: " + institutionDomicileImportLocation, e);
        }
    }

    /**
     * Parses country currency file.
     *
     * @return map of country codes to currency codes
     */
    private Map<String, String> parseCountryCurrencies(String importLocation) throws IOException {
        Map<String, String> countryCurrencies = Maps.newHashMap();
        URL fileUrl = new DefaultResourceLoader().getResource(importLocation).getURL();
        CSVReader reader = new CSVReader(new InputStreamReader(fileUrl.openStream(), Charsets.UTF_8));
        String[] row;
        while ((row = reader.readNext()) != null) {
            String countryCode = row[2];
            String currencyCode = row[14];
            countryCurrencies.put(countryCode, currencyCode);
        }
        return countryCurrencies;
    }

    public List<CountryType> unmarshalInstitutionDomiciles(final String fileLocation) throws Exception {
        try {
            URL fileUrl = new DefaultResourceLoader().getResource(fileLocation).getURL();
            JAXBContext jaxbContext = JAXBContext.newInstance(CountryCodesType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<CountryCodesType> unmarshalled = (JAXBElement<CountryCodesType>) unmarshaller.unmarshal(fileUrl);
            CountryCodesType countryCodes = (CountryCodesType) unmarshalled.getValue();
            return countryCodes.getCountry();
        } finally {
            Authenticator.setDefault(null);
        }
    }

    private List<Object> unmarshalEntities(final ImportedEntityFeed importedEntityFeed) throws Exception {
        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(importedEntityFeed.getUsername(), importedEntityFeed.getPassword().toCharArray());
                }
            });

            PrismImportedEntity importedEntityType = importedEntityFeed.getImportedEntityType();

            URL fileUrl = new DefaultResourceLoader().getResource(importedEntityFeed.getLocation()).getURL();
            JAXBContext jaxbContext = JAXBContext.newInstance(importedEntityType.getJaxbClass());

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new DefaultResourceLoader().getResource(importedEntityType.getSchemaLocation()).getFile());

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);

            Object unmarshalled = unmarshaller.unmarshal(fileUrl);
            return (List<Object>) PropertyUtils.getSimpleProperty(unmarshalled, importedEntityType.getJaxbPropertyName());
        } finally {
            Authenticator.setDefault(null);
        }
    }

    private void mergeImportedPrograms(Institution institution, List<ProgrammeOccurrence> programDefinitions) throws Exception {
        LocalDate baseline = new LocalDate();

        importedEntityService.disableAllImportedPrograms(institution, baseline);
        HashMultimap<String, ProgrammeOccurrence> batchedOccurrences = getBatchedImportedPrograms(programDefinitions);

        for (String programCode : batchedOccurrences.keySet()) {
            Set<ProgrammeOccurrence> occurrencesInBatch = batchedOccurrences.get(programCode);
            importedEntityService.mergeImportedProgram(institution, occurrencesInBatch, baseline);
        }
    }

    private void mergeImportedInstitutions(Institution institution,
                                           List<com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution> institutionDefinitions) throws Exception {
        importedEntityService.disableAllEntities(ImportedInstitution.class, institution);
        for (com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution transientImportedInstitution : institutionDefinitions) {
            importedEntityService.mergeImportedInstitution(institution, transientImportedInstitution);
        }
    }

    private void mergeImportedLanguageQualificationTypes(Institution institution, List<LanguageQualificationType> languageQualificationTypeDefinitions)
            throws Exception {
        importedEntityService.disableAllEntities(ImportedLanguageQualificationType.class, institution);
        for (LanguageQualificationType languageQualificationTypeDefinition : languageQualificationTypeDefinitions) {
            importedEntityService.mergeImportedLanguageQualificationType(institution, languageQualificationTypeDefinition);
        }
    }

    private void mergeImportedEntities(Class<ImportedEntity> importedEntityClass, Institution institution, List<Object> entityDefinitions) throws Exception {
        importedEntityService.disableAllEntities(importedEntityClass, institution);
        for (Object entityDefinition : entityDefinitions) {
            importedEntityService.mergeImportedEntity(importedEntityClass, institution, entityDefinition);
        }
    }

    private void mergeInstitutionDomiciles(List<CountryType> countries, Map<String, String> countryCurrencies) throws DataImportException {
        importedEntityService.disableAllInstitutionDomiciles();
        for (CountryType country : countries) {
            importedEntityService.mergeInstitutionDomicile(country, countryCurrencies);
        }
    }

    private void mergeAdvertCategories(CSVReader reader) throws Exception {
        importedEntityService.disableAllAdvertCategories();
        String[] row;
        while ((row = reader.readNext()) != null) {
            AdvertCategoryImportRowDTO importRow = getAdvertCategoryRowDescriptor(row);
            if (importRow != null) {
                importedEntityService.createOrUpdateAdvertCategory(importRow);
            }
        }
    }

    private AdvertCategoryImportRowDTO getAdvertCategoryRowDescriptor(String[] row) {
        if (row.length < 5) {
            return null;
        }
        for (int i = 0; i < 4; i++) {
            try {
                int id = Integer.parseInt(row[i]);
                return new AdvertCategoryImportRowDTO(id, row[4]);
            } catch (NumberFormatException e) {
            }
        }
        return null;
    }

    private HashMultimap<String, ProgrammeOccurrence> getBatchedImportedPrograms(List<ProgrammeOccurrence> importedPrograms) {
        HashMultimap<String, ProgrammeOccurrence> batchedImports = HashMultimap.create();
        for (ProgrammeOccurrence occurrence : importedPrograms) {
            batchedImports.put(occurrence.getProgramme().getCode(), occurrence);
        }
        return batchedImports;
    }

}
