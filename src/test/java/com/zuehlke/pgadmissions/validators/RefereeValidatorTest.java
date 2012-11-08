package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class RefereeValidatorTest {

	private Referee referee;
	
	@Autowired
	private RefereeValidator refereeValidator;
	
	@Autowired
	private UserService userServiceMock;
	
	private RegisteredUser currentUser;
	
	@Before
    public void setup() {
        currentUser = new RegisteredUserBuilder().id(9).email("me@test.com").toUser();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.replay(userServiceMock);
        referee = new RefereeBuilder().application(new ApplicationFormBuilder().id(2).toApplicationForm())
                .email("email@test.com").firstname("bob").lastname("smith").addressCountry(new Country())
                .address1("london").address3("london3").jobEmployer("zuhlke").jobTitle("se").messenger("skypeAddress")
                .phoneNumber("hallihallo").phoneNumber("0000").toReferee();
    }
	
	@Test
	@DirtiesContext
	public void shouldSupportRefereeValidator() {
		assertTrue(refereeValidator.supports(Referee.class));
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfFirstNameIsEmpty() {
		referee.setFirstname(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "firstname");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("firstname").getCode());
	}
	
    @Test
    @DirtiesContext
    public void shouldRejectIfPhoneNumberIsLargerThan35() {
        referee.setPhoneNumber("0123456789012345678901234567890123456789");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "phoneNumber");
        refereeValidator.validate(referee, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 35 characters are allowed.", mappingResult.getFieldError("phoneNumber").getDefaultMessage());
    }	
    
    @Test
    @DirtiesContext
    public void shouldRejectIfPhoneNumberIsInvalid() {
        referee.setPhoneNumber("012345678901234567--8901");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "phoneNumber");
        refereeValidator.validate(referee, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("You must enter telephone numbers in the following format +44 (0) 123 123 1234.", mappingResult.getFieldError("phoneNumber").getDefaultMessage());
    }       

	@Test
	@DirtiesContext
	public void shouldRejectIfLasttNameIsEmpty() {
		referee.setLastname(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "lastname");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("lastname").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfAddressLocationIsEmpty() {
		referee.setAddressLocation(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "addressLocation");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("addressLocation").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfJobEmployeeIsEmpty() {
		referee.setJobEmployer(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "jobEmployer");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("jobEmployer").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfJobTitleIsEmpty() {
		referee.setJobTitle(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "jobTitle");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("jobTitle").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfEmailNotValidEmail() {
		referee.setEmail("nonvalidemail");
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("nonvalidemail")).andReturn(null).anyTimes();
		EasyMock.replay(userServiceMock);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "email");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("You must enter a valid email address.", mappingResult.getFieldError("email").getDefaultMessage());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfEmailSameAsCurrentUser() {
		referee.setEmail("me@test.com");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "email");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.email.notyourself", mappingResult.getFieldError("email").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfNoTelephone() {
		referee.setPhoneNumber(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "phoneNumber");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("phoneNumber").getCode());
	}
}
