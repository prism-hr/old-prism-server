package com.zuehlke.pgadmissions.services.importers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ImportedObject;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.referencedata.adapters.CountryOfBirthAdapter;
import com.zuehlke.pgadmissions.referencedata.adapters.ImportData;
import com.zuehlke.pgadmissions.referencedata.adapters.PrismProgrammeAdapter;
import com.zuehlke.pgadmissions.referencedata.builders.CountryOfBirthBuilder;
import com.zuehlke.pgadmissions.referencedata.builders.PrismProgrammeBuilder;

public class ImportServiceTest {

    private ImportService importService;
    private List<ImportedObject> currentData;
    private List<ImportData> importData;

    @Test
    public void testMergeData() {
        currentData.add(new CountryBuilder().id(1).code("UK").enabled(true).name("United Kingdom").build()); // 1-0 enabled
        currentData.add(new CountryBuilder().id(2).code("A").enabled(false).name("Aaa").build()); // 1-0 disabled
        currentData.add(new CountryBuilder().id(3).code("PL").enabled(true).name("Poland").build()); // 1-1 enabled same attributes
        importData.add(new CountryOfBirthAdapter(new CountryOfBirthBuilder().code("PL").name("Poland").toCountry()));
        currentData.add(new CountryBuilder().id(33).code("PL").enabled(false).name("PL").build()); // another one with same code
        currentData.add(new CountryBuilder().id(34).code("PL").enabled(true).name("PL2").build()); // another enabled with same code
        currentData.add(new CountryBuilder().id(4).code("P").enabled(false).name("Ppppp").build()); // 1-1 disabled same attributes
        importData.add(new CountryOfBirthAdapter(new CountryOfBirthBuilder().code("P").name("Ppppp").toCountry()));
        currentData.add(new CountryBuilder().id(44).code("P").enabled(false).name("PP").build()); // 1-1 disabled same attributes
        importData.add(new CountryOfBirthAdapter(new CountryOfBirthBuilder().code("P").name("P").toCountry()));
        currentData.add(new CountryBuilder().id(5).code("PLL").enabled(true).name("B").build()); // 1-1 enabled different attributes
        importData.add(new CountryOfBirthAdapter(new CountryOfBirthBuilder().code("PLL").name("BB").toCountry()));
        currentData.add(new CountryBuilder().id(6).code("PP").enabled(false).name("C").build()); // 1-1 disabled different attributes
        importData.add(new CountryOfBirthAdapter(new CountryOfBirthBuilder().code("PP").name("CC").toCountry()));
        importData.add(new CountryOfBirthAdapter(new CountryOfBirthBuilder().code("N").name("N").toCountry())); // 0-1

        List<ImportedObject> changes = importService.merge(currentData, importData);
        Assert.assertEquals(8, changes.size());
        Assert.assertEquals("N", changes.get(0).getStringCode());
        Assert.assertEquals(Boolean.TRUE, changes.get(0).getEnabled());
        Assert.assertEquals("P", changes.get(1).getStringCode());
        Assert.assertEquals("P", changes.get(1).getName());
        Assert.assertEquals(Boolean.TRUE, changes.get(1).getEnabled());
        Assert.assertEquals("P", changes.get(2).getStringCode());
        Assert.assertEquals("Ppppp", changes.get(2).getName());
        Assert.assertEquals(Boolean.TRUE, changes.get(2).getEnabled());
        Assert.assertEquals("PL", changes.get(3).getStringCode());
        Assert.assertEquals("PL2", changes.get(3).getName());
        Assert.assertEquals(Boolean.FALSE, changes.get(3).getEnabled());
        Assert.assertEquals("PLL", changes.get(4).getStringCode());
        Assert.assertEquals(Boolean.FALSE, changes.get(4).getEnabled());
        Assert.assertEquals("PLL", changes.get(5).getStringCode());
        Assert.assertEquals(Boolean.TRUE, changes.get(5).getEnabled());
        Assert.assertEquals("PP", changes.get(6).getStringCode());
        Assert.assertEquals(Boolean.TRUE, changes.get(6).getEnabled());
        Assert.assertEquals("UK", changes.get(7).getStringCode());
        Assert.assertEquals(Boolean.FALSE, changes.get(7).getEnabled());
    }

