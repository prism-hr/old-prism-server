package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.dto.AdvertCategoryImportRowDTO;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.iso.jaxb.CountryCodesType;
import com.zuehlke.pgadmissions.iso.jaxb.CountryType;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class ImportedEntityServiceHelperSystem extends AbstractServiceHelper {

    private static final Logger logger = LoggerFactory.getLogger(ImportedEntityServiceHelperInstitution.class);
    
    @Value("${import.advertCategory.location}")
    private String advertCategoryImportLocation;

    @Value("${import.institutionDomicile.location}")
    private String institutionDomicileImportLocation;

    @Value("${import.countryCurrency.location}")
    private String countryCurrencyImportLocation;
    
    @Autowired
    private ImportedEntityService importedEntityService;
    
    @Autowired
    private SystemService systemService;
    
    @Override
    public void execute() throws DataImportException, IOException {
        LocalDate baseline = new LocalDate();
        System system = systemService.getSystem();
        LocalDate lastImportDate = system.getLastDataImportDate();
        if (lastImportDate == null || lastImportDate.isBefore(baseline)) {
            importInstitutionDomiciles();
            importAdvertCategories();
            systemService.setLastDataImportDate(baseline);
        }
    }

    private void importInstitutionDomiciles() throws DataImportException {
        logger.info("Starting the import from file: " + institutionDomicileImportLocation);
        try {
            List<CountryType> unmarshalled = unmarshalInstitutionDomiciles(institutionDomicileImportLocation);
            Map<String, String> countryCurrencies = parseCountryCurrencies(countryCurrencyImportLocation);
            mergeInstitutionDomiciles(unmarshalled, countryCurrencies);
        } catch (Exception e) {
            throw new DataImportException("Error during the import of file: " + institutionDomicileImportLocation, e);
        }
    }
    
    private void importAdvertCategories() throws DataImportException, IOException  {
        logger.info("Starting the import from file: " + advertCategoryImportLocation);
        URL fileUrl = new DefaultResourceLoader().getResource(advertCategoryImportLocation).getURL();
        CSVReader reader = new CSVReader(new InputStreamReader(fileUrl.openStream(), Charsets.UTF_8));
        try {
            mergeAdvertCategories(reader);
        } catch (Exception e) {
            throw new DataImportException("Error during the import of file: " + advertCategoryImportLocation, e);
        } finally {
            reader.close();
        }
    }

    private Map<String, String> parseCountryCurrencies(String importLocation) throws IOException, DataImportException {
        URL fileUrl = new DefaultResourceLoader().getResource(importLocation).getURL();
        CSVReader reader = new CSVReader(new InputStreamReader(fileUrl.openStream(), Charsets.UTF_8));
        try {
            Map<String, String> countryCurrencies = Maps.newHashMap();
            String[] row;
            while ((row = reader.readNext()) != null) {
                String countryCode = row[2];
                String currencyCode = row[14];
                countryCurrencies.put(countryCode, currencyCode);
            }
            return countryCurrencies;
        } catch (Exception e) {
            throw new DataImportException("Error during the import of file: " + advertCategoryImportLocation, e);
        } finally {
            reader.close();
        }
    }
    
    @SuppressWarnings("unchecked")
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
    
}
