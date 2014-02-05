package com.zuehlke.pgadmissions.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.ibm.icu.text.SimpleDateFormat;
import com.zuehlke.pgadmissions.controllers.prospectus.AdvertsController;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramClosingDateBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ResearchOpportunitiesFeedBuilder;
import com.zuehlke.pgadmissions.domain.enums.FeedFormat;
import com.zuehlke.pgadmissions.domain.enums.OpportunityListType;
import com.zuehlke.pgadmissions.services.AdvertService;

public class AdvertsControllerTest {

    private AdvertsController controller;
    private AdvertService advertServiceMock;
    private ProgramDAO programDAOMock;
    private UserDAO userDAOMock;
    private HttpServletRequest request;

    @Before
    public void setUp() {
        advertServiceMock = EasyMock.createMock(AdvertService.class);
        programDAOMock = EasyMock.createMock(ProgramDAO.class);
        userDAOMock = EasyMock.createMock(UserDAO.class);
        request = EasyMock.createMock(HttpServletRequest.class);
        controller = new AdvertsController(advertServiceMock, programDAOMock, userDAOMock);
    }

    @Test
    public void shouldReturnNoAdvertsInJson() {
        EasyMock.expect(advertServiceMock.getActiveAdverts()).andReturn(Collections.<Advert> emptyList());
        EasyMock.replay(advertServiceMock);
        int expectedAdvertsSize = 0;
        assertAdvertsElementPresentWithExpectedLenght(expectedAdvertsSize, controller.getOpportunities(null, null, request));
    }

