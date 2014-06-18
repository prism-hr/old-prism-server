package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramExport;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismProgramExportFormat;
import com.zuehlke.pgadmissions.services.ProgramExportService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.FieldErrorUtils;

@Controller
@RequestMapping("/prospectus/researchOpportunitiesFeed")
public class ProgramExportController {

    private final UserService userService;
    
    private final ProgramService programsService; 
    
    private final ProgramExportService feedService;
    
    private final MessageSource messageSource;
    
    private static final String SUCCESS = "success";
    
    public ProgramExportController() {
        this(null, null, null, null);
    }
    
    @Autowired
    public ProgramExportController(final UserService userService, final ProgramService programsService,
            final ProgramExportService feedService, final MessageSource messageSource) {
        this.userService = userService;
        this.programsService = programsService;
        this.feedService = feedService;
        this.messageSource = messageSource;
    }
    
    @RequestMapping(value = "programmes", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> getProgrammes() {
        ArrayList<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
        for (Program p : programsService.getProgramsForWhichCanManageProjects(getCurrentUser())) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", p.getId());
            map.put("title", p.getTitle());
            response.add(map);
        }
        return response;
    }
    
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> getAllResearchOpportunitiesFeedForCurrentUser() {
        ArrayList<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
        List<ProgramExport> feeds = feedService.getAllFeedsForUser(getCurrentUser());
        for (ProgramExport feed : feeds) {
            response.add(convertToMap(feed));
        }
        return response;
    }
    
    @RequestMapping(value = "{feedId}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public Map<String, Object> deleteFeedById(@PathVariable final Integer feedId) {
        feedService.deleteById(feedId);
        return Collections.<String, Object>singletonMap(SUCCESS, true);
    }
    
    @RequestMapping(value = "{feedId}", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Map<String, Object> updateFeedById(@PathVariable final Integer feedId, @RequestBody final HashMap<String, Object> json) {
        User currentUser = getCurrentUser();
        List<Integer> selectedProgramIds = (List<Integer>) json.get("selectedPrograms");
        String feedFormat = (String) json.get("feedSize");
        String title = (String) json.get("feedTitle");
        
        Map<String, Object> responseMap = validate(selectedProgramIds, feedFormat, title, currentUser, true);
        if ((Boolean) responseMap.get(SUCCESS)) {
            ProgramExport feed = feedService.updateFeed(feedId, selectedProgramIds, currentUser, PrismProgramExportFormat.valueOf(feedFormat), title);
            responseMap.put("iframeCode", feedService.getIframeHtmlCode(feed));
        }
        return responseMap;
    }
    
    @RequestMapping(value = "{feedId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> getFeedById(@PathVariable final Integer feedId) {
        ArrayList<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
        ProgramExport feed = feedService.getById(feedId);
        response.add(convertToMap(feed));
        return response;
    }
    
    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Map<String, Object> saveFeed(@RequestBody final HashMap<String, Object> json) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        User currentUser = getCurrentUser();
        List<Integer> selectedProgramIds = (List<Integer>) json.get("selectedPrograms");
        String feedFormat = (String) json.get("feedSize");
        String title = (String) json.get("feedTitle");
        responseMap = validate(selectedProgramIds, feedFormat, title, currentUser);
        if ((Boolean) responseMap.get(SUCCESS)) {
            ProgramExport feed = feedService.saveNewFeed(selectedProgramIds, currentUser, PrismProgramExportFormat.valueOf(feedFormat), title);
            responseMap.put("iframeCode", feedService.getIframeHtmlCode(feed));
            responseMap.put("id", feed.getId());
        } 
        return responseMap;
    }
    
    protected User getCurrentUser() {
        return userService.getCurrentUser();
    }
    
    private HashMap<String, Object> convertToMap(final ProgramExport feed) {
        HashMap<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("id", feed.getId());
        responseMap.put("title", feed.getTitle());
        responseMap.put("feedSize", feed.getFormat());
        ArrayList<Integer> programId = new ArrayList<Integer>();
        for (Program p : feed.getPrograms()) {
            programId.add(p.getId());
        }
        responseMap.put("selectedPrograms", programId);
        responseMap.put("iframeCode", feedService.getIframeHtmlCode(feed));
        return responseMap;
    }
    
    private Map<String, Object> validate(final List<Integer> selectedProgramIds, final String feedFormat,
            final String title, final User currentUser) {
        return validate(selectedProgramIds, feedFormat, title, currentUser, false);
    }
    
    private Map<String, Object> validate(final List<Integer> selectedProgramIds, final String feedFormat,
            final String title, final User currentUser, boolean ignoreUniqueTitleContraint) {
        HashMap<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put(SUCCESS, true);
        
        if (selectedProgramIds == null || selectedProgramIds.isEmpty()) {
            responseMap.put(SUCCESS, false);
            responseMap.put("selectedPrograms", FieldErrorUtils.resolveMessage("dropdown.radio.select.none", messageSource));
        }
        
        try {
            PrismProgramExportFormat.valueOf(StringUtils.upperCase(feedFormat));
        } catch (Exception e) {
            responseMap.put(SUCCESS, false);
            responseMap.put("feedSize", FieldErrorUtils.resolveMessage("dropdown.radio.select.none", messageSource));
        }
        
        if (StringUtils.isBlank(title)) {
            responseMap.put(SUCCESS, false);
            responseMap.put("feedTitle", FieldErrorUtils.resolveMessage("text.field.empty", messageSource));
        } else if (!ignoreUniqueTitleContraint && !feedService.isUniqueFeedTitleForUser(title, currentUser)) {
            responseMap.put(SUCCESS, false);
            responseMap.put("feedTitle", FieldErrorUtils.resolveMessage("prospectus.researchOpportunityFeed.duplicate.title", messageSource));
        }
        
        return responseMap;
    }
    
}