package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.domain.Country;

@Service
public class CountryService {

	private final CountriesDAO countriesDAO;

	CountryService(){
		this(null);
	}
	
	@Autowired
	public CountryService(CountriesDAO countriesDAO) {
		this.countriesDAO = countriesDAO;
	
	}

	@Transactional
	public List<Country> getAllCountries() {
		return countriesDAO.getAllCountries();
	}

	@Transactional
	public Country getCountryById(Integer id) {
		return countriesDAO.getCountryById(id);
	}

}
