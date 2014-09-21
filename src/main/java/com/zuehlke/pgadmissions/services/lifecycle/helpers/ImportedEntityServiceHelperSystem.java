package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.BooleanUtils;
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
import com.zuehlke.pgadmissions.domain.GeocodableLocation;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.dto.InstitutionDomicileImportDTO;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.google.jaxb.GeocodeResponse;
import com.zuehlke.pgadmissions.iso.jaxb.CountryCodesType;
import com.zuehlke.pgadmissions.iso.jaxb.CountryType;
import com.zuehlke.pgadmissions.iso.jaxb.SubdivisionType;
import com.zuehlke.pgadmissions.services.GeocodableLocationService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class ImportedEntityServiceHelperSystem extends AbstractServiceHelper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HashMap<LocalDate, Integer> geocodingRequestTotals = Maps.newHashMap();

    @Value("${import.institutionDomicile.location}")
    private String institutionDomicileImportLocation;

    @Value("${import.countryCurrency.location}")
    private String countryCurrencyImportLocation;

    @Value("${integration.google.geocoding.code.import.data}")
    private Boolean googleGeocodeCode;

    @Value("${integration.google.geocoding.api.day.batch.limit}")
    private Integer googleGeocodeApiBatchLimit;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private GeocodableLocationService geocodableLocationService;

    @Autowired
    private SystemService systemService;

    @Override
    public void execute() throws DataImportException, IOException {
        LocalDate baseline = new LocalDate();
        System system = systemService.getSystem();
        LocalDate lastImportDate = system.getLastDataImportDate();
        if (lastImportDate == null || lastImportDate.isBefore(baseline)) {
            importInstitutionDomiciles();
            systemService.setLastDataImportDate(baseline);
        } else {
            logger.info("Skipped the import from file " + institutionDomicileImportLocation);
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
            throw new DataImportException("Error during the import of file: " + countryCurrencyImportLocation, e);
        } finally {
            reader.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<CountryType> unmarshalInstitutionDomiciles(final String fileLocation) throws IOException, JAXBException {
        URL fileUrl = new DefaultResourceLoader().getResource(fileLocation).getURL();
        JAXBContext jaxbContext = JAXBContext.newInstance(CountryCodesType.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<CountryCodesType> unmarshalled = (JAXBElement<CountryCodesType>) unmarshaller.unmarshal(fileUrl);
        CountryCodesType countryCodes = (CountryCodesType) unmarshalled.getValue();
        return countryCodes.getCountry();
    }

    private void mergeInstitutionDomiciles(List<CountryType> countries, Map<String, String> countryCurrencies) throws DataImportException,
            DeduplicationException, IOException, JAXBException, InterruptedException {
        LocalDate baseline = new LocalDate();
        removeExpiredGeocodingRequestTotals(baseline);
        importedEntityService.disableAllInstitutionDomiciles();
        for (CountryType country : countries) {
            InstitutionDomicileImportDTO importDTO = importedEntityService.mergeInstitutionDomicile(country, countryCurrencies);
            if (importDTO != null) {
                InstitutionDomicile domicile = importDTO.getDomicile();
                geocodeLocation(domicile, baseline);
                mergeInstitutionDomicileRegions(domicile.getId(), importDTO.getSubdivisions(), importDTO.getCategories(), baseline);
            }
        }
    }

    private void mergeInstitutionDomicileRegions(String domicileId, List<SubdivisionType> subdivisions, Map<Short, String> categories, LocalDate baseline)
            throws DeduplicationException, IOException, JAXBException, InterruptedException {
        for (SubdivisionType subdivision : subdivisions) {
            InstitutionDomicileRegion region = importedEntityService.mergeInstitutionDomicileRegion(domicileId, subdivision, categories);
            geocodeLocation(region, baseline);
            mergeNestedInstitutionDomicileRegions(domicileId, region.getId(), subdivision, categories, baseline);
        }
    }

    private void mergeNestedInstitutionDomicileRegions(String domicileId, String regionId, SubdivisionType subdivision, Map<Short, String> categories,
            LocalDate baseline) throws IOException, JAXBException, InterruptedException, DeduplicationException {
        for (SubdivisionType nestedSubdivision : subdivision.getSubdivision()) {
            InstitutionDomicileRegion nested = importedEntityService.mergeNestedInstitutionDomicileRegion(domicileId, regionId, nestedSubdivision, categories);
            geocodeLocation(nested, baseline);
            mergeNestedInstitutionDomicileRegions(domicileId, nested.getId(), nestedSubdivision, categories, baseline);
        }
    }

    private <T extends GeocodableLocation> void geocodeLocation(T location, LocalDate baseline) throws IOException, JAXBException, InterruptedException {
        Integer geocodedCounter = geocodingRequestTotals.get(baseline);
        geocodedCounter = geocodedCounter == null ? 0 : geocodedCounter;
        String address = location.getLocationString();
        if (BooleanUtils.isTrue(googleGeocodeCode) && geocodedCounter < googleGeocodeApiBatchLimit && !location.isGeocoded()) {
            GeocodeResponse response = geocodableLocationService.getLocation(location, address);
            if (response.getStatus().equals("OK")) {
                try {
                    logger.info("Geocoding location: " + address + " - request " + (geocodedCounter + 1) + " of " + googleGeocodeApiBatchLimit + " today");
                    geocodableLocationService.setLocation(location, response);
                    geocodingRequestTotals.put(baseline, geocodedCounter + 1);
                } catch (Exception e) {
                    logger.error("Error geocoding location: " + address, e);
                }
            } else if (location.getClass().equals(InstitutionDomicileRegion.class)) {
                geocodableLocationService.setFallbackLocation((InstitutionDomicileRegion) location);
                logger.info("Setting fallback location for: " + address + " - zero results in geocoding request");
            } else {
                logger.info("No geocoding location found for country " + address);
            }
        } else {
            logger.info("Skipped geocoding for location :" + address);
        }
    }
    
    private void removeExpiredGeocodingRequestTotals(LocalDate baseline) {
        for (LocalDate day : geocodingRequestTotals.keySet()) {
            if (day.isBefore(baseline)) {
                geocodingRequestTotals.remove(day);
            }
        }
    }

}
