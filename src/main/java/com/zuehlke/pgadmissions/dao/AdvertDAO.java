package com.zuehlke.pgadmissions.dao;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertFilterCategory;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.user.User;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class AdvertDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Advert> getActiveAdverts(List<PrismState> activeProgramStates, List<PrismState> activeProjectStates) {
        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .createAlias("program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("program")) //
                                .add(Restrictions.in("program.state.id", activeProgramStates))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("project")) //
                                .add(Restrictions.in("project.state.id", activeProjectStates)))) //
                .addOrder(Order.desc("sequenceIdentifier")) //
                .list();
    }

    public List<Advert> getRecommendedAdverts(User user, List<PrismState> activeProgramStates, List<PrismState> activeProjectStates) {
        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("application.advert")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.applications", "application", JoinType.INNER_JOIN) //
                .createAlias("application.advert", "recommendedAdvert", JoinType.INNER_JOIN) //
                .createAlias("recommendedAdvert.program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("recommendedAdvert.project", "project", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.ne("application.user", user)) //
                .add(Restrictions.isNotNull("application.submittedTimestamp")) //
                .add(Restrictions.neProperty("advert", "application.advert")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("recommendedAdvert.program")) //
                                .add(Restrictions.in("program.state.id", activeProgramStates))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("recommendedAdvert.project")) //
                                .add(Restrictions.in("project.state.id", activeProjectStates)))) //
                .addOrder(Order.desc("application.submittedTimestamp")) //
                .addOrder(Order.desc("recommendedAdvert.sequenceIdentifier")) //
                .setMaxResults(25) //
                .list();
    }

    public List<Advert> getAdvertsWithElapsedClosingDates(LocalDate baseline) {
        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .createAlias("closingDate", "closingDate", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("closingDates", "otherClosingDate", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions //
                        .disjunction() //
                        .add(Restrictions.lt("closingDate.closingDate", baseline)) //
                        .add(Restrictions.conjunction().add(Restrictions.isNull("closingDate.id")) //
                                .add(Restrictions.ge("otherClosingDate.closingDate", baseline)))) //
                .list();
    }

    public AdvertClosingDate getNextAdvertClosingDate(Advert advert, LocalDate baseline) {
        return (AdvertClosingDate) sessionFactory.getCurrentSession().createCriteria(AdvertClosingDate.class) //
                .setProjection(Projections.min("closingDate")) //
                .add(Restrictions.eq("id", advert.getId())) //
                .add(Restrictions.ge("closingDate", baseline)) //
                .uniqueResult();
    }

    public List<Advert> getAdvertsWithElapsedCurrencyConversions(LocalDate baseline, List<PrismState> activeProgramStates, List<PrismState> activeProjectStates) {
        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .createAlias("program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("program")) //
                                .add(Restrictions.in("program.state.id", activeProgramStates))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("project")) //
                                .add(Restrictions.in("project.state.id", activeProjectStates)))) //
                .add(Restrictions.lt("lastCurrencyConversionDate", baseline)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("fee.currencySpecified")) //
                                .add(Restrictions.isNotNull("fee.currencyAtLocale")) //
                                .add(Restrictions.neProperty("fee.currencySpecified", "fee.currencyAtLocale"))).add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("pay.currencySpecified")) //
                                .add(Restrictions.isNotNull("pay.currencyAtLocale")) //
                                .add(Restrictions.neProperty("pay.currencySpecified", "pay.currencyAtLocale")))).list();
    }

    public List<String> getLocalizedTags(Institution institution, PrismLocale locale, Class<? extends AdvertFilterCategory> clazz) {
        String propertyName = clazz.getSimpleName().replace("Advert", "").toLowerCase();
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(clazz) //
                .setProjection(Projections.groupProperty(propertyName)) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.program", "projectProgram", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNull("project.institution")) //
                                .add(Restrictions.eq("program.institution", institution))//
                                .add(Restrictions.eq("program.locale", locale))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNull("program.institution")) //
                                .add(Restrictions.eq("projectProgram.institution", institution)) //
                                .add(Restrictions.eq("projectProgram.locale", locale)))) //
                .list();
    }

    public List<String> getLocalizedThemes(Application application) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(AdvertTheme.class)
                .setProjection(Projections.groupProperty("theme"))
                .createAlias("advert", "advert", JoinType.INNER_JOIN)
                .createAlias("advert.program", "program", JoinType.LEFT_OUTER_JOIN)
                .createAlias("advert.project", "project", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.disjunction()
                        .add(Restrictions.eqOrIsNull("program.id", application.getProgram().getId()))
                        .add(Restrictions.eqOrIsNull("project.id", application.getProject() == null ? null : application.getProject().getId())))
                .list();
    }

}
