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

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.ui.ModelMap;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.ibm.icu.text.SimpleDateFormat;
import com.zuehlke.pgadmissions.controllers.prospectus.AdvertsController;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramClosingDateBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ResearchOpportunitiesFeedBuilder;
import com.zuehlke.pgadmissions.domain.enums.FeedFormat;
import com.zuehlke.pgadmissions.dto.AdvertDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.ResearchOpportunitiesFeedService;

public class AdvertsControllerTest {

    private AdvertsController controller;
    private AdvertService advertServiceMock;
    private ResearchOpportunitiesFeedService feedServiceMock;
    private ProgramDAO programDAOMock;
    private ProgramsService programsServiceMock;
    private ApplicationFormDAO applicationFormDAOMock;
    private final static Integer NO_SELECTED_ADVERT = Integer.MIN_VALUE;

    @Before
    public void setUp() {
        advertServiceMock = EasyMock.createMock(AdvertService.class);
        feedServiceMock = EasyMock.createMock(ResearchOpportunitiesFeedService.class);
        applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
        programDAOMock = EasyMock.createMock(ProgramDAO.class);
        programsServiceMock = EasyMock.createMock(ProgramsService.class);
        controller = new AdvertsController(advertServiceMock, feedServiceMock, applicationFormDAOMock, programDAOMock, programsServiceMock);
    }

