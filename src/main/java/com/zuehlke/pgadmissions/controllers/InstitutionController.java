package com.zuehlke.pgadmissions.controllers;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import com.zuehlke.pgadmissions.services.EntityService;
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
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
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
    private EntityService entityService;

    @Autowired
    private UserService userService;

    private Gson gson;

    @PostConstruct
    public void customizeJsonSerializer() {
        gson = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                .registerTypeAdapter(ImportedInstitution.class, new JsonSerializer<ImportedInstitution>() {
                    @Override
                    public JsonElement serialize(ImportedInstitution src, Type typeOfSrc, JsonSerializationContext context) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("id", src.getId());
                        jsonObject.addProperty("name", src.getName());
                        return jsonObject;
                    }
                }).create();
    }

    @RequestMapping(value = "/getInstitutionInformation", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getInstitutions(@RequestParam Integer domicileId) {
        Domicile domicile = entityService.getById(Domicile.class, domicileId);
        List<ImportedInstitution> institutions = qualificationInstitutionService.getEnabledImportedInstitutionsByDomicile(domicile);
        return gson.toJson(institutions);
    }

    @RequestMapping(value = "/getUserInstitutionInformation", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getAdministratorInstitutions(@RequestParam Integer domicileId) {
        Domicile domicile = entityService.getById(Domicile.class, domicileId);
        Integer userId = userService.getCurrentUser().getId();
        LinkedHashMap<Object, Object> returnMap = Maps.newLinkedHashMap();
//        returnMap.put("userInstitutions", qualificationInstitutionService.getEnabledInstitutionsByUserIdAndDomicile(userId, domicile));
//        returnMap.put("otherInstitutions", qualificationInstitutionService.getEnabledInstitutionsByDomicileExludingUserId(userId, domicile));
        return gson.toJson(returnMap);
    }

}