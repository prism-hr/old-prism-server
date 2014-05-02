package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertSame;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;

public class ApplicationsFilteringServiceTest {

    private ApplicationsFilteringService service;

    @Test
    public void shouldReturnStoredFiltering() {
        ApplicationsFiltering filtering = new ApplicationsFiltering();
        User user = new UserBuilder().userAccount(new UserAccount().withFilterGroup(filtering)).build();

        ApplicationsFiltering actualFiltering = service.getDefaultApplicationFiltering(user);

        assertSame(filtering, actualFiltering);
    }

    @Test
    public void shouldReturnActiveApplicationFiltering() {
        User user = new UserBuilder().build();
        ApplicationsFiltering actualFiltering = service.getDefaultApplicationFiltering(user);
        Assert.assertEquals(3, actualFiltering.getFilters().size());
    }

    @Before
    public void prepare() {
        service = new ApplicationsFilteringService();
    }

}
