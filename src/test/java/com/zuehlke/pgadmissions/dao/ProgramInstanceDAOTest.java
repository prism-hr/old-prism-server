package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramFeed;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramFeedBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ProgramInstanceDAOTest extends AutomaticRollbackTestCase {

    private QualificationInstitution institution;

    @Override
    public void setup() {
        super.setup();
        institution = QualificationInstitutionBuilder.aQualificationInstitution().build();
        save(institution);
    }

    @Test
    public void shouldNotReturnProgramInstanceForOtherProgram() {
        Program progOne = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("aaaaa").title("hi").institution(institution).build();
        Program progTwo = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("bbbb").title("hello").institution(institution).build();
        save(progOne, progTwo);
        Date now = Calendar.getInstance().getTime();
        Date oneYearInFuture = DateUtils.addYears(now, 1);

        ProgramInstance programInstanceOne = new ProgramInstanceBuilder().program(progOne).applicationDeadline(oneYearInFuture).studyOption("31", "Part-time")
                .applicationStartDate(now).academicYear("2013").enabled(true).build();
        ProgramInstance programInstanceTwo = new ProgramInstanceBuilder().program(progTwo).applicationDeadline(oneYearInFuture).studyOption("31", "Part-time")
                .applicationStartDate(now).academicYear("2013").enabled(true).build();
        save(programInstanceOne, programInstanceTwo);
        flushAndClearSession();

        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);

        List<ProgramInstance> activeInstances = dao.getActiveProgramInstances(progOne);
        assertTrue(listContainsId(programInstanceOne, activeInstances));
        assertFalse(listContainsId(programInstanceTwo, activeInstances));
    }

    @Test
    public void shouldReturnProgramInstanceWithDeadlineInTheFuture() {
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("aaaaa").title("hi").institution(institution).build();
        save(program);
        Date now = Calendar.getInstance().getTime();
        Date oneYearInFuture = DateUtils.addYears(now, 1);

        ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearInFuture).studyOption("31", "Part-time")
                .applicationStartDate(now).applicationStartDate(now).academicYear("2013").enabled(true).build();

        save(programInstance);
        flushAndClearSession();

        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);

        List<ProgramInstance> activeInstances = dao.getActiveProgramInstances(program);
        assertTrue(listContainsId(programInstance, activeInstances));
    }

    @Test
    public void shouldReturnProgramInstanceWithDeadlineToday() {
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("aaaaa").title("hi").institution(institution).build();
        save(program);
        Date now = Calendar.getInstance().getTime();
        Date today = DateUtils.truncate(now, Calendar.DATE);
        ProgramInstance programInstance = new ProgramInstanceBuilder().applicationDeadline(today).program(program).studyOption("31", "Part-time")
                .applicationStartDate(now).applicationStartDate(now).academicYear("2013").enabled(true).build();
        save(programInstance);
        flushAndClearSession();

        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);

        List<ProgramInstance> activeInstances = dao.getActiveProgramInstances(program);
        assertTrue(listContainsId(programInstance, activeInstances));
    }

    @Test
    public void shouldNotReturnProgramInstanceWithDeadlineInThePast() {
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("aaaaa").title("hi").institution(institution).build();
        save(program);
        Date now = Calendar.getInstance().getTime();
        Date oneYearAgo = DateUtils.addYears(now, -1);
        ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearAgo).studyOption("31", "Part-time")
                .applicationStartDate(now).academicYear("2013").enabled(true).build();
        save(programInstance);
        flushAndClearSession();

        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);

        List<ProgramInstance> activeInstances = dao.getActiveProgramInstances(program);
        assertFalse(activeInstances.contains(programInstance));
    }

    @Test
    public void shouldNotReturnProgramInstanceWithStudyOptionAndDeadlineNotInThePastForOtherProgram() {
        Program progOne = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("aaaaa").title("hi").institution(institution).build();
        Program progTwo = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("bbbb").title("hello").institution(institution).build();
        save(progOne, progTwo);
        Date now = Calendar.getInstance().getTime();
        Date today = DateUtils.truncate(now, Calendar.DATE);
        ProgramInstance programInstanceOne = new ProgramInstanceBuilder().program(progOne).applicationDeadline(today).studyOption("1", "Full-time")
                .applicationStartDate(now).academicYear("2013").enabled(true).build();
        ProgramInstance programInstanceTwo = new ProgramInstanceBuilder().program(progTwo).applicationDeadline(today).studyOption("1", "Full-time")
                .applicationStartDate(now).academicYear("2013").enabled(true).build();
        save(programInstanceOne, programInstanceTwo);
        flushAndClearSession();

        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);

        List<ProgramInstance> matchedInstances = dao.getActiveProgramInstancesByStudyOption(progOne, "Full-time");
        assertTrue(listContainsId(programInstanceOne, matchedInstances));
        assertFalse(listContainsId(programInstanceTwo, matchedInstances));
    }

    @Test
    public void shouldReturnProgramInstanceWithStudyOptionAndDeadlineNotInThePast() {
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a57").domicileCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("aaaaa").title("hi").institution(institution).build();
        save(institution, program);
        Date now = Calendar.getInstance().getTime();
        Date today = DateUtils.truncate(now, Calendar.DATE);
        ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(today).studyOption("1", "Full-time")
                .applicationStartDate(now).academicYear("2013").enabled(true).build();
        save(programInstance);
        flushAndClearSession();

        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);

        List<ProgramInstance> matchedInstances = dao.getActiveProgramInstancesByStudyOption(program, "Full-time");
        assertTrue(listContainsId(programInstance, matchedInstances));
    }

    @Test
    public void shouldNotReturnProgramInstanceWithStudyOptionAndDeadlineInThePast() {
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a63").domicileCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("aaaaa").title("hi").institution(institution).build();
        save(institution, program);
        Date now = Calendar.getInstance().getTime();
        Date oneYearAgo = DateUtils.addYears(now, -1);
        ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearAgo).studyOption("1", "Full-time")
                .applicationStartDate(now).academicYear("2013").enabled(true).build();
        save(programInstance);
        flushAndClearSession();

        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);

        List<ProgramInstance> matchedInstances = dao.getActiveProgramInstancesByStudyOption(program, "Full-time");
        assertFalse(matchedInstances.contains(programInstance));
    }

    @Test
    public void shouldNotReturnProgramInstanceWithoutStudyOptionAndDeadlineNotInThePast() {
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("aaaaa").title("hi").institution(institution).build();
        save(program);
        Date now = Calendar.getInstance().getTime();
        Date oneYearAgo = DateUtils.addYears(now, -1);
        ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearAgo).studyOption("1", "Full-time")
                .applicationStartDate(now).academicYear("2013").enabled(true).build();
        save(programInstance);
        flushAndClearSession();

        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);

        List<ProgramInstance> matchedInstances = dao.getActiveProgramInstancesByStudyOption(program, "Full-time");
        assertFalse(matchedInstances.contains(programInstance));
    }

    @Test
    public void shouldFindProgrameInstancesWithAStartDateInTheFuture() {
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("aaaaa").title("hi").institution(institution).build();
        save(program);
        Date now = Calendar.getInstance().getTime();
        Date yesterday = DateUtils.addDays(now, -1);
        Date eightMonthsAgo = DateUtils.addMonths(now, -8);
        Date fourMonthsFromNow = DateUtils.addMonths(now, 4);
        Date oneYearAndfourMonthsFromNow = DateUtils.addMonths(now, 16);

        Date startDateInOneMonth = DateUtils.addMonths(now, 1);

        ProgramInstance programInstanceOne = new ProgramInstanceBuilder().program(program).applicationDeadline(eightMonthsAgo)
                .studyOption("31", "Modular/flexible study").applicationStartDate(yesterday).academicYear("2013").enabled(true).build();

        ProgramInstance programInstanceTwo = new ProgramInstanceBuilder().program(program).applicationDeadline(fourMonthsFromNow)
                .studyOption("31", "Modular/flexible study").applicationStartDate(startDateInOneMonth).academicYear("2013").enabled(true).build();
        ProgramInstance programInstanceThree = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearAndfourMonthsFromNow)
                .studyOption("1", "Full-time").applicationStartDate(yesterday).academicYear("2013").enabled(true).build();
        ProgramInstance programInstanceFour = new ProgramInstanceBuilder().program(program).applicationDeadline(fourMonthsFromNow)
                .studyOption("31", "Part-time").applicationStartDate(yesterday).academicYear("2013").enabled(true).build();
        save(programInstanceOne, programInstanceThree, programInstanceFour, programInstanceTwo);
        flushAndClearSession();

        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
        List<ProgramInstance> activeProgramInstancesOrderedByApplicationStartDate = dao.getActiveProgramInstancesOrderedByApplicationStartDate(program,
                "Modular/flexible study");

        assertEquals(1, activeProgramInstancesOrderedByApplicationStartDate.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFindAllProgramInstancesForProgramFeed() {
        ProgramFeed programFeed1 = new ProgramFeedBuilder().feedUrl("url").institution(institution).build();
        ProgramFeed programFeed2 = new ProgramFeedBuilder().feedUrl("url2").institution(institution).build();

        Program program1 = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).programFeed(programFeed1).code("AAA").institution(institution).build();
        Program program2 = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).programFeed(programFeed2).code("BBB").institution(institution).build();
        Program program3 = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).programFeed(programFeed2).code("CCC").institution(institution).build();

        ProgramInstance programInstance1 = new ProgramInstanceBuilder().identifier("i1").program(program1).academicYear("1985")
                .applicationStartDate(new Date()).applicationDeadline(new Date()).enabled(true).build();
        ProgramInstance programInstance2 = new ProgramInstanceBuilder().identifier("i2").program(program2).academicYear("1985")
                .applicationStartDate(new Date()).applicationDeadline(new Date()).enabled(true).build();
        ProgramInstance programInstance3 = new ProgramInstanceBuilder().identifier("i3").program(program3).academicYear("1985")
                .applicationStartDate(new Date()).applicationDeadline(new Date()).enabled(true).build();

        save(programFeed1, programFeed2, program1, program2, program3, programInstance1, programInstance2, programInstance3);
        flushAndClearSession();

        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
        List<ProgramInstance> instances = dao.getAllProgramInstances(programFeed2);

        assertThat(instances, containsInAnyOrder(hasProperty("identifier", equalTo("i2")), hasProperty("identifier", equalTo("i3"))));
    }

    @Test
    public void shouldGetDistinctsStudyOptions() {
        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
        List<Object[]> studyOptions = dao.getDistinctStudyOptions();
        assertTrue(studyOptions.size() > 0);
    }

    @Test
    public void shouldGetLapsedInstances() {
        Program program = ProgramBuilder.aProgram(institution).contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).build();
        DateTime today = new DateTime().withTimeAtStartOfDay();
        DateTime yesterday = today.minusDays(1);
        DateTime tomorrow = today.plusDays(1);

        ProgramInstance instance1 = ProgramInstanceBuilder.aProgramInstance(program).identifier("CUSTOM").enabled(true).disabledDate(yesterday.toDate())
                .build();
        ProgramInstance instance2 = ProgramInstanceBuilder.aProgramInstance(program).identifier("CUSTOM").enabled(true).disabledDate(today.toDate()).build();
        ProgramInstance instance3 = ProgramInstanceBuilder.aProgramInstance(program).identifier("CUSTOM").enabled(true).disabledDate(tomorrow.toDate()).build();
        ProgramInstance instance4 = ProgramInstanceBuilder.aProgramInstance(program).identifier("CUSTOM").enabled(false).disabledDate(yesterday.toDate())
                .build();
        ProgramInstance instance5 = ProgramInstanceBuilder.aProgramInstance(program).identifier("000").enabled(true).disabledDate(yesterday.toDate()).build();

        save(program, instance1, instance2, instance3, instance4, instance5);
        flushAndClearSession();

        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
        List<ProgramInstance> lapsedInstances = dao.getLapsedInstances();

        assertThat(lapsedInstances, hasItem(Matchers.<ProgramInstance> hasProperty("id", equalTo(instance1.getId()))));
        assertThat(lapsedInstances, not(hasItem(Matchers.<ProgramInstance> hasProperty("id", equalTo(instance2.getId())))));
        assertThat(lapsedInstances, not(hasItem(Matchers.<ProgramInstance> hasProperty("id", equalTo(instance3.getId())))));
        assertThat(lapsedInstances, not(hasItem(Matchers.<ProgramInstance> hasProperty("id", equalTo(instance4.getId())))));
        assertThat(lapsedInstances, not(hasItem(Matchers.<ProgramInstance> hasProperty("id", equalTo(instance5.getId())))));
    }

    @Test
    public void shouldGetLatestActiveInstanceDeadlineForProgram() {
        Program program1 = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("AAA").institution(institution).build();
        Program program2 = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("BBB").institution(institution).build();

        DateTime today = new DateTime().withTimeAtStartOfDay();
        DateTime yesterday = today.minusDays(1);
        DateTime tomorrow = today.plusDays(1);

        ProgramInstance instance1 = ProgramInstanceBuilder.aProgramInstance(program1).enabled(true).applicationDeadline(yesterday.toDate()).build();
        ProgramInstance instance2 = ProgramInstanceBuilder.aProgramInstance(program1).enabled(true).applicationDeadline(today.toDate()).build();
        ProgramInstance instance3 = ProgramInstanceBuilder.aProgramInstance(program2).enabled(true).applicationDeadline(tomorrow.toDate()).build();
        ProgramInstance instance4 = ProgramInstanceBuilder.aProgramInstance(program1).enabled(false).applicationDeadline(tomorrow.toDate()).build();

        save(program1, program2, instance1, instance2, instance3, instance4);

        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
        Date deadline = dao.getLatestActiveInstanceDeadline(program1);

        assertEquals(today.toDate(), deadline);
    }

    @Test
    public void shouldGetStudyOptionsForProgram() {
        Program program1 = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("AAA").institution(institution).build();
        Program program2 = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("BBB").institution(institution).build();

        ProgramInstance instance1 = ProgramInstanceBuilder.aProgramInstance(program1).enabled(true).studyOption("o1", "o1").build();
        ProgramInstance instance2 = ProgramInstanceBuilder.aProgramInstance(program1).enabled(false).studyOption("o2", "o2").build();
        ProgramInstance instance3 = ProgramInstanceBuilder.aProgramInstance(program2).enabled(true).studyOption("o3", "o3").build();
        ProgramInstance instance4 = ProgramInstanceBuilder.aProgramInstance(program1).enabled(true).studyOption("o4", "o4").build();

        save(program1, program2, instance1, instance2, instance3, instance4);

        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
        List<String> studyOptions = dao.getStudyOptions(program1);

        assertThat(studyOptions, contains("o1", "o4"));
    }

    private boolean listContainsId(ProgramInstance instance, List<ProgramInstance> instances) {
        for (ProgramInstance entry : instances) {
            if (entry.getId().equals(instance.getId())) {
                return true;
            }
        }
        return false;
    }
}
