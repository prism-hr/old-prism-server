package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertSame;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;

public class ApplicationsFilteringServiceTest {

    private ApplicationsFilteringService service;

    @Test
    public void shouldReturnStoredFiltering() {
        ApplicationsFiltering filtering = new ApplicationsFiltering();
        User user = new User().withAccount(new UserAccount().withFilterGroup(filtering));

        ApplicationsFiltering actualFiltering = service.getDefaultApplicationFiltering(user);

        assertSame(filtering, actualFiltering);
    }

    @Test
    public void shouldReturnActiveApplicationFiltering() {
        User user = new User();
        ApplicationsFiltering actualFiltering = service.getDefaultApplicationFiltering(user);
        Assert.assertEquals(3, actualFiltering.getFilters().size());
    }

    @Before
    public void prepare() {
        service = new ApplicationsFilteringService();
    }

}
