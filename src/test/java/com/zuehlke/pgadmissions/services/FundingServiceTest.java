package com.zuehlke.pgadmissions.services;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.dao.FundingDAO;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class FundingServiceTest {

    @Mock
    @InjectIntoByType
    private FundingDAO fundingDAOMock;

    @Mock
    @InjectIntoByType
    ApplicationDAO applicationFormDAOMock;

    @Mock
    @InjectIntoByType
    private DocumentService documentServiceMock;
    
    @TestedObject
    private FundingService service;

}