    @Test
    public void shouldReturnOneAdvertsInJson() {
        Program program = new ProgramBuilder().code("code1").title("another title").build();
        Advert advert = new AdvertBuilder().id(1).title("Title").description("Advert").funding("Funding").studyDuration(1).build();
        List<Advert> advertList = Arrays.asList(advert);

        EasyMock.expect(advertServiceMock.getProgram(advert)).andReturn(program);
        EasyMock.expect(advertServiceMock.getActiveAdverts()).andReturn(advertList);
        EasyMock.expect(programDAOMock.getNextClosingDate(program)).andReturn(null);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(program)).andReturn(null);
        EasyMock.replay(advertServiceMock, programDAOMock);
        int expectedAdvertsSize = 1;
        String activeAdvertsJson = controller.getOpportunities(null, null, request);
        assertAdvertsElementPresentWithExpectedLenght(expectedAdvertsSize, activeAdvertsJson);
        assertThat(activeAdvertsJson, containsString("Advert"));
    }

    @Test
    public void shouldReturnTwoAdvertsInJson() {
        Program programOne = new ProgramBuilder().code("code1").title("another title").build();
        Program programTwo = new ProgramBuilder().code("code2").title("another title2").build();
        Advert advertOne = new AdvertBuilder().id(1).title("Title1").description("Advert1").funding("Funding1").studyDuration(1).build();
        Advert advertTwo = new AdvertBuilder().id(1).title("Title2").description("Advert2").funding("Funding2").studyDuration(1).build();
        List<Advert> advertList = Arrays.asList(advertOne, advertTwo);

        EasyMock.expect(advertServiceMock.getProgram(advertOne)).andReturn(programOne);
        EasyMock.expect(advertServiceMock.getProgram(advertTwo)).andReturn(programTwo);
        EasyMock.expect(advertServiceMock.getActiveAdverts()).andReturn(advertList);
        EasyMock.expect(programDAOMock.getNextClosingDate(programOne)).andReturn(null);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(programOne)).andReturn(null);
        EasyMock.expect(programDAOMock.getNextClosingDate(programTwo)).andReturn(null);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(programTwo)).andReturn(null);
        EasyMock.replay(advertServiceMock, programDAOMock);
        int expectedAdvertsSize = 2;
        String activeAdvertsJson = controller.getOpportunities(null, null, request);
        assertAdvertsElementPresentWithExpectedLenght(expectedAdvertsSize, activeAdvertsJson);
        assertThat(activeAdvertsJson, containsString("Advert1"));
        assertThat(activeAdvertsJson, containsString("Advert2"));
    }

    @Test
    public void shouldConvertAdvertWithoutClosingDateAndWithoutEmail() {
        Program program = new ProgramBuilder().code("code1").title("another title").build();
        Advert advert = new AdvertBuilder().id(1).description("Advert").funding("Funding").studyDuration(1).build();
        List<Advert> advertList = Arrays.asList(advert);

        EasyMock.expect(advertServiceMock.getProgram(advert)).andReturn(program);
        EasyMock.expect(advertServiceMock.getActiveAdverts()).andReturn(advertList);
        EasyMock.expect(programDAOMock.getNextClosingDate(program)).andReturn(null);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(program)).andReturn(null);
        EasyMock.replay(advertServiceMock, programDAOMock);
        String activeAdvertsJson = controller.getOpportunities(null, null, request);
        assertThat(activeAdvertsJson, containsString(jsonProperty("id", advert.getId())));
        assertThat(activeAdvertsJson, containsString(jsonProperty("title", program.getTitle())));
        assertThat(activeAdvertsJson, containsString(jsonProperty("description", advert.getDescription())));
        assertThat(activeAdvertsJson, containsString(jsonProperty("funding", advert.getFunding())));
        assertThat(activeAdvertsJson, containsString(jsonProperty("programCode", program.getCode())));
        assertThat(activeAdvertsJson, not(containsString(jsonProperty("selected", true))));
        assertThat(activeAdvertsJson, not(containsString("email")));
        assertThat(activeAdvertsJson, not(containsString("closingDate")));
    }

    @Test
    public void shouldConvertAdvertWithFirstClosingDateAndFirstValidAdministratorEmail() throws Exception {
        Date now = com.zuehlke.pgadmissions.utils.DateUtils.truncateToDay(new Date());
        ProgramClosingDate programClosingDateOld = new ProgramClosingDateBuilder().closingDate(DateUtils.addDays(now, -1)).build();
        ProgramClosingDate programClosingDateSecond = new ProgramClosingDateBuilder().closingDate(DateUtils.addDays(now, 10)).build();
        ProgramClosingDate programClosingDateFirst = new ProgramClosingDateBuilder().closingDate(DateUtils.addDays(now, 1)).build();
        RegisteredUser expiredAdmin = new RegisteredUserBuilder().email("invalidAccountEmail").accountNonExpired(false).build();
        RegisteredUser lockedAdmin = new RegisteredUserBuilder().email("invalidAccountEmail").accountNonLocked(false).build();
        RegisteredUser credentialsExpiredAdmin = new RegisteredUserBuilder().email("invalidAccountEmail").credentialsNonExpired(false).build();
        RegisteredUser notEnabledAdmin = new RegisteredUserBuilder().email("invalidAccountEmail").enabled(false).build();
        RegisteredUser validAdmin = new RegisteredUserBuilder().email("accountEmail").build();
        
        Program program = new ProgramBuilder().code("code1").title("another title")
                .closingDates(programClosingDateOld, programClosingDateSecond, programClosingDateFirst)
                .administrators(expiredAdmin, lockedAdmin, credentialsExpiredAdmin, notEnabledAdmin, validAdmin).build();
        Advert advert = new AdvertBuilder().id(1).description("Advert").funding("Funding").studyDuration(1).build();
        List<Advert> advertList = Arrays.asList(advert);

        EasyMock.expect(advertServiceMock.getProgram(advert)).andReturn(program);
        EasyMock.expect(advertServiceMock.getActiveAdverts()).andReturn(advertList);
        EasyMock.expect(programDAOMock.getNextClosingDate(program)).andReturn(programClosingDateFirst.getClosingDate());
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(program)).andReturn(validAdmin);
        EasyMock.replay(advertServiceMock, programDAOMock);
        String activeAdvertsJson = controller.getOpportunities(null, null, request);
        assertThat(activeAdvertsJson, not(containsString(jsonProperty("selected", true))));
        assertThat(activeAdvertsJson, containsString(jsonProperty("id", advert.getId())));
        assertThat(activeAdvertsJson, containsString(jsonProperty("title", program.getTitle())));
        assertThat(activeAdvertsJson, containsString(jsonProperty("description", advert.getDescription())));
        assertThat(activeAdvertsJson, containsString(jsonProperty("funding", advert.getFunding())));
        assertThat(activeAdvertsJson, containsString(jsonProperty("programCode", program.getCode())));
        assertThat(activeAdvertsJson, containsString(jsonProperty("email", validAdmin.getEmail())));
        assertThat(activeAdvertsJson, containsString(jsonProperty("closingDate", programClosingDateFirst.getClosingDate())));
    }

    @Test
    public void shouldHaveASelectedAdvert() {
        Program program = new ProgramBuilder().code("code1").title("another title").build();
        Advert advert = new AdvertBuilder().id(1).description("Advert").funding("Funding").studyDuration(1).build();
        List<Advert> advertList = Arrays.asList(advert);
        request.setAttribute("advert", advert.getId().toString());
        EasyMock.expect(advertServiceMock.getProgram(advert)).andReturn(program);
        EasyMock.expect(advertServiceMock.getActiveAdverts()).andReturn(advertList);
        EasyMock.expect(programDAOMock.getNextClosingDate(program)).andReturn(null);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(program)).andReturn(null);
        EasyMock.replay(advertServiceMock, programDAOMock, request);
        String activeAdvertsJson = controller.getOpportunities(null, null, request);
        assertThat(activeAdvertsJson, containsString(jsonProperty("id", advert.getId())));
        assertThat(activeAdvertsJson, containsString(jsonProperty("selected", true)));
    }

    @Test
    public void shouldHaveSelectedAdvertAsFirstElement() {
        Advert selectedAdvert = new AdvertBuilder().description("Advert").funding("Funding").studyDuration(1).build();
        Advert notSelectedAdvert = new AdvertBuilder().description("Advert").funding("Funding").studyDuration(1).build();
        Program program = new ProgramBuilder().code("code1").title("another title").build();
        List<Advert> advertList = Arrays.asList(notSelectedAdvert, selectedAdvert);
        request.setAttribute("advert", selectedAdvert.getId().toString());
        EasyMock.expect(advertServiceMock.getProgram(selectedAdvert)).andReturn(program);
        EasyMock.expect(advertServiceMock.getProgram(notSelectedAdvert)).andReturn(program);
        EasyMock.expect(advertServiceMock.getActiveAdverts()).andReturn(advertList);
        EasyMock.expect(programDAOMock.getNextClosingDate(program)).andReturn(null).times(2);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(program)).andReturn(null).times(2);
        EasyMock.replay(advertServiceMock, programDAOMock);
        String resultJson = controller.getOpportunities(null, null, request);
        Map<?, ?> resultMap = new Gson().fromJson(resultJson, Map.class);
        List<?> activeAdvertsList = (List<?>) resultMap.get("adverts");
        Map<?, ?> selectedAdvertMap = (Map<?, ?>) activeAdvertsList.get(0);
        Assert.assertEquals(selectedAdvertMap.get("selected"), true);
        Map<?, ?> notSelectedAdvertMap = (Map<?, ?>) activeAdvertsList.get(1);
        Assert.assertEquals(notSelectedAdvertMap.get("selected"), false);
    }

    @Test
    public void shouldReturnAdvertsByFeedId() {
        Advert advert = new AdvertBuilder().description("Advert").funding("Funding").studyDuration(1).build();
        Program program = new ProgramBuilder().code("code1").title("another title").advert(advert).build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().title("foobar").feedFormat(FeedFormat.LARGE).programs(program).build();
        EasyMock.expect(advertServiceMock.getAdvertsByFeedId(feed.getId()));
        EasyMock.expect(advertServiceMock.getProgram(advert)).andReturn(program);
        EasyMock.expect(programDAOMock.getNextClosingDate(program)).andReturn(null);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(program)).andReturn(null);       
        EasyMock.replay(advertServiceMock, programDAOMock);
        String resultJson = controller.getOpportunities(OpportunityListType.OPPORTUNITIESBYFEEDID, feed.getId().toString(), request);
        EasyMock.verify(advertServiceMock);
        assertThat(resultJson, containsString("code1"));
    }

    @Test
    public void shouldReturnAdvertsByUsername() {
        Advert advert1 = new AdvertBuilder().description("Advert").funding("Funding").studyDuration(1).build();
        Advert advert2 = new AdvertBuilder().description("Advert2").funding("Funding2").studyDuration(1).build();
        Program program1 = new ProgramBuilder().code("code1").title("another title").advert(advert1).build();
        Program program2 = new ProgramBuilder().code("code2").title("another title").advert(advert2).build();
        EasyMock.expect(advertServiceMock.getAdvertsByUserUsername("feeder")).andReturn(Lists.newArrayList(advert1, advert2));
        EasyMock.expect(advertServiceMock.getProgram(advert1)).andReturn(program1);
        EasyMock.expect(advertServiceMock.getProgram(advert2)).andReturn(program2);
        EasyMock.expect(programDAOMock.getNextClosingDate(program1)).andReturn(null);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(program1)).andReturn(null);
        EasyMock.expect(programDAOMock.getNextClosingDate(program2)).andReturn(null);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(program2)).andReturn(null);
        EasyMock.replay(advertServiceMock, programDAOMock);
        String resultJson = controller.getOpportunities(OpportunityListType.OPPORTUNITIESBYUSERUSERNAME, "feeder", request);
        assertThat(resultJson, containsString("code1"));
        assertThat(resultJson, containsString("code2"));
        EasyMock.verify(advertServiceMock);

    }

    @Test
    public void shouldOpenNewTabForStandaloneAdvert() {
        ModelMap modelMap = new ModelMap();
        controller.getStandaloneOpportunities(null, null, modelMap);

        assertEquals(2, modelMap.size());
        assertTrue(modelMap.containsAttribute("shouldOpenNewTab"));
        assertEquals(8, modelMap.get("feedId"));

    }

    private String jsonProperty(String key, Date closingDate) {
        SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy");
        return String.format("\"%s\":\"%s", key, format.format(closingDate));
    }

    private String jsonProperty(String key, String value) {
        return String.format("\"%s\":\"%s\"", key, value);
    }

    private String jsonProperty(String key, Object value) {
        return String.format("\"%s\":%s", key, value);
    }

    private void assertAdvertsElementPresentWithExpectedLenght(int expectedAdvertsSize, String activeAdvertsJson) {
        String resultJson = activeAdvertsJson;
        assertThat(resultJson, notNullValue());
        assertThat(resultJson, containsString("adverts"));
        Map<?, ?> resultMap = new Gson().fromJson(resultJson, Map.class);
        List<?> activeAdvertsList = (List<?>) resultMap.get("adverts");
        assertThat(activeAdvertsList, hasSize(expectedAdvertsSize));
    }

}
