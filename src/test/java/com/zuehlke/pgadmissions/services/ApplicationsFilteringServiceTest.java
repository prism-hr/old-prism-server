package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertSame;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ApplicationsFilteringServiceTest {

    private SystemService systemService;
    private ApplicationsFilteringService service;

    @Test
    public void shouldReturnStoredFiltering() {
        Filter filter = new Filter();
        User user = new User().withAccount(new UserAccount());
        user.getUserAccount().getFilters().put(systemService.getScope(PrismScope.APPLICATION), filter);

        Filter actualFiltering = service.getDefaultApplicationFiltering(user);

        assertSame(filter, actualFiltering);
    }

    @Test
    public void shouldReturnActiveApplicationFiltering() {
        User user = new User();
        Filter actualFiltering = service.getDefaultApplicationFiltering(user);
        Assert.assertEquals(3, actualFiltering.getFilterConstraints().size());
    }

    @Before
    public void prepare() {
        service = new ApplicationsFilteringService();
        systemService = new SystemService();
    }

}
