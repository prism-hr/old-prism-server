package com.zuehlke.pgadmissions.controllers.applicantform;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.QualificationInstitutionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.HibernateProxyTypeAdapter;

@RequestMapping("/update")
@Controller
public class QualificationInstitutionsController {

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private DomicileDAO domicileDAO;

    @Autowired
    private QualificationInstitutionService qualificationInstitutionService;

    @Autowired
    private UserService userService;

    private Gson gson;

    @PostConstruct
    public void customizeJsonSerializer() throws IOException {
        gson = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                .registerTypeAdapter(QualificationInstitution.class, new JsonSerializer<QualificationInstitution>() {
                    @Override
                    public JsonElement serialize(QualificationInstitution src, Type typeOfSrc, JsonSerializationContext context) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("code", src.getCode());
                        jsonObject.addProperty("name", src.getName());
                        return jsonObject;
                    }
                }).create();
    }

    @RequestMapping(value = "/getInstitutionInformation", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getInstitutions(@RequestParam String country_id) throws IOException {
        if (Strings.isNullOrEmpty(country_id)) {
            return "";
        }

        Integer decryptedCountryId = encryptionHelper.decryptToInteger(country_id);
        Domicile domicileCountry = domicileDAO.getDomicileById(decryptedCountryId);

        List<QualificationInstitution> institutions = qualificationInstitutionService.getEnabledInstitutionsByDomicileCode(domicileCountry.getCode());
        return gson.toJson(institutions);
    }

    @RequestMapping(value = "/getUserInstitutionInformation", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getInstitutionsUserCategorized(@RequestParam String country_id) throws IOException {
        if (Strings.isNullOrEmpty(country_id)) {
            return "";
        }
        RegisteredUser user = userService.getCurrentUser();

        Integer decryptedCountryId = encryptionHelper.decryptToInteger(country_id);
        String domicileCode = domicileDAO.getDomicileById(decryptedCountryId).getCode();

        List<QualificationInstitution> institutions = qualificationInstitutionService.getEnabledInstitutionsByDomicileCode(domicileCode);
        List<QualificationInstitution> userInstitutions = Lists.newLinkedList();
        for (QualificationInstitution i : user.getInstitutions()) {
            if (i.getDomicileCode().equals(domicileCode)) {
                userInstitutions.add(i);
            }
        }

        Iterables.removeAll(institutions, userInstitutions);

        LinkedHashMap<Object, Object> returnMap = Maps.newLinkedHashMap();
        returnMap.put("userInstitutions", userInstitutions);
        returnMap.put("otherInstitutions", institutions);
        return gson.toJson(returnMap);
    }
}
