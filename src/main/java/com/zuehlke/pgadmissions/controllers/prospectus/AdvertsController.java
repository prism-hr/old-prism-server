package com.zuehlke.pgadmissions.controllers.prospectus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.OpportunityListType;
import com.zuehlke.pgadmissions.dto.AdvertDTO;
import com.zuehlke.pgadmissions.dto.ProjectAdvertDTO;
import com.zuehlke.pgadmissions.services.AdvertService;

@Controller
@RequestMapping("/opportunities")
public class AdvertsController {

    private final AdvertService advertService;
    private final ProgramDAO programDAO;
    private final UserDAO userDAO;

    public AdvertsController() {
        this(null, null, null);
    }

    @Autowired
    public AdvertsController(final AdvertService advertService, final ProgramDAO programDAO, final UserDAO userDAO) {
        this.advertService = advertService;
        this.programDAO = programDAO;
        this.userDAO = userDAO;
    }

    @RequestMapping(value = "embedded", method = RequestMethod.GET)
    @ResponseBody
    public String getOpportunities(@RequestParam(required = false) OpportunityListType feedKey, @RequestParam(required = false) String feedKeyValue,
            final HttpServletRequest request) {
        List<Advert> adverts = null;
        DefaultSavedRequest defaultSavedRequest = (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        String advertId = getSavedRequestTextParam(defaultSavedRequest, "advert");
        String programCode = getSavedRequestTextParam(defaultSavedRequest, "program");
        String projectId = getSavedRequestTextParam(defaultSavedRequest, "project");
        Advert selectedAdvert = advertService.getAdvertFromSession(advertId, programCode, projectId);
        
        if (feedKey == null && feedKeyValue == null) {
            adverts = advertService.getActiveAdverts();
            prepareAdvertList(adverts, feedKey, selectedAdvert);
        } else {
            switch (feedKey) {
            case OPPORTUNITIESBYFEEDID:
                adverts = advertService.getAdvertsByFeedId(Integer.parseInt(feedKeyValue));
                prepareAdvertList(adverts, feedKey, selectedAdvert);
                break;
            case OPPORTUNITIESBYUSERUPI:
                adverts = advertService.getAdvertsByUserUPI(feedKeyValue);
                prepareAdvertList(adverts, feedKey, selectedAdvert);
                break;
            case OPPORTUNITIESBYUSERUSERNAME:
                adverts = advertService.getAdvertsByUserUsername(feedKeyValue);
                prepareAdvertList(adverts, feedKey, selectedAdvert);
                break;
            case RECOMMENDEDOPPORTUNTIIES:
                adverts = advertService.getRecommendedAdverts(userDAO.get(Integer.parseInt(feedKeyValue)));
                break;
            case CURRENTOPPORTUNITY:
                adverts = Arrays.asList(advertService.getAdvertById(Integer.parseInt(feedKeyValue)));
                prepareAdvertList(adverts, feedKey, adverts.get(0));
                break;
            }
        }

        Map<String, Object> map = Maps.newHashMap();
        List<AdvertDTO> convertedAdverts = convertAdverts(adverts);
        map.put("adverts", convertedAdverts);
        return new Gson().toJson(map);
    }

    private void prepareAdvertList(List<Advert> adverts, OpportunityListType type, Advert selectedAdvert) {
        if (type != OpportunityListType.RECOMMENDEDOPPORTUNTIIES) {
            if (!adverts.isEmpty()) {
                Collections.shuffle(adverts, new Random(System.currentTimeMillis()));
            }
            if (selectedAdvert != null) {
                adverts.remove(selectedAdvert);
                adverts.add(0, selectedAdvert);
                selectedAdvert.setSelected(true);
            }
        }
    }

    @RequestMapping(value = "/standaloneOpportunities", method = RequestMethod.GET)
    public String getStandaloneOpportunities(@RequestParam(required = false) OpportunityListType feedKey, @RequestParam(required = false) String feedKeyValue, ModelMap model) {
        if (!(feedKey == null || feedKeyValue == null)) {
            model.put("feedKey", feedKey);
            model.put("feedKeyValue", feedKeyValue);
        }
        model.put("shouldOpenNewTab", "true");
        return "public/login/standalone";
    }

    private String getSavedRequestTextParam(DefaultSavedRequest savedRequest, String paramName) {
        if (savedRequest != null) {
            String[] values = savedRequest.getParameterValues(paramName);
            if (!ArrayUtils.isEmpty(values) && !StringUtils.isBlank(values[0])) {
                return values[0];
            }
        }
        return null;
    }

    private List<AdvertDTO> convertAdverts(List<Advert> activeAdverts) {
        List<AdvertDTO> newList = new ArrayList<AdvertDTO>();
        for (Advert advert : activeAdverts) {
            newList.add(convertAdvertToDto(advert));
        }
        return newList;
    }
    
    private AdvertDTO convertAdvertToDto(Advert advert) {
        Program program = advertService.getProgram(advert);
        AdvertDTO dto = null;
        if (program != null) {
            dto = new AdvertDTO(advert.getId());
            dto.setProgramCode(program.getCode());
            dto.setTitle(program.getTitle());
            dto.setClosingDate(programDAO.getNextClosingDate(program));
            Person primarySupervisor = convertAdvertUserToContact(programDAO.getFirstAdministratorForProgram(program));
            dto.setPrimarySupervisor(primarySupervisor);
            if (primarySupervisor != null) {
                dto.setSupervisorEmail(primarySupervisor.getEmail());
            }
            dto.setType("program");
            dto.setStudyDuration(advert.getStudyDuration());
        } else {
            Project project = advertService.getProject(advert);
            ProjectAdvertDTO projectDto = new ProjectAdvertDTO(advert.getId());
            projectDto.setProjectId(project.getId());
            program = project.getProgram();
            projectDto.setProgramCode(program.getCode());
            projectDto.setStudyDuration(project.getAdvert().getStudyDuration());
            projectDto.setTitle(advert.getTitle());
            projectDto.setClosingDate(programDAO.getNextClosingDate(program));
            RegisteredUser supervisor = project.getPrimarySupervisor();
            projectDto.setPrimarySupervisor(convertAdvertUserToContact(supervisor));
            projectDto.setSupervisorEmail(supervisor.getEmail());
            projectDto.setSecondarySupervisor(convertAdvertUserToContact(project.getSecondarySupervisor()));
            projectDto.setType("project");
            dto = projectDto;
        }
        dto.setDescription(advert.getDescription());
        dto.setFunding(advert.getFunding());
        dto.setSelected(advert.getSelected());
        return dto;
    }
    
    private Person convertAdvertUserToContact(RegisteredUser user) {
        if (user == null) {
            return null;
        }
        Person person = new Person();
        person.setFirstname(user.getFirstName());
        person.setLastname(user.getLastName());
        person.setEmail(user.getEmail());
        return person;
    }

}
