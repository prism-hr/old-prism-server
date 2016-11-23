package uk.co.alumeni.prism.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import uk.co.alumeni.prism.domain.Tag;

import javax.inject.Inject;
import java.util.List;

@Repository
public class TagDAO {

    @Inject
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public <T extends Tag> List<T> getTags(Class<T> tagClass, String searchTerm) {
        return sessionFactory.getCurrentSession().createCriteria(tagClass)
                .add(Restrictions.like("name", searchTerm, MatchMode.ANYWHERE))
                .list();
    }

}
