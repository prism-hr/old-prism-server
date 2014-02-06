package com.zuehlke.pgadmissions.controllers.applicantform;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

@RequestMapping("/update")
@Controller
public class QualificationInstitutionsController {

    @Autowired
    private EncryptionHelper encryptionHelper;
    
    @Autowired
    private DomicileDAO domicileDAO;
    
    @Autowired
    private QualificationInstitutionDAO qualificationInstitutionDAO;
    
    @RequestMapping(value = "/getInstitutionInformation", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getInstitutions(@RequestParam String country_id) throws IOException {
        if (StringUtils.isBlank(country_id)) {
            return StringUtils.EMPTY;
        }
        
        Integer decryptedCountryId = encryptionHelper.decryptToInteger(country_id);
        Domicile domicileCountry = domicileDAO.getDomicileById(decryptedCountryId);
        
        List<QualificationInstitution> institutions = qualificationInstitutionDAO.getEnabledInstitutionsByCountryCode(domicileCountry.getCode());
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(QualificationInstitution.class, new JsonSerializer<QualificationInstitution>() {
            @Override
            public JsonElement serialize(QualificationInstitution src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("code", src.getCode());
                jsonObject.addProperty("name", src.getName());
                return jsonObject;
            }
        });
        return gson.create().toJson(institutions);
    }
}