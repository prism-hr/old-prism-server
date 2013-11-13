package com.zuehlke.pgadmissions.pdf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PassportInformation;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;

public class PdfModelBuilder extends AbstractPdfModelBuilder {

    private static final String NOT_REQUIRED = "Not Required";

    private final Logger log = LoggerFactory.getLogger(PdfModelBuilder.class);

    private boolean includeCriminialConvictions = false;

    private boolean includeAttachments = true;

    private boolean includeDisability = false;

    private boolean includeEthnicity = false;

    private boolean includeReferences = false;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

    private int pageCounter = 0;

    private int appendixCounter = 0;

    private Map<Integer, Object> bookmarkMap = new HashMap<Integer, Object>();

    private HeaderEvent headerEvent;

    public PdfModelBuilder() {
    }

    public PdfModelBuilder includeReferences(final boolean flag) {
        this.includeReferences = flag;
        return this;
    }

    public PdfModelBuilder includeCriminialConvictions(final boolean flag) {
        this.includeCriminialConvictions = flag;
        return this;
    }

    public PdfModelBuilder includeAttachments(final boolean flag) {
        this.includeAttachments = flag;
        return this;
    }

    public PdfModelBuilder includeDisability(final boolean flag) {
        this.includeDisability = flag;
        return this;
    }

    public PdfModelBuilder includeEthnicity(final boolean flag) {
        this.includeEthnicity = flag;
        return this;
    }

    public PdfModelBuilder dateFormat(final SimpleDateFormat format) {
        this.dateFormat = format;
        return this;
    }

    public boolean isIncludeCriminialConvictions() {
        return includeCriminialConvictions;
    }

    public boolean isIncludeAttachments() {
        return includeAttachments;
    }

    public boolean isIncludeDisability() {
        return includeDisability;
    }

    public boolean isIncludeEthnicity() {
        return includeEthnicity;
    }

    public boolean isIncludeReferences() {
        return includeReferences;
    }

    public Map<Integer, Object> getBookmarkMap() {
        return bookmarkMap;
    }

