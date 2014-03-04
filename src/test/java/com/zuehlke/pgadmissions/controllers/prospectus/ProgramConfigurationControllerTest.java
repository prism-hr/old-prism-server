package com.zuehlke.pgadmissions.controllers.prospectus;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;
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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramTypeBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.OpportunityRequestValidator;

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

    @Mock
    @InjectIntoByType
    private OpportunityRequestValidator opportunityRequestValidator;

    @Mock
    @InjectIntoByType
    private DomicilePropertyEditor domicilePropertyEditor;

    @Mock
    @InjectIntoByType
    private ProgramPropertyEditor programPropertyEditor;

    @Mock
    @InjectIntoByType
    private UserService userService;

    @Mock
    @InjectIntoByType
    private OpportunitiesService opportunitiesService;

    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContext;

    @InjectIntoByType
    private Gson gson = new Gson();

    @TestedObject
    private ProgramConfigurationController controller;

    @Test
    public void shouldRegisterPropertyEditorsForOpportunityRequest() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(opportunityRequestValidator);
        binderMock.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binderMock.registerCustomEditor(Program.class, programPropertyEditor);
        binderMock.registerCustomEditor(eq(String.class), isA(StringTrimmerEditor.class));

        replay();
        controller.registerPropertyEditorsForOpportunityRequest(binderMock);
        verify();
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void shouldGetOpportunityData() {
        Domicile domicile = new DomicileBuilder().id(88).build();
        Program program = new ProgramBuilder().code("07").institution(new QualificationInstitutionBuilder().domicileCode("PL").code("inst").build()) //
                .advert(new AdvertBuilder().id(999).build()).locked(true).title("Dlaczego w pizdzie nie ma krzesel?").description("Zeby chuj stal").studyDuration(8).
                funding("Ni ma kasy").active(true).atasRequired(false).programType(new ProgramTypeBuilder().id(ProgramTypeId.INTERNSHIP).build())
                .locked(true)
                .build();

        Map<String, Object> dataMap = Maps.newHashMap();
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

        Map<String, Object> resultMap = new Gson().fromJson(result, Map.class);
        
        assertEquals(999, ((Double)resultMap.get("programId")).intValue());
        assertEquals("Dlaczego w pizdzie nie ma krzesel?", resultMap.get("programTitle"));
        assertEquals("Zeby chuj stal", resultMap.get("programDescription"));
        assertEquals(8, ((Double)resultMap.get("programStudyDuration")).intValue());
        assertEquals("Ni ma kasy", resultMap.get("programFunding"));
        assertTrue((Boolean)resultMap.get("programIsActive"));
        assertTrue((Boolean)resultMap.get("isCustomProgram"));
        assertTrue((Boolean)resultMap.get("isCustomProgram"));
        assertFalse((Boolean)resultMap.get("atasRequired"));
        assertEquals("INTERNSHIP", resultMap.get("programType"));
        assertEquals("encPL", resultMap.get("institutionCountryCode"));
        assertEquals("inst", resultMap.get("institutionCode"));
        assertTrue((Boolean)resultMap.get("programLock"));
        assertEquals(2084, ((Double)resultMap.get("advertisingDeadline")).intValue());
        assertEquals(Arrays.asList("opt1", "opt2" ), resultMap.get("studyOptions"));
    }

    @Test
    public void shouldNotSaveOpportunityIfValidationErrors() {
        OpportunityRequest opportunityRequest = new OpportunityRequest();
        BindingResult bindingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");
        bindingResult.rejectValue("otherInstitution", "institution.did.you.mean", null, "dupa");

        expect(applicationContext.getMessage(eq(bindingResult.getFieldError()), isA(Locale.class))).andReturn("dupa");

        replay();
        String result = controller.saveOpportunity(opportunityRequest, bindingResult);
        verify();

        assertEquals("{\"otherInstitution\":{\"errorCode\":\"institution.did.you.mean\",\"institutions\":\"dupa\"}}", result);
    }

    @Test
    public void shouldSaveOpportunity() {
        Program program = new ProgramBuilder().code("p07").build();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().sourceProgram(program).build();
        BindingResult bindingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");
        RegisteredUser user = new RegisteredUser();

        expect(userService.getCurrentUser()).andReturn(user);
        expect(programsService.canChangeInstitution(user, opportunityRequest)).andReturn(true);
        expect(programsService.saveProgramOpportunity(opportunityRequest)).andReturn(program);

        replay();
        String result = controller.saveOpportunity(opportunityRequest, bindingResult);
        verify();

        assertSame(user, opportunityRequest.getAuthor());
        assertEquals("{\"programCode\":\"p07\",\"success\":true}", result);
    }

    @Test
    public void shouldNotSaveOpportunityButCreateNewOpportunityChangeRequest() {
        Program program = new ProgramBuilder().code("p07").build();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().sourceProgram(program).build();
        BindingResult bindingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");
        RegisteredUser user = new RegisteredUser();

        expect(userService.getCurrentUser()).andReturn(user);
        expect(programsService.canChangeInstitution(user, opportunityRequest)).andReturn(false);
        opportunitiesService.createOpportunityRequest(opportunityRequest, false);

        replay();
        String result = controller.saveOpportunity(opportunityRequest, bindingResult);
        verify();

        assertSame(user, opportunityRequest.getAuthor());
        assertEquals("{\"changeRequestCreated\":true}", result);
    }

}
