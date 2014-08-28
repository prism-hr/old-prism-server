package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;

@Service
@Transactional
public class AdvertService {

    @Autowired
    private AdvertDAO advertDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private StateService stateService;

    public Advert getById(Integer id) {
        return entityService.getById(Advert.class, id);
    }

    // TODO: user filters
    public List<Advert> getActiveAdverts() {
        List<State> activeProgramStates = stateService.getActiveProgramStates();
        List<State> activeProjectStates = stateService.getActiveProjectStates();
        return advertDAO.getActiveAdverts(activeProgramStates, activeProjectStates);
    }

    public List<Advert> getRecommendedAdverts(User user) {
        List<State> activeProgramStates = stateService.getActiveProgramStates();
        List<State> activeProjectStates = stateService.getActiveProjectStates();
        return advertDAO.getRecommendedAdverts(user, activeProgramStates, activeProjectStates);
    }

    // TODO: internal application link and other summary information (e.g. pay/fee according to user requirements)
    public String getRecommendedAdvertsForEmail(User user) {
        List<Advert> adverts = getRecommendedAdverts(user);
        List<String> recommendations = Lists.newLinkedList();

        for (Advert advert : adverts) {
            Project project = advert.getProject();
            String applyLink = advert.getApplyLink();

            recommendations.add(advert.getProgram().getTitle() + "<br/>" + project == null ? ""
                    : project.getTitle() + "<br/>" + applyLink == null ? "whatever the internal application link is" : applyLink);
        }

        return Joiner.on("<br/>").join(recommendations);
    }
    
    public void updateAdvertClosingDates() {
        LocalDate baseline = new LocalDate();
        List<Advert> adverts = advertDAO.getAdvertsWithElapsedClosingDates(baseline);

        for (Advert advert : adverts) {
            AdvertClosingDate nextClosingDate = advertDAO.getNextAdvertClosingDate(advert, baseline);
            advert.setClosingDate(nextClosingDate);
            
            if (advert.isProjectAdvert() && nextClosingDate == null) {
                advert.getProject().setDueDate(new LocalDate());
            }
        }
    }

}
