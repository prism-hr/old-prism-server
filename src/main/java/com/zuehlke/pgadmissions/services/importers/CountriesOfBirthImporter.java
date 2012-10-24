package com.zuehlke.pgadmissions.services.importers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.referencedata.adapters.CountryOfBirthAdapter;
import com.zuehlke.pgadmissions.referencedata.jaxb.Countries;

@Service
public class CountriesOfBirthImporter implements Importer {

	private static final Logger log = Logger
			.getLogger(CountriesOfBirthImporter.class);

	private final JAXBContext context;
	private final URL xmlFileLocation;

	@Autowired
	private final CountriesDAO countriesDAO;
	private final ImportService importService;

	@Autowired
	public CountriesOfBirthImporter(
			CountriesDAO countriesDAO,
			ImportService importService,
			@Value("${xml.data.import.countriesOfBirth.url}") URL xmlFileLocation)
			throws JAXBException {
		this.countriesDAO = countriesDAO;
		this.importService = importService;
		this.xmlFileLocation = xmlFileLocation;
		this.context = JAXBContext.newInstance(Countries.class);
	}

	@Override
	@Transactional
	public void importData() throws JAXBException {
		log.info("Starting the import from xml file: " + xmlFileLocation);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Countries countries = (Countries) unmarshaller
				.unmarshal(xmlFileLocation);
		List<CountryOfBirthAdapter> importData = createAdapter(countries);
		List<Country> currentData = countriesDAO.getAllCountries();
		List<Country> changes = importService.merge(currentData, importData);
		for (Country country : changes) {
			countriesDAO.save(country);
		}
		log.info("Import done. Wrote " + changes.size() + " change(s) to the database.");
	}

	private List<CountryOfBirthAdapter> createAdapter(Countries countries) {
		List<CountryOfBirthAdapter> result = new ArrayList<CountryOfBirthAdapter>(
				countries.getCountry().size());
		for (com.zuehlke.pgadmissions.referencedata.jaxb.Countries.Country country : countries
				.getCountry()) {
			result.add(new CountryOfBirthAdapter(country));
		}
		return result;
	}

}
