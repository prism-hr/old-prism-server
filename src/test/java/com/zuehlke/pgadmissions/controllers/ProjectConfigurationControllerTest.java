package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;

import org.apache.struts.mock.MockHttpServletRequest;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.controllers.prospectus.ApplyTemplateRenderer;
import com.zuehlke.pgadmissions.controllers.prospectus.ProjectConfigurationController;
import com.zuehlke.pgadmissions.converters.ProjectConverter;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ProjectDTOValidator;

public class ProjectConfigurationControllerTest {

    ProjectConfigurationController controller;
    private ProjectConverter projectConverter;
    private UserService userService;
    private ProgramsService programsService;
    private DurationOfStudyPropertyEditor durationOfStudyPropertyEditor;

    @Before
    public void setup() throws IOException {
        userService = EasyMock.createMock(UserService.class);
        programsService = EasyMock.createMock(ProgramsService.class);
        ApplicationContext applicationContext = EasyMock.createMock(ApplicationContext.class);
        ProjectDTOValidator projectDTOValidator = EasyMock.createMock(ProjectDTOValidator.class);
        DatePropertyEditor datePropertyEditor = EasyMock.createMock(DatePropertyEditor.class);
        ProgramPropertyEditor programPropertyEditor = EasyMock.createMock(ProgramPropertyEditor.class);
        PersonPropertyEditor personPropertyEditor = EasyMock.createMock(PersonPropertyEditor.class);
        projectConverter = EasyMock.createMock(ProjectConverter.class);
        ApplyTemplateRenderer templateRenderer = EasyMock.createMock(ApplyTemplateRenderer.class);
        controller = new ProjectConfigurationController(userService, programsService, applicationContext, projectDTOValidator, datePropertyEditor,
                programPropertyEditor, personPropertyEditor, projectConverter, templateRenderer, durationOfStudyPropertyEditor);
        controller.customizeJsonSerializer();
    }

    @Test
    public void shouldSaveProjectIfSecondarySupervisorIsNull() {
        ProjectDTO projectDTO = EasyMock.createMock(ProjectDTO.class);
        BindingResult result = EasyMock.createMock(BindingResult.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Project project = createProject();

        EasyMock.expect(result.getErrorCount()).andReturn(0).anyTimes();
        EasyMock.expect(result.hasErrors()).andReturn(false).anyTimes();
        EasyMock.expect(projectConverter.toDomainObject(projectDTO)).andReturn(project);
        EasyMock.expectLastCall().times(1);
        programsService.save(project);
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(projectConverter, programsService, result);
        project.setSecondarySupervisor(null);

        controller.saveProject(projectDTO, result, request);
        EasyMock.verify(projectConverter, programsService, result);
    }

    @Test
    public void shouldAddProjectIfSecondarySupervisorIsNull() {
        ProjectDTO projectDTO = EasyMock.createMock(ProjectDTO.class);
        BindingResult result = EasyMock.createMock(BindingResult.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Project project = createProject();

        EasyMock.expect(result.getErrorCount()).andReturn(0).anyTimes();
        EasyMock.expect(result.hasErrors()).andReturn(false).anyTimes();
        EasyMock.expect(projectConverter.toDomainObject(projectDTO)).andReturn(project);
        EasyMock.expectLastCall().times(1);
        programsService.save(project);
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(projectConverter, programsService, result);
        project.setSecondarySupervisor(null);

        controller.saveProject(projectDTO, result, request);
        EasyMock.verify(projectConverter, programsService, result);
    }

    private Project createProject() {
        Program program = new ProgramBuilder().code("KLOP").id(1).build();
        Advert advert = new AdvertBuilder().id(1).title("title").studyDuration(6).build();
        RegisteredUser primarySupervisor = new RegisteredUserBuilder().firstName("Ezio").lastName("Imbecilo").email("ezio@mail.com").id(1).build();
        RegisteredUser secondarySupervisor = new RegisteredUserBuilder().firstName("Genowefa").lastName("Pigwa").email("gienia@mail.com").id(2).build();
        Project project = new ProjectBuilder().id(1).advert(advert).program(program).primarySupervisor(primarySupervisor)
                .secondarySupervisor(secondarySupervisor).build();
        return project;
    }
}
