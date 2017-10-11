package uk.co.alumeni.prism.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.address.AddressLocationPart;

import javax.inject.Inject;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class AddressDAO {

    @Inject
    private SessionFactory sessionFactory;

    public void deleteAddressLocations(Address address) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete AddressLocation " //
                        + "where address = :address") //
                .setParameter("address", address) //
                .executeUpdate();
    }

    public void unlinkAddressLocationParts() {
        sessionFactory.getCurrentSession().createQuery( //
                "update AddressLocationPart " //
                        + "set parent = null") //
                .executeUpdate();
    }

    public List<AddressLocationPart> getOrphanAddressLocationParts() {
        return (List<AddressLocationPart>) sessionFactory.getCurrentSession().createCriteria(AddressLocationPart.class) //
                .createAlias("locations", "location", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.isNull("location.id")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public List<Integer> getAddressesWithNoLocationParts() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Address.class) //
                .setProjection(Projections.groupProperty("id"))
                .createAlias("locations", "location", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.isNull("location.id")) //
                .list();
    }

}
