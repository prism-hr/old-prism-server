package com.zuehlke.pgadmissions.services.helpers;

import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.LanguageQualificationType;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.dto.AdvertCategoryImportRowDTO;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.iso.jaxb.CountryCodesType;
import com.zuehlke.pgadmissions.iso.jaxb.CountryType;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.converters.ImportedEntityConverter;
import com.zuehlke.pgadmissions.services.converters.ImportedInstitutionConverter;
import com.zuehlke.pgadmissions.services.converters.ImportedLanguageQualificationTypeConverter;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ImportedEntityServiceHelper {

    private static final Logger logger = LoggerFactory.getLogger(ImportedEntityServiceHelper.class);

    @Value("${import.advertCategory.location}")
    private String advertCategoryImportLocation;

    @Value("${import.institutionDomicile.location}")
    private String institutionDomicileImportLocation;

    @Autowired
    private EntityService entityService;

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
        }
    }

    public void importEntities(ImportedEntityFeed importedEntityFeed) throws DataImportException {
        String fileLocation = importedEntityFeed.getLocation();
        logger.info("Starting the import from file: " + fileLocation);

        try {
            List unmarshalled = unmarshallEntities(importedEntityFeed);

            Class<ImportedEntity> entityClass = (Class<ImportedEntity>) importedEntityFeed.getImportedEntityType().getEntityClass();

            Institution institution = importedEntityFeed.getInstitution();
            if (entityClass.equals(Program.class)) {
                mergeImportedPrograms(institution, (List<ProgrammeOccurrence>) unmarshalled);
            } else {
                Function<Object, ? extends ImportedEntity> entityConverter;
                if (entityClass.equals(LanguageQualificationType.class)) {
                    entityConverter = new ImportedLanguageQualificationTypeConverter(institution);
                } else if (entityClass.equals(ImportedInstitution.class)) {
                    entityConverter = new ImportedInstitutionConverter(institution, entityService);
                } else {
                    entityConverter = ImportedEntityConverter.create(institution, entityClass);
                }

                if (entityClass.equals(ImportedInstitution.class)) {
                    Iterable<ImportedInstitution> newImportedEntities = Iterables.transform(unmarshalled, entityConverter);
                    mergeImportedInstitutions(importedEntityFeed.getInstitution(), newImportedEntities);
                } else {
                    Iterable<ImportedEntity> newImportedEntities = Iterables.transform(unmarshalled, entityConverter);
                    mergeImportedEntities(entityClass, importedEntityFeed.getInstitution(), newImportedEntities);
                }
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
            List<CountryType> unmarshalled = unmarshallInstitutionDomiciles(institutionDomicileImportLocation);
            mergeInstitutionDomiciles(unmarshalled);
        } catch (Exception e) {
            throw new DataImportException("Error during the import of file: " + institutionDomicileImportLocation, e);
        }
    }

    public List<CountryType> unmarshallInstitutionDomiciles(final String fileLocation) throws Exception {
        try {
            URL fileUrl = new DefaultResourceLoader().getResource(fileLocation).getURL();
            JAXBContext jaxbContext = JAXBContext.newInstance(CountryCodesType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<CountryCodesType> unmarshaled = (JAXBElement<CountryCodesType>) unmarshaller.unmarshal(fileUrl);
            CountryCodesType countryCodes = (CountryCodesType) unmarshaled.getValue();
            return countryCodes.getCountry();
        } finally {
            Authenticator.setDefault(null);
        }
    }

    private List<Object> unmarshallEntities(final ImportedEntityFeed importedEntityFeed) throws Exception {
        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(importedEntityFeed.getUsername(), importedEntityFeed.getPassword().toCharArray());
                }
            });

            URL fileUrl = new DefaultResourceLoader().getResource(importedEntityFeed.getLocation()).getURL();
            JAXBContext jaxbContext = JAXBContext.newInstance(importedEntityFeed.getImportedEntityType().getJaxbClass());
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Object unmarshaled = unmarshaller.unmarshal(fileUrl);
            return (List<Object>) PropertyUtils.getSimpleProperty(unmarshaled, importedEntityFeed.getImportedEntityType().getJaxbPropertyName());
        } finally {
            Authenticator.setDefault(null);
        }
    }

    private void mergeImportedEntities(Class<ImportedEntity> importedEntityClass, Institution institution, Iterable<ImportedEntity> transientImportedEntities)
            throws Exception {
        importedEntityService.disableAllEntities(importedEntityClass, institution);
        for (ImportedEntity transientImportedEntity : transientImportedEntities) {
            importedEntityService.mergeImportedEntity(importedEntityClass, institution, transientImportedEntity);
        }
    }

    private void mergeImportedInstitutions(Institution institution, Iterable<ImportedInstitution> transientImportedInstitutions) throws Exception {
        importedEntityService.disableAllEntities(ImportedInstitution.class, institution);
        for (ImportedInstitution transientImportedInstitution : transientImportedInstitutions) {
            importedEntityService.mergeImportedInstitution(institution, transientImportedInstitution);
        }
    }

    private void mergeImportedPrograms(Institution institution, List<ProgrammeOccurrence> importedPrograms) throws Exception {
        LocalDate baseline = new LocalDate();

        importedEntityService.disableAllImportedPrograms(institution, baseline);
        HashMultimap<String, ProgrammeOccurrence> batchedOccurrences = getBatchedImportedPrograms(importedPrograms);

        for (String programCode : batchedOccurrences.keySet()) {
            Set<ProgrammeOccurrence> occurrencesInBatch = batchedOccurrences.get(programCode);
            importedEntityService.mergeImportedProgram(institution, occurrencesInBatch, baseline);
        }
    }

    private HashMultimap<String, ProgrammeOccurrence> getBatchedImportedPrograms(List<ProgrammeOccurrence> importedPrograms) {
        HashMultimap<String, ProgrammeOccurrence> batchedImports = HashMultimap.create();
        for (ProgrammeOccurrence occurrence : importedPrograms) {
            batchedImports.put(occurrence.getProgramme().getCode(), occurrence);
        }
        return batchedImports;
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

    public void mergeInstitutionDomiciles(List<CountryType> countries) throws DataImportException {
        importedEntityService.disableAllInstitutionDomiciles();
        for (CountryType country : countries) {
            importedEntityService.mergeInstitutionDomicile(country);
        }
    }

}
