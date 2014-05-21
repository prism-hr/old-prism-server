package com.zuehlke.pgadmissions.controllers;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class CommentTimelineControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationService applicationsServiceMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private CommentService commentServiceMock;

    @TestedObject
    private CommentTimelineController controller;

    // @Test
    // public void shouldGetApplicationFormFromId() {
    // ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
    // RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
    // EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
    // EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(false);
    // EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
    // EasyMock.replay(currentUser, userServiceMock);
    //
    // EasyMock.expect(applicationsServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
    // EasyMock.replay(applicationsServiceMock);
    // ApplicationForm returnedApplication = controller.getApplicationForm("5");
    // assertEquals(returnedApplication, applicationForm);
    // }
    //
    // @Test(expected = ResourceNotFoundException.class)
    // public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist() {
    // EasyMock.expect(applicationsServiceMock.getByApplicationNumber("5")).andReturn(null);
    // EasyMock.replay(applicationsServiceMock);
    // controller.getApplicationForm("5");
    // }
    //
    // @Test
    // public void shouldGetAllPhasesForApplication() throws ParseException {
    //
    // RegisteredUser currentUser = new RegisteredUserBuilder().id(5).build();
    // EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
    // EasyMock.replay(userServiceMock);
    //
    // final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
    //
    // controller = new CommentTimelineController(applicationsServiceMock, userServiceMock, timelineServiceMock) {
    //
    // @Override
    // public ApplicationForm getApplicationForm(String id) {
    // if ("applicationNumber".equals(id)) {
    // return applicationForm;
    // }
    // return null;
    // }
    //
    // };
    // TimelineObject timelineObjectOne = new TimelinePhase();
    // TimelineObject timelineObjectTwo = new TimelinePhase();
    // EasyMock.expect(timelineServiceMock.getTimelineObjects(applicationForm)).andReturn(Arrays.asList(timelineObjectOne, timelineObjectTwo));
    // EasyMock.replay(timelineServiceMock);
    //
    // List<TimelineObject> timelineObjects = controller.getTimelineObjects("applicationNumber");
    // assertEquals(2, timelineObjects.size());
    // assertEquals(timelineObjectOne, timelineObjects.get(0));
    // assertEquals(timelineObjectTwo, timelineObjects.get(1));
    //
    // }
    //
    // @Test
    // public void shouldReturnAllValidationQuestionOptions() {
    // assertArrayEquals(ValidationQuestionOptions.values(), controller.getValidationQuestionOptions());
    // }
    //
    // @Test
    // public void shouldReturnHomeOrOverseasOptions() {
    // assertArrayEquals(HomeOrOverseas.values(), controller.getHomeOrOverseasOptions());
    // }
    //
    // @Test
    // public void shouldReturnTimeLine() {
    // assertEquals("private/staff/admin/comment/timeline", controller.getCommentsView());
    // }
    //
    // @Test
    // public void shouldReturnCurrentUser() {
    // RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
    // EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
    // EasyMock.replay(userServiceMock);
    // assertEquals(currentUser, controller.getUser());
    // }

}