    public void build(final ApplicationForm form, final Document pdfDocument, final PdfWriter pdfWriter) throws PdfDocumentBuilderException {
        try {
            addCoverPage(form, pdfDocument, pdfWriter);

            addHeaderEvent(form, pdfWriter);

            addProgrammeSection(form, pdfDocument);

            pdfDocument.add(addSectionSeparators());

            addPersonalDetailsSection(form, pdfDocument);

            pdfDocument.add(addSectionSeparators());

            addAddressSection(form, pdfDocument);

            pdfDocument.add(addSectionSeparators());

            addQualificationSection(form, pdfDocument);

            pdfDocument.add(addSectionSeparators());

            addEmploymentSection(form, pdfDocument);

            pdfDocument.add(addSectionSeparators());

            addFundingSection(form, pdfDocument);

            pdfDocument.add(addSectionSeparators());

            addReferencesSection(form, pdfDocument);

            pdfDocument.add(addSectionSeparators());

            addDocumentsSection(form, pdfDocument);

            pdfDocument.add(addSectionSeparators());

            addAdditionalInformationSection(form, pdfDocument);

            pdfDocument.add(addSectionSeparators());

            addSupportingDocuments(form, pdfDocument, pdfWriter);

        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            throw new PdfDocumentBuilderException(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new PdfDocumentBuilderException(e.getMessage(), e);
        } catch (DocumentException e) {
            log.error(e.getMessage(), e);
            throw new PdfDocumentBuilderException(e.getMessage(), e);
        }
    }

    protected void addHeaderEvent(final ApplicationForm form, final PdfWriter writer) {
        Chunk submittedDateHeader = null;

        if (form.getSubmittedDate() != null) {
            submittedDateHeader = new Chunk(dateFormat.format(form.getSubmittedDate()), SMALLER_FONT);
        } else {
            submittedDateHeader = new Chunk("", SMALLER_FONT);
        }

        headerEvent = new HeaderEvent(new Chunk(form.getProgram().getTitle(), SMALLER_FONT), new Chunk(form.getApplicationNumber(), SMALLER_FONT),
                        submittedDateHeader);
        writer.setPageEvent(headerEvent);
    }

    protected void addCoverPage(final ApplicationForm form, final Document pdfDocument, final PdfWriter writer) throws MalformedURLException, IOException,
                    DocumentException {
        pdfDocument.newPage();

        Image image = Image.getInstance(this.getClass().getResource("/prism_logo.png"));
        image.scalePercent(50f);

        image.setAbsolutePosition(pdfDocument.right() - image.getWidth() * 0.5f, pdfDocument.top() + 20f);
        pdfDocument.add(image);

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.drawLine(writer.getDirectContent(), pdfDocument.left(), pdfDocument.right(), pdfDocument.top() + 10f);

        pdfDocument.add(addSectionSeparators());
        pdfDocument.add(addSectionSeparators());

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
        table.addCell(newGrayTableCell("APPLICATION", BOLD_FONT));
        pdfDocument.add(table);
        pdfDocument.add(addSectionSeparators());
        table = new PdfPTable(2);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
        table.addCell(newTableCell("Applicant", SMALL_BOLD_FONT));

        RegisteredUser applicant = form.getApplicant();
        String fullName = Joiner.on(" ").skipNulls()
                        .join(applicant.getFirstName(), applicant.getFirstName2(), applicant.getFirstName3(), applicant.getLastName());
        table.addCell(newTableCell(fullName, SMALL_FONT));
        table.addCell(newTableCell("Programme", SMALL_BOLD_FONT));
        table.addCell(newTableCell(form.getProgram().getTitle(), SMALL_FONT));

        addProjectTitleToTable(table, form);
        addClosingDateToTable(table, form);

        table.addCell(newTableCell("Application Number", SMALL_BOLD_FONT));
        table.addCell(newTableCell(form.getApplicationNumber(), SMALL_FONT));

        if (form.getSubmittedDate() != null) {
            table.addCell(newTableCell("Submission date", SMALL_BOLD_FONT));
            table.addCell(newTableCell(dateFormat.format(form.getSubmittedDate()), SMALL_FONT));
        }

        pdfDocument.add(table);
        pdfDocument.newPage();
    }

    protected void addProgrammeSection(final ApplicationForm form, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
        table.addCell(newGrayTableCell("PROGRAMME", BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(addSectionSeparators());

        table = new PdfPTable(2);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
        table.addCell(newTableCell("Programme", SMALL_BOLD_FONT));
        table.addCell(newTableCell(form.getProgram().getTitle(), SMALL_FONT));

        table.addCell(newTableCell("Study Option", SMALL_BOLD_FONT));
        if (form.getProgrammeDetails().getStudyOption() != null) {
            table.addCell(newTableCell(form.getProgrammeDetails().getStudyOption(), SMALL_FONT));
        } else {
            table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
        }

        addProjectTitleToTable(table, form);

        addClosingDateToTable(table, form);

        table.addCell(newTableCell("Start Date", SMALL_BOLD_FONT));
        if (form.getProgrammeDetails().getStartDate() != null) {
            table.addCell(newTableCell(dateFormat.format(form.getProgrammeDetails().getStartDate()), SMALL_FONT));
        } else {
            table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
        }

        table.addCell(newTableCell("How did you find us?", SMALL_BOLD_FONT));
        if (form.getProgrammeDetails().getSourcesOfInterest() != null) {
            table.addCell(newTableCell(form.getProgrammeDetails().getSourcesOfInterest().getName(), SMALL_FONT));
        } else {
            table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
        }

        if (form.getProgrammeDetails().getSourcesOfInterest() != null && form.getProgrammeDetails().getSourcesOfInterest().isFreeText()) {
            table.addCell(newTableCell("Please explain", SMALL_BOLD_FONT));
            table.addCell(newTableCell(form.getProgrammeDetails().getSourcesOfInterestText(), SMALL_FONT));
        }

        pdfDocument.add(table);
        pdfDocument.add(addSectionSeparators());

        if (form.getProgrammeDetails().getSuggestedSupervisors().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
            table.addCell(newTableCell("Supervisor", SMALL_BOLD_FONT));
            table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
            pdfDocument.add(table);
            pdfDocument.add(addSectionSeparators());
        } else {
            int counter = 1;
            for (SuggestedSupervisor supervisor : form.getProgrammeDetails().getSuggestedSupervisors()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
                PdfPCell headerCell = newTableCell("Supervisor (" + counter++ + ")", SMALL_BOLD_FONT);
                headerCell.setColspan(2);
                table.addCell(headerCell);

                table.addCell(newTableCell("Supervisor First Name", SMALL_BOLD_FONT));
                table.addCell(newTableCell(supervisor.getFirstname(), SMALL_FONT));

                table.addCell(newTableCell("Supervisor Last Name", SMALL_BOLD_FONT));
                table.addCell(newTableCell(supervisor.getLastname(), SMALL_FONT));

                table.addCell(newTableCell("Supervisor Email", SMALL_BOLD_FONT));
                table.addCell(newTableCell(supervisor.getEmail(), SMALL_FONT));

                table.addCell(newTableCell("Is this supervisor aware of your application?", SMALL_BOLD_FONT));

                if (BooleanUtils.isTrue(supervisor.isAware())) {
                    table.addCell(newTableCell("Yes", SMALL_FONT));
                } else {
                    table.addCell(newTableCell("No", SMALL_FONT));
                }
                pdfDocument.add(table);
                pdfDocument.add(addSectionSeparators());
            }
        }
    }

    protected void addPersonalDetailsSection(final ApplicationForm form, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
        table.addCell(newGrayTableCell("PERSONAL DETAILS", BOLD_FONT));
        pdfDocument.add(table);
        pdfDocument.add(addSectionSeparators());

        table = new PdfPTable(2);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);

        table.addCell(newTableCell("Title", SMALL_BOLD_FONT));
        if (form.getPersonalDetails().getTitle() == null) {
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            table.addCell(newTableCell(form.getPersonalDetails().getTitle().getDisplayValue(), SMALL_FONT));
        }

        table.addCell(newTableCell("First Name", SMALL_BOLD_FONT));
        table.addCell(newTableCell(form.getApplicant().getFirstName(), SMALL_FONT));

        table.addCell(newTableCell("First Name 2", SMALL_BOLD_FONT));
        table.addCell(newTableCell(form.getApplicant().getFirstName2(), SMALL_FONT));

        table.addCell(newTableCell("First Name 3", SMALL_BOLD_FONT));
        table.addCell(newTableCell(form.getApplicant().getFirstName3(), SMALL_FONT));

        table.addCell(newTableCell("Last Name", SMALL_BOLD_FONT));
        table.addCell(newTableCell(form.getApplicant().getLastName(), SMALL_FONT));

        table.addCell(newTableCell("Gender", SMALL_BOLD_FONT));
        if (form.getPersonalDetails().getGender() == null) {
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            table.addCell(newTableCell(form.getPersonalDetails().getGender().getDisplayValue(), SMALL_FONT));
        }

        table.addCell(newTableCell("Date of Birth", SMALL_BOLD_FONT));
        if (form.getPersonalDetails().getDateOfBirth() == null) {
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            table.addCell(newTableCell(dateFormat.format(form.getPersonalDetails().getDateOfBirth()), SMALL_FONT));
        }
        table.addCell(newTableCell("Country of Birth", SMALL_BOLD_FONT));
        if (form.getPersonalDetails().getCountry() == null) {
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            table.addCell(newTableCell(form.getPersonalDetails().getCountry().getName(), SMALL_FONT));
        }

        table.addCell(newTableCell("Nationality", SMALL_BOLD_FONT));
        StringBuilder sb = new StringBuilder();

        Language firstNationality = form.getPersonalDetails().getFirstNationality();
        if (firstNationality != null) {
            sb.append(firstNationality.getName());
        }
        Language secondNationality = form.getPersonalDetails().getSecondNationality();
        if (secondNationality != null) {
            sb.append(", " + secondNationality.getName());
        }

        table.addCell(newTableCell(sb.toString(), SMALL_FONT));

        table.addCell(newTableCell("Is English your first language?", SMALL_BOLD_FONT));
        if (form.getPersonalDetails().getEnglishFirstLanguage() == null) {
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            if (BooleanUtils.isTrue(form.getPersonalDetails().getEnglishFirstLanguage())) {
                table.addCell(newTableCell("Yes", SMALL_FONT));
            } else {
                table.addCell(newTableCell("No", SMALL_FONT));
            }
        }

        table.addCell(newTableCell("Do you have an English language qualification?", SMALL_BOLD_FONT));
        if (form.getPersonalDetails().getLanguageQualificationAvailable() == null) {
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            if (BooleanUtils.isTrue(form.getPersonalDetails().getLanguageQualificationAvailable())) {
                table.addCell(newTableCell("Yes", SMALL_FONT));
            } else {
                table.addCell(newTableCell("No", SMALL_FONT));
            }
        }

        table.addCell(newTableCell("Country of Residence", SMALL_BOLD_FONT));
        if (form.getPersonalDetails().getResidenceCountry() == null) {
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            table.addCell(newTableCell(form.getPersonalDetails().getResidenceCountry().getName(), SMALL_FONT));
        }

        table.addCell(newTableCell("Do you Require a Visa to Study in the UK?", SMALL_BOLD_FONT));
        if (form.getPersonalDetails().getRequiresVisa() == null) {
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            if (BooleanUtils.isTrue(form.getPersonalDetails().getRequiresVisa())) {
                table.addCell(newTableCell("Yes", SMALL_FONT));
            } else {
                table.addCell(newTableCell("No", SMALL_FONT));
            }
        }

        table.addCell(newTableCell("Do you have a passport?", SMALL_BOLD_FONT));
        if (form.getPersonalDetails().getPassportAvailable() == null) {
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            if (BooleanUtils.isTrue(form.getPersonalDetails().getPassportAvailable())) {
                table.addCell(newTableCell("Yes", SMALL_FONT));
            } else {
                table.addCell(newTableCell("No", SMALL_FONT));
            }
        }

        if (BooleanUtils.isTrue(form.getPersonalDetails().getRequiresVisa())) {
            PassportInformation passportInformation = form.getPersonalDetails().getPassportInformation();
            if (passportInformation != null) {
                table.addCell(newTableCell("Passport Number", SMALL_BOLD_FONT));

                if (StringUtils.isBlank(passportInformation.getPassportNumber())) {
                    table.addCell(newTableCell(null, SMALL_FONT));
                } else {
                    table.addCell(newTableCell(passportInformation.getPassportNumber(), SMALL_FONT));
                }

                table.addCell(newTableCell("Name on Passport", SMALL_BOLD_FONT));
                if (StringUtils.isBlank(passportInformation.getNameOnPassport())) {
                    table.addCell(newTableCell(null, SMALL_FONT));
                } else {
                    table.addCell(newTableCell(passportInformation.getNameOnPassport(), SMALL_FONT));
                }

                table.addCell(newTableCell("Passport Issue Date", SMALL_BOLD_FONT));
                if (passportInformation.getPassportIssueDate() == null) {
                    table.addCell(newTableCell(null, SMALL_FONT));
                } else {
                    table.addCell(newTableCell(dateFormat.format(passportInformation.getPassportIssueDate()), SMALL_FONT));
                }

                table.addCell(newTableCell("Passport Expiry Date", SMALL_BOLD_FONT));
                if (passportInformation.getPassportExpiryDate() == null) {
                    table.addCell(newTableCell(null, SMALL_FONT));
                } else {
                    table.addCell(newTableCell(dateFormat.format(passportInformation.getPassportExpiryDate()), SMALL_FONT));
                }
            }
        }

        table.addCell(newTableCell("Email", SMALL_BOLD_FONT));
        table.addCell(newTableCell(form.getApplicant().getEmail(), SMALL_FONT));

        table.addCell(newTableCell("Telephone", SMALL_BOLD_FONT));
        table.addCell(newTableCell(form.getPersonalDetails().getPhoneNumber(), SMALL_FONT));

        table.addCell(newTableCell("Skype", SMALL_BOLD_FONT));
        table.addCell(newTableCell(form.getPersonalDetails().getMessenger(), SMALL_FONT));

        if (includeEthnicity) {
            table.addCell(newTableCell("Ethnicity", SMALL_BOLD_FONT));
            if (form.getPersonalDetails().getEthnicity() == null) {
                table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
            } else {
                table.addCell(newTableCell(form.getPersonalDetails().getEthnicity().getName(), SMALL_FONT));
            }
        }

        if (includeDisability) {
            table.addCell(newTableCell("Disability", SMALL_BOLD_FONT));
            if (form.getPersonalDetails().getDisability() == null) {
                table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
            } else {
                table.addCell(newTableCell(form.getPersonalDetails().getDisability().getName(), SMALL_FONT));
            }
        }

        pdfDocument.add(table);
        pdfDocument.add(addSectionSeparators());

        if (form.getPersonalDetails().getLanguageQualifications().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
            table.addCell(newTableCell("English Language Qualifications", SMALL_BOLD_FONT));
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            int counter = 1;
            for (LanguageQualification qualification : form.getPersonalDetails().getLanguageQualifications()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
                PdfPCell headerCell = newTableCell("English Language Qualification (" + counter++ + ")", SMALL_BOLD_FONT);
                headerCell.setColspan(2);
                table.addCell(headerCell);

                table.addCell(newTableCell("Qualification Type", SMALL_BOLD_FONT));
                table.addCell(newTableCell(qualification.getQualificationType().getDisplayValue(), SMALL_FONT));

                table.addCell(newTableCell("Other Qualification Type Name", SMALL_BOLD_FONT));
                table.addCell(newTableCell(qualification.getOtherQualificationTypeName(), SMALL_FONT));

                table.addCell(newTableCell("Date of Examination", SMALL_BOLD_FONT));
                table.addCell(newTableCell(dateFormat.format(qualification.getDateOfExamination()), SMALL_FONT));

                table.addCell(newTableCell("Overall Score", SMALL_BOLD_FONT));
                table.addCell(newTableCell(qualification.getOverallScore(), SMALL_FONT));

                table.addCell(newTableCell("Reading Score", SMALL_BOLD_FONT));
                table.addCell(newTableCell(qualification.getReadingScore(), SMALL_FONT));

                table.addCell(newTableCell("Essay / Writing Score", SMALL_BOLD_FONT));
                table.addCell(newTableCell(qualification.getWritingScore(), SMALL_FONT));

                table.addCell(newTableCell("Speaking Score", SMALL_BOLD_FONT));
                table.addCell(newTableCell(qualification.getSpeakingScore(), SMALL_FONT));

                table.addCell(newTableCell("Listening Score", SMALL_BOLD_FONT));
                table.addCell(newTableCell(qualification.getListeningScore(), SMALL_FONT));

                table.addCell(newTableCell("Did you sit the exam online?", SMALL_BOLD_FONT));
                if (qualification.getExamTakenOnline() == null) {
                    table.addCell(newTableCell(null, SMALL_FONT));
                } else {
                    if (BooleanUtils.isTrue(qualification.getExamTakenOnline())) {
                        table.addCell(newTableCell("Yes", SMALL_FONT));
                    } else {
                        table.addCell(newTableCell("No", SMALL_FONT));
                    }
                }

                table.addCell(newTableCell("Certificate (PDF)", SMALL_BOLD_FONT));
                if (includeAttachments) {
                    if (qualification.getLanguageQualificationDocument() != null) {
                        table.addCell(newTableCell("See APPENDIX(" + appendixCounter + ")", LINK_FONT, appendixCounter));
                        bookmarkMap.put(appendixCounter++, qualification.getLanguageQualificationDocument());
                    } else {
                        table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
                    }
                } else {
                    if (qualification.getLanguageQualificationDocument() != null) {
                        table.addCell(newTableCell(PROVIDED, SMALL_FONT));
                    } else {
                        table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
                    }
                }
            }
        }

        pdfDocument.add(table);
    }

    protected void addAddressSection(final ApplicationForm form, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
        table.addCell(newGrayTableCell("ADDRESS", BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(addSectionSeparators());

        table = new PdfPTable(2);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);

        table.addCell(newTableCell("Current Address", SMALL_BOLD_FONT));
        if (form.getCurrentAddress() == null) {
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            table.addCell(newTableCell(form.getCurrentAddress().getLocationString(), SMALL_FONT));
        }

        table.addCell(newTableCell("Country", SMALL_BOLD_FONT));
        if (form.getCurrentAddress() == null) {
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            table.addCell(newTableCell(form.getCurrentAddress().getDomicile().getName(), SMALL_FONT));
        }

        table.addCell(newTableCell("Contact Address", SMALL_BOLD_FONT));
        if (form.getContactAddress() == null) {
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            table.addCell(newTableCell(form.getContactAddress().getLocationString(), SMALL_FONT));
        }

        table.addCell(newTableCell("Country", SMALL_BOLD_FONT));
        if (form.getContactAddress() == null) {
            table.addCell(newTableCell(null, SMALL_FONT));
        } else {
            table.addCell(newTableCell(form.getContactAddress().getDomicile().getName(), SMALL_FONT));
        }
        pdfDocument.add(table);
    }

    protected void addQualificationSection(final ApplicationForm form, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
        table.addCell(newGrayTableCell("QUALIFICATIONS", BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(addSectionSeparators());

        if (form.getQualifications().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
            table.addCell(newTableCell("Qualification", SMALL_BOLD_FONT));
            table.addCell(newTableCell(null, SMALL_FONT));
            pdfDocument.add(table);
        } else {
            int counter = 1;
            for (Qualification qualification : form.getQualifications()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
                PdfPCell headerCell = newTableCell("Qualification (" + counter++ + ")", SMALL_BOLD_FONT);
                headerCell.setColspan(2);
                table.addCell(headerCell);
                table.addCell(newTableCell("Institution Country", SMALL_BOLD_FONT));
                table.addCell(newTableCell(qualification.getInstitutionCountry().getName(), SMALL_FONT));

                table.addCell(newTableCell("Institution/Provider Name", SMALL_BOLD_FONT));
                table.addCell(newTableCell(qualification.getQualificationInstitution(), SMALL_FONT));

                table.addCell(newTableCell("Other Institution / Provider Name", SMALL_BOLD_FONT));
                if (StringUtils.isNotBlank(qualification.getOtherQualificationInstitution())) {
                    table.addCell(newTableCell(qualification.getOtherQualificationInstitution(), SMALL_FONT));
                } else {
                    table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
                }

                table.addCell(newTableCell("Qualification Type", SMALL_BOLD_FONT));
                table.addCell(newTableCell(qualification.getQualificationType().getName(), SMALL_FONT));

                table.addCell(newTableCell("Qualification Title", SMALL_BOLD_FONT));
                table.addCell(newTableCell(qualification.getQualificationTitle(), SMALL_FONT));

                table.addCell(newTableCell("Qualification Subject", SMALL_BOLD_FONT));
                table.addCell(newTableCell(qualification.getQualificationSubject(), SMALL_FONT));

                table.addCell(newTableCell("Language of Study", SMALL_BOLD_FONT));
                table.addCell(newTableCell(qualification.getQualificationLanguage(), SMALL_FONT));

                table.addCell(newTableCell("Start Date", SMALL_BOLD_FONT));
                table.addCell(newTableCell(dateFormat.format(qualification.getQualificationStartDate()), SMALL_FONT));

                table.addCell(newTableCell("Has this Qualification been awarded", SMALL_BOLD_FONT));
                if (BooleanUtils.isTrue(qualification.isQualificationCompleted())) {
                    table.addCell(newTableCell("Yes", SMALL_FONT));
                } else {
                    table.addCell(newTableCell("No", SMALL_FONT));
                }

                if (qualification.isQualificationCompleted()) {
                    table.addCell(newTableCell("Grade/Result/GPA", SMALL_BOLD_FONT));
                } else {
                    table.addCell(newTableCell("Expected Grade/Result/GPA", SMALL_BOLD_FONT));
                }
                table.addCell(newTableCell(qualification.getQualificationGrade(), SMALL_FONT));

                if (BooleanUtils.isTrue(qualification.isQualificationCompleted())) {
                    table.addCell(newTableCell("Award Date", SMALL_BOLD_FONT));
                } else {
                    table.addCell(newTableCell("Expected Award Date", SMALL_BOLD_FONT));
                }
                if (qualification.getQualificationAwardDate() == null) {
                    table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
                } else {
                    table.addCell(newTableCell(dateFormat.format(qualification.getQualificationAwardDate()), SMALL_FONT));
                }

                if (qualification.getProofOfAward() != null) {
                    table.addCell(newTableCell("Proof of award", SMALL_BOLD_FONT));
                } else {
                    table.addCell(newTableCell("Interim Transcript", SMALL_BOLD_FONT));
                }

                if (includeAttachments) {
                    if (qualification.getProofOfAward() != null) {
                        table.addCell(newTableCell("See APPENDIX(" + appendixCounter + ")", LINK_FONT, appendixCounter));
                        bookmarkMap.put(appendixCounter++, qualification.getProofOfAward());
                    } else {
                        table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
                    }
                } else {
                    if (qualification.getProofOfAward() != null) {
                        table.addCell(newTableCell(PROVIDED, SMALL_FONT));
                    } else {
                        table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
                    }
                }

                pdfDocument.add(table);
                pdfDocument.add(addSectionSeparators());
            }
        }
    }

    protected void addEmploymentSection(final ApplicationForm form, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
        table.addCell(newGrayTableCell("EMPLOYMENT", BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(addSectionSeparators());

        if (form.getEmploymentPositions().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
            table.addCell(newTableCell("Position", SMALL_BOLD_FONT));
            table.addCell(newTableCell(null, SMALL_FONT));
            pdfDocument.add(table);
        } else {
            int counter = 1;
            for (EmploymentPosition position : form.getEmploymentPositions()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
                PdfPCell headerCell = newTableCell("Position (" + counter++ + ")", SMALL_BOLD_FONT);
                headerCell.setColspan(2);
                table.addCell(headerCell);
                table.addCell(newTableCell("Country", SMALL_BOLD_FONT));
                table.addCell(newTableCell(position.getEmployerAddress().getDomicile().getName(), SMALL_FONT));

                table.addCell(newTableCell("Employer Name", SMALL_BOLD_FONT));
                table.addCell(newTableCell(position.getEmployerName(), SMALL_FONT));

                table.addCell(newTableCell("Employer Address", SMALL_BOLD_FONT));
                table.addCell(newTableCell(position.getEmployerAddress().getLocationString(), SMALL_FONT));

                table.addCell(newTableCell("Position", SMALL_BOLD_FONT));
                table.addCell(newTableCell(position.getPosition(), SMALL_FONT));

                table.addCell(newTableCell("Roles and Responsibilities", SMALL_BOLD_FONT));
                table.addCell(newTableCell(position.getRemit(), SMALL_FONT));

                table.addCell(newTableCell("Start Date", SMALL_BOLD_FONT));
                table.addCell(newTableCell(dateFormat.format(position.getStartDate()), SMALL_FONT));

                table.addCell(newTableCell("Is this your Current Position", SMALL_BOLD_FONT));
                if (BooleanUtils.isTrue(position.isCurrent())) {
                    table.addCell(newTableCell("Yes", SMALL_FONT));
                } else {
                    table.addCell(newTableCell("No", SMALL_FONT));
                }

                table.addCell(newTableCell("End Date", SMALL_BOLD_FONT));

                if (position.getEndDate() == null) {
                    table.addCell(newTableCell(null, SMALL_GREY_FONT));
                } else {
                    table.addCell(newTableCell(dateFormat.format(position.getEndDate()), SMALL_FONT));
                }

                pdfDocument.add(table);
                pdfDocument.add(addSectionSeparators());
            }
        }
    }

    protected void addFundingSection(final ApplicationForm form, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
        table.addCell(newGrayTableCell("FUNDING", BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(addSectionSeparators());

        if (form.getFundings().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
            table.addCell(newTableCell("Funding", SMALL_BOLD_FONT));
            table.addCell(newTableCell(null, SMALL_FONT));
            pdfDocument.add(table);
        } else {
            int counter = 1;
            for (Funding funding : form.getFundings()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
                PdfPCell headerCell = newTableCell("Funding (" + counter++ + ")", SMALL_BOLD_FONT);
                headerCell.setColspan(2);
                table.addCell(headerCell);
                table.addCell(newTableCell("Funding Type", SMALL_BOLD_FONT));
                table.addCell(newTableCell(funding.getType().getDisplayValue(), SMALL_FONT));

                table.addCell(newTableCell("Description", SMALL_BOLD_FONT));
                table.addCell(newTableCell(funding.getDescription(), SMALL_FONT));

                table.addCell(newTableCell("Value of Award (GBP)", SMALL_BOLD_FONT));
                table.addCell(newTableCell(funding.getValue(), SMALL_FONT));

                table.addCell(newTableCell("Award Date", SMALL_BOLD_FONT));
                table.addCell(newTableCell(dateFormat.format(funding.getAwardDate()), SMALL_FONT));

                table.addCell(newTableCell("Proof Of Award", SMALL_BOLD_FONT));
                if (includeAttachments) {
                    if (funding.getDocument() != null) {
                        table.addCell(newTableCell("See APPENDIX(" + appendixCounter + ")", LINK_FONT, appendixCounter));
                        bookmarkMap.put(appendixCounter++, funding.getDocument());
                    } else {
                        table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
                    }
                } else {
                    if (funding.getDocument() != null) {
                        table.addCell(newTableCell(PROVIDED, SMALL_FONT));
                    } else {
                        table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
                    }
                }

                pdfDocument.add(table);
                pdfDocument.add(addSectionSeparators());
            }
        }
    }

    protected void addReferencesSection(final ApplicationForm form, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
        table.addCell(newGrayTableCell("REFERENCES", BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(addSectionSeparators());

        if (form.getReferees().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
            table.addCell(newTableCell("Reference", SMALL_BOLD_FONT));
            table.addCell(newTableCell(null, SMALL_FONT));
            pdfDocument.add(table);
        } else {
            int counter = 1;
            for (Referee referee : form.getReferees()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
                PdfPCell headerCell = newTableCell("Reference (" + counter++ + ")", SMALL_BOLD_FONT);
                headerCell.setColspan(2);
                table.addCell(headerCell);
                table.addCell(newTableCell("First Name", SMALL_BOLD_FONT));
                table.addCell(newTableCell(referee.getFirstname(), SMALL_FONT));

                table.addCell(newTableCell("Last Name", SMALL_BOLD_FONT));
                table.addCell(newTableCell(referee.getLastname(), SMALL_FONT));

                table.addCell(newTableCell("Employer", SMALL_BOLD_FONT));
                table.addCell(newTableCell(referee.getJobEmployer(), SMALL_FONT));

                table.addCell(newTableCell("Position", SMALL_BOLD_FONT));
                table.addCell(newTableCell(referee.getJobTitle(), SMALL_FONT));

                table.addCell(newTableCell("Address", SMALL_BOLD_FONT));
                table.addCell(newTableCell(referee.getAddressLocation().getLocationString(), SMALL_FONT));

                table.addCell(newTableCell("Country", SMALL_BOLD_FONT));
                table.addCell(newTableCell(referee.getAddressLocation().getDomicile().getName(), SMALL_FONT));

                table.addCell(newTableCell("Email", SMALL_BOLD_FONT));
                table.addCell(newTableCell(referee.getEmail(), SMALL_FONT));

                table.addCell(newTableCell("Telephone", SMALL_BOLD_FONT));
                table.addCell(newTableCell(referee.getPhoneNumber(), SMALL_FONT));

                table.addCell(newTableCell("Skype", SMALL_BOLD_FONT));
                table.addCell(newTableCell(referee.getMessenger(), SMALL_FONT));

                if (includeReferences) {
                    table.addCell(newTableCell("Reference", SMALL_BOLD_FONT));
                    if (referee.getReference() != null) {
                        table.addCell(newTableCell("See APPENDIX(" + appendixCounter + ")", LINK_FONT, appendixCounter));
                        bookmarkMap.put(appendixCounter++, referee.getReference());
                    } else {
                        table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
                    }
                }
                pdfDocument.add(table);
                pdfDocument.add(addSectionSeparators());
            }
        }
    }

    protected void addDocumentsSection(final ApplicationForm form, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
        table.addCell(newGrayTableCell("DOCUMENTS", BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(addSectionSeparators());

        table = new PdfPTable(2);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);

        table.addCell(newTableCell("Personal Statement", SMALL_BOLD_FONT));
        if (includeAttachments) {
            if (form.getPersonalStatement() != null) {
                table.addCell(newTableCell("See APPENDIX(" + appendixCounter + ")", LINK_FONT, appendixCounter));
                bookmarkMap.put(appendixCounter++, form.getPersonalStatement());
            } else {
                table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
            }
        } else {
            if (form.getPersonalStatement() != null) {
                table.addCell(newTableCell(PROVIDED, SMALL_FONT));
            } else {
                table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
            }
        }

        table.addCell(newTableCell("CV/Resume", SMALL_BOLD_FONT));
        if (includeAttachments) {
            if (form.getCv() != null) {
                table.addCell(newTableCell("See APPENDIX(" + appendixCounter + ")", LINK_FONT, appendixCounter));
                bookmarkMap.put(appendixCounter++, form.getCv());
            } else {
                table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
            }
        } else {
            if (form.getCv() != null) {
                table.addCell(newTableCell(PROVIDED, SMALL_FONT));
            } else {
                table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
            }
        }
        pdfDocument.add(table);
    }

    protected void addAdditionalInformationSection(final ApplicationForm form, Document pdfDocument) throws DocumentException {
        if (!includeCriminialConvictions) {
            return;
        }

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
        table.addCell(newGrayTableCell("ADDITIONAL INFORMATION", BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(addSectionSeparators());

        table = new PdfPTable(2);
        table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
        table.addCell(newTableCell("Do you have any unspent Criminial Convictions?", SMALL_BOLD_FONT));
        if (form.getAdditionalInformation().getConvictions() == null) {
            table.addCell(newTableCell(NOT_PROVIDED, SMALL_GREY_FONT));
        } else if (BooleanUtils.isTrue(form.getAdditionalInformation().getConvictions())) {
            table.addCell(newTableCell("Yes", SMALL_FONT));
        } else {
            table.addCell(newTableCell("No", SMALL_FONT));
        }

        table.addCell(newTableCell("Description", SMALL_BOLD_FONT));
        table.addCell(newTableCell(form.getAdditionalInformation().getConvictionsText(), SMALL_FONT));

        pdfDocument.add(table);
    }

    protected void addSupportingDocuments(final ApplicationForm form, final Document pdfDocument, final PdfWriter pdfWriter) throws DocumentException {
        for (Integer integer : bookmarkMap.keySet()) {
            pdfDocument.newPage();

            headerEvent.setAddHeaderAndFooter(true);

            Object obj = bookmarkMap.get(integer);
            if (obj instanceof com.zuehlke.pgadmissions.domain.Document) {

                if (!includeAttachments) {
                    continue;
                }

                com.zuehlke.pgadmissions.domain.Document doc = (com.zuehlke.pgadmissions.domain.Document) obj;
                if (doc != null) {
                    pdfDocument.add(new Chunk("APPENDIX (" + integer + ")").setLocalDestination(integer.toString()));

                    if (DocumentType.PERSONAL_STATEMENT == doc.getType()) {
                        pdfDocument.add(new Chunk(" - Personal Statement"));
                    } else if (DocumentType.CV == doc.getType()) {
                        pdfDocument.add(new Chunk(" - CV"));
                    } else if (DocumentType.SUPPORTING_FUNDING == doc.getType()) {
                        pdfDocument.add(new Chunk(" - Funding proof of award"));
                    } else if (DocumentType.PROOF_OF_AWARD == doc.getType()) {
                        pdfDocument.add(new Chunk(" - Qualification Transcript"));
                    } else if (DocumentType.LANGUAGE_QUALIFICATION == doc.getType()) {
                        pdfDocument.add(new Chunk(" - English Language Certificate"));
                    }

                    try {
                        readPdf(pdfDocument, doc, pdfWriter);
                    } catch (Exception e) {
                        log.warn(String.format("Error in generating pdf while appending supporting document %s for %s", form.getApplicationNumber(),
                                        doc.getFileName()), e);
                    }
                }
            } else if (obj instanceof ReferenceComment) {
                ReferenceComment reference = (ReferenceComment) obj;
                pdfDocument.add(new Chunk("APPENDIX (" + integer + ")").setLocalDestination(integer.toString()));

                pdfDocument.add(new Chunk(" - Reference"));
                pdfDocument.add(addSectionSeparators());
                pdfDocument.add(addSectionSeparators());
                pdfDocument.add(addSectionSeparators());
                pdfDocument.add(addSectionSeparators());

                PdfPTable table = new PdfPTable(1);
                table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
                table.addCell(newGrayTableCell("REFERENCE", BOLD_FONT));
                pdfDocument.add(table);
                pdfDocument.add(addSectionSeparators());

                table = new PdfPTable(2);
                table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
                table.addCell(newTableCell("Referee", SMALL_BOLD_FONT));
                table.addCell(newTableCell(reference.getReferee().getFirstname() + " " + reference.getReferee().getLastname(), SMALL_FONT));
                table.addCell(newTableCell("Comment", SMALL_BOLD_FONT));
                table.addCell(newTableCell(reference.getComment(), SMALL_FONT));
                table.addCell(newTableCell("Is the applicant suitable for postgraduate study at UCL?", SMALL_BOLD_FONT));
                if (BooleanUtils.isTrue(reference.getSuitableForUCL())) {
                    table.addCell(newTableCell("Yes", SMALL_FONT));
                } else {
                    table.addCell(newTableCell("No", SMALL_FONT));
                }
                table.addCell(newTableCell("Is the applicant suitable for their chosen postgraduate study programme?", SMALL_BOLD_FONT));
                if (BooleanUtils.isTrue(reference.getSuitableForProgramme())) {
                    table.addCell(newTableCell("Yes", SMALL_FONT));
                } else {
                    table.addCell(newTableCell("No", SMALL_FONT));
                }
                pdfDocument.add(table);
                for (com.zuehlke.pgadmissions.domain.Document refDocument : reference.getDocuments()) {
                    try {
                        readPdf(pdfDocument, refDocument, pdfWriter);
                    } catch (Exception e) {
                        log.warn(String.format("Error in generating pdf while appending supporting document %s for %s", form.getApplicationNumber(),
                                        refDocument.getFileName()), e);
                    }
                }
            }
        }
    }

    private void readPdf(final Document pdfDocument, final com.zuehlke.pgadmissions.domain.Document document, final PdfWriter pdfWriter) throws IOException {
        PdfReader pdfReader = new PdfReader(document.getContent());
        PdfContentByte cb = pdfWriter.getDirectContent();
        for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
            PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, i);
            pdfDocument.setPageSize(new Rectangle(page.getWidth(), page.getHeight()));
            pdfDocument.newPage();
            headerEvent.setAddHeaderAndFooter(false);
            cb.addTemplate(page, 0, 0);
            pdfDocument.setPageSize(PageSize.A4);
        }
    }

    private void addClosingDateToTable(PdfPTable table, final ApplicationForm form) {
        table.addCell(newTableCell("Closing date", SMALL_BOLD_FONT));
        Project project = form.getProject();
        String closingDate = NOT_REQUIRED;
        if (project != null && project.getClosingDate() != null) {
            closingDate = dateFormat.format(project.getClosingDate());
        }
        table.addCell(newTableCell(closingDate, SMALL_FONT));
    }

    private void addProjectTitleToTable(PdfPTable table, final ApplicationForm form) {
        table.addCell(newTableCell("Project", SMALL_BOLD_FONT));
        String projectTitle = form.getProjectTitle();
        if (StringUtils.isBlank(projectTitle)) {
            projectTitle = NOT_REQUIRED;
        }
        table.addCell(newTableCell(projectTitle, SMALL_FONT));
    }

    class HeaderEvent extends PdfPageEventHelper {
        private final Chunk programmeHeader;
        private final Chunk applicationHeader;
        private final Chunk submittedDateHeader;
        private boolean addHeaderAndFooter = true;
        private boolean first = true;

        public HeaderEvent(Chunk programmeHeader, Chunk applicationHeader, Chunk submittedDateHeader) {
            this.programmeHeader = programmeHeader;
            this.applicationHeader = applicationHeader;
            this.submittedDateHeader = submittedDateHeader;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            if (first) {
                pageCounter = document.getPageNumber();
                first = false;
            }
            try {
                if (addHeaderAndFooter) {
                    addHeaderToPage(writer, document);
                    addFooterToPage(writer, document);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void addFooterToPage(PdfWriter writer, Document document) {
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.drawLine(writer.getDirectContent(), document.left(), document.right(), document.bottom() - 15f);
            Phrase footerPhrase = new Phrase("Page " + (1 + document.getPageNumber() - pageCounter), SMALLER_FONT);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, footerPhrase, document.left(), document.bottom() - 25f, 0);
        }

        private void addHeaderToPage(PdfWriter writer, Document document) throws DocumentException, BadElementException, MalformedURLException, IOException {
            PdfPTable table = new PdfPTable(2);
            table.setTotalWidth(0.75f * document.getPageSize().getWidth());
            table.setWidths(new float[] { 25f, 75f });

            PdfPCell c1 = new PdfPCell(new Phrase("Programme", SMALLER_BOLD_FONT));
            c1.setBorder(0);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(programmeHeader));
            c1.setBorder(0);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Application Number", SMALLER_BOLD_FONT));
            c1.setBorder(0);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(applicationHeader));
            c1.setBorder(0);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Submitted", SMALLER_BOLD_FONT));
            c1.setBorder(0);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(submittedDateHeader));
            c1.setBorder(0);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c1);

            table.writeSelectedRows(0, -1, document.left(), document.top() + 55f, writer.getDirectContent());

            Image image = Image.getInstance(this.getClass().getResource("/prism_logo.png"));
            image.scalePercent(50f);

            image.setAbsolutePosition(document.right() - image.getWidth() * 0.5f, document.top() + 20f);
            document.add(image);
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.drawLine(writer.getDirectContent(), document.left(), document.right(), document.top() + 10f);
        }

        public void setAddHeaderAndFooter(boolean addHeader) {
            this.addHeaderAndFooter = addHeader;
        }

    }

}