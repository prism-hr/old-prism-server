package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class AddressDAOTest extends AutomaticRollbackTestCase {
    private RegisteredUser user;

    @Test
    public void shouldDeleteAddress() {
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Domicile domicileById = domicileDAO.getDomicileById(1);
        Address address = new AddressBuilder().domicile(domicileById).address1("1 Main Street").build();
        save(address);
        flushAndClearSession();

        Integer id = address.getId();
        AddressDAO addressDAO = new AddressDAO(sessionFactory);
        addressDAO.delete(address);
        flushAndClearSession();
        assertNull(sessionFactory.getCurrentSession().get(Address.class, id));
    }

    @Test(expected = NullPointerException.class)
    public void shouldSendNullPointerException() {
        AddressDAO addressDAO = new AddressDAO();
        Address address = new AddressBuilder().id(1).build();
        addressDAO.delete(address);
    }

    @Before
    public void prepare() {
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                        .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        save(user);
        flushAndClearSession();
    }
}
