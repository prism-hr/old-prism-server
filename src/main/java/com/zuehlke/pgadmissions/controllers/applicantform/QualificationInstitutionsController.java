package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

@RequestMapping("/update")
@Controller
public class QualificationInstitutionsController {

    private EncryptionHelper encryptionHelper;
    private DomicileDAO domicileDAO;
    private QualificationInstitutionDAO qualificationInstitutionDAO;

    QualificationInstitutionsController(){
        this(null, null, null);
    }
    
    @Autowired
    public QualificationInstitutionsController(EncryptionHelper encryptionHelper, 
            DomicileDAO domicileDAO, QualificationInstitutionDAO qualificationInstitutionDAO) {
        this.encryptionHelper = encryptionHelper;
        this.domicileDAO = domicileDAO;
        this.qualificationInstitutionDAO = qualificationInstitutionDAO;
    }
    
    @RequestMapping(value = "/getInstitutionInformation", method = RequestMethod.GET)
    @ResponseBody
    public String getInstitutions(@RequestParam String country_id, @RequestParam String term) {
        Integer decryptedCountryId = encryptionHelper.decryptToInteger(country_id);
        Domicile country = domicileDAO.getDomicileById(decryptedCountryId);
        
        List<QualificationInstitution> institutions = qualificationInstitutionDAO.getInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive(country.getName(), term);
        List<String> institutionsNameList = new ArrayList<String>();
        for (QualificationInstitution inst : institutions) {
            institutionsNameList.add(inst.getName());
        }
        Gson gson = new Gson();
        return gson.toJson(institutionsNameList);
    }
}
