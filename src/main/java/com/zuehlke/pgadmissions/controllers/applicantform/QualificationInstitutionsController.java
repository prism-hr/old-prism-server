package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.CountryService;

@RequestMapping("/update")
@Controller
public class QualificationInstitutionsController {

    private EncryptionHelper encryptionHelper;
    private CountryService countryService;
    private QualificationInstitutionDAO qualificationInstitutionDAO;

    QualificationInstitutionsController(){
        this(null, null, null);
    }
    
    @Autowired
    public QualificationInstitutionsController(EncryptionHelper encryptionHelper, 
            CountryService countryService, QualificationInstitutionDAO qualificationInstitutionDAO) {
        this.encryptionHelper = encryptionHelper;
        this.countryService = countryService;
        this.qualificationInstitutionDAO = qualificationInstitutionDAO;
    }
    
    @RequestMapping(value = "/getInstitutionInformation", method = RequestMethod.GET)
    @ResponseBody
    public String getInstitutions(@RequestParam String country_id, @RequestParam String term) {
        
        Integer decryptedCountryId = encryptionHelper.decryptToInteger(country_id);
        Country country = countryService.getCountryById(decryptedCountryId);
        
        List<QualificationInstitution> institutions = qualificationInstitutionDAO.getInstitutionsByCountryCode(country.getCode()); 
        List<String> institutionsNameList = new ArrayList<String>();
        for (QualificationInstitution inst : institutions) {
            if (StringUtils.containsIgnoreCase(inst.getName(), term)) {
                institutionsNameList.add(inst.getName());
            }
            
            // Might be worth to think about adding fuzzy matching here
            // LevenshteinDistance.computeLevenshteinDistance(term, inst.getName())
        }
        
        Gson gson = new Gson();
        return gson.toJson(institutionsNameList);
    }
}
