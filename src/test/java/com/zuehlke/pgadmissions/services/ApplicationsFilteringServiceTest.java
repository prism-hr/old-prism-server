package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertSame;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationFilterGroup;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;

public class ApplicationsFilteringServiceTest {

    private ApplicationsFilteringService service;

    @Test
    public void shouldReturnStoredFiltering() {
        ApplicationFilterGroup filtering = new ApplicationFilterGroup();
        User user = new User().withAccount(new UserAccount().withFilterGroup(filtering));

        ApplicationFilterGroup actualFiltering = service.getDefaultApplicationFiltering(user);

        assertSame(filtering, actualFiltering);
    }

    @Test
    public void shouldReturnActiveApplicationFiltering() {
        User user = new User();
        ApplicationFilterGroup actualFiltering = service.getDefaultApplicationFiltering(user);
        Assert.assertEquals(3, actualFiltering.getFilters().size());
    }

    @Before
    public void prepare() {
        service = new ApplicationsFilteringService();
    }

}
