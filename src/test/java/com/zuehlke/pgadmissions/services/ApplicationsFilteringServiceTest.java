package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertSame;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;

public class ApplicationsFilteringServiceTest {

    private ApplicationsFilteringService service;

    @Test
    public void shouldReturnStoredFiltering() {
        Filter filtering = new Filter();
        User user = new User().withAccount(new UserAccount().withFilterGroup(filtering));

        Filter actualFiltering = service.getDefaultApplicationFiltering(user);

        assertSame(filtering, actualFiltering);
    }

    @Test
    public void shouldReturnActiveApplicationFiltering() {
        User user = new User();
        Filter actualFiltering = service.getDefaultApplicationFiltering(user);
        Assert.assertEquals(3, actualFiltering.getFilters().size());
    }

    @Before
    public void prepare() {
        service = new ApplicationsFilteringService();
    }

}
