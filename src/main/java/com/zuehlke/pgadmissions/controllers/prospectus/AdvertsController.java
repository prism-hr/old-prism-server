package com.zuehlke.pgadmissions.controllers.prospectus;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

    @ModelAttribute("selectedAdvert")
    public Advert getSelectedAdvert(final HttpServletRequest request) {
        DefaultSavedRequest defaultSavedRequest = (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        String advertId = getSavedRequestTextParam(defaultSavedRequest, "advert");
        String programCode = getSavedRequestTextParam(defaultSavedRequest, "program");
        String projectId = getSavedRequestTextParam(defaultSavedRequest, "project");
        return advertService.getAdvertFromSession(advertId, programCode, projectId);
    }

    @RequestMapping(value = "embedded", method = RequestMethod.GET)
    public String getOpportunities(@RequestParam(required = false) OpportunityListType key, @RequestParam(required = false) String value,
            @ModelAttribute Advert selectedAdvert) {
        List<Advert> adverts = null;

        if (key == null && value == null) {
            adverts = advertService.getActiveAdverts();
            prepareAdvertList(adverts, key, selectedAdvert);
        } else {
            switch (key) {
            case OPPORTUNITIESBYFEEDID:
                adverts = advertService.getAdvertsByFeedId(Integer.parseInt(value));
                prepareAdvertList(adverts, key, selectedAdvert);
                break;
            case OPPORTUNITIESBYUSERUPI:
                adverts = advertService.getAdvertsByUserUPI(value);
                prepareAdvertList(adverts, key, selectedAdvert);
                break;
            case OPPORTUNITIESBYUSERUSERNAME:
                adverts = advertService.getAdvertsByUserUsername(value);
                prepareAdvertList(adverts, key, selectedAdvert);
                break;
            case RECOMMENDEDOPPORTUNTIIES:
                adverts = advertService.getRecommendedAdverts(userDAO.get(Integer.parseInt(value)));
                break;
            }
        }

        Map<String, Object> map = Maps.newHashMap();
        List<AdvertDTO> activeAdverts = convertAdverts(adverts);
        map.put("adverts", activeAdverts);
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
            }
        }
    }

    @RequestMapping(value = "/standaloneOpportunities", method = RequestMethod.GET)
    public String standaloneOpportunities(@RequestParam(required = false) OpportunityListType key, @RequestParam(required = false) String value, ModelMap model) {
        if (!(key == null || value == null)) {
            model.addAttribute("key", key);
            model.addAttribute("value", value);
        }
        model.put("shouldOpenNewTab", "true");
        return "public/login/standalone";
    }

    private String getSavedRequestTextParam(DefaultSavedRequest request, String paramName) {
        String[] values = request.getParameterValues(paramName);
        if (!ArrayUtils.isEmpty(values) && !StringUtils.isBlank(values[0])) {
            return values[0];
        }
        return null;
    }

    private List<AdvertDTO> convertAdverts(List<Advert> activeAdverts) {
        List<AdvertDTO> newList = new ArrayList<AdvertDTO>();
        AdvertConverter converter = new AdvertConverter();
        for (Advert advert : activeAdverts) {
            newList.add(converter.convert(advert));
        }
        return newList;
    }

    private class AdvertConverter {

        public AdvertDTO convert(Advert input) {
            Program program = advertService.getProgram(input);
            AdvertDTO dto = null;
            if (program != null) {
                dto = new AdvertDTO(input.getId());
                dto.setProgramCode(program.getCode());
                dto.setTitle(program.getTitle());
                dto.setClosingDate(programDAO.getNextClosingDate(program));
                Person primarySupervisor = toPerson(programDAO.getFirstAdministratorForProgram(program));
                dto.setPrimarySupervisor(primarySupervisor);
                if (primarySupervisor != null) {
                    dto.setSupervisorEmail(primarySupervisor.getEmail());
                }
                dto.setType("program");
                dto.setStudyDuration(input.getStudyDuration());
            } else {
                Project project = advertService.getProject(input);
                ProjectAdvertDTO projectDto = new ProjectAdvertDTO(input.getId());
                projectDto.setProjectId(project.getId());
                program = project.getProgram();
                projectDto.setProgramCode(program.getCode());
                projectDto.setStudyDuration(project.getAdvert().getStudyDuration());
                projectDto.setTitle(input.getTitle());
                projectDto.setClosingDate(programDAO.getNextClosingDate(program));
                RegisteredUser supervisor = project.getPrimarySupervisor();
                projectDto.setPrimarySupervisor(toPerson(supervisor));
                projectDto.setSupervisorEmail(supervisor.getEmail());
                projectDto.setSecondarySupervisor(toPerson(project.getSecondarySupervisor()));
                projectDto.setType("project");
                dto = projectDto;
            }
            dto.setDescription(input.getDescription());
            dto.setFunding(input.getFunding());
            return dto;
        }

        private Person toPerson(RegisteredUser user) {
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

}