    @Test
    public void shouldReturnNoAdvertsInJson() {
        EasyMock.expect(advertServiceMock.getActiveAdverts()).andReturn(Collections.<Advert> emptyList());
        EasyMock.replay(advertServiceMock);
        int expectedAdvertsSize = 0;
        assertAdvertsElementPresentWithExpectedLenght(expectedAdvertsSize, controller.activeAdverts(null));
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
        String activeAdvertsJson = controller.activeAdverts(NO_SELECTED_ADVERT);
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
        String activeAdvertsJson = controller.activeAdverts(NO_SELECTED_ADVERT);
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
        String activeAdvertsJson = controller.activeAdverts(NO_SELECTED_ADVERT);
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
        String activeAdvertsJson = controller.activeAdverts(NO_SELECTED_ADVERT);
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

        EasyMock.expect(advertServiceMock.getProgram(advert)).andReturn(program);
        EasyMock.expect(advertServiceMock.getActiveAdverts()).andReturn(advertList);
        EasyMock.expect(programDAOMock.getNextClosingDate(program)).andReturn(null);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(program)).andReturn(null);
        EasyMock.replay(advertServiceMock, programDAOMock);
        String activeAdvertsJson = controller.activeAdverts(advert.getId());
        assertThat(activeAdvertsJson, containsString(jsonProperty("id", advert.getId())));
        assertThat(activeAdvertsJson, containsString(jsonProperty("selected", true)));
    }

    @Test
    public void shouldHaveSelectedAdvertAsFirstElement() {
        Advert selectedAdvert = new AdvertBuilder().id(new Integer(1)).description("Advert").funding("Funding").studyDuration(1).build();
        Advert notSelectedAdvert = new AdvertBuilder().id(new Integer(2)).description("Advert").funding("Funding").studyDuration(1).build();
        Program program = new ProgramBuilder().code("code1").title("another title").build();

        List<Advert> advertList = Arrays.asList(notSelectedAdvert, selectedAdvert);

        EasyMock.expect(advertServiceMock.getProgram(selectedAdvert)).andReturn(program);
        EasyMock.expect(advertServiceMock.getProgram(notSelectedAdvert)).andReturn(program);
        EasyMock.expect(advertServiceMock.getActiveAdverts()).andReturn(advertList);
        EasyMock.expect(programDAOMock.getNextClosingDate(program)).andReturn(null).times(2);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(program)).andReturn(null).times(2);
        EasyMock.replay(advertServiceMock, programDAOMock);

        String resultJson = controller.activeAdverts(new Integer(1));

        Map<?, ?> resultMap = new Gson().fromJson(resultJson, Map.class);
        List<?> activeAdvertsList = (List<?>) resultMap.get("adverts");
        Map<?, ?> selectedAdvertMap = (Map<?, ?>) activeAdvertsList.get(0);
        Assert.assertEquals(selectedAdvertMap.get("selected"), true);
        Map<?, ?> notSelectedAdvertMap = (Map<?, ?>) activeAdvertsList.get(1);
        Assert.assertEquals(notSelectedAdvertMap.get("selected"), false);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void shouldReturnAdvertsByFeedId() {
        Advert advert = new AdvertBuilder().id(new Integer(1)).description("Advert").funding("Funding").studyDuration(1).build();
        Program program = new ProgramBuilder().code("code1").title("another title").advert(advert).build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().title("foobar").feedFormat(FeedFormat.LARGE).id(1).programs(program).build();
        EasyMock.expect(feedServiceMock.getById(1)).andReturn(feed);
        EasyMock.expect(advertServiceMock.getProgram(advert)).andReturn(program);
        EasyMock.expect(programDAOMock.getNextClosingDate(program)).andReturn(null);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(program)).andReturn(null);
        
        EasyMock.replay(feedServiceMock, advertServiceMock, programDAOMock);
        Map feeds = controller.getFeeds(1, null, null);
        EasyMock.verify(feedServiceMock, advertServiceMock);

        List<?> advertsList = (List<?>) feeds.get("adverts");
        AdvertDTO dto = (AdvertDTO) advertsList.get(0);
        Assert.assertEquals("code1", dto.getProgramCode());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void shouldReturnAdvertsByUsername() {
        Advert advert1 = new AdvertBuilder().id(new Integer(1)).description("Advert").funding("Funding").studyDuration(1).build();
        Advert advert2 = new AdvertBuilder().id(new Integer(2)).description("Advert2").funding("Funding2").studyDuration(1).build();
        Program program1 = new ProgramBuilder().code("code1").title("another title").advert(advert1).build();
        Program program2 = new ProgramBuilder().code("code1").title("another title").advert(advert2).build();
        ResearchOpportunitiesFeed feed1 = new ResearchOpportunitiesFeedBuilder().title("foobar").feedFormat(FeedFormat.LARGE).id(1).programs(program1).build();
        ResearchOpportunitiesFeed feed2 = new ResearchOpportunitiesFeedBuilder().title("foobar").feedFormat(FeedFormat.LARGE).id(1).programs(program2).build();
        EasyMock.expect(feedServiceMock.getDefaultOpportunitiesFeedsByUsername("feeder", null)).andReturn(Lists.newArrayList(feed1, feed2));
        EasyMock.expect(advertServiceMock.getProgram(advert1)).andReturn(program1);
        EasyMock.expect(advertServiceMock.getProgram(advert2)).andReturn(program2);
        EasyMock.expect(programDAOMock.getNextClosingDate(program1)).andReturn(null);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(program1)).andReturn(null);
        EasyMock.expect(programDAOMock.getNextClosingDate(program2)).andReturn(null);
        EasyMock.expect(programDAOMock.getFirstAdministratorForProgram(program2)).andReturn(null);
        EasyMock.replay(feedServiceMock, advertServiceMock, programDAOMock);
        Map feeds = controller.getFeeds(null, "feeder", null);
        EasyMock.verify(feedServiceMock, advertServiceMock);

        List<AdvertDTO> advertsList = (List<AdvertDTO>) feeds.get("adverts");
        Matcher<Iterable<AdvertDTO>> advertsListMatcher = Matchers.hasItems(Matchers.hasProperty("programCode", Matchers.equalTo("code1")));
        assertThat(advertsList, advertsListMatcher);
    }
    
    @Test
    public void shouldGetSelectedAvertFromSessionIfSpecified(){
        DefaultSavedRequest savedRequest = EasyMock.createMock(DefaultSavedRequest.class);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_SAVED_REQUEST", savedRequest);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        
        EasyMock.expect(savedRequest.getParameterValues("advert")).andReturn(new String[]{"88"});
        
        EasyMock.replay(savedRequest);
        int advertId = controller.getSelectedAdvert(null, request);
        EasyMock.verify(savedRequest);
        
        assertEquals(88, advertId);
    }

    @Test
    public void shouldGetSelectedAvertFromSessionIfOnlyProjectSpecified(){
        DefaultSavedRequest savedRequest = EasyMock.createMock(DefaultSavedRequest.class);
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Project project = new ProjectBuilder().advert(new AdvertBuilder().id(11).build()).build();

        session.setAttribute("SPRING_SECURITY_SAVED_REQUEST", savedRequest);
        request.setSession(session);
        
        EasyMock.expect(savedRequest.getParameterValues("advert")).andReturn(new String[]{});
        EasyMock.expect(savedRequest.getParameterValues("project")).andReturn(new String[]{"33"});
        EasyMock.expect(programsServiceMock.getProject(33)).andReturn(project);
        
        EasyMock.replay(savedRequest, programsServiceMock);
        int advertId = controller.getSelectedAdvert(null, request);
        EasyMock.verify(savedRequest, programsServiceMock);
        
        assertEquals(11, advertId);
    }
    
    @Test
    public void shouldGetSelectedAvertFromSessionIfOnlyProgramSpecified(){
        DefaultSavedRequest savedRequest = EasyMock.createMock(DefaultSavedRequest.class);
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Program program = new ProgramBuilder().advert(new AdvertBuilder().id(99).build()).build();

        session.setAttribute("SPRING_SECURITY_SAVED_REQUEST", savedRequest);
        request.setSession(session);
        
        EasyMock.expect(savedRequest.getParameterValues("advert")).andReturn(new String[]{});
        EasyMock.expect(savedRequest.getParameterValues("project")).andReturn(new String[]{});
        EasyMock.expect(savedRequest.getParameterValues("program")).andReturn(new String[]{"p1"});
        EasyMock.expect(programsServiceMock.getProgramByCode("p1")).andReturn(program);
        
        EasyMock.replay(savedRequest, programsServiceMock);
        int advertId = controller.getSelectedAdvert(null, request);
        EasyMock.verify(savedRequest, programsServiceMock);
        
        assertEquals(99, advertId);
    }

    @Test
    public void shouldOpenNewTabForStandaloneAdvert() {
        ModelMap modelMap = new ModelMap();
        controller.standaloneOpportunities(8, null, null, modelMap);

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
