package com.zuehlke.pgadmissions.controllers.prospectus;

import static java.util.Collections.singletonMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.dto.AdvertDTO;
import com.zuehlke.pgadmissions.dto.ProjectAdvertDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.ResearchOpportunitiesFeedService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Controller
@RequestMapping("/opportunities")
public class AdvertsController {

    private final AdvertService advertService;

    private static final AdvertDTO NULL_ADVERT = new AdvertDTO(Integer.MIN_VALUE);

    private final ResearchOpportunitiesFeedService feedService;

    private final ProgramsService programsService;

    public AdvertsController() {
        this(null, null, null);
    }

    @Autowired
    public AdvertsController(final AdvertService advertService, final ResearchOpportunitiesFeedService feedService, final ProgramsService programsService) {
        this.advertService = advertService;
        this.feedService = feedService;
        this.programsService = programsService;
    }

    @ModelAttribute("advertId")
    public Integer getSelectedAdvert(final Integer advert, final HttpServletRequest request) {
        return advert == null ? getSelectedAdvertFromSession(request) : advert;
    }

    private Integer getSelectedAdvertFromSession(final HttpServletRequest request) {
        Integer advertId = null;
        DefaultSavedRequest defaultSavedRequest = (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (defaultSavedRequest != null) {
            advertId = getSavedRequestNumericParam(defaultSavedRequest, "advert");
            if (advertId == null) {
                Integer projectId = getSavedRequestNumericParam(defaultSavedRequest, "project");
                if (projectId != null) {
                    Project project = programsService.getProject(projectId);
                    if (project != null) {
                        advertId = project.getAdvert().getId();
                    }
                }
            }
            if (advertId == null) {
                String programCode = getSavedRequestTextParam(defaultSavedRequest, "program");
                if (programCode != null) {
                    Program program = programsService.getProgramByCode(programCode);
                    if (program != null) {
                        advertId = program.getAdvert().getId();
                    }
                }
            }
        }
        return advertId;
    }

    private Integer getSavedRequestNumericParam(DefaultSavedRequest request, String paramName) {
        String[] values = request.getParameterValues(paramName);
        if (!ArrayUtils.isEmpty(values) && !StringUtils.isBlank(values[0]) && StringUtils.isNumeric(values[0])) {
            return Integer.valueOf(values[0]);
        }
        return null;
    }

    private String getSavedRequestTextParam(DefaultSavedRequest request, String paramName) {
        String[] values = request.getParameterValues(paramName);
        if (!ArrayUtils.isEmpty(values) && !StringUtils.isBlank(values[0])) {
            return values[0];
        }
        return null;
    }

    @RequestMapping(value = "/standaloneOpportunities", method = RequestMethod.GET)
    public String standaloneOpportunities(@RequestParam(required = false) Integer feed, @RequestParam(required = false) String user,
            @RequestParam(required = false) String upi, ModelMap model) {
        if (feed != null) {
            model.addAttribute("feedId", feed);
        }
        if (user != null) {
            model.addAttribute("user", user);
        }
        if (upi != null) {
            model.addAttribute("upi", upi);
        }
        model.put("shouldOpenNewTab", "true");
        return "public/login/standalone";
    }

    @RequestMapping(value = "/activeOpportunities", method = RequestMethod.GET)
    @ResponseBody
    public String activeAdverts(@ModelAttribute("advertId") Integer advert) {
        Map<String, Object> map = Maps.newHashMap();
        List<AdvertDTO> activeAdverts = convertAdverts(advertService.getActiveAdverts());
        Collections.shuffle(activeAdverts, new Random(System.currentTimeMillis()));
        AdvertDTO selectedAdvert = getSelectedAdvert(advert, activeAdverts);
        setSelectedAndBringToFront(selectedAdvert, activeAdverts);
        for (AdvertDTO advertDTO : activeAdverts) {
            advertDTO.setDescription(replaceLineChangeWithHTMLLineChange(advertDTO.getDescription()));
        }
        map.put("adverts", activeAdverts);
        return new Gson().toJson(map);
    }

    private String replaceLineChangeWithHTMLLineChange(String originalDescription) {
        return originalDescription.replaceAll("\n", "<br>");
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/feeds", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map getFeeds(@RequestParam(required = false) Integer feedId, @RequestParam(required = false) String user, @RequestParam(required = false) String upi) {
        List<ResearchOpportunitiesFeed> feeds = null;
        if (feedId != null) {
            feeds = Collections.singletonList(feedService.getById(feedId));
        } else if (upi != null) {
            feeds = feedService.getDefaultOpportunitiesFeedsByUpi(upi, null);
        } else if (user != null) {
            feeds = feedService.getDefaultOpportunitiesFeedsByUsername(user, null);
        }
        Set<Advert> advertList = new HashSet<Advert>();
        for (ResearchOpportunitiesFeed feed : feeds) {
            for (Program p : feed.getPrograms()) {
                Advert advert = p.getAdvert();
                if (advert != null) {
                    advertList.add(advert);
                }
            }
        }
        return singletonMap("adverts", convertAdverts(Lists.newArrayList(advertList)));
    }

    private void setSelectedAndBringToFront(AdvertDTO selectedAdvert, List<AdvertDTO> activeAdverts) {
        selectedAdvert.setSelected(true);
        if (activeAdverts.remove(selectedAdvert)) {
            activeAdverts.add(0, selectedAdvert);
        }
    }

    private AdvertDTO getSelectedAdvert(Integer selectedAdvertId, List<AdvertDTO> activeAdverts) {
        if (selectedAdvertId != null) {
            for (AdvertDTO advert : activeAdverts) {
                if (selectedAdvertId.equals(advert.getId())) {
                    return advert;
                }
            }
        }
        return NULL_ADVERT;
    }

    private List<AdvertDTO> convertAdverts(List<Advert> activeAdverts) {
        List<AdvertDTO> newList = new ArrayList<AdvertDTO>();
        AdvertConverter converter = new AdvertConverter();
        for (Advert advert : activeAdverts) {
            newList.add(converter.convert(advert));
        }
        return newList;
    }

    class AdvertConverter {

        public AdvertDTO convert(Advert input) {
            Program program = advertService.getProgram(input);
            AdvertDTO dto = null;
            if (program != null) {
                dto = new AdvertDTO(input.getId());
                dto.setProgramCode(program.getCode());
                dto.setTitle(program.getTitle());
                dto.setClosingDate(getFirstClosingDate(program));
                Person primarySupervisor = getFirstValidAdministrator(program);
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
                if (program.getAdvert() != null) {
                    projectDto.setStudyDuration(program.getAdvert().getStudyDuration());
                }
                projectDto.setTitle(input.getTitle());
                projectDto.setClosingDate(getFirstClosingDate(program));
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

        private Date getFirstClosingDate(Program program) {
            if (CollectionUtils.isEmpty(program.getClosingDates())) {
                return null;
            }
            Date now = DateUtils.truncateToDay(new Date());
            for (ProgramClosingDate closingDate : program.getClosingDates()) {
                if (now.compareTo(closingDate.getClosingDate()) <= 0) {
                    return closingDate.getClosingDate();
                }
            }
            return null;
        }

        private Person getFirstValidAdministrator(Program program) {
            List<RegisteredUser> administrators = program.getAdministrators();
            for (RegisteredUser administrator : administrators) {
                if (isValid(administrator)) {
                    return toPerson(administrator);
                }
            }
            return null;
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

        private boolean isValid(RegisteredUser admin) {
            return admin != null && admin.isAccountNonExpired() && admin.isAccountNonLocked() && admin.isCredentialsNonExpired() && admin.isEnabled();
        }
    }
    
}