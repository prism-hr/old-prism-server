package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.ibm.icu.text.SimpleDateFormat;
import com.zuehlke.pgadmissions.controllers.prospectus.AdvertsController;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramClosingDateBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.AdvertService;

public class AdvertsControllerTest {

	private AdvertsController controller;
	private AdvertService advertService;
	private final static Integer NO_SELECTED_ADVERT=Integer.MIN_VALUE;
	
	@Before
	public void setUp(){
		advertService = createMock(AdvertService.class);
		controller = new AdvertsController(advertService);
	}
	
	@Test
	public void shouldReturnNoAdvertsInJson(){
		expect(advertService.getActiveAdverts()).andReturn(Collections.<Advert>emptyList());
		replay(advertService);
		int expectedAdvertsSize = 0;
		assertAdvertsElementPresentWithExpectedLenght(expectedAdvertsSize, controller.activeAdverts(null));
	}

	@Test
	public void shouldReturnOneAdvertsInJson(){
		Program program = new ProgramBuilder().code("code1").title("another title").build();
		Advert advert = new AdvertBuilder().id(1).title("Title").description("Advert").funding("Funding").isProgramAdvert(true).studyDuration(1).program(program).build();
		List<Advert> advertList = Arrays.asList(advert);
		expect(advertService.getActiveAdverts()).andReturn(advertList);
		replay(advertService);
		int expectedAdvertsSize = 1;
		String activeAdvertsJson = controller.activeAdverts(NO_SELECTED_ADVERT);
		assertAdvertsElementPresentWithExpectedLenght(expectedAdvertsSize, activeAdvertsJson);
		assertThat(activeAdvertsJson, containsString("Advert"));
	}

	@Test
	public void shouldReturnTwoAdvertsInJson(){
		Program programOne = new ProgramBuilder().code("code1").title("another title").build();
		Program programTwo = new ProgramBuilder().code("code2").title("another title2").build();
		Advert advertOne = new AdvertBuilder().id(1).title("Title1").description("Advert1").funding("Funding1").isProgramAdvert(true).studyDuration(1).program(programOne).build();
		Advert advertTwo = new AdvertBuilder().id(1).title("Title2").description("Advert2").funding("Funding2").isProgramAdvert(true).studyDuration(1).program(programTwo).build();
		List<Advert> advertList = Arrays.asList(advertOne,advertTwo);
		expect(advertService.getActiveAdverts()).andReturn(advertList);
		replay(advertService);
		int expectedAdvertsSize = 2;
		String activeAdvertsJson = controller.activeAdverts(NO_SELECTED_ADVERT);
		assertAdvertsElementPresentWithExpectedLenght(expectedAdvertsSize, activeAdvertsJson);
		assertThat(activeAdvertsJson, containsString("Advert1"));
		assertThat(activeAdvertsJson, containsString("Advert2"));
	}
	
	
	@Test
	public void shouldConvertAdvertWithoutClosingDateAndWithoutEmail(){
		Program program = new ProgramBuilder().code("code1").title("another title").build();
		Advert advert = new AdvertBuilder().id(1).description("Advert").funding("Funding").isProgramAdvert(true).studyDuration(1).program(program).build();
		List<Advert> advertList = Arrays.asList(advert);
		expect(advertService.getActiveAdverts()).andReturn(advertList);
		replay(advertService);
		String activeAdvertsJson = controller.activeAdverts(NO_SELECTED_ADVERT);
		assertThat(activeAdvertsJson, containsString(jsonProperty("id",advert.getId())));
		assertThat(activeAdvertsJson, containsString(jsonProperty("title",program.getTitle())));
		assertThat(activeAdvertsJson, containsString(jsonProperty("description",advert.getDescription())));
		assertThat(activeAdvertsJson, containsString(jsonProperty("funding",advert.getFunding())));
		assertThat(activeAdvertsJson, containsString(jsonProperty("programCode",program.getCode())));
		assertThat(activeAdvertsJson, not(containsString(jsonProperty("selected",true))));
		assertThat(activeAdvertsJson, not(containsString("email")));
		assertThat(activeAdvertsJson, not(containsString("closingDate")));
	}
	
