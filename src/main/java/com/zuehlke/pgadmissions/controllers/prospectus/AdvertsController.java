package com.zuehlke.pgadmissions.controllers.prospectus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.AdvertDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Controller
@RequestMapping("/adverts")
public class AdvertsController {

    private final AdvertService advertService;
    private static final AdvertDTO NULL_ADVERT = new AdvertDTO(Integer.MIN_VALUE);

    public AdvertsController() {
        this(null);
    }

    @Autowired
    public AdvertsController(AdvertService advertService) {
        this.advertService = advertService;
    }

    @RequestMapping(value = "/activeAdverts", method = RequestMethod.GET)
    @ResponseBody
    public String activeAdverts(@RequestParam(required = false) Integer selectedAdvertId) {
        Map<String, Object> map = Maps.newHashMap();
        List<AdvertDTO> activeAdverts = convertAdverts(advertService.getActiveAdverts());
        Collections.shuffle(activeAdverts, new Random(System.currentTimeMillis()));
        AdvertDTO selectedAdvert = getSelectedAdvert(selectedAdvertId, activeAdverts);
        setSelectedAndBringToFront(selectedAdvert, activeAdverts);
        map.put("adverts", activeAdverts);
        return new Gson().toJson(map);
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
            AdvertDTO dto = new AdvertDTO(input.getId());
            dto.setDescription(input.getDescription());
            dto.setFunding(input.getFunding());
            dto.setStudyDuration(input.getStudyDuration());
            Program program = advertService.getProgram(input);
            if (program != null) {
                dto.setProgramCode(program.getCode());
                dto.setTitle(program.getTitle());
                dto.setClosingDate(getFirstClosingDate(program));
                dto.setSupervisorEmail(getFirstValidAdministrator(program));
            } else {
                Project project = advertService.getProject(input);
                dto.setProgramCode(project.getProgram().getCode());
                dto.setTitle(input.getTitle());
                dto.setSupervisorEmail(project.getPrimarySupervisor().getEmail());
            }
            return dto;
        }

        private Date getFirstClosingDate(Program program) {
            if (CollectionUtils.isEmpty(program.getClosingDates())) {
                return null;
            }
            Date now = DateUtils.truncateToDay(new Date());
            for(ProgramClosingDate closingDate:program.getClosingDates()){
            	if(now.compareTo(closingDate.getClosingDate())<=0){
            		return closingDate.getClosingDate();
            	}
            }
			return null;
        }

        private String getFirstValidAdministrator(Program program) {
            List<RegisteredUser> administrators = program.getAdministrators();
            for (RegisteredUser administrator : administrators) {
                if (isValid(administrator)) {
                    return administrator.getEmail();
                }
            }
            return null;
        }

        private boolean isValid(RegisteredUser admin) {
            return admin != null && admin.isAccountNonExpired() && admin.isAccountNonLocked() && admin.isCredentialsNonExpired() && admin.isEnabled();
        }
    }
}