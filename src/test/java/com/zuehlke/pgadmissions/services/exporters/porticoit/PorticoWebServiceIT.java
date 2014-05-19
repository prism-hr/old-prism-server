package com.zuehlke.pgadmissions.services.exporters.porticoit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.core.WebServiceTemplate;

import au.com.bytecode.opencsv.CSVWriter;

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.GenderTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.QualificationsTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationTransferError;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.exceptions.ExportServiceException;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.exporters.ExportService;
import com.zuehlke.pgadmissions.services.exporters.TransferListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/testPorticoIntegrationContext.xml" })
@TransactionConfiguration(defaultRollback = true)
@Ignore
public class PorticoWebServiceIT {

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    private ApplicationFormService applicationsService;

    @Autowired
    private ExportService uclExportService;

    @Autowired
    private SessionFactory sessionFactory;

    private static final String TEST_REPORT_FILENAME = "PorticoWebServiceIT.csv";

    private SecureRandom random = new SecureRandom();

    private List<String> csvEntries;

    private CSVWriter writer;

    private String randomLastname;

    private String randomFirstname;

    private String randomEmail;

    private String receivedApplicantId;

    private Application randomApplicationForm;

    private static Set<String> USED_APPLICATION_NUMBERS = new HashSet<String>();

    @Before
    public void prepare() throws IOException {
        writer = new CSVWriter(new FileWriter(TEST_REPORT_FILENAME, true), ',');
        csvEntries = new ArrayList<String>();
        randomLastname = getRandomString();
        randomFirstname = getRandomString();
        randomEmail = getRandomEmailString();
    }

    @After
    public void finish() throws IOException {
        if (!csvEntries.isEmpty()) {
            writer.writeNext(csvEntries.toArray(new String[] {}));
            writer.close();
        }
    }

