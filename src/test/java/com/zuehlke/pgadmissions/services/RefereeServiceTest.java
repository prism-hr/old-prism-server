package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReferenceEvent;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.mail.refactor.MailSendingService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class RefereeServiceTest {

    private RefereeService refereeService;
    private UserService userServiceMock;
    private RefereeDAO refereeDAOMock;
    private RoleDAO roleDAOMock;
    private CommentService commentServiceMock;
    private EventFactory eventFactoryMock;
    private ApplicationFormDAO applicationFormDAOMock;
    private EncryptionUtils encryptionUtilsMock;
    private EncryptionHelper encryptionHelper;
    private MailSendingService mailServiceMock;

    @Before
    public void setUp() {
        refereeDAOMock = EasyMock.createMock(RefereeDAO.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        roleDAOMock = EasyMock.createMock(RoleDAO.class);
        commentServiceMock = EasyMock.createMock(CommentService.class);
        eventFactoryMock = EasyMock.createMock(EventFactory.class);
        applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
        encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
        encryptionHelper = createMock(EncryptionHelper.class);
        mailServiceMock = createMock(MailSendingService.class);
        refereeService = new RefereeService(refereeDAOMock, encryptionUtilsMock, userServiceMock,
                roleDAOMock, commentServiceMock, eventFactoryMock, applicationFormDAOMock, encryptionHelper, mailServiceMock);
    }

    @Test
    public void shouldSaveReferenceAndSendEmailsAdminsAndApplicant() throws UnsupportedEncodingException {

        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        Role applicantRole = new RoleBuilder().authorityEnum(Authority.APPLICANT).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        RegisteredUser admin2 = new RegisteredUserBuilder().id(2).role(adminRole).firstName("anna").lastName("allen").email("email2@test.com").build();
        RegisteredUser applicant = new RegisteredUserBuilder().id(3).role(applicantRole).firstName("fred").lastName("freddy").email("email3@test.com").build();
        Referee referee = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").build();
        Program program = new ProgramBuilder().title("program title").administrators(admin1, admin2).build();

        ApplicationForm form = new ApplicationFormBuilder().applicationNumber("xyz").applicant(applicant).referees(referee).id(2).program(program).build();
        referee.setApplication(form);
        ReferenceEvent event = new ReferenceEventBuilder().id(4).build();
        EasyMock.expect(eventFactoryMock.createEvent(referee)).andReturn(event);
        applicationFormDAOMock.save(form);
        
        mailServiceMock.scheduleReferenceSubmitConfirmation(form);
        
        EasyMock.replay(refereeDAOMock, eventFactoryMock, applicationFormDAOMock, mailServiceMock);
        refereeService.saveReferenceAndSendMailNotifications(referee);
        EasyMock.verify(refereeDAOMock, eventFactoryMock, applicationFormDAOMock, mailServiceMock);

        assertEquals(1, form.getEvents().size());
        assertEquals(event, form.getEvents().get(0));
    }

    @Test
    public void shouldSaveAndSendEmailToReferee() throws UnsupportedEncodingException {

        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        Role applicantRole = new RoleBuilder().authorityEnum(Authority.APPLICANT).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        RegisteredUser admin2 = new RegisteredUserBuilder().id(2).role(adminRole).firstName("anna").lastName("allen").email("email2@test.com").build();
        RegisteredUser applicant = new RegisteredUserBuilder().id(3).role(applicantRole).firstName("fred").lastName("freddy").email("email3@test.com").build();
        Referee referee = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").build();
        Program program = new ProgramBuilder().title("program title").administrators(admin1, admin2).build();

        ApplicationForm form = new ApplicationFormBuilder().applicationNumber("xyz").applicant(applicant).referees(referee).id(2).program(program).build();
        referee.setApplication(form);
        ProgrammeDetails programmeDetails = new ProgrammeDetails();
        programmeDetails.setId(1);
        form.setProgrammeDetails(programmeDetails);
        
        mailServiceMock.sendReferenceRequest(referee, form);

        EasyMock.replay(mailServiceMock);

        refereeService.sendRefereeMailNotification(referee);
        EasyMock.verify(mailServiceMock);
    }

   
    @Test
    public void shouldEditReferenceComment() throws UnsupportedEncodingException {
        RegisteredUser refereeUser = new RegisteredUserBuilder().id(2).firstName("Bob").build();
        ReferenceComment referenceComment = new ReferenceCommentBuilder().comment("old comment").suitableForProgramme(false).suitableForUcl(false).document(null)
                .build();
        Referee referee = new RefereeBuilder().user(refereeUser).id(8).reference(referenceComment).build();

        Document document = new DocumentBuilder().build();
        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setReferenceDocument(document);
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(true);

        EasyMock.expect(encryptionHelper.decryptToInteger("refereeId")).andReturn(8);
        EasyMock.expect(refereeDAOMock.getRefereeById(8)).andReturn(referee);

        EasyMock.replay(encryptionHelper, refereeDAOMock, userServiceMock, commentServiceMock);
        refereeService.editReferenceComment(refereesAdminEditDTO);
        EasyMock.verify(encryptionHelper, refereeDAOMock, userServiceMock, commentServiceMock);

        
        assertEquals("comment text", referenceComment.getComment());
        assertEquals(document, referenceComment.getDocuments().get(0));
        assertEquals(true, referenceComment.getSuitableForProgramme());
        assertEquals(true, referenceComment.getSuitableForUCL());
    }

    @Test
    public void shouldPostReferenceOnBehalfOfReferee() throws UnsupportedEncodingException {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).build();

        RegisteredUser currentUser = new RegisteredUserBuilder().id(1).firstName("Alice").build();
        RegisteredUser refereeUser = new RegisteredUserBuilder().id(2).firstName("Bob").build();
        Referee referee = new RefereeBuilder().user(refereeUser).id(8).application(applicationForm).build();

        applicationForm.setReferees(Arrays.asList(referee));

        Document document = new DocumentBuilder().build();
        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setReferenceDocument(document);
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(false);

        EasyMock.expect(encryptionHelper.decryptToInteger("refereeId")).andReturn(8);
        EasyMock.expect(refereeDAOMock.getRefereeById(8)).andReturn(referee);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        commentServiceMock.save(EasyMock.isA(ReferenceComment.class));
        EasyMock.expectLastCall().once();
        applicationFormDAOMock.save(applicationForm);
        EasyMock.expectLastCall().once();

        EasyMock.replay(encryptionHelper, refereeDAOMock, userServiceMock, commentServiceMock);
        ReferenceComment referenceComment = refereeService.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO);
        referee.setReference(referenceComment);
        EasyMock.verify(encryptionHelper, refereeDAOMock, userServiceMock, commentServiceMock);

        assertSame(applicationForm, referenceComment.getApplication());
        assertSame(referee, referenceComment.getReferee());
        assertEquals(CommentType.REFERENCE, referenceComment.getType());
        assertSame(refereeUser, referenceComment.getUser());
        assertSame(currentUser, referenceComment.getProvidedBy());
        assertEquals("comment text", referenceComment.getComment());
        assertEquals(1, referenceComment.getDocuments().size());
        assertSame(document, referenceComment.getDocuments().get(0));
        assertEquals(true, referenceComment.getSuitableForProgramme());
        assertEquals(false, referenceComment.getSuitableForUCL());
        assertEquals(1, applicationForm.getReferencesToSendToPortico().size());
        assertEquals("comment text", applicationForm.getReferencesToSendToPortico().get(0).getComment());
    }

    @Test
    public void shouldCreateRefereeRegisteredUserAndPostReferenceOnBehalfOfReferee() throws UnsupportedEncodingException {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).build();

        RegisteredUser currentUser = new RegisteredUserBuilder().id(1).firstName("Alice").build();
        Referee referee = new RefereeBuilder().application(applicationForm).firstname("Franciszek").lastname("Pieczka").build();

        Document document = new DocumentBuilder().build();
        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setReferenceDocument(document);
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(false);

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(referee.getEmail())).andReturn(null);
        EasyMock.expect(encryptionHelper.decryptToInteger("refereeId")).andReturn(8);
        EasyMock.expect(refereeDAOMock.getRefereeById(8)).andReturn(referee);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        userServiceMock.save(EasyMock.isA(RegisteredUser.class));
        EasyMock.expectLastCall().once();
        refereeDAOMock.save(referee);
        EasyMock.expectLastCall().once();
        commentServiceMock.save(EasyMock.isA(ReferenceComment.class));
        EasyMock.expectLastCall().once();
        applicationFormDAOMock.save(applicationForm);
        EasyMock.expectLastCall().once();

        EasyMock.replay(encryptionHelper, refereeDAOMock, userServiceMock, commentServiceMock);
        ReferenceComment referenceComment = refereeService.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO);
        EasyMock.verify(encryptionHelper, refereeDAOMock, userServiceMock, commentServiceMock);

        RegisteredUser refereeUser = referenceComment.getUser();
        assertEquals("Franciszek", refereeUser.getFirstName());
        assertEquals("Pieczka", refereeUser.getLastName());

        assertSame(applicationForm, referenceComment.getApplication());
        assertSame(referee, referenceComment.getReferee());
        assertTrue(referenceComment.getReferee().getSendToUCL());
        assertEquals(CommentType.REFERENCE, referenceComment.getType());

        assertSame(currentUser, referenceComment.getProvidedBy());
        assertEquals("comment text", referenceComment.getComment());
        assertEquals(1, referenceComment.getDocuments().size());
        assertSame(document, referenceComment.getDocuments().get(0));
        assertEquals(true, referenceComment.getSuitableForProgramme());
        assertEquals(false, referenceComment.getSuitableForUCL());
    }

    @Test
    public void shouldCreateNewRefereeAndPostReferenceOnBehalfOfHim() throws UnsupportedEncodingException {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).build();

        RegisteredUser currentUser = new RegisteredUserBuilder().id(1).firstName("Alice").build();

        Document document = new DocumentBuilder().build();
        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setReferenceDocument(document);
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(false);
        refereesAdminEditDTO.setFirstname("Franciszek");
        refereesAdminEditDTO.setLastname("Pieczka");
        refereesAdminEditDTO.setJobEmployer("Employer");
        refereesAdminEditDTO.setJobTitle("Job");
        Address address = new AddressBuilder().address1("1").address2("2").address3("3").address4("4").address5("5")
                .country(new CountryBuilder().code("aa").build()).build();
        refereesAdminEditDTO.setAddressLocation(address);
        refereesAdminEditDTO.setEmail("aaa@.fff.ccc");
        refereesAdminEditDTO.setPhoneNumber("+44 111111111");
        refereesAdminEditDTO.setContainsRefereeData(true);

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("aaa@.fff.ccc")).andReturn(null);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        userServiceMock.save(EasyMock.isA(RegisteredUser.class));
        EasyMock.expectLastCall().once();
        refereeDAOMock.save(EasyMock.isA(Referee.class));
        EasyMock.expectLastCall().once();
        commentServiceMock.save(EasyMock.isA(ReferenceComment.class));
        EasyMock.expectLastCall().once();
        applicationFormDAOMock.save(applicationForm);
        EasyMock.expectLastCall().once();

        EasyMock.replay(encryptionHelper, refereeDAOMock, userServiceMock, commentServiceMock);
        ReferenceComment referenceComment = refereeService.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO);
        EasyMock.verify(encryptionHelper, refereeDAOMock, userServiceMock, commentServiceMock);

        RegisteredUser refereeUser = referenceComment.getUser();
        assertEquals("Franciszek", refereeUser.getFirstName());
        assertEquals("Pieczka", refereeUser.getLastName());

        assertSame(applicationForm, referenceComment.getApplication());
        assertEquals("Franciszek", referenceComment.getReferee().getFirstname());
        assertTrue(referenceComment.getReferee().getSendToUCL());
        assertEquals(CommentType.REFERENCE, referenceComment.getType());

        assertSame(currentUser, referenceComment.getProvidedBy());
        assertEquals("comment text", referenceComment.getComment());
        assertEquals(1, referenceComment.getDocuments().size());
        assertSame(document, referenceComment.getDocuments().get(0));
        assertEquals(true, referenceComment.getSuitableForProgramme());
        assertEquals(false, referenceComment.getSuitableForUCL());
    }

    @Test
    public void shouldReturnUserIfRefereeAlreadyExists() {
        Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).build();
        RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(reviewerRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        userServiceMock.save(reviewer);
        Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").build();
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(reviewer);
        EasyMock.replay(userServiceMock);
        RegisteredUser existedReferee = refereeService.getRefereeIfAlreadyRegistered(referee);
        Assert.assertNotNull(existedReferee);
    }

    @Test
    public void shouldReturnNullIfRefereeNotExists() {
        Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).build();
        RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(reviewerRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        userServiceMock.save(reviewer);
        Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("otherrefemail@test.com").build();
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("otherrefemail@test.com")).andReturn(null);
        EasyMock.replay(userServiceMock);
        RegisteredUser existedReferee = refereeService.getRefereeIfAlreadyRegistered(referee);
        Assert.assertNull(existedReferee);
    }

    @Test
    public void shouldAddRefereeRoleIfUserExistsAndIsNotAReferee() {
        Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1).role(reviewerRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        userServiceMock.save(user);
        Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").build();
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(user);
        userServiceMock.save(user);
        EasyMock.replay(userServiceMock);
        RegisteredUser existedReferee = refereeService.processRefereeAndGetAsUser(referee);
        Assert.assertNotNull(existedReferee);
        Assert.assertEquals(2, existedReferee.getRoles().size());
    }

    @Test
    public void shouldAddRefereeRoleIfUserExistsAndIsApproverReviewerAdmin() {
        Role reviewerRole = new RoleBuilder().id(1).authorityEnum(Authority.REVIEWER).build();
        Role adminRole = new RoleBuilder().id(2).authorityEnum(Authority.ADMINISTRATOR).build();
        Role approverRole = new RoleBuilder().id(3).authorityEnum(Authority.APPROVER).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1).roles(reviewerRole, adminRole, approverRole).firstName("bob").lastName("bobson")
                .email("email@test.com").build();
        userServiceMock.save(user);
        Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").build();
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(user);
        userServiceMock.save(user);
        EasyMock.replay(userServiceMock);
        RegisteredUser existedReferee = refereeService.processRefereeAndGetAsUser(referee);
        Assert.assertNotNull(existedReferee);
        Assert.assertEquals(4, existedReferee.getRoles().size());
    }

    @Test
    public void shouldNotAddRefereeRoleIfUserExistsAndIsAlreadyAReferee() {
        Role refereeRole = new RoleBuilder().authorityEnum(Authority.REFEREE).build();
        RegisteredUser user = new RegisteredUserBuilder().id(3).role(refereeRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        userServiceMock.save(user);
        Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").build();
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(user);
        userServiceMock.save(user);
        EasyMock.replay(userServiceMock);
        RegisteredUser existedReferee = refereeService.processRefereeAndGetAsUser(referee);
        Assert.assertNotNull(existedReferee);
        Assert.assertEquals(1, existedReferee.getRoles().size());
    }

    @Test
    public void shouldCreateUserWithRefereeRoleIfRefereeDoesNotExist() {
        final RegisteredUser user = new RegisteredUserBuilder().id(1).accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false)
                .enabled(true).build();
        Referee referee = new RefereeBuilder().id(1).firstname("ref").lastname("erre").email("emailemail@test.com")
                .application(new ApplicationFormBuilder().id(1).applicationNumber("abc").build()).build();
        refereeService = new RefereeService(refereeDAOMock, encryptionUtilsMock, userServiceMock,
                roleDAOMock, commentServiceMock, eventFactoryMock, applicationFormDAOMock, encryptionHelper, mailServiceMock) {
            @Override
            RegisteredUser newRegisteredUser() {
                return user;
            }
        };
        Role role = new RoleBuilder().id(1).build();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.REFEREE)).andReturn(role);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("emailemail@test.com")).andReturn(null);
        userServiceMock.save(user);
        referee.setUser(user);
        refereeDAOMock.save(referee);
        EasyMock.expect(encryptionUtilsMock.generateUUID()).andReturn("abc");
        EasyMock.replay(userServiceMock, refereeDAOMock, encryptionUtilsMock, roleDAOMock);

        RegisteredUser newUser = refereeService.processRefereeAndGetAsUser(referee);
        EasyMock.verify(refereeDAOMock, userServiceMock);
        Assert.assertNotNull(newUser);
        Assert.assertEquals(1, newUser.getRoles().size());
        Assert.assertEquals(role, newUser.getRoles().get(0));
        Assert.assertEquals("ref", newUser.getFirstName());
        Assert.assertEquals("erre", newUser.getLastName());
        Assert.assertEquals("emailemail@test.com", newUser.getEmail());
        Assert.assertEquals("emailemail@test.com", newUser.getUsername());
        assertTrue(newUser.isAccountNonExpired());
        assertTrue(newUser.isAccountNonLocked());
        assertTrue(newUser.isCredentialsNonExpired());
        assertFalse(newUser.isEnabled());
        assertEquals("abc", newUser.getActivationCode());
        assertEquals(DirectURLsEnum.ADD_REFERENCE.displayValue() + "abc", newUser.getDirectToUrl());
    }

    @Test
    public void shouldReturnRefereeById() {
        Referee referee = EasyMock.createMock(Referee.class);
        EasyMock.expect(refereeDAOMock.getRefereeById(23)).andReturn(referee);
        EasyMock.replay(referee, refereeDAOMock);

        Assert.assertEquals(referee, refereeService.getRefereeById(23));
    }

    @Test
    public void shouldGetRefereeByUserAndApplication() {
        ApplicationForm form = new ApplicationFormBuilder().id(1).build();
        Referee referee1 = new RefereeBuilder().id(1).firstname("ref").lastname("erre").email("emailemail1@test.com").application(form).build();
        Referee referee2 = new RefereeBuilder().id(2).firstname("ref").lastname("erre").email("emailemail2@test.com").build();
        Referee referee3 = new RefereeBuilder().id(3).firstname("ref").lastname("erre").email("emailemail3@test.com").build();

        RegisteredUser user = new RegisteredUserBuilder().referees(referee1, referee2, referee3).id(1).build();

        Referee referee = refereeService.getRefereeByUserAndApplication(user, form);

        assertEquals(referee1, referee);
    }

    @Test
    public void shouldDelegateDeleteToDAO() {
        Referee referee = new RefereeBuilder().id(2).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1).referees(referee).build();
        referee.setUser(user);
        refereeDAOMock.delete(referee);
        EasyMock.replay(refereeDAOMock);
        refereeService.delete(referee);
        EasyMock.verify(refereeDAOMock);
    }

    @Test
    public void shouldDelegateSaveToDAO() {
        Referee referee = new RefereeBuilder().id(2).build();
        refereeDAOMock.save(referee);
        EasyMock.replay(refereeDAOMock);
        refereeService.save(referee);
        EasyMock.verify(refereeDAOMock);
    }

    @Test
    public void shouldSetDeclineAndSendDeclineNotificationToApplicantAndAdministrators() throws UnsupportedEncodingException {

        RegisteredUser applicant = new RegisteredUserBuilder().id(3).firstName("fred").lastName("freddy").email("email3@test.com").build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).firstName("bob").lastName("bobson").email("email@test.com").build();
        RegisteredUser admin2 = new RegisteredUserBuilder().id(2).firstName("anna").lastName("allen").email("email@test.com").build();
        Referee referee = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").build();
        ApplicationForm form = new ApplicationFormBuilder().id(2342).applicationNumber("xyz").applicant(applicant)
                .program(new ProgramBuilder().title("klala").administrators(admin1, admin2).build()).build();
        referee.setApplication(form);

        refereeDAOMock.save(referee);

        ReferenceEvent event = new ReferenceEventBuilder().id(4).build();
        
        EasyMock.expect(eventFactoryMock.createEvent(referee)).andReturn(event);
        applicationFormDAOMock.save(form);
        
        mailServiceMock.scheduleReferenceSubmitConfirmation(form);

        EasyMock.replay(refereeDAOMock, eventFactoryMock, applicationFormDAOMock, mailServiceMock);

        refereeService.declineToActAsRefereeAndSendNotification(referee);

        assertTrue(referee.isDeclined());
        assertEquals(1, form.getEvents().size());
        assertEquals(event, form.getEvents().get(0));
        EasyMock.verify(refereeDAOMock, eventFactoryMock, applicationFormDAOMock, mailServiceMock);
    }

   
    @Test
    public void shouldNotSendDeclineNotificationIfSaveFails() {

        RegisteredUser applicant = new RegisteredUserBuilder().id(3).firstName("fred").lastName("freddy").email("email3@test.com").build();
        Referee referee = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").build();
        ApplicationForm form = new ApplicationFormBuilder().applicant(applicant).program(new Program()).build();
        referee.setApplication(form);

        refereeDAOMock.save(referee);
        EasyMock.expectLastCall().andThrow(new RuntimeException("aaaaaaarrrrhh"));

        EasyMock.replay(refereeDAOMock);

        try {
            refereeService.declineToActAsRefereeAndSendNotification(referee);
        } catch (Exception e) {
            // expected..ignore
        }

        EasyMock.verify(refereeDAOMock);
    }

    @Test
    public void shouldSetFlagSendToUclOnSelectedQualifications() {
        ApplicationForm applicationFormMock = EasyMock.createMock(ApplicationForm.class);

        Referee referee1 = new RefereeBuilder().id(1).sendToUCL(true).build();
        Referee referee2 = new RefereeBuilder().id(2).sendToUCL(true).build();
        Referee referee3 = new RefereeBuilder().id(3).sendToUCL(false).build();
        Referee referee4 = new RefereeBuilder().id(4).sendToUCL(false).build();

        EasyMock.expect(applicationFormMock.getReferees()).andReturn(Arrays.asList(referee1, referee2, referee3, referee4));
        EasyMock.expect(refereeDAOMock.getRefereeById(3)).andReturn(referee3);
        EasyMock.expect(refereeDAOMock.getRefereeById(4)).andReturn(referee4);

        EasyMock.replay(applicationFormMock, refereeDAOMock, applicationFormDAOMock, encryptionHelper);

        refereeService.selectForSendingToPortico(applicationFormMock, Arrays.asList(new Integer[] { 3, 4 }));

        EasyMock.verify(applicationFormMock, refereeDAOMock, applicationFormDAOMock, encryptionHelper);

        assertTrue("SendToUcl flag has not been updated to true", referee3.getSendToUCL());
        assertTrue("SendToUcl flag has not been updated to true", referee4.getSendToUCL());
        assertFalse("SendToUcl flag has not been updated to false", referee1.getSendToUCL());
        assertFalse("SendToUcl flag has not been updated to false", referee2.getSendToUCL());
    }
}
