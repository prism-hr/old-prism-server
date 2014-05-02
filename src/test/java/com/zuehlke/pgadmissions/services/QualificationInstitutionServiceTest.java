package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
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
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

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
        Institution institution = new Institution();

        expect(qualificationInstitutionDAO.getByCode("aa")).andReturn(institution);

        replay();
        Institution returned = service.getByCode("aa");
        verify();

        assertSame(institution, returned);
    }

    @Test
    public void shouldGenerateNextInstitutionCode() {
        Institution lastCustomInstitution = new Institution().withCode("CUST00084");

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
        Domicile domicile = new Domicile();
        Institution institution = new Institution();

        expect(applicationContext.getBean(QualificationInstitutionService.class)).andReturn(null);
        expect(qualificationInstitutionDAO.getByCode("BBB")).andReturn(institution);

        replay();
        Institution returned = service.getOrCreate("BBB", domicile, "other");
        verify();

        assertSame(returned, institution);
    }

    @Test
    public void shouldCreateNewCustomInstitution() {
        Domicile country = new DomicileBuilder().code("PL").build();
        QualificationInstitutionService thisBean = EasyMockUnitils.createMock(QualificationInstitutionService.class);
        OpportunityRequest opportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(null, country).institutionCode("OTHER")
                .otherInstitution("instytucja").build();

        expect(applicationContext.getBean(QualificationInstitutionService.class)).andReturn(thisBean);
        expect(qualificationInstitutionDAO.getByDomicileAndName("PL", "instytucja")).andReturn(null);
        expect(thisBean.generateNextInstitutionCode()).andReturn("00008");
        Capture<Institution> institutionCapture = new Capture<Institution>();
        qualificationInstitutionDAO.save(capture(institutionCapture));

        replay();
        Institution returned = service.getOrCreate("OTHER", country, "instytucja");
        verify();

        assertSame(returned, institutionCapture.getValue());
        assertEquals("PL", returned.getDomicileCode());
        assertEquals(PrismState.INSTITUTION_APPROVED, returned.getState());
        assertEquals(opportunityRequest.getOtherInstitution(), returned.getName());
        assertEquals("00008", returned.getCode());
    }

}