    // ----------------------------------------------------------------------------------
    // * Withdrawn application with no match at 'tran' - replace the real name
    // and email with some ridiculous fictitious values.
    // * Withdrawn application with no match at 'tran' resent - resend the one
    // above with the user identity returned by the web service.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void withdrawnApplicationWithNoMatchAtTRAN_withdrawnApplicationWithNoMatchAtTRANResent() throws ExportServiceException {
        withdrawnApplicationWithNoMatchAtTRAN();
        withdrawnApplicationWithNoMatchAtTRANResent();
    }

    private void withdrawnApplicationWithNoMatchAtTRAN() throws ExportServiceException {
        csvEntries.add("Withdrawn application with no match at 'tran'");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                request.getApplication().getApplicant().getFullName().setSurname(randomLastname);
                request.getApplication().getApplicant().getFullName().setForename1(randomFirstname);
                request.getApplication().getApplicant().getHomeAddress().setEmail(randomEmail);
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail(randomEmail);
                request.getApplication().getCourseApplication().setApplicationStatus("WITHDRAWN");
                saveRequest(randomApplicationForm, request, "1");
            }
        });
    }

    private void withdrawnApplicationWithNoMatchAtTRANResent() throws ExportServiceException {
        csvEntries.add("Withdrawn application with no match at 'tran' resent");
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                request.getApplication().getApplicant().setApplicantID(receivedApplicantId);
                request.getApplication().getApplicant().getFullName().setSurname(randomLastname);
                request.getApplication().getApplicant().getFullName().setForename1(randomFirstname);
                request.getApplication().getApplicant().getHomeAddress().setEmail(randomEmail);
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail(randomEmail);
                request.getApplication().getCourseApplication().setApplicationStatus("WITHDRAWN");
                saveRequest(randomApplicationForm, request, "2");
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Withdrawn application with match at 'tran' and no active user identity
    // (MUA) -
    // replace corresponding fields (except IPR code) with G in spreadsheet.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void withdrawnApplicationWithMatchAtTRANAndNoActiveUserIdentity_MUA() throws ExportServiceException {
        csvEntries.add("Withdrawn application with match at 'tran' and no active user identity (MUA)");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                // request.getApplication().getApplicant().setApplicantID("SL609837");
                request.getApplication().getApplicant().getFullName().setSurname("AIYEGBUSI");
                request.getApplication().getApplicant().getFullName().setForename1("ISRAEL ADEYEMI");
                request.getApplication().getApplicant().setSex(GenderTp.M);
                request.getApplication().getApplicant().setDateOfBirth(buildXmlDate(new DateTime(1982, 7, 7, 8, 0).toDate()));
                request.getApplication().getApplicant().getNationality().setCode("000");
                request.getApplication().getApplicant().getHomeAddress().setEmail("aaiyegbusi@aol.com");
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail("aaiyegbusi@aol.com");
                request.getApplication().getCourseApplication().setApplicationStatus("WITHDRAWN");
                saveRequest(randomApplicationForm, request);
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Withdrawn application with match at 'tran' and active user identity
    // (MUA) - replace corresponding
    // fields (except IPR code) with D in spreadsheet.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void withdrawnApplicationWithMatchAtTRANAndActiveUserIdentity_MUA() throws ExportServiceException {
        csvEntries.add("Withdrawn application with match at 'tran' and active user identity (MUA)");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                // request.getApplication().getApplicant().setApplicantID("12023085");
                request.getApplication().getApplicant().getFullName().setSurname("EDWARDS");
                request.getApplication().getApplicant().getFullName().setForename1("DAVID ALLEN");
                request.getApplication().getApplicant().setSex(GenderTp.M);
                request.getApplication().getApplicant().setDateOfBirth(buildXmlDate(new DateTime(1988, 4, 25, 8, 0).toDate()));
                request.getApplication().getApplicant().getNationality().setCode("000");
                request.getApplication().getApplicant().getNationality().setName(null);
                request.getApplication().getApplicant().getCountryOfBirth().setCode("XK");
                request.getApplication().getApplicant().getCountryOfBirth().setName(null);
                request.getApplication().getApplicant().getCountryOfDomicile().setCode("XK");
                request.getApplication().getApplicant().getCountryOfDomicile().setName(null);
                request.getApplication().getApplicant().getHomeAddress().setEmail("euanedwards@gmail.com");
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail("euanedwards@gmail.com");
                request.getApplication().getCourseApplication().setApplicationStatus("WITHDRAWN");
                saveRequest(randomApplicationForm, request);
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Withdrawn application with active user identity (MUA) known to UCL
    // Prism - replace corresponding
    // fields (INCLUDING IPR code) with K in spreadsheet.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void withdrawnApplicationWithActiveUserIdentity_MUA_knownToUclPrism() throws ExportServiceException {
        csvEntries.add("Withdrawn application with active user identity (MUA) known to UCL Prism");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                request.getApplication().getApplicant().getFullName().setSurname("NOGUCHI");
                request.getApplication().getApplicant().getFullName().setForename1("YUMIKO");
                request.getApplication().getApplicant().setSex(GenderTp.F);
                request.getApplication().getApplicant().setDateOfBirth(buildXmlDate(new DateTime(1989, 3, 28, 8, 0).toDate()));
                request.getApplication().getApplicant().getNationality().setCode("JP");
                request.getApplication().getApplicant().getNationality().setName(null);
                request.getApplication().getApplicant().getCountryOfBirth().setCode("JP");
                request.getApplication().getApplicant().getCountryOfBirth().setName(null);
                request.getApplication().getApplicant().getCountryOfDomicile().setCode("JP");
                request.getApplication().getApplicant().getCountryOfDomicile().setName(null);
                request.getApplication().getApplicant().getHomeAddress().setEmail("noguchi@ttpc.jp");
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail("noguchi@ttpc.jp");
                request.getApplication().getCourseApplication().setApplicationStatus("WITHDRAWN");
                saveRequest(randomApplicationForm, request);
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Withdrawn UCL Prism application by applicant with a corresponding first
    // application in progress in
    // the UCL Portico system - replace corresponding fields with A in
    // spreadsheet.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void withdrawnUclPrismApplicationByApplicantWithACorrespondingFirstApplicationInProgressInTheUCLPorticoSystem() throws ExportServiceException {
        csvEntries.add("Withdrawn UCL Prism application by applicant with a corresponding first application in progress in the UCL Portico system");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                request.getApplication().getApplicant().setApplicantID("13064319");
                request.getApplication().getApplicant().getFullName().setSurname("WEI");
                request.getApplication().getApplicant().getFullName().setForename1("SIYING");
                request.getApplication().getApplicant().setSex(GenderTp.F);
                request.getApplication().getApplicant().setDateOfBirth(buildXmlDate(new DateTime(1991, 6, 18, 8, 0).toDate()));
                request.getApplication().getApplicant().getNationality().setCode("CN");
                request.getApplication().getApplicant().getNationality().setName(null);
                request.getApplication().getApplicant().getCountryOfBirth().setCode("CN");
                request.getApplication().getApplicant().getCountryOfBirth().setName(null);
                request.getApplication().getApplicant().getCountryOfDomicile().setCode("CN");
                request.getApplication().getApplicant().getCountryOfDomicile().setName(null);
                request.getApplication().getApplicant().getHomeAddress().setEmail("whweisiying24922@163.com");
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail("whweisiying24922@163.com");
                request.getApplication().getCourseApplication().setApplicationStatus("WITHDRAWN");
                saveRequest(randomApplicationForm, request);
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Rejected application with no match at 'tran' - replace the real name
    // and email with some ridiculous fictitious values.
    // * Rejected application with no match at 'tran' resent - resend the one
    // above with the user identity returned by the web service.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void rejectedApplicationWithNoMatchAtTRAN_rejectedApplicationWithNoMatchAtTRANResent() throws ExportServiceException {
        rejectedApplicationWithNoMatchAtTRAN();
        rejectedApplicationWithNoMatchAtTRANResent();
    }

    private void rejectedApplicationWithNoMatchAtTRAN() throws ExportServiceException {
        csvEntries.add("Rejected application with no match at 'tran'");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                request.getApplication().getApplicant().getFullName().setSurname(randomLastname);
                request.getApplication().getApplicant().getFullName().setForename1(randomFirstname);
                request.getApplication().getApplicant().getHomeAddress().setEmail(randomEmail);
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail(randomEmail);
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("REJECT");
                saveRequest(randomApplicationForm, request, "1");
            }
        });
    }

    private void rejectedApplicationWithNoMatchAtTRANResent() throws ExportServiceException {
        csvEntries.add("Rejected application with no match at 'tran' resent");
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                request.getApplication().getApplicant().setApplicantID(receivedApplicantId);
                request.getApplication().getApplicant().getFullName().setSurname(randomLastname);
                request.getApplication().getApplicant().getFullName().setForename1(randomFirstname);
                request.getApplication().getApplicant().getHomeAddress().setEmail(randomEmail);
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail(randomEmail);
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("REJECT");
                saveRequest(randomApplicationForm, request, "2");
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Rejected application with match at 'tran' and no active user identity
    // (MUA) -
    // replace corresponding fields (except IPR code) with H in spreadsheet.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void rejectedApplicationWithMatchAtTRANAndNoActiveUserIdentity_MUA() throws ExportServiceException {
        csvEntries.add("Rejected application with match at 'tran' and no active user identity (MUA)");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                // request.getApplication().getApplicant().setApplicantID("SL639411");
                request.getApplication().getApplicant().getFullName().setSurname("RAOOFI");
                request.getApplication().getApplicant().getFullName().setForename1("AZAM");
                request.getApplication().getApplicant().setSex(GenderTp.F);
                request.getApplication().getApplicant().setDateOfBirth(buildXmlDate(new DateTime(1982, 10, 31, 8, 0).toDate()));
                request.getApplication().getApplicant().getNationality().setCode("IR");
                request.getApplication().getApplicant().getNationality().setName(null);
                request.getApplication().getApplicant().getCountryOfDomicile().setCode("IR");
                request.getApplication().getApplicant().getCountryOfDomicile().setName(null);
                request.getApplication().getApplicant().getHomeAddress().setEmail("azra6182@yahoo.com");
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail("azra6182@yahoo.com");
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("REJECT");
                saveRequest(randomApplicationForm, request);
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Rejected application with match at 'tran' and active user identity
    // (MUA) -
    // replace corresponding fields (except IPR code) with E in spreadsheet.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void rejectedApplicationWithMatchAtTRANAndActiveUserIdentity_MUA() throws ExportServiceException {
        csvEntries.add("Rejected application with match at 'tran' and active user identity (MUA)");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                // request.getApplication().getApplicant().setApplicantID("13063189");
                request.getApplication().getApplicant().getFullName().setSurname("SHARMA");
                request.getApplication().getApplicant().getFullName().setForename1("DEEPSHIKHA");
                request.getApplication().getApplicant().setSex(GenderTp.F);
                request.getApplication().getApplicant().setDateOfBirth(buildXmlDate(new DateTime(1991, 2, 1, 8, 0).toDate()));
                request.getApplication().getApplicant().getNationality().setCode("IN");
                request.getApplication().getApplicant().getNationality().setName(null);
                request.getApplication().getApplicant().getCountryOfBirth().setCode("IN");
                request.getApplication().getApplicant().getCountryOfBirth().setName(null);
                request.getApplication().getApplicant().getCountryOfDomicile().setCode("IN");
                request.getApplication().getApplicant().getCountryOfDomicile().setName(null);
                request.getApplication().getApplicant().getHomeAddress().setEmail("deepshikha003@gmail.com");
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail("deepshikha003@gmail.com");
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("REJECT");
                saveRequest(randomApplicationForm, request);
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Rejected application with active user identity (MUA) known to UCL Prism
    // -
    // replace corresponding fields (INCLUDING IPR code) with L in spreadsheet.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void rejectedApplicationWithActiveUserIdentity_MUA_knownToUCLPrism() throws ExportServiceException {
        csvEntries.add("Rejected application with active user identity (MUA) known to UCL Prism");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                request.getApplication().getApplicant().getFullName().setSurname("SPENCER");
                request.getApplication().getApplicant().getFullName().setForename1("ROBIN GRAHAM NELSON");
                request.getApplication().getApplicant().setSex(GenderTp.M);
                request.getApplication().getApplicant().setDateOfBirth(buildXmlDate(new DateTime(1958, 5, 27, 8, 0).toDate()));
                request.getApplication().getApplicant().getNationality().setCode("000");
                request.getApplication().getApplicant().getNationality().setName(null);
                request.getApplication().getApplicant().getCountryOfBirth().setCode("XK");
                request.getApplication().getApplicant().getCountryOfBirth().setName(null);
                request.getApplication().getApplicant().getCountryOfDomicile().setCode("XK");
                request.getApplication().getApplicant().getCountryOfDomicile().setName(null);
                request.getApplication().getApplicant().getHomeAddress().setEmail("robin.spencer@live.co.uk");
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail("robin.spencer@live.co.uk");
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("REJECT");
                saveRequest(randomApplicationForm, request);
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Rejected UCL Prism application by applicant with a corresponding first
    // application in progress in the
    // UCL Portico system - replace corresponding fields with B in spreadsheet.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void rejectedUCLPrismApplicationByApplicantWithACorrespondingFirstApplicationInProgressInTheUCLPorticoSystem() throws ExportServiceException {
        csvEntries.add("Rejected UCL Prism application by applicant with a corresponding first application in progress in the UCL Portico system");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                request.getApplication().getApplicant().setApplicantID("13064277");
                request.getApplication().getApplicant().getFullName().setSurname("JONES");
                request.getApplication().getApplicant().getFullName().setForename1("MATTHEW LLOYD");
                request.getApplication().getApplicant().setSex(GenderTp.M);
                request.getApplication().getApplicant().setDateOfBirth(buildXmlDate(new DateTime(1991, 2, 19, 8, 0).toDate()));
                request.getApplication().getApplicant().getNationality().setCode("000");
                request.getApplication().getApplicant().getNationality().setName(null);
                request.getApplication().getApplicant().getCountryOfBirth().setCode("XK");
                request.getApplication().getApplicant().getCountryOfBirth().setName(null);
                request.getApplication().getApplicant().getCountryOfDomicile().setCode("XK");
                request.getApplication().getApplicant().getCountryOfDomicile().setName(null);
                request.getApplication().getApplicant().getHomeAddress().setEmail("mjones5@hotmail.co.uk");
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail("mjones5@hotmail.co.uk");
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("REJECT");
                saveRequest(randomApplicationForm, request);
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Approved application with no match at 'tran' - replace the real name
    // and email with some ridiculous fictitious values.
    // * Approved application with no match at 'tran' resent - resend the one
    // above with the user identity returned by the web service.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void approvedApplicationWithNoMatchAtTRAN_approvedApplicationWithNoMatchAtTRANResent() throws ExportServiceException {
        approvedApplicationWithNoMatchAtTRAN();
        approvedApplicationWithNoMatchAtTRANResent();
    }

    private void approvedApplicationWithNoMatchAtTRAN() throws ExportServiceException {
        csvEntries.add("Approved application with no match at 'tran'");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                request.getApplication().getApplicant().getFullName().setSurname(randomLastname);
                request.getApplication().getApplicant().getFullName().setForename1(randomFirstname);
                request.getApplication().getApplicant().getHomeAddress().setEmail(randomEmail);
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail(randomEmail);
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
                saveRequest(randomApplicationForm, request, "1");
            }
        });
    }

    private void approvedApplicationWithNoMatchAtTRANResent() throws ExportServiceException {
        csvEntries.add("Approved application with no match at 'tran' resent");
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                request.getApplication().getApplicant().setApplicantID(receivedApplicantId);
                request.getApplication().getApplicant().getFullName().setSurname(randomLastname);
                request.getApplication().getApplicant().getFullName().setForename1(randomFirstname);
                request.getApplication().getApplicant().getHomeAddress().setEmail(randomEmail);
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail(randomEmail);
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
                saveRequest(randomApplicationForm, request, "2");
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Approved application with match at 'tran' and no active user identity
    // (MUA) -
    // replace corresponding fields (except IPR code) with I in spreadsheet.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void approvedApplicationWithMatchAtTRANAndNoActiveUserIdentity_MUA() throws ExportServiceException {
        csvEntries.add("Approved application with match at 'tran' and no active user identity (MUA)");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                // request.getApplication().getApplicant().setApplicantID("SL676328");
                request.getApplication().getApplicant().getFullName().setSurname("ABIRHIRE");
                request.getApplication().getApplicant().getFullName().setForename1("OGHENEMINE");
                request.getApplication().getApplicant().setSex(GenderTp.F);
                request.getApplication().getApplicant().setDateOfBirth(buildXmlDate(new DateTime(1983, 3, 8, 8, 0).toDate()));
                request.getApplication().getApplicant().getNationality().setCode("NG");
                request.getApplication().getApplicant().getNationality().setName(null);
                request.getApplication().getApplicant().getHomeAddress().setEmail("mehmet@atlasedu.com");
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail("mehmet@atlasedu.com");
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
                saveRequest(randomApplicationForm, request);
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Approved application with match at 'tran' and active user identity
    // (MUA) -
    // replace corresponding fields (except IPR code) with F in spreadsheet.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void approvedApplicationWithMatchAtTRANAndActiveUserIdentity_MUA() throws ExportServiceException {
        csvEntries.add("Approved application with match at 'tran' and active user identity (MUA)");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                // request.getApplication().getApplicant().setApplicantID("13050747");
                request.getApplication().getApplicant().getFullName().setSurname("KYRIAZIS");
                request.getApplication().getApplicant().getFullName().setForename1("GEORGIOS");
                request.getApplication().getApplicant().setSex(GenderTp.M);
                request.getApplication().getApplicant().setDateOfBirth(buildXmlDate(new DateTime(1989, 3, 11, 8, 0).toDate()));
                request.getApplication().getApplicant().getNationality().setCode("XA");
                request.getApplication().getApplicant().getNationality().setName(null);
                request.getApplication().getApplicant().getCountryOfBirth().setCode("XA");
                request.getApplication().getApplicant().getCountryOfBirth().setName(null);
                request.getApplication().getApplicant().getCountryOfDomicile().setCode("XA");
                request.getApplication().getApplicant().getCountryOfDomicile().setName(null);
                request.getApplication().getApplicant().getHomeAddress().setEmail("george_s.k@hotmail.com");
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail("george_s.k@hotmail.com");
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
                saveRequest(randomApplicationForm, request);
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Approved application with active user identity (MUA) known to UCL Prism
    // -
    // replace corresponding fields (INCLUDING IPR code) with M in spreadsheet.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void approvedApplicationWithActiveUserIdentity_MUA_knownToUCLPrism() throws ExportServiceException {
        csvEntries.add("Approved application with active user identity (MUA) known to UCL Prism");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                request.getApplication().getApplicant().getFullName().setSurname("WHITE");
                request.getApplication().getApplicant().getFullName().setForename1("HEATHER ELIZABETH JANET");
                request.getApplication().getApplicant().setSex(GenderTp.F);
                request.getApplication().getApplicant().setDateOfBirth(buildXmlDate(new DateTime(1981, 6, 7, 8, 0).toDate()));
                request.getApplication().getApplicant().getNationality().setCode("000");
                request.getApplication().getApplicant().getNationality().setName(null);
                request.getApplication().getApplicant().getCountryOfBirth().setCode("XK");
                request.getApplication().getApplicant().getCountryOfBirth().setName(null);
                request.getApplication().getApplicant().getCountryOfDomicile().setCode("XK");
                request.getApplication().getApplicant().getCountryOfDomicile().setName(null);
                request.getApplication().getApplicant().getHomeAddress().setEmail("h.white.11@ucl.ac.uk");
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail("h.white.11@ucl.ac.uk");
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
                saveRequest(randomApplicationForm, request);
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Approved UCL Prism application by applicant with a corresponding first
    // application in progress
    // in the UCL Portico system - replace corresponding fields with C in
    // spreadsheet.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void approvedUCLPrismApplicationByApplicantWithACorrespondingFirstApplicationInProgressInTheUCLPorticoSystem() throws ExportServiceException {
        csvEntries.add("Approved UCL Prism application by applicant with a corresponding first application in progress in the UCL Portico system");
        randomApplicationForm = randomlyPickApplicationForm();
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                request.getApplication().getApplicant().setApplicantID("13064301");
                request.getApplication().getApplicant().getFullName().setSurname("BARNETT");
                request.getApplication().getApplicant().getFullName().setForename1("BRANDIE");
                request.getApplication().getApplicant().setSex(GenderTp.F);
                request.getApplication().getApplicant().setDateOfBirth(buildXmlDate(new DateTime(1989, 5, 19, 8, 0).toDate()));
                request.getApplication().getApplicant().getNationality().setCode("US");
                request.getApplication().getApplicant().getNationality().setName(null);
                request.getApplication().getApplicant().getCountryOfBirth().setCode("US");
                request.getApplication().getApplicant().getCountryOfBirth().setName(null);
                request.getApplication().getApplicant().getCountryOfDomicile().setCode("US");
                request.getApplication().getApplicant().getCountryOfDomicile().setName(null);
                request.getApplication().getApplicant().getHomeAddress().setEmail("brbarnett66@gmail.com");
                request.getApplication().getApplicant().getCorrespondenceAddress().setEmail("brbarnett66@gmail.com");
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
                saveRequest(randomApplicationForm, request);
            }
        });
    }

    // ----------------------------------------------------------------------------------
    // * Approved UCL Prism application by applicant with a duplicate
    // application
    // in the UCL Portico system - use RRDCIVSGEO01-2012-000032.
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void approvedUCLPrismApplicationByApplicantWithADuplicateApplicationInTheUCLPorticoSystem() throws ExportServiceException {
        csvEntries.add("Approved UCL Prism application by applicant with a duplicate application in the UCL Portico system");
        randomApplicationForm = applicationsService.getByApplicationNumber("RRDCIVSGEO01-2012-000032");

        for (Referee referee : randomApplicationForm.getReferees()) {
            if (referee.getComment() != null) {
                referee.setSendToUCL(true);
            }
        }

        // we need to append another referee for this to work
        Referee referee = randomApplicationForm.getReferees().get(0);
        String addressStr = "Zuhlke Engineering Ltd\n43 Whitfield Street\nLondon\n\nW1T 4HD\nUnited Kingdom";
        Domicile domicile = new DomicileBuilder().id(Integer.MAX_VALUE).code("XK").name("United Kingdom").enabled(true).build();
        ReferenceComment referenceComment1 = new ReferenceCommentBuilder().comment("Hello World").providedBy(referee.getUser()).suitableForProgramme(true)
                .suitableForUcl(true).user(referee.getUser()).build();
        Referee refereeOne = new RefereeBuilder()
                .user(referee.getUser())
                .address(
                        new Address().withDomicile(domicile).withLine1(addressStr.split("\n")[0]).withLine2(addressStr.split("\n")[1])
                                .withTown(addressStr.split("\n")[2]).withRegion(addressStr.split("\n")[3]).withCode(addressStr.split("\n")[4]))
                .jobEmployer("Zuhlke Engineering Ltd.").jobTitle("Software Engineer").messenger("skypeAddress").phoneNumber("+44 (0) 123 123 1234")
                .sendToUCL(true).reference(referenceComment1).build();
        refereeOne.setComment(referenceComment1);
        randomApplicationForm.getReferees().add(refereeOne);

        applicationsService.save(randomApplicationForm);

        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(randomApplicationForm);
        uclExportService.sendToPortico(randomApplicationForm, applicationFormTransfer, new AbstractPorticoITTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                super.webServiceCallStarted(request, form);
                saveRequest(randomApplicationForm, request);
            }
        });
    }

    private String getRandomString() {
        return new BigInteger(128, random).toString(32);
    }

    private String getRandomEmailString() {
        return new BigInteger(54, random).toString(32) + "@" + new BigInteger(54, random).toString(32) + ".com";
    }

    private XMLGregorianCalendar buildXmlDate(Date date) {
        if (date != null) {
            try {
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTimeInMillis(date.getTime());
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
    }

    private Application randomlyPickApplicationForm() {
        List<Application> allApplicationsByStatus = applicationsService.getApplicationsByStatus(PrismState.APPLICATION_REVIEW);
        Application applicationForm = null;
        boolean foundEnoughDataForQualifications = false;
        boolean foundEnoughDataForReferees = false;

        do {
            int numberOfQualifications = 0;
            int numberOfReferees = 0;
            foundEnoughDataForQualifications = false;
            foundEnoughDataForReferees = false;

            applicationForm = allApplicationsByStatus.get(random.nextInt(allApplicationsByStatus.size()));

            for (Qualification qualification : applicationForm.getQualifications()) {
                if (qualification.getDocument() != null) {
                    qualification.setExport(true);
                    numberOfQualifications++;
                    if (numberOfQualifications == 2) {
                        break;
                    }
                }
            }

            for (Referee referee : applicationForm.getReferees()) {
                if (referee.getComment() != null) {
                    referee.setSendToUCL(true);
                    numberOfReferees++;
                    if (numberOfReferees == 2) {
                        break;
                    }
                }
            }

            if (numberOfQualifications >= 2) {
                foundEnoughDataForQualifications = true;
            }

            if (numberOfReferees == 2) {
                foundEnoughDataForReferees = true;
            }
        } while (!(foundEnoughDataForQualifications && foundEnoughDataForReferees && !USED_APPLICATION_NUMBERS.contains(applicationForm.getApplicationNumber())));

        USED_APPLICATION_NUMBERS.add(applicationForm.getApplicationNumber());

        applicationsService.save(applicationForm);

        return applicationForm;
    }

    private abstract class AbstractPorticoITTransferListener implements TransferListener {

        @Override
        public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
            for (QualificationsTp detailsTp : request.getApplication().getApplicant().getQualificationList().getQualificationDetail()) {
                detailsTp.getInstitution().setCode("UK0275");
            }
        }

        @Override
        public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
            receivedApplicantId = response.getReference().getApplicantID();
            addReceivedPorticoIdsToCsvFile(response);
        }

        protected void saveRequest(Application applicationForm, SubmitAdmissionsApplicationRequest request) {
            saveRequest(applicationForm, request, StringUtils.EMPTY);
        }

        protected void saveRequest(Application applicationForm, SubmitAdmissionsApplicationRequest request, String postFix) {
            // request.getApplication().getApplicant().getFullName().setSurname(randomLastname);
            // request.getApplication().getApplicant().getFullName().setForename1(randomFirstname);
            // request.getApplication().getApplicant().getHomeAddress().setEmail(randomEmail);
            // request.getApplication().getApplicant().getCorrespondenceAddress().setEmail(randomEmail);

            String prismId = request.getApplication().getCourseApplication().getExternalApplicationID();
            prismId = prismId.replace("2011", "2014");
            prismId = prismId.replace("2012", "2014");
            prismId = prismId.replace("2013", "2014");
            prismId = prismId + "-2";
            request.getApplication().getCourseApplication().setExternalApplicationID(prismId);

            addFirstAndLastnameToCsvFile(request);

            String pPostFix = StringUtils.isNotBlank(postFix) ? "_" + postFix : postFix;
            Marshaller marshaller = webServiceTemplate.getMarshaller();
            try {
                marshaller.marshal(request, new StreamResult(new File("request_" + applicationForm.getApplicationNumber() + pPostFix + ".txt")));
            } catch (Exception e) {
                Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
            }
        }

        private void addReceivedPorticoIdsToCsvFile(AdmissionsApplicationResponse response) {
            csvEntries.add(response.getReference().getApplicantID());
            csvEntries.add(response.getReference().getApplicationID());
            csvEntries.add("null"); // error field
        }

        private void addFirstAndLastnameToCsvFile(SubmitAdmissionsApplicationRequest request) {
            csvEntries.add(request.getApplication().getCourseApplication().getExternalApplicationID());
            csvEntries.add(request.getApplication().getApplicant().getFullName().getSurname());
            csvEntries.add(request.getApplication().getApplicant().getFullName().getForename1());
            csvEntries.add(request.getApplication().getApplicant().getDateOfBirth().toString());
        }

        @Override
        public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
            csvEntries.add("null"); // applicantId
            csvEntries.add("null"); // applicationId
            csvEntries.add(error.getDiagnosticInfo());
            Assert.fail(String.format("Received error from web service [reason=%s]", error.getDiagnosticInfo()));
        }

        @Override
        public void sftpTransferStarted(Application form) {
            // we do nothing here
        }

        @Override
        public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
            csvEntries.add(zipFileName);
            csvEntries.add("null");

            writer.writeNext(csvEntries.toArray(new String[] {}));
            try {
                writer.close();
            } catch (Exception e) {
                // do nothing
            }
            csvEntries.clear();
        }

        @Override
        public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
            csvEntries.add("null"); // zipFileName
            csvEntries.add(error.getDiagnosticInfo());
            Assert.fail(String.format("SFTP call failed with: %s", error.getDiagnosticInfo()));
        }
    }
}