    @Test
    public void testMergeProgrammeData() {
        ProgramBuilder pb = new ProgramBuilder();
        Program p1 = pb.code("A").title("A").id(1).build();
        Program p2 = pb.code("B").title("B").id(2).build();

        Date deadline = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        String deadlineStr = new SimpleDateFormat("yyyy-MM-dd").format(deadline);
        Date startDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        String startDateStr = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        Date yesterday = new DateTime().minusDays(1).toDate();

        currentData.add(new ProgramInstanceBuilder().id(1).academicYear("1").identifier("0001").program(p1).applicationDeadline(deadline)
                .applicationStartDate(startDate).studyOption("1", "option").enabled(true).build());
        currentData.add(new ProgramInstanceBuilder().id(2).academicYear("1").identifier("0002").program(p1).applicationDeadline(deadline)
                .applicationStartDate(startDate).studyOption("2", "option2").enabled(true).build());
        currentData.add(new ProgramInstanceBuilder().id(3).academicYear("1").identifier("0001").program(p2).applicationDeadline(deadline)
                .applicationStartDate(startDate).studyOption("1", "option").disabledDate(yesterday).enabled(false).build());
        currentData.add(new ProgramInstanceBuilder().id(4).academicYear("1").identifier("0002").program(p2).applicationDeadline(deadline)
                .applicationStartDate(startDate).studyOption("2", "option2").disabledDate(yesterday).enabled(false).build());
        importData.add(new PrismProgrammeAdapter(new PrismProgrammeBuilder().academicYear("1").identifier("0001").code("A").endDate(deadlineStr).name("A")
                .startDate(startDateStr).studyOption("option").studyOptionCode("1").toPrismProgramme()));
        importData.add(new PrismProgrammeAdapter(new PrismProgrammeBuilder().academicYear("1").identifier("0001").code("B").endDate(deadlineStr).name("B")
                .startDate(startDateStr).studyOption("option").studyOptionCode("1").toPrismProgramme()));
        importData.add(new PrismProgrammeAdapter(new PrismProgrammeBuilder().academicYear("1").identifier("0001").code("B").endDate(deadlineStr).name("B")
                .startDate(startDateStr).studyOption("option").studyOptionCode("1").toPrismProgramme()));
        importData.add(new PrismProgrammeAdapter(new PrismProgrammeBuilder().academicYear("1").identifier("0001").code("B").endDate(deadlineStr).name("C")
                .startDate(startDateStr).studyOption("option").studyOptionCode("1").toPrismProgramme()));
        importData.add(new PrismProgrammeAdapter(new PrismProgrammeBuilder().academicYear("1").identifier("0001").code("C").endDate(deadlineStr).name("D")
                .startDate(startDateStr).studyOption("option").studyOptionCode("1").toPrismProgramme()));
        importData.add(new PrismProgrammeAdapter(new PrismProgrammeBuilder().academicYear("1").identifier("0002").code("C").endDate(deadlineStr).name("D")
                .startDate(startDateStr).studyOption("option2").studyOptionCode("2").toPrismProgramme()));

        List<ImportedObject> changes = importService.merge(currentData, importData);
        Assert.assertEquals(6, changes.size());

        ProgramInstance change1 = (ProgramInstance) changes.get(0);
        ProgramInstance change2 = (ProgramInstance) changes.get(1);
        ProgramInstance change3 = (ProgramInstance) changes.get(2);
        ProgramInstance change4 = (ProgramInstance) changes.get(3);
        ProgramInstance change5 = (ProgramInstance) changes.get(4);
        ProgramInstance change6 = (ProgramInstance) changes.get(5);

        Assert.assertEquals(Integer.valueOf(2), change1.getId());
        Assert.assertFalse(change1.getEnabled());
        Assert.assertEquals(new Date().getTime(), change1.getDisabledDate().getTime(), 1000);

        Assert.assertEquals(3, change2.getId().intValue());
        Assert.assertEquals(yesterday, change2.getDisabledDate());

        Assert.assertEquals("B", change3.getName());
        Assert.assertNull(change3.getDisabledDate());
        
        Assert.assertEquals("C", change4.getName());
        Assert.assertEquals("B", change4.getStringCode());
        Assert.assertEquals("D", change5.getName());
        Assert.assertEquals("D", change6.getName());

        Assert.assertSame(change2.getProgram(), p2);
        Assert.assertSame(change5.getProgram(), change6.getProgram());
    }

    @Before
    public void setUp() {
        importService = new ImportService();
        currentData = new ArrayList<ImportedObject>();
        importData = new ArrayList<ImportData>();
    }

}
