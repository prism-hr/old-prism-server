package com.zuehlke.pgadmissions.dao;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.application.ApplicationAdvertRelationSection;
import com.zuehlke.pgadmissions.domain.user.UserAdvertRelationSection;

@Repository
public class ProfileDAO {

    @Inject
    private SessionFactory sessionFactory;

    public <T extends UserAdvertRelationSection, U extends ApplicationAdvertRelationSection> void deleteUserProfileSection(Class<T> userProfileSectionClass,
            Class<U> applicationSectionClass, Integer applicationSectionId) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete " + userProfileSectionClass.getSimpleName() + " " //
                        + "where " + applicationSectionClass.getSimpleName().toLowerCase() + ".id = :propertyId") //
                .setParameter("applicationSectionId", applicationSectionId) //
                .executeUpdate();
    }

}
