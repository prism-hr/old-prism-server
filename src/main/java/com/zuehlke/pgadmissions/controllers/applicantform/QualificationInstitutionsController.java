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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
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
    
    @RequestMapping(value = "/getInstitutionInformation", method = RequestMethod.GET, produces = "application/json")
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
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(new JsonPrimitive(encryptionHelper.encrypt(src.getId())));
                jsonArray.add(new JsonPrimitive(src.getCode()));
                jsonArray.add(new JsonPrimitive(src.getName()));
                jsonArray.add(new JsonPrimitive(src.getDomicileCode()));
                return jsonArray;
            }
        });
        return gson.create().toJson(institutions);
    }
}
