package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.dto.Funding;


public class FundingValidatorTest {

	private FundingValidator validator;
	private Funding funding;
	
	@Test
	public void shouldSupportFunding() {
		assertTrue(validator.supports(Funding.class));
	}
	
	@Test
	public void shouldAcceptFunding() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfAwardDateNull() {
		funding.setFundingAwardDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfFundingDescriptionNull() {
		funding.setFundingDescription(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfFundingTypeNull() {
		funding.setFundingType(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfFundingFileNull() {
		funding.setFundingFile(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfFundingFileNoName() {
		funding.setFundingFile(new MultipartFile() {
			
			@Override
			public void transferTo(File dest) throws IOException, IllegalStateException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public long getSize() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getOriginalFilename() {
				return "";
			}
			
			@Override
			public String getName() {
				return "test";
			}
			
			@Override
			public InputStream getInputStream() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getContentType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public byte[] getBytes() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test 
	public void shouldRejectIfFundingValueNull() {
		funding.setFundingValue(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfAwardFateIsFutureDate(){
		Date tomorrow;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		tomorrow = calendar.getTime();
		funding.setFundingAwardDate(tomorrow);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "fundingAwardDate");
		validator.validate(funding, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("funding.fundingAwardDate.future", mappingResult.getFieldError("fundingAwardDate").getCode());
	}
	
	
	@SuppressWarnings("deprecation")
	@Before
	public void setup() throws ParseException{
		validator = new FundingValidator();
		funding = new Funding();
		funding.setFundingAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"));
		funding.setFundingDescription("Description");
		funding.setFundingId(2);
		funding.setFundingType(FundingType.EMPLOYER);
		funding.setFundingValue("2000");
		funding.setFundingFile(new MultipartFile() {
			
			@Override
			public void transferTo(File dest) throws IOException, IllegalStateException {
				// TODO Auto-generated method stub
			}
			
			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public long getSize() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getOriginalFilename() {
				return "test.pdf";
			}
			
			@Override
			public String getName() {
				return "test";
			}
			
			@Override
			public InputStream getInputStream() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getContentType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public byte[] getBytes() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}
}
