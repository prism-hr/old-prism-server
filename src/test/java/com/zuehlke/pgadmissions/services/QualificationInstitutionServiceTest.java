package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import org.easymock.Capture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class QualificationInstitutionServiceTest {

    @Mock
    @InjectIntoByType
    private QualificationInstitutionDAO qualificationInstitutionDAO;

    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContext;

    @TestedObject
    private QualificationInstitutionService service = new QualificationInstitutionService();

    @Test
    public void shouldGetInstitutionByCode() {
        QualificationInstitution institution = new QualificationInstitution();

        expect(qualificationInstitutionDAO.getInstitutionByCode("aa")).andReturn(institution);

        replay();
        QualificationInstitution returned = service.getInstitutionByCode("aa");
        verify();

        assertSame(institution, returned);
    }

    @Test
    public void shouldGenerateNextInstitutionCode() {
        QualificationInstitution lastCustomInstitution = new QualificationInstitutionBuilder().code("CUST00084").build();

        expect(qualificationInstitutionDAO.getLastCustomInstitution()).andReturn(lastCustomInstitution);

        replay();
        String result = service.generateNextInstitutionCode();
        verify();

        assertEquals("CUST00085", result);
    }

    @Test
    public void shouldGenerateFirstInstitutionCode() {
        expect(qualificationInstitutionDAO.getLastCustomInstitution()).andReturn(null);

        replay();
        String result = service.generateNextInstitutionCode();
        verify();

        assertEquals("CUST00000", result);
    }

    @Test
    public void shouldGetExistingInstitution() {
        QualificationInstitution institution = new QualificationInstitution();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().institutionCode("BBB").build();

        expect(applicationContext.getBean(QualificationInstitutionService.class)).andReturn(null);
        expect(qualificationInstitutionDAO.getInstitutionByCode("BBB")).andReturn(institution);

        replay();
        QualificationInstitution returned = service.getOrCreateCustomInstitution(opportunityRequest);
        verify();

        assertSame(returned, institution);
    }

    @Test
    public void shouldCreateNewCustomInstitution() {
        Domicile country = new DomicileBuilder().code("PL").build();
        QualificationInstitutionService thisBean = EasyMockUnitils.createMock(QualificationInstitutionService.class);
        OpportunityRequest opportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(null, country).institutionCode("OTHER").build();

        expect(applicationContext.getBean(QualificationInstitutionService.class)).andReturn(thisBean);
        expect(thisBean.generateNextInstitutionCode()).andReturn("00008");
        Capture<QualificationInstitution> institutionCapture = new Capture<QualificationInstitution>();
        qualificationInstitutionDAO.save(capture(institutionCapture));

        replay();
        QualificationInstitution returned = service.getOrCreateCustomInstitution(opportunityRequest);
        verify();

        assertSame(returned, institutionCapture.getValue());
        assertEquals("PL", returned.getDomicileCode());
        assertTrue(returned.getEnabled());
        assertEquals(opportunityRequest.getOtherInstitution(), returned.getName());
        assertEquals("00008", returned.getCode());
    }

}
