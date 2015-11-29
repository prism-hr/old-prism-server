package uk.co.alumeni.prism.dao;

import static org.apache.commons.lang.WordUtils.uncapitalize;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.application.ApplicationAdvertRelationSection;
import uk.co.alumeni.prism.domain.user.UserAdvertRelationSection;

@Repository
public class ProfileDAO {

    @Inject
    private SessionFactory sessionFactory;

    public <T extends UserAdvertRelationSection, U extends ApplicationAdvertRelationSection> boolean deleteUserProfileSection(Class<T> userProfileSectionClass,
            Class<U> applicationSectionClass, Integer applicationSectionId) {
        return sessionFactory.getCurrentSession().createQuery( //
                "delete " + userProfileSectionClass.getSimpleName() + " " //
                        + "where " + uncapitalize(applicationSectionClass.getSimpleName()) + ".id = :applicationSectionId") //
                .setParameter("applicationSectionId", applicationSectionId) //
                .executeUpdate() > 0;
    }

}
