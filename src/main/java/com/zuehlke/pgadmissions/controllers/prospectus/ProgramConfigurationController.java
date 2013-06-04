package com.zuehlke.pgadmissions.controllers.prospectus;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramAdvert;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.HibernateUtils;
import com.zuehlke.pgadmissions.validators.AbstractValidator;
import com.zuehlke.pgadmissions.validators.ProgramAdvertValidator;
import com.zuehlke.pgadmissions.validators.ProgramClosingDateValidator;

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Controller
@RequestMapping("/prospectus/programme")
public class ProgramConfigurationController {

    public static final String LINK_TO_APPLY = "/private/prospectus/link_to_apply.ftl";
    public static final String BUTTON_TO_APPLY = "/private/prospectus/button_to_apply.ftl";

    private final UserService userService;

    private final ProgramsService programsService;
    private final String host;

    private final ApplicationContext applicationContext;

    private final DurationOfStudyPropertyEditor durationOfStudyPropertyEditor;

    private final ProgramAdvertValidator programAdvertValidator;

    private final ProgramClosingDateValidator closingDateValidator;
    private final DatePropertyEditor datePropertyEditor;
    private final ProgramPropertyEditor programPropertyEditor;

    private final FreeMarkerConfigurer freeMarkerConfigurer;
    private Template buttonToApplyTemplate;
    private Template linkToApplyTemplate;

    public ProgramConfigurationController() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ProgramConfigurationController(UserService userService, ProgramsService programsService, @Value("${application.host}") final String host,
                    ApplicationContext applicationContext, ProgramAdvertValidator programAdvertValidator,
                    DurationOfStudyPropertyEditor durationOfStudyPropertyEditor, FreeMarkerConfigurer freeMarkerConfigurer,
                    ProgramClosingDateValidator closingDateValidator, DatePropertyEditor datePropertyEditor, ProgramPropertyEditor programPropertyEditor) {
        this.userService = userService;
        this.programsService = programsService;
        this.host = host;
        this.applicationContext = applicationContext;
        this.programAdvertValidator = programAdvertValidator;
        this.durationOfStudyPropertyEditor = durationOfStudyPropertyEditor;
        this.freeMarkerConfigurer = freeMarkerConfigurer;
        this.closingDateValidator = closingDateValidator;
        this.datePropertyEditor = datePropertyEditor;
        this.programPropertyEditor = programPropertyEditor;
    }

    @PostConstruct
    public void loadFreeMarkerTemplates() throws IOException {
        linkToApplyTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(LINK_TO_APPLY);
        buttonToApplyTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(BUTTON_TO_APPLY);
    }

