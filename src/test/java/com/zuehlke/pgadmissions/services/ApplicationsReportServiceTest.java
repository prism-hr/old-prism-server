package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.DateValue;
import com.google.visualization.datasource.datatable.value.NumberValue;
import com.google.visualization.datasource.datatable.value.TextValue;
import com.google.visualization.datasource.datatable.value.Value;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

public class ApplicationsReportServiceTest {

    private RegisteredUser user;

    private ApplicationsReportService service;

    private ApplicationsService applicationsServiceMock;

    @Test
    public void testGetEmptyApplicationsReport() {
        // GIVEN
        List<ApplicationForm> applications = Lists.newArrayList();

        List<ApplicationsFilter> filters = Lists.newLinkedList();
        EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user, filters, SortCategory.APPLICANT_NAME, SortOrder.ASCENDING))
                .andReturn(applications);

        // WHEN
        EasyMock.replay(applicationsServiceMock);
        DataTable dataTable = service.getApplicationsReport(user, filters, SortCategory.APPLICANT_NAME, SortOrder.ASCENDING);
        EasyMock.verify(applicationsServiceMock);

        // THEN
        assertTrue(dataTable.getRows().isEmpty());
    }

    @Test
    public void testGetMinimalisticApplicationReport() {
        // GIVEN
        RegisteredUser applicant1 = new RegisteredUserBuilder().firstName("Genowefa").lastName("Pigwa").email("gienia@pigwa.pl").build();
        Program program1 = new ProgramBuilder().code("ABC").title("BBC").build();
        ProgrammeDetails programmeDetails1 = new ProgrammeDetailsBuilder().build();
        ApplicationForm app1 = new ApplicationFormBuilder().applicant(applicant1).applicationNumber("07").program(program1).programmeDetails(programmeDetails1)
                .build();
        List<ApplicationForm> applications = Lists.newArrayList(app1);

        List<ApplicationsFilter> filters = Lists.newLinkedList();
        EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user, filters, SortCategory.APPLICANT_NAME, SortOrder.ASCENDING))
                .andReturn(applications);

        // WHEN
        EasyMock.replay(applicationsServiceMock);
        DataTable table = service.getApplicationsReport(user, filters, SortCategory.APPLICANT_NAME, SortOrder.ASCENDING);
        EasyMock.verify(applicationsServiceMock);

        // THEN
        assertEquals(1, table.getRows().size());

        TableRow row = table.getRow(0);
        assertEquals("07", getTextValue(table, row, "applicationId"));
        assertEquals("Genowefa", getTextValue(table, row, "firstNames"));
        assertEquals("Pigwa", getTextValue(table, row, "lastName"));
        assertEquals("gienia@pigwa.pl", getTextValue(table, row, "email"));
        assertEquals("ABC", getTextValue(table, row, "programmeId"));
        assertEquals("BBC", getTextValue(table, row, "programmeName"));
        assertEquals("", getTextValue(table, row, "projectTitle"));
        assertEquals("", getTextValue(table, row, "studyOption"));
        assertEquals("", getTextValue(table, row, "provisionalSupervisors"));
        assertEquals("", getTextValue(table, row, "academicYear"));
        assertEquals("null", getDateValue(table, row, "submittedDate"));
        assertEquals("null", getDateValue(table, row, "lastEditedDate"));
        assertEquals("Not Submitted", getTextValue(table, row, "status"));
        assertEquals(0, getNumberValue(table, row, "validationTime"), 0);
        assertEquals("", getTextValue(table, row, "feeStatus"));
        assertEquals("", getTextValue(table, row, "academicallyQualified"));
        assertEquals("", getTextValue(table, row, "adequateEnglish"));
        assertEquals(0, getNumberValue(table, row, "receivedReferences"), 0);
        assertEquals(0, getNumberValue(table, row, "positiveReviewEndorsements"), 0);
        assertEquals(0, getNumberValue(table, row, "negativeReviewEndorsements"),0);
        assertEquals(0, getNumberValue(table, row, "declinedReferences"),0);
        assertEquals(0, getNumberValue(table, row, "interviewTime"),0);
        assertEquals(0, getNumberValue(table, row, "interviewReports"),0);
        assertEquals(0, getNumberValue(table, row, "positiveInterviewEndorsements"),0);
        assertEquals(0, getNumberValue(table, row, "negativeInterviewEndorsements"),0);
        assertEquals(0, getNumberValue(table, row, "approvalTime"),0);
        assertEquals(0, getNumberValue(table, row, "approvalStages"),0);
        assertEquals("", getTextValue(table, row, "primarySupervisor"));
        assertEquals("", getTextValue(table, row, "secondarySupervisor"));
        assertEquals("Not approved", getTextValue(table, row, "outcome"));
        assertEquals("null", getDateValue(table, row, "outcomedate"));
        assertEquals("", getTextValue(table, row, "outcomeType"));
        assertEquals("", getTextValue(table, row, "outcomeNote"));
    }

    @Before
    public void setUp() {
        user = new RegisteredUser();
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        service = new ApplicationsReportService(applicationsServiceMock);
    }

    public String getTextValue(DataTable table, TableRow row, String columnId) {
        int columnIndex = table.getColumnIndex(columnId);
        TextValue value = (TextValue) row.getCell(columnIndex).getValue();
        return value.getValue();
    }
    
    public String getDateValue(DataTable table, TableRow row, String columnId) {
        int columnIndex = table.getColumnIndex(columnId);
        DateValue value = (DateValue) row.getCell(columnIndex).getValue();
        return value.toString();
    }

    public double getNumberValue(DataTable table, TableRow row, String columnId) {
        int columnIndex = table.getColumnIndex(columnId);
        NumberValue value = (NumberValue) row.getCell(columnIndex).getValue();
        return value.getValue();
    }
    
}
