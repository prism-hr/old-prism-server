package com.zuehlke.pgadmissions.controllers;

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

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.HibernateProxyTypeAdapter;

@RequestMapping("/update")
@Controller
public class InstitutionController {

    @Autowired
    private InstitutionService qualificationInstitutionService;
    
    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private UserService userService;

    private Gson gson;

    @PostConstruct
    public void customizeJsonSerializer() {
        gson = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                .registerTypeAdapter(Institution.class, new JsonSerializer<Institution>() {
                    @Override
                    public JsonElement serialize(Institution src, Type typeOfSrc, JsonSerializationContext context) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("code", src.getCode());
                        jsonObject.addProperty("name", src.getName());
                        return jsonObject;
                    }
                }).create();
    }

    @RequestMapping(value = "/getInstitutionInformation", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getInstitutions(@RequestParam Integer domicileId) {
        Domicile domicile = importedEntityService.getDomicileById(domicileId);
        List<Institution> institutions = qualificationInstitutionService.getEnabledInstitutionsByDomicile(domicile);
        return gson.toJson(institutions);
    }

    @RequestMapping(value = "/getUserInstitutionInformation", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getAdministratorInstitutions(@RequestParam Integer domicileId) {
        Domicile domicile = importedEntityService.getDomicileById(domicileId);
        Integer userId = userService.getCurrentUser().getId();
        LinkedHashMap<Object, Object> returnMap = Maps.newLinkedHashMap();
        returnMap.put("userInstitutions", qualificationInstitutionService.getEnabledInstitutionsByUserIdAndDomicile(userId, domicile));
        returnMap.put("otherInstitutions", qualificationInstitutionService.getEnabledInstitutionsByDomicileExludingUserId(userId, domicile));
        return gson.toJson(returnMap);
    }

}
