package com.zuehlke.pgadmissions.controllers.prospectus;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramsService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ProgramConfigurationControllerTest {

    @Mock
    @InjectIntoByType
    private ProgramsService programsService;

    @Mock
    @InjectIntoByType
    private DomicileService domicileService;

    @Mock
    @InjectIntoByType
    private EncryptionHelper encryptionHelper;

    @Mock
    @InjectIntoByType
    private ProgramInstanceService programInstanceService;

    @Mock
    @InjectIntoByType
    private ApplyTemplateRenderer templateRenderer;

    @InjectIntoByType
    private Gson gson = new Gson();;

    @TestedObject
    private ProgramConfigurationController controller;

    @Test
    public void shouldGetOpportunityData() {
        Domicile domicile = new DomicileBuilder().id(88).build();
        Program program = new ProgramBuilder().code("07").institution(new QualificationInstitutionBuilder().domicileCode("PL").build()) //
                .advert(new AdvertBuilder().id(999).build()).build();

        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("programCode", "07");
        dataMap.put("advertId", 999);

        expect(programsService.getProgramByCode("07")).andReturn(program);
        expect(domicileService.getEnabledDomicileByCode("PL")).andReturn(domicile);
        expect(encryptionHelper.encrypt(88)).andReturn("encPL");
        expect(programInstanceService.getAdvertisingDeadlineYear(program)).andReturn(2084);
        expect(programInstanceService.getStudyOptions(program)).andReturn(Lists.newArrayList("opt1", "opt2"));
        expect(templateRenderer.renderButton(dataMap)).andReturn("button");
        expect(templateRenderer.renderLink(dataMap)).andReturn("button");

        replay();
        String result = controller.getOpportunityData("07");
        verify();

        assertEquals(
                "{\"atasRequired\":false,\"studyOptions\":[\"opt1\",\"opt2\"],\"advert\":{\"id\":999,\"active\":true},\"buttonToApply\":\"button\",\"linkToApply\":\"button\",\"institutionCountryCode\":\"encPL\",\"advertisingDeadline\":2084,\"isCustomProgram\":true}",
                result);
    }
    
    @Test
    public void shouldSaveOpportunity() {
        OpportunityRequest opportunityRequest = new OpportunityRequest();
        BindingResult bindingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");
        Program program = new ProgramBuilder().code("p07").build();
        
        expect(programsService.saveProgramOpportunity(opportunityRequest)).andReturn(program);

        replay();
        String result = controller.saveOpportunity(opportunityRequest, bindingResult);
        verify();
        
        assertEquals("{\"programCode\":\"p07\",\"success\":true}", result);
    }

}
