package com.zuehlke.pgadmissions.controllers;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.FullTextSearchService;

@Controller
@RequestMapping("/autosuggest")
public class AutosuggestUserController {

    private final FullTextSearchService searchService;

    public AutosuggestUserController() {
        this(null);
    }
    
    @Autowired
    public AutosuggestUserController(final FullTextSearchService searchService) {
        this.searchService = searchService;
    }
    
    @RequestMapping(value="/users/firstname/{searchTerm}", method = RequestMethod.GET)
    @ResponseBody
    public String provideSuggestionsForFirstname(@PathVariable final String searchTerm) {
        return convertToJson(searchService.getMatchingUsersWithFirstnameLike(searchTerm));
    }
    
    @RequestMapping(value="/users/lastname/{searchTerm}", method = RequestMethod.GET)
    @ResponseBody
    public String provideSuggestionsForLastname(@PathVariable final String searchTerm) {
        return convertToJson(searchService.getMatchingUsersWithLastnameLike(searchTerm));
    }
    
    @RequestMapping(value="/users/email/{searchTerm}", method = RequestMethod.GET)
    @ResponseBody
    public String provideSuggestionsForEmail(@PathVariable final String searchTerm) {
        return convertToJson(searchService.getMatchingUsersWithEmailLike(searchTerm));
    }
    
    private String convertToJson(final List<RegisteredUser> users) {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(RegisteredUser.class, new JsonSerializer<RegisteredUser>() {
            @Override
            public JsonElement serialize(final RegisteredUser src, final Type typeOfSrc, final JsonSerializationContext context) {
                JsonObject wrapper = new JsonObject();
                wrapper.add("k", new JsonPrimitive(src.getFirstName()));
                wrapper.add("v", new JsonPrimitive(src.getLastName()));
                wrapper.add("d", new JsonPrimitive(src.getEmail()));
                return wrapper;
            }
        });
        return gson.create().toJson(users);
    }
}