    @InitBinder("programAdvert")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(programAdvertValidator);
        binder.registerCustomEditor(Integer.class, "durationOfStudyInMonth", durationOfStudyPropertyEditor);
    }

    @InitBinder("programClosingDate")
    public void registerEditorsAndValidatorsForClosingDate(WebDataBinder binder) {
        binder.setValidator(closingDateValidator);
        binder.registerCustomEditor(Program.class, "program", programPropertyEditor);
        binder.registerCustomEditor(Date.class, "closingDate", datePropertyEditor);
    }

    @ModelAttribute("program")
    public Program getProgram(@RequestParam(required = false) String programCode) {
        if (programCode == null) {
            return null;
        }
        return programsService.getProgramByCode(programCode);
    }

    private ProgramAdvert getProgrameAdvert(String programCode) {
        Program program = getProgram(programCode);
        if (program == null) {
            return null;
        }
        return program.getAdvert();
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("programmes")
    public List<Program> getProgrammes() {
        if (userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
            return programsService.getAllPrograms();
        }
        return userService.getCurrentUser().getProgramsOfWhichAdministrator();
    }

    @RequestMapping(value = "/getAdvertData", method = RequestMethod.GET)
    @ResponseBody
    public String getAdvertData(@RequestParam String programCode) throws TemplateException, IOException {
        ProgramAdvert advert = getProgrameAdvert(programCode);

        Map<String, Object> result = Maps.newHashMap();
        result.put("advert", HibernateUtils.unproxy(advert));

        HashMap<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("programCode", programCode);
        dataMap.put("host", host);

        result.put("buttonToApply", processTemplate(buttonToApplyTemplate, dataMap));
        result.put("linkToApply", processTemplate(linkToApplyTemplate, dataMap));

        return new Gson().toJson(result);
    }

    @RequestMapping(value = "/saveProgramAdvert", method = RequestMethod.POST)
    @ResponseBody
    public String saveProgramAdvert(@RequestParam String programCode, @Valid ProgramAdvert programAdvert, BindingResult result, HttpServletRequest request) {
        Map<String, Object> map = Maps.newHashMap();

        Program program = programsService.getProgramByCode(programCode);

        if (program == null) {
            map.put("program", applicationContext.getMessage(AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE, null, request.getLocale()));
        }

        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                map.put(error.getField(), applicationContext.getMessage(error, request.getLocale()));
            }
        }

        if (map.isEmpty()) {
            program.setAdvert(programAdvert);
            programsService.save(program);
            map.put("success", "true");
        }

        Gson gson = new Gson();
        return gson.toJson(map);
    }

    @RequestMapping(value = "/addClosingDate", method = RequestMethod.POST)
    @ResponseBody
    public String addClosingDate(@Valid ProgramClosingDate programClosingDate, BindingResult result, HttpServletRequest request) {
        Map<String, Object> map = Maps.newHashMap();

        for (FieldError error : result.getFieldErrors()) {
            map.put(error.getField(), applicationContext.getMessage(error, request.getLocale()));
        }

        if(!result.hasErrors())
        {
        		Program program = programClosingDate.getProgram();
	            program.addClosingDate(programClosingDate);
	            programsService.save(program);
	            programClosingDate.setProgram(null);
	            map.put("programClosingDate", programClosingDate);
        }

        Gson gson = new Gson();
        return gson.toJson(map);
    }

    @RequestMapping(value = "/updateClosingDate", method = RequestMethod.POST)
    @ResponseBody
    public String updateClosingDate(@Valid ProgramClosingDate programClosingDate, BindingResult result, HttpServletRequest request) {
    	Map<String, Object> map = Maps.newHashMap();
    	
    	for (FieldError error : result.getFieldErrors()) {
    		map.put(error.getField(), applicationContext.getMessage(error, request.getLocale()));
    	}
    	
    	if(!result.hasErrors())
    	{
    		Program program = programClosingDate.getProgram();
    		program.updateClosingDate(programClosingDate);
    		programsService.save(program);
    		programClosingDate.setProgram(null);
    		map.put("programClosingDate", programClosingDate);
    	}
    	
    	Gson gson = new Gson();
    	return gson.toJson(map);
    }
    
    @RequestMapping(value = "/getClosingDates", method = RequestMethod.GET)
    @ResponseBody
    public String getClosingDates(@RequestParam String programCode, HttpServletRequest request) throws TemplateException, IOException {
    	Map<String, Object> map = Maps.newHashMap();
        Program program = programsService.getProgramByCode(programCode);
        
        if (program == null) {
            map.put("program", applicationContext.getMessage(AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE, null, request.getLocale()));
        }
        
        if(map.isEmpty()){
        	map.put("programCode", programCode);
        
			map.put("closingDates", unproxyDates(program.getClosingDates()));
        }
        return new Gson().toJson(map);
    }

    @RequestMapping(value = "/removeClosingDate", method = RequestMethod.POST)
    @ResponseBody
    public String removeClosingDate(@RequestParam String programCode, @RequestParam Integer closingDateId, HttpServletRequest request) throws TemplateException, IOException {
    	Map<String, Object> map = Maps.newHashMap();
    	Program program = programsService.getProgramByCode(programCode);
    	
    	if (program == null) {
    		map.put("program", applicationContext.getMessage(AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE, null, request.getLocale()));
    	}
    	
    	if(map.isEmpty()){
    		program.removeClosingDate(closingDateId);
    		programsService.save(program);
    		map.put("removedDate", closingDateId);
    	}
    	return new Gson().toJson(map);
    }


	private List<ProgramClosingDate> unproxyDates(List<ProgramClosingDate> closingDates) {
		if(CollectionUtils.isNotEmpty(closingDates)){
			for(ProgramClosingDate closingDate:closingDates){
				closingDate.setProgram(null);
			}
			return closingDates;
		}
		return Collections.<ProgramClosingDate>emptyList();
	}

	protected String processTemplate(Template template, Map<String, String> dataMap) throws TemplateException, IOException {
        StringWriter writer = new StringWriter();
        template.process(dataMap, writer);
        String result = writer.toString();
        writer.close();
        return result;
    }
}
