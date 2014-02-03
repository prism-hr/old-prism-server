package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.ImportedObject;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.adapters.CountryOfBirthAdapter;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.Countries;

@Service
public class CountriesOfBirthImporter implements Importer {

    private final Logger log = LoggerFactory.getLogger(CountriesOfBirthImporter.class);

    private final JAXBContext context;
    private final URL xmlFileLocation;
    private final CountriesDAO countriesDAO;
    private final ImportService importService;

    public CountriesOfBirthImporter() throws JAXBException {
        this(null, null, null);
    }

    @Autowired
    public CountriesOfBirthImporter(CountriesDAO countriesDAO, ImportService importService,
            @Value("${xml.data.import.countriesOfBirth.url}") URL xmlFileLocation) throws JAXBException {
        this.countriesDAO = countriesDAO;
        this.importService = importService;
        this.xmlFileLocation = xmlFileLocation;
        this.context = JAXBContext.newInstance(Countries.class);
    }

    @Override
    @Transactional
    public void importData() throws XMLDataImportException {
        log.info("Starting the import from xml file: " + xmlFileLocation);
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Countries countries = (Countries) unmarshaller.unmarshal(xmlFileLocation);
            List<CountryOfBirthAdapter> importData = createAdapter(countries);
            List<Country> currentData = countriesDAO.getAllCountries();
            List<Country> changes = importService.merge(currentData, importData);
            for (Country country : changes) {
                countriesDAO.save(country);
            }
            log.info("Import done. Wrote " + changes.size() + " change(s) to the database.");
        } catch (Exception e) {
            throw new XMLDataImportException("Error during the import of file: " + xmlFileLocation, e);
        }
    }

    private List<CountryOfBirthAdapter> createAdapter(Countries countries) {
        List<CountryOfBirthAdapter> result = new ArrayList<CountryOfBirthAdapter>(countries.getCountry().size());
        for (com.zuehlke.pgadmissions.referencedata.v2.jaxb.Countries.Country country : countries.getCountry()) {
            result.add(new CountryOfBirthAdapter(country));
        }
        return result;
    }

    @Override
    public Class<? extends ImportedObject> getImportedType() {
        return Country.class;
    }

}
