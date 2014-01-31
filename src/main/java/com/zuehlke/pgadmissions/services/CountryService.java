package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.domain.Country;

@Service
@Transactional
public class CountryService {

    private final CountriesDAO countriesDAO;

    public CountryService() {
        this(null);
    }

    @Autowired
    public CountryService(CountriesDAO countriesDAO) {
        this.countriesDAO = countriesDAO;
    }

    public List<Country> getAllCountries() {
        return countriesDAO.getAllCountries();
    }

    public List<Country> getAllEnabledCountries() {
        return countriesDAO.getAllEnabledCountries();
    }

    public Country getCountryById(Integer id) {
        return countriesDAO.getCountryById(id);
    }
}
