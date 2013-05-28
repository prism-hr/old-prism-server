package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import junit.framework.Assert;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.UclIrisProfileService;
import com.zuehlke.pgadmissions.services.UserService;

public class RegisteredUserIrisProfleControllerTest {

    private RegisteredUserIrisProfileController controller;
    
    private UclIrisProfileService irisServiceMock;
    
    private UserService userServiceMock;
    
    private MessageSource messageSourceMock;
    
    @Before
    public void prepare() {
        irisServiceMock = EasyMock.createMock(UclIrisProfileService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        messageSourceMock = EasyMock.createMock(MessageSource.class);
        controller = new RegisteredUserIrisProfileController(irisServiceMock, userServiceMock, messageSourceMock);
    }
    
    @Test
    public void shouldReturnErrorIfUpiIsNotAlphanumeric() {
        EasyMock.expect(messageSourceMock.getMessage("account.iris.upi.invalid", null, null)).andReturn("account.iris.upi.invalid");
        EasyMock.replay(messageSourceMock);
        Map<String, Object> resultMap = controller.irisProfileExists("http://www.evil.org");
        EasyMock.verify(messageSourceMock);
        Assert.assertFalse((Boolean) resultMap.get("success")); 
        Assert.assertEquals("account.iris.upi.invalid", resultMap.get("irisProfile"));
    }
    
    @Test
    public void shouldReturnErrorIfUpiIsAlphanumericAndDoesNotExists() {
        final String upi = "ABCDXX4";
        EasyMock.expect(irisServiceMock.profileExists(upi)).andReturn(false);
        EasyMock.replay(irisServiceMock);
        Map<String, Object> resultMap = controller.irisProfileExists(upi);
        EasyMock.verify(irisServiceMock);
        Assert.assertFalse((Boolean) resultMap.get("success")); 
    }
    
    @Test
    public void shouldReturnSuccessIfUpiIsAlphanumericAndExists() {
        final String upi = "ABCDXX4";
        EasyMock.expect(irisServiceMock.profileExists(upi)).andReturn(true);
        EasyMock.replay(irisServiceMock);
        Map<String, Object> resultMap = controller.irisProfileExists(upi);
        EasyMock.verify(irisServiceMock);
        Assert.assertTrue((Boolean) resultMap.get("success")); 
    }
    
    @Test
    public void shouldNotSetIrisProfileIfUpiIsNotAlphanumeric() {
        RegisteredUser user = new RegisteredUserBuilder().id(10).build();
        EasyMock.expect(userServiceMock.getUser(user.getId())).andReturn(user);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(messageSourceMock.getMessage("account.iris.upi.invalid", null, null)).andReturn("account.iris.upi.invalid");
        EasyMock.replay(messageSourceMock, userServiceMock);
        Map<String, Object> resultMap = controller.setIrisProfile(String.valueOf(user.getId()), "http://www.evil.org");
        EasyMock.verify(messageSourceMock, userServiceMock);
        Assert.assertFalse((Boolean) resultMap.get("success")); 
        Assert.assertEquals("account.iris.upi.invalid", resultMap.get("irisProfile"));
    }
    
    @Test
    public void shouldSetIrisProfileForUserWithMultipleAccounts() {
        final String upi = "ABCDXX4";
        RegisteredUser linkedUser = new RegisteredUserBuilder().id(11).build();
        RegisteredUser user = new RegisteredUserBuilder().id(10).linkedAccounts(linkedUser).build();
        EasyMock.expect(irisServiceMock.profileExists(upi)).andReturn(true);
        EasyMock.expect(userServiceMock.getUser(user.getId())).andReturn(user);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(userServiceMock.getUsersWithUpi(upi)).andReturn(new ArrayList<RegisteredUser>());
        userServiceMock.save(user);
        userServiceMock.save(linkedUser);
        EasyMock.replay(irisServiceMock, userServiceMock);
        Map<String, Object> resultMap = controller.setIrisProfile(String.valueOf(user.getId()), upi);
        EasyMock.verify(irisServiceMock, userServiceMock);
        Assert.assertTrue((Boolean) resultMap.get("success")); 
        Assert.assertEquals(upi, user.getUpi());
        Assert.assertEquals(upi, linkedUser.getUpi());
    }
    
    @Test
    public void shouldNotSetIrisProfileIfUserTriesToEnterAlreadyRegisteredIrisProfile() {
        final String upi = "ABCDXX4";
        RegisteredUser impersonator = new RegisteredUserBuilder().id(10).build();
        RegisteredUser existingIrisProfile = new RegisteredUserBuilder().id(11).upi(upi).build();
        EasyMock.expect(irisServiceMock.profileExists(upi)).andReturn(true);
        EasyMock.expect(userServiceMock.getUser(impersonator.getId())).andReturn(impersonator);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(impersonator);
        EasyMock.expect(userServiceMock.getUsersWithUpi(upi)).andReturn(Arrays.asList(existingIrisProfile));
        EasyMock.expect(messageSourceMock.getMessage("account.iris.upi.registered", null, null)).andReturn("account.iris.upi.registered");
        EasyMock.replay(irisServiceMock, userServiceMock, messageSourceMock);
        Map<String, Object> resultMap = controller.setIrisProfile(String.valueOf(impersonator.getId()), upi);
        EasyMock.verify(irisServiceMock, userServiceMock, messageSourceMock);
        Assert.assertFalse((Boolean) resultMap.get("success")); 
        Assert.assertEquals("account.iris.upi.registered", resultMap.get("irisProfile"));
    }
}