	@Test
	public void shouldConvertAdvertWithFirstClosingDateAndFirstValidAdministratorEmail() throws Exception{
		ProgramClosingDate programClosingDateSecond=new ProgramClosingDateBuilder().closingDate(parseDate("01-Aug-2013")).build();
		ProgramClosingDate programClosingDateFirst=new ProgramClosingDateBuilder().closingDate(parseDate("01-Jun-2013")).build();
		RegisteredUser expiredAdmin= new RegisteredUserBuilder().email("invalidAccountEmail").accountNonExpired(false).build();
		RegisteredUser lockedAdmin= new RegisteredUserBuilder().email("invalidAccountEmail").accountNonLocked(false).build();
		RegisteredUser credentialsExpiredAdmin= new RegisteredUserBuilder().email("invalidAccountEmail").credentialsNonExpired(false).build();
		RegisteredUser notEnabledAdmin= new RegisteredUserBuilder().email("invalidAccountEmail").enabled(false).build();
		RegisteredUser validAdmin=new RegisteredUserBuilder().email("accountEmail").build();;
		Program program = new ProgramBuilder().code("code1").title("another title").closingDates(programClosingDateSecond, programClosingDateFirst).
				administrators(expiredAdmin,lockedAdmin, credentialsExpiredAdmin, notEnabledAdmin, validAdmin).build();
		Advert advert = new AdvertBuilder().id(1).description("Advert").funding("Funding").isProgramAdvert(true).studyDuration(1).program(program).build();
		List<Advert> advertList = Arrays.asList(advert);
		expect(advertService.getActiveAdverts()).andReturn(advertList);
		replay(advertService);
		String activeAdvertsJson = controller.activeAdverts(NO_SELECTED_ADVERT);
		assertThat(activeAdvertsJson, not(containsString(jsonProperty("selected",true))));
		assertThat(activeAdvertsJson, containsString(jsonProperty("id",advert.getId())));
		assertThat(activeAdvertsJson, containsString(jsonProperty("title",program.getTitle())));
		assertThat(activeAdvertsJson, containsString(jsonProperty("description",advert.getDescription())));
		assertThat(activeAdvertsJson, containsString(jsonProperty("funding",advert.getFunding())));
		assertThat(activeAdvertsJson, containsString(jsonProperty("programCode",program.getCode())));
		assertThat(activeAdvertsJson, containsString(jsonProperty("email",validAdmin.getEmail())));
		assertThat(activeAdvertsJson, containsString(jsonProperty("closingDate",programClosingDateFirst.getClosingDate())));
	}

	@Test
	public void shouldHaveASelectedAdvert(){
		Program program = new ProgramBuilder().code("code1").title("another title").build();
		Advert advert = new AdvertBuilder().id(1).description("Advert").funding("Funding").isProgramAdvert(true).studyDuration(1).program(program).build();
		List<Advert> advertList = Arrays.asList(advert);
		expect(advertService.getActiveAdverts()).andReturn(advertList);
		replay(advertService);
		String activeAdvertsJson = controller.activeAdverts(advert.getId());
		assertThat(activeAdvertsJson, containsString(jsonProperty("id",advert.getId())));
		assertThat(activeAdvertsJson, containsString(jsonProperty("selected",true)));
	}

	@Test
	public void shouldHaveSelectedAdvertAsFirstElement(){
		Program program = new ProgramBuilder().code("code1").title("another title").build();
		Advert selectedAdvert = new AdvertBuilder().id(1).description("Advert").funding("Funding").isProgramAdvert(true).studyDuration(1).program(program).build();
		Advert notSelectedAdvert = new AdvertBuilder().id(2).description("Advert").funding("Funding").isProgramAdvert(true).studyDuration(1).program(program).build();
		List<Advert> advertList = Arrays.asList(notSelectedAdvert,selectedAdvert);
		expect(advertService.getActiveAdverts()).andReturn(advertList);
		replay(advertService);
		
		String resultJson = controller.activeAdverts(selectedAdvert.getId());
		
		Map<?,?> resultMap = new Gson().fromJson(resultJson, Map.class);
		List<?> activeAdvertsList = (List<?>)resultMap.get("adverts");
		Map<?,?> selectedAdvertMap = (Map<?, ?>) activeAdvertsList.get(0);
		Assert.assertEquals(selectedAdvertMap.get("selected"), true);
		Map<?,?> notSelectedAdvertMap = (Map<?, ?>) activeAdvertsList.get(1);
		Assert.assertEquals(notSelectedAdvertMap.get("selected"), false);
	}

	private String jsonProperty(String key, Date closingDate) {
		SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy");
		return String.format("\"%s\":\"%s", key, format.format(closingDate));
	}

	private String jsonProperty(String key, String value) {
		return String.format("\"%s\":\"%s\"", key,value);
	}

	private String jsonProperty(String key, Object value) {
		return String.format("\"%s\":%s", key,value);
	}

	private void assertAdvertsElementPresentWithExpectedLenght(
			int expectedAdvertsSize, String activeAdvertsJson) {
		String resultJson = activeAdvertsJson;
		assertThat(resultJson, notNullValue());
		assertThat(resultJson, containsString("adverts"));
		Map<?,?> resultMap = new Gson().fromJson(resultJson, Map.class);
		List<?> activeAdvertsList = (List<?>)resultMap.get("adverts");
		assertThat(activeAdvertsList, hasSize(expectedAdvertsSize));
	}
	
	private Date parseDate(String strDate) throws ParseException{
		return DateUtils.parseDate(strDate, new String[] {"dd-MMM-yyyy", "dd MMM yyyy"});
	}
	
	
}
