package com.zuehlke.pgadmissions.services.importers;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ImportedObject;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.referencedata.adapters.CountryOfBirthAdapter;
import com.zuehlke.pgadmissions.referencedata.adapters.ImportData;
import com.zuehlke.pgadmissions.referencedata.builders.CountryOfBirthBuilder;

public class ImportServiceTest {

	private ImportService importService;
	private List<ImportedObject> currentData;
	private List<ImportData> importData;

	@Test
	public void testMergeData() {
		List<ImportedObject> changes = importService.merge(currentData, importData);
		Assert.assertEquals(6, changes.size());
		Assert.assertEquals("N", changes.get(0).getCode());
		Assert.assertEquals(Boolean.TRUE, changes.get(0).getEnabled());
		Assert.assertEquals("P", changes.get(1).getCode());
		Assert.assertEquals(Boolean.TRUE, changes.get(1).getEnabled());
		Assert.assertEquals("PLL", changes.get(2).getCode());
		Assert.assertEquals(Boolean.FALSE, changes.get(2).getEnabled());
		Assert.assertEquals("PLL", changes.get(3).getCode());
		Assert.assertEquals(Boolean.TRUE, changes.get(3).getEnabled());
		Assert.assertEquals("PP", changes.get(4).getCode());
		Assert.assertEquals(Boolean.TRUE, changes.get(4).getEnabled());
		Assert.assertEquals("UK", changes.get(5).getCode());
		Assert.assertEquals(Boolean.FALSE, changes.get(5).getEnabled());
	}

	@Before
	public void setUp() {
		importService = new ImportService();
		currentData = new ArrayList<ImportedObject>();
		importData = new ArrayList<ImportData>();
		currentData.add(new CountryBuilder().id(1).code("UK").enabled(true).name("United Kingdom").toCountry()); // 1-0 enabled
		currentData.add(new CountryBuilder().id(2).code("A").enabled(false).name("Aaa").toCountry()); // 1-0 disabled
		currentData.add(new CountryBuilder().id(3).code("PL").enabled(true).name("Poland").toCountry()); // 1-1 enabled same attributes
		importData.add(new CountryOfBirthAdapter(new CountryOfBirthBuilder().code("PL").name("Poland").toCountry()));
		currentData.add(new CountryBuilder().id(33).code("PL").enabled(false).name("PL").toCountry()); // another one with same code
		currentData.add(new CountryBuilder().id(4).code("P").enabled(false).name("Ppppp").toCountry()); // 1-1 disabled same attributes
		importData.add(new CountryOfBirthAdapter(new CountryOfBirthBuilder().code("P").name("Ppppp").toCountry()));
		currentData.add(new CountryBuilder().id(44).code("P").enabled(false).name("PP").toCountry()); // 1-1 disabled same attributes
		currentData.add(new CountryBuilder().id(5).code("PLL").enabled(true).name("B").toCountry()); // 1-1 enabled different attributes
		importData.add(new CountryOfBirthAdapter(new CountryOfBirthBuilder().code("PLL").name("BB").toCountry()));
		currentData.add(new CountryBuilder().id(6).code("PP").enabled(false).name("C").toCountry()); // 1-1 disabled different attributes
		importData.add(new CountryOfBirthAdapter(new CountryOfBirthBuilder().code("PP").name("CC").toCountry()));
		importData.add(new CountryOfBirthAdapter(new CountryOfBirthBuilder().code("N").name("N").toCountry())); // 0-1
	}

}
