package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.SourcesOfInterestBuilder;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SourcesOfInterestPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SuggestedSupervisorJSONPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.ProgramDetailsService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.SourcesOfInterestService;
import com.zuehlke.pgadmissions.validators.ProgramDetailsValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ProgrammeDetailsControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationFormService;

    @Mock
    @InjectIntoByType
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Mock
    @InjectIntoByType
    private DatePropertyEditor datePropertyEditor;

    @Mock
    @InjectIntoByType
    private ProgramDetailsValidator programmeDetailsValidator;

    @Mock
    @InjectIntoByType
    private ProgramDetailsService programDetailsService;

    @Mock
    @InjectIntoByType
    private ProgramService programService;

    @Mock
    @InjectIntoByType
    private SourcesOfInterestService sourcesOfInterestService;

    @Mock
    @InjectIntoByType
    private SuggestedSupervisorJSONPropertyEditor supervisorJSONPropertyEditor;

    @Mock
    @InjectIntoByType
    private SourcesOfInterestPropertyEditor sourcesOfInterestPropertyEditor;

    @TestedObject
    private ProgramDetailsController controller;

    @Test
    public void shouldReturnAvaialbeStudyOptionLevels() {
        final String applicationNumber = "1";
        Program program = new Program().withId(7);
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber(applicationNumber).program(program).build();

        StudyOption option1 = new StudyOption("1", "Full-time");
        StudyOption option2 = new StudyOption("31", "Part-time");

        List<StudyOption> optionsList = Arrays.asList(option1, option2);

        EasyMock.expect(programService.getAvailableStudyOptions(program)).andReturn(optionsList);
        EasyMock.replay(programService);
        List<StudyOption> studyOptions = controller.getStudyOptions(applicationNumber);
        assertSame(studyOptions, optionsList);
    }

    @Test
    public void shouldReturnAllSourcesOfInterest() {
        SourcesOfInterest sourcesOfInterest = new SourcesOfInterestBuilder().id(1).code("ZZ").name("ZZ").build();
        // EasyMock.expect(programDetailsServiceMock.getAllEnabledSourcesOfInterest()).andReturn(Collections.singletonList(sourcesOfInterest));
        // EasyMock.replay(programDetailsServiceMock);
        assertEquals(controller.getSourcesOfInterests().get(0), sourcesOfInterest);
    }

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(programmeDetailsValidator);
        binderMock.registerCustomEditor(Date.class, datePropertyEditor);
        binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
        binderMock.registerCustomEditor(SuggestedSupervisor.class, supervisorJSONPropertyEditor);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(SourcesOfInterest.class, sourcesOfInterestPropertyEditor);
        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);
        EasyMock.verify(binderMock);
    }

}