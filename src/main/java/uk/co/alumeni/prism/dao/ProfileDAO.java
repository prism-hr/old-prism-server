package uk.co.alumeni.prism.dao;

import static org.apache.commons.lang.WordUtils.uncapitalize;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.application.ApplicationSection;
import uk.co.alumeni.prism.domain.profile.ProfileEmploymentPosition;
import uk.co.alumeni.prism.domain.profile.ProfileEntity;
import uk.co.alumeni.prism.domain.profile.ProfileQualification;
import uk.co.alumeni.prism.domain.user.UserSection;

@Repository
@SuppressWarnings("unchecked")
public class ProfileDAO {

    @Inject
    private SessionFactory sessionFactory;

    public <T extends UserSection, U extends ApplicationSection> boolean deleteUserProfileSection(Class<T> userProfileSectionClass,
            Class<U> applicationSectionClass, Integer applicationSectionId) {
        return sessionFactory.getCurrentSession().createQuery( //
                "delete " + userProfileSectionClass.getSimpleName() + " " //
                        + "where " + uncapitalize(applicationSectionClass.getSimpleName()) + ".id = :applicationSectionId") //
                .setParameter("applicationSectionId", applicationSectionId) //
                .executeUpdate() > 0;
    }

    public <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileQualification<T>> U getCurrentQualification(T profile,
            Class<U> qualificationClass) {
        return getLatestQualification(profile, qualificationClass, false);
    }

    public <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileQualification<T>> U getMostRecentQualification(T profile,
            Class<U> qualificationClass) {
        return getLatestQualification(profile, qualificationClass, true);
    }

    public <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileEmploymentPosition<T>> U getCurrentEmploymentPosition(T profile,
            Class<U> qualificationClass) {
        return getLatestEmploymentPosition(profile, qualificationClass, true);
    }

    public <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileEmploymentPosition<T>> U getMostRecentEmploymentPosition(T profile,
            Class<U> qualificationClass) {
        return getLatestEmploymentPosition(profile, qualificationClass, false);
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileQualification<T>> U getLatestQualification(T profile,
            Class<U> qualificationClass, boolean completed) {
        return (U) sessionFactory.getCurrentSession().createCriteria(qualificationClass) //
                .add(Restrictions.eq("association", profile)) //
                .add(Restrictions.eq("completed", completed)) //
                .addOrder(Order.desc(completed ? "awardYear" : "startYear")) //
                .addOrder(Order.desc(completed ? "awardMonth" : "startMonth")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileEmploymentPosition<T>> U getLatestEmploymentPosition(T profile,
            Class<U> qualificationClass, boolean current) {
        return (U) sessionFactory.getCurrentSession().createCriteria(qualificationClass) //
                .add(Restrictions.eq("association", profile)) //
                .add(Restrictions.eq("current", current)) //
                .addOrder(Order.desc(current ? "startYear" : "endYear")) //
                .addOrder(Order.desc(current ? "startMonth" : "endMonth")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

}
