package com.zuehlke.pgadmissions.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
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
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PassportInformation;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.exceptions.PDFException;

@Component
public class PdfDocumentBuilder {
    
    private static final String NOT_PROVIDED = "Not Provided";

    private static final Logger LOG = Logger.getLogger(PdfDocumentBuilder.class);
    
    private static Font boldFont = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
    private static Font smallBoldFont = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
    private static Font smallFont = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);
    private static Font smallGrayFont = new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.LIGHT_GRAY);
    private static Font smallerBoldFont = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
    private static Font smallerFont = new Font(FontFamily.HELVETICA, 8, Font.NORMAL);
    private static Font linkFont = new Font(FontFamily.HELVETICA, 10, Font.UNDERLINE, BaseColor.BLUE);

    private final BaseColor grayColor = new BaseColor(220, 220, 220);

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
    private Map<Integer, Object> bookmarkMap;
    private int appendixCounter = 1;
    private HeaderEvent headerEvent;
    private int pageCounter = 0;
    
    public void writeCombinedReferencesAsPdfToOutputStream(ReferenceComment referenceComment, OutputStream outputStream) {
        try {
            Document document = new Document(PageSize.A4, 50, 50, 100, 50);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setCloseStream(false); // otherwise we're loosing our ZipOutputstream for calling zos.closeEntry();
            document.open();
            
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100f);
            table.addCell(newTableCell("Referee Comment", boldFont, BaseColor.GRAY));
            document.add(table);
            document.add(new Paragraph(" "));
            
            if (referenceComment.getReferee() != null && BooleanUtils.isTrue(referenceComment.getReferee().isDeclined())) {
                document.add(new Paragraph("Comment:\nDeclined to provide a reference."));
            } else {
                document.add(new Paragraph("Comment:\n" + referenceComment.getComment()));
            }
            
            PdfContentByte cb = writer.getDirectContent();
            for (com.zuehlke.pgadmissions.domain.Document in : referenceComment.getDocuments()) {
                PdfReader reader = new PdfReader(in.getContent());
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    document.newPage();
                    PdfImportedPage page = writer.getImportedPage(reader, i);
                    cb.addTemplate(page, 0, 0);
                }
            }
            document.newPage();
            document.close();
        } catch (Exception e) {
            throw new PDFException(e);
        }
    }

   public void buildPdf(ApplicationForm applicationForm, OutputStream outputStream, boolean includeAttachments) {
        try {
            Document document = new Document(PageSize.A4, 50, 50, 100, 50);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setCloseStream(false); // otherwise we're loosing our ZipOutputstream for calling zos.closeEntry();
            document.open();
            buildDocument(applicationForm, document, writer, includeAttachments);
            document.newPage();
            document.close();
        } catch (Exception e) {
            throw new PDFException(e);
        }
    }

    public byte[] buildPdfWithAttachments(ApplicationForm... applications) {
        try {
            Document document = new Document(PageSize.A4, 50, 50, 100, 50);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();
            for (ApplicationForm applicationForm : applications) {
                try {
                    buildDocument(applicationForm, document, writer);
                } catch (Exception e) {
                    LOG.warn("Error in generating pdf for application " + applicationForm.getApplicationNumber(), e);
                }
                document.newPage();
            }
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new PDFException(e);
        }
    }
    
    public byte[] buildTranscript1FromApprovalRoundComment(ApplicationForm application) {
        try{
            Document document = new Document(PageSize.A4, 50, 50, 100, 50);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100f);
            table.addCell(newTableCell("No Transcripts Provided", boldFont, BaseColor.GRAY));
            document.add(table);
            document.add(new Paragraph(" "));
            
            ApprovalRound latestApprovalRound = application.getLatestApprovalRound();
            if (latestApprovalRound != null) {
                if (StringUtils.isBlank(latestApprovalRound.getMissingQualificationExplanation())) {
                    document.add(new Paragraph(String.format("Approval Round Comment:\n%s", latestApprovalRound.getMissingQualificationExplanation())));
                } else {
                    document.add(new Paragraph(String.format("Approval Round Comment:\n%s", NOT_PROVIDED)));
                }
            } else {
                document.add(new Paragraph(String.format("Approval Round Comment:\n%s", NOT_PROVIDED)));
            }
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new PDFException(e);
        }
    }
    
    private void buildDocument(ApplicationForm application, Document document, PdfWriter writer) throws DocumentException, MalformedURLException, IOException {
        buildDocument(application, document, writer, true);
    }
    
    private void buildDocument(ApplicationForm application, Document document, PdfWriter writer, boolean includeAttachments) throws DocumentException, MalformedURLException, IOException {
        bookmarkMap = new HashMap<Integer, Object>();
        appendixCounter = 1;
        addCoverPage(application, writer, document);
        Chunk submittedDateHeader = null;
        if (application.getSubmittedDate() != null) {
            submittedDateHeader = new Chunk(new SimpleDateFormat("dd MMMM yyyy").format(application.getSubmittedDate()), smallerFont);
        } else {
            submittedDateHeader = new Chunk("", smallerFont);
        }

        headerEvent = new HeaderEvent(
                new Chunk(application.getProgram().getTitle(), smallerFont), 
                new Chunk(application.getApplicationNumber(), smallerFont), 
                submittedDateHeader);
        
        writer.setPageEvent(headerEvent);
        
        addProgrammeSection(application, document);

        addSectionSeparators(document);

        addPersonalDetailsSection(application, document);

        addSectionSeparators(document);

        addAddressSection(application, document);

        addSectionSeparators(document);

        addQualificationSection(application, document);

        addSectionSeparators(document);

        addEmploymentSection(application, document);

        addSectionSeparators(document);

        addFundingSection(application, document);

        addSectionSeparators(document);

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
            if (currentUser.hasAdminRightsOnApplication(application)) {
                addReferencesSection(application, document);
            }
        }

        addSectionSeparators(document);

        addDocumentsSection(application, document);

        addSectionSeparators(document);

        addAdditionalInformationSection(application, document);

        addSectionSeparators(document);

        addSupportingDocuments(application, document, writer, includeAttachments);
    }

    private void addCoverPage(ApplicationForm application, PdfWriter writer, Document document) throws DocumentException, MalformedURLException, IOException {
        document.newPage();

        Image image = Image.getInstance(this.getClass().getResource("/prism_logo.png"));
        image.scalePercent(50f);

        image.setAbsolutePosition(document.right() - image.getWidth() * 0.5f, document.top() + 20f);
        document.add(image);
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.drawLine(writer.getDirectContent(), document.left(), document.right(), document.top() + 10f);
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100f);
        table.addCell(newTableCell("APPLICATION", boldFont, BaseColor.GRAY));
        document.add(table);
        document.add(new Paragraph(" "));
        table = new PdfPTable(2);
        table.setWidthPercentage(100f);
        table.addCell(newTableCell("Applicant", smallBoldFont));
        table.addCell(newTableCell(application.getApplicant().getFirstName() + " " + application.getApplicant().getLastName(), smallFont));
        table.addCell(newTableCell("Programme", smallBoldFont));
        table.addCell(newTableCell(application.getProgram().getTitle() , smallFont));
        table.addCell(newTableCell("Application Number", smallBoldFont));
        table.addCell(newTableCell(application.getApplicationNumber(), smallFont));
        if (application.getSubmittedDate() != null) {
            table.addCell(newTableCell("Submission date", smallBoldFont));
            table.addCell(newTableCell(new SimpleDateFormat("dd MMMM yyyy").format(application.getSubmittedDate()), smallFont));
        } 
        
        document.add(table);
        document.newPage();
    }

    private void addSectionSeparators(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
    }

    private void addProgrammeSection(ApplicationForm application, Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100f);
        table.addCell(newTableCell("PROGRAMME", boldFont, BaseColor.GRAY));
        document.add(table);
        document.add(new Paragraph(" "));

        table = new PdfPTable(2);
        table.setWidthPercentage(100f);
        table.addCell(newTableCell("Programme", smallBoldFont));
        table.addCell(newTableCell(application.getProgram().getTitle(), smallFont));

        table.addCell(newTableCell("Study Option", smallBoldFont));
        if (application.getProgrammeDetails().getStudyOption() != null) {
            table.addCell(newTableCell(application.getProgrammeDetails().getStudyOption(), smallFont));
        } else {
            table.addCell(newTableCell(NOT_PROVIDED, smallGrayFont));
        }

        table.addCell(newTableCell("Project", smallBoldFont));
        if (!StringUtils.isBlank(application.getProgrammeDetails().getProjectName())) {
            table.addCell(newTableCell(application.getProgrammeDetails().getProjectName(), smallFont));
        } else {
            table.addCell(newTableCell(NOT_PROVIDED, smallGrayFont));
        }

        table.addCell(newTableCell("Start Date", smallBoldFont));
        if (application.getProgrammeDetails().getStartDate() != null) {
            table.addCell(newTableCell(simpleDateFormat.format(application.getProgrammeDetails().getStartDate()), smallFont));
        } else {
            table.addCell(newTableCell(NOT_PROVIDED, smallGrayFont));
        }

        table.addCell(newTableCell("How did you find us?", smallBoldFont));
        if (application.getProgrammeDetails().getSourcesOfInterest() != null) {
            table.addCell(newTableCell(application.getProgrammeDetails().getSourcesOfInterest().getName(), smallFont));
        } else {
            table.addCell(newTableCell(NOT_PROVIDED, smallGrayFont));
        }
        
        if (application.getProgrammeDetails().getSourcesOfInterest() != null && application.getProgrammeDetails().getSourcesOfInterest().isFreeText()) {
            table.addCell(newTableCell("Please explain", smallBoldFont));
            table.addCell(newTableCell(application.getProgrammeDetails().getSourcesOfInterestText(), smallFont));
        }
        
        document.add(table);
        document.add(new Paragraph(" "));

        if (application.getProgrammeDetails().getSuggestedSupervisors().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(100f);
            table.addCell(newTableCell("Supervisor", smallBoldFont));
            table.addCell(newTableCell(NOT_PROVIDED, smallGrayFont));
            document.add(table);
            document.add(new Paragraph(" "));
        } else {

            int counter = 1;
            for (SuggestedSupervisor supervisor : application.getProgrammeDetails().getSuggestedSupervisors()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(100f);
                PdfPCell headerCell = newTableCell("Supervisor (" + counter++ + ")", smallBoldFont);
                headerCell.setColspan(2);
                table.addCell(headerCell);

                table.addCell(newTableCell("Supervisor First Name", smallerBoldFont));
                table.addCell(newTableCell(supervisor.getFirstname(), smallerFont));

                table.addCell(newTableCell("Supervisor Last Name", smallerBoldFont));
                table.addCell(newTableCell(supervisor.getLastname(), smallerFont));

                table.addCell(newTableCell("Supervisor Email", smallerBoldFont));
                table.addCell(newTableCell(supervisor.getEmail(), smallerFont));

                table.addCell(newTableCell("Is this supervisor aware of your application?", smallerBoldFont));

                if (BooleanUtils.isTrue(supervisor.isAware())) {
                    table.addCell(newTableCell("Yes", smallerFont));
                } else {
                    table.addCell(newTableCell("No", smallerFont));
                }
                document.add(table);
                document.add(new Paragraph(" "));
            }
        }
    }

    private PdfPCell newTableCell(String content, Font font) {

        PdfPCell cell = null;
        if (StringUtils.isNotBlank(content)) {
            cell = new PdfPCell(new Phrase(content, font));
        } else {
            cell = new PdfPCell(new Phrase(NOT_PROVIDED, smallGrayFont));
        }
        cell.setPaddingBottom(5);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);

        return cell;
    }

    private PdfPCell newTableCell(String content, Font font, BaseColor backgrounColor) {
        PdfPCell c1 = newTableCell(content, font);
        c1.setBackgroundColor(grayColor);
        return c1;
    }

    private PdfPCell newTableCell(String content, Font font, Integer appendixNumber) {
        PdfPCell cell = null;
        if (StringUtils.isNotBlank(content)) {
            cell = new PdfPCell(new Phrase(new Chunk(content, font).setLocalGoto(appendixNumber.toString())));
        } else {
            cell = new PdfPCell(new Phrase(NOT_PROVIDED, smallGrayFont));
        }
        cell.setPaddingBottom(5);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);

        return cell;
    }

    private void addPersonalDetailsSection(ApplicationForm application, Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100f);
        table.addCell(newTableCell("PERSONAL DETAILS", boldFont, BaseColor.GRAY));
        document.add(table);
        document.add(new Paragraph(" "));

        table = new PdfPTable(2);
        table.setWidthPercentage(100f);
        
        table.addCell(newTableCell("Title", smallBoldFont));
        if (application.getPersonalDetails().getTitle() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            table.addCell(newTableCell(application.getPersonalDetails().getTitle().getDisplayValue(), smallFont));
        }
        
        table.addCell(newTableCell("First Name", smallBoldFont));
        table.addCell(newTableCell(application.getPersonalDetails().getFirstName(), smallFont));

        table.addCell(newTableCell("Last Name", smallBoldFont));
        table.addCell(newTableCell(application.getPersonalDetails().getLastName(), smallFont));

        table.addCell(newTableCell("Gender", smallBoldFont));
        if (application.getPersonalDetails().getGender() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            table.addCell(newTableCell(application.getPersonalDetails().getGender().getDisplayValue(), smallFont));
        }

        table.addCell(newTableCell("Date of Birth", smallBoldFont));
        if (application.getPersonalDetails().getDateOfBirth() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            table.addCell(newTableCell(simpleDateFormat.format(application.getPersonalDetails().getDateOfBirth()), smallFont));
        }
        table.addCell(newTableCell("Country of Birth", smallBoldFont));
        if (application.getPersonalDetails().getCountry() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            table.addCell(newTableCell(application.getPersonalDetails().getCountry().getName(), smallFont));
        }

        table.addCell(newTableCell("Nationality", smallBoldFont));
        StringBuilder sb = new StringBuilder();
        for (Language languageCountry : application.getPersonalDetails().getCandidateNationalities()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(languageCountry.getName());
        }
        table.addCell(newTableCell(sb.toString(), smallFont));

        table.addCell(newTableCell("Is English your first language?", smallBoldFont));
        if (application.getPersonalDetails().getEnglishFirstLanguage() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            if (BooleanUtils.isTrue(application.getPersonalDetails().getEnglishFirstLanguage())) {
                table.addCell(newTableCell("Yes", smallFont));
            } else {
                table.addCell(newTableCell("No", smallFont));
            }
        }
        
        table.addCell(newTableCell("Do you have an English language qualification?", smallBoldFont));
        if (application.getPersonalDetails().getLanguageQualificationAvailable() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            if (BooleanUtils.isTrue(application.getPersonalDetails().getLanguageQualificationAvailable())) {
                table.addCell(newTableCell("Yes", smallFont));
            } else {
                table.addCell(newTableCell("No", smallFont));
            }
        }
        
        table.addCell(newTableCell("Country of Residence", smallBoldFont));
        if (application.getPersonalDetails().getResidenceCountry() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            table.addCell(newTableCell(application.getPersonalDetails().getResidenceCountry().getName(), smallFont));
        }

        table.addCell(newTableCell("Do you Require a Visa to Study in the UK?", smallBoldFont));
        if (application.getPersonalDetails().getRequiresVisa() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            if (BooleanUtils.isTrue(application.getPersonalDetails().getRequiresVisa())) {
                table.addCell(newTableCell("Yes", smallFont));
            } else {
                table.addCell(newTableCell("No", smallFont));
            }
        }
        
        table.addCell(newTableCell("Do you have a passport?", smallBoldFont));
        if (application.getPersonalDetails().getPassportAvailable() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            if (BooleanUtils.isTrue(application.getPersonalDetails().getPassportAvailable())) {
                table.addCell(newTableCell("Yes", smallFont));
            } else {
                table.addCell(newTableCell("No", smallFont));
            }
        }
        
        if (BooleanUtils.isTrue(application.getPersonalDetails().getRequiresVisa())) {
            PassportInformation passportInformation = application.getPersonalDetails().getPassportInformation();
            if (passportInformation != null) {
                table.addCell(newTableCell("Passport Number", smallBoldFont));
                
                if (StringUtils.isBlank(passportInformation.getPassportNumber())) {
                    table.addCell(newTableCell(null, smallFont));
                } else {
                    table.addCell(newTableCell(passportInformation.getPassportNumber(), smallFont));
                }
                
                table.addCell(newTableCell("Name on Passport", smallBoldFont));
                if (StringUtils.isBlank(passportInformation.getNameOnPassport())) {
                    table.addCell(newTableCell(null, smallFont));
                } else {
                    table.addCell(newTableCell(passportInformation.getNameOnPassport(), smallFont));
                }
                
                table.addCell(newTableCell("Passport Issue Date", smallBoldFont));
                if (passportInformation.getPassportIssueDate() == null) {
                    table.addCell(newTableCell(null, smallFont));
                } else {
                    table.addCell(newTableCell(simpleDateFormat.format(passportInformation.getPassportIssueDate()), smallFont));
                }
                
                table.addCell(newTableCell("Passport Expiry Date", smallBoldFont));
                if (passportInformation.getPassportExpiryDate() == null) {
                    table.addCell(newTableCell(null, smallFont));
                } else {
                    table.addCell(newTableCell(simpleDateFormat.format(passportInformation.getPassportExpiryDate()), smallFont));
                }
            }
        }

        table.addCell(newTableCell("Email", smallBoldFont));
        table.addCell(newTableCell(application.getPersonalDetails().getEmail(), smallFont));

        table.addCell(newTableCell("Telephone", smallBoldFont));
        table.addCell(newTableCell(application.getPersonalDetails().getPhoneNumber(), smallFont));

        table.addCell(newTableCell("Skype", smallBoldFont));
        table.addCell(newTableCell(application.getPersonalDetails().getMessenger(), smallFont));
        document.add(table);
        
        document.add(new Paragraph(" "));
        
        if (application.getPersonalDetails().getLanguageQualifications().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(100f);
            table.addCell(newTableCell("English Language Qualifications", smallBoldFont));
            table.addCell(newTableCell(null, smallFont));
        } else {
            int counter = 1;
            for (LanguageQualification qualification : application.getPersonalDetails().getLanguageQualifications()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(100f);
                PdfPCell headerCell = newTableCell("English Language Qualification (" + counter++ + ")", smallBoldFont);
                headerCell.setColspan(2);
                table.addCell(headerCell);
        
                table.addCell(newTableCell("Qualification Type", smallBoldFont));
                table.addCell(newTableCell(qualification.getQualificationType().getDisplayValue(), smallFont));
                
                table.addCell(newTableCell("Other Qualification Type Name", smallBoldFont));
                table.addCell(newTableCell(qualification.getOtherQualificationTypeName(), smallFont));
                
                table.addCell(newTableCell("Date of Examination", smallBoldFont));
                table.addCell(newTableCell(simpleDateFormat.format(qualification.getDateOfExamination()), smallFont));
                
                table.addCell(newTableCell("Overall Score", smallBoldFont));
                table.addCell(newTableCell(qualification.getOverallScore(), smallFont));
                
                table.addCell(newTableCell("Reading Score", smallBoldFont));
                table.addCell(newTableCell(qualification.getReadingScore(), smallFont));
                
                table.addCell(newTableCell("Essay / Writing Score", smallBoldFont));
                table.addCell(newTableCell(qualification.getWritingScore(), smallFont));
                
                table.addCell(newTableCell("Speaking Score", smallBoldFont));
                table.addCell(newTableCell(qualification.getSpeakingScore(), smallFont));
                
                table.addCell(newTableCell("Listening Score", smallBoldFont));
                table.addCell(newTableCell(qualification.getListeningScore(), smallFont));
                
                table.addCell(newTableCell("Did you sit the exam online?", smallBoldFont));
                if (qualification.getExamTakenOnline() == null) {
                    table.addCell(newTableCell(null, smallFont));
                } else {
                    if (BooleanUtils.isTrue(qualification.getExamTakenOnline())) {
                        table.addCell(newTableCell("Yes", smallFont));
                    } else {
                        table.addCell(newTableCell("No", smallFont));
                    }
                }
                
                table.addCell(newTableCell("Certificate (PDF)", smallBoldFont));
                if (qualification.getLanguageQualificationDocument() != null) {
                    table.addCell(newTableCell("See APPENDIX(" + appendixCounter + ")", linkFont, appendixCounter));
                    bookmarkMap.put(appendixCounter++, qualification.getLanguageQualificationDocument());
                } else {
                    table.addCell(newTableCell(NOT_PROVIDED, smallGrayFont));
                }
            }
        }
        
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
            if (currentUser.isInRole(Authority.SUPERADMINISTRATOR) || currentUser.getId().equals(application.getApplicant().getId())) {
                table.addCell(newTableCell("Ethnicity", smallBoldFont));
                if (application.getPersonalDetails().getEthnicity() == null) {
                    table.addCell(newTableCell(NOT_PROVIDED, smallGrayFont));
                } else {
                    table.addCell(newTableCell(application.getPersonalDetails().getEthnicity().getName(), smallFont));                
                }
                
                table.addCell(newTableCell("Disability", smallBoldFont));
                if (application.getPersonalDetails().getDisability() == null) {
                    table.addCell(newTableCell(NOT_PROVIDED, smallGrayFont));
                } else {
                    table.addCell(newTableCell(application.getPersonalDetails().getDisability().getName(), smallFont));                
                }
            }
        }
        document.add(table);
    }

    private void addAddressSection(ApplicationForm application, Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100f);
        table.addCell(newTableCell("ADDRESS", boldFont, BaseColor.GRAY));
        document.add(table);
        document.add(new Paragraph(" "));
        table = new PdfPTable(2);
        table.setWidthPercentage(100f);

        table.addCell(newTableCell("Current Address", smallBoldFont));
        if (application.getCurrentAddress() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            table.addCell(newTableCell(application.getCurrentAddress().getLocationString(), smallFont));
        }

        table.addCell(newTableCell("Country", smallBoldFont));
        if (application.getCurrentAddress() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            table.addCell(newTableCell(application.getCurrentAddress().getCountry().getName(), smallFont));
        }

        table.addCell(newTableCell("Contact Address", smallBoldFont));
        if (application.getContactAddress() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            table.addCell(newTableCell(application.getContactAddress().getLocationString(), smallFont));
        }

        table.addCell(newTableCell("Country", smallBoldFont));
        if (application.getContactAddress() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            table.addCell(newTableCell(application.getContactAddress().getCountry().getName(), smallFont));
        }
        document.add(table);
    }

    private void addQualificationSection(ApplicationForm application, Document document) throws DocumentException, IOException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100f);
        table.addCell(newTableCell("QUALIFICATIONS", boldFont, BaseColor.GRAY));
        document.add(table);
        document.add(new Paragraph(" "));

        if (application.getQualifications().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(100f);
            table.addCell(newTableCell("Qualification", smallBoldFont));
            table.addCell(newTableCell(null, smallFont));
            document.add(table);
        } else {
            int counter = 1;
            for (Qualification qualification : application.getQualifications()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(100f);
                PdfPCell headerCell = newTableCell("Qualification (" + counter++ + ")", smallBoldFont);
                headerCell.setColspan(2);
                table.addCell(headerCell);
                table.addCell(newTableCell("Institution Country", smallBoldFont));
                table.addCell(newTableCell(qualification.getInstitutionCountry().getName(), smallFont));

                table.addCell(newTableCell("Institution/Provider Name", smallBoldFont));
                table.addCell(newTableCell(qualification.getQualificationInstitution(), smallFont));
                
                table.addCell(newTableCell("Other Institution / Provider Name", smallBoldFont));
                if (StringUtils.isNotBlank(qualification.getOtherQualificationInstitution())) {
                    table.addCell(newTableCell(qualification.getOtherQualificationInstitution(), smallFont));                    
                } else {
                    table.addCell(newTableCell(NOT_PROVIDED, smallGrayFont));                    
                }

                table.addCell(newTableCell("Qualification Type", smallBoldFont));
                table.addCell(newTableCell(qualification.getQualificationType().getName(), smallFont));

                table.addCell(newTableCell("Qualification Title", smallBoldFont));
                table.addCell(newTableCell(qualification.getQualificationTitle(), smallFont));

                table.addCell(newTableCell("Qualification Subject", smallBoldFont));
                table.addCell(newTableCell(qualification.getQualificationSubject(), smallFont));
                
                table.addCell(newTableCell("Language of Study", smallBoldFont));
                table.addCell(newTableCell(qualification.getQualificationLanguage(), smallFont));

                table.addCell(newTableCell("Start Date", smallBoldFont));
                table.addCell(newTableCell(simpleDateFormat.format(qualification.getQualificationStartDate()), smallFont));

                table.addCell(newTableCell("Has this Qualification been awarded", smallBoldFont));
                if (BooleanUtils.isTrue(qualification.isQualificationCompleted())) {
                    table.addCell(newTableCell("Yes", smallFont));
                } else {
                    table.addCell(newTableCell("No", smallFont));
                }

                if (qualification.isQualificationCompleted()) {
                    table.addCell(newTableCell("Grade/Result/GPA", smallBoldFont));
                } else {
                    table.addCell(newTableCell("Expected Grade/Result/GPA", smallBoldFont));
                }
                table.addCell(newTableCell(qualification.getQualificationGrade(), smallFont));

                table.addCell(newTableCell("Award Date", smallBoldFont));
                if (qualification.getQualificationAwardDate() == null) {
                    table.addCell(newTableCell(NOT_PROVIDED, smallGrayFont));
                } else {
                    table.addCell(newTableCell(simpleDateFormat.format(qualification.getQualificationAwardDate()), smallFont));
                }

                table.addCell(newTableCell("Transcript", smallBoldFont));
                if (qualification.isQualificationCompleted() && qualification.getProofOfAward() != null) {
                    table.addCell(newTableCell("See APPENDIX(" + appendixCounter + ")", linkFont, appendixCounter));
                    bookmarkMap.put(appendixCounter++, qualification.getProofOfAward());
                } else {
                    table.addCell(newTableCell(NOT_PROVIDED, smallGrayFont));
                }

                document.add(table);
                document.add(new Paragraph(" "));
            }
        }
    }

    private void addEmploymentSection(ApplicationForm application, Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100f);
        table.addCell(newTableCell("EMPLOYMENT", boldFont, BaseColor.GRAY));
        document.add(table);
        document.add(new Paragraph(" "));

        if (application.getEmploymentPositions().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(100f);
            table.addCell(newTableCell("Position", smallBoldFont));
            table.addCell(newTableCell(null, smallFont));
            document.add(table);
        } else {
            int counter = 1;
            for (EmploymentPosition position : application.getEmploymentPositions()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(100f);
                PdfPCell headerCell = newTableCell("Position (" + counter++ + ")", smallBoldFont);
                headerCell.setColspan(2);
                table.addCell(headerCell);
                table.addCell(newTableCell("Country", smallBoldFont));
                table.addCell(newTableCell(position.getEmployerAddress().getCountry().getName(), smallFont));

                table.addCell(newTableCell("Employer Name", smallBoldFont));
                table.addCell(newTableCell(position.getEmployerName(), smallFont));

                table.addCell(newTableCell("Employer Address", smallBoldFont));
                table.addCell(newTableCell(position.getEmployerAddress().getLocationString(), smallFont));
                
                table.addCell(newTableCell("Position", smallBoldFont));
                table.addCell(newTableCell(position.getPosition(), smallFont));

                table.addCell(newTableCell("Roles and Responsibilities", smallBoldFont));
                table.addCell(newTableCell(position.getRemit(), smallFont));

                table.addCell(newTableCell("Start Date", smallBoldFont));
                table.addCell(newTableCell(simpleDateFormat.format(position.getStartDate()), smallFont));

                table.addCell(newTableCell("Is this your Current Position", smallBoldFont));
                if (BooleanUtils.isTrue(position.isCurrent())) {
                    table.addCell(newTableCell("Yes", smallFont));
                } else {
                    table.addCell(newTableCell("No", smallFont));
                }

                table.addCell(newTableCell("End Date", smallBoldFont));

                if (position.getEndDate() == null) {
                    table.addCell(newTableCell(null, smallGrayFont));
                } else {
                    table.addCell(newTableCell(simpleDateFormat.format(position.getEndDate()), smallFont));
                }

                document.add(table);
                document.add(new Paragraph(" "));
            }
        }
    }

    private void addFundingSection(ApplicationForm application, Document document) throws DocumentException, IOException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100f);
        table.addCell(newTableCell("FUNDING", boldFont, BaseColor.GRAY));
        document.add(table);
        document.add(new Paragraph(" "));

        if (application.getFundings().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(100f);
            table.addCell(newTableCell("Funding", smallBoldFont));
            table.addCell(newTableCell(null, smallFont));
            document.add(table);
        } else {
            int counter = 1;
            for (Funding funding : application.getFundings()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(100f);
                PdfPCell headerCell = newTableCell("Funding (" + counter++ + ")", smallBoldFont);
                headerCell.setColspan(2);
                table.addCell(headerCell);
                table.addCell(newTableCell("Funding Type", smallBoldFont));
                table.addCell(newTableCell(funding.getType().getDisplayValue(), smallFont));

                table.addCell(newTableCell("Description", smallBoldFont));
                table.addCell(newTableCell(funding.getDescription(), smallFont));

                table.addCell(newTableCell("Value of Award (GBP)", smallBoldFont));
                table.addCell(newTableCell(funding.getValue(), smallFont));

                table.addCell(newTableCell("Award Date", smallBoldFont));
                table.addCell(newTableCell(simpleDateFormat.format(funding.getAwardDate()), smallFont));

                table.addCell(newTableCell("Proof Of Award", smallBoldFont));

                table.addCell(newTableCell("See APPENDIX(" + appendixCounter + ")", linkFont, appendixCounter));
                bookmarkMap.put(appendixCounter++, funding.getDocument());

                document.add(table);
                document.add(new Paragraph(" "));
            }
        }
    }

    private void addReferencesSection(ApplicationForm application, Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100f);
        table.addCell(newTableCell("REFERENCES", boldFont, BaseColor.GRAY));
        document.add(table);
        document.add(new Paragraph(" "));

        if (application.getReferees().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(100f);
            table.addCell(newTableCell("Reference", smallBoldFont));
            table.addCell(newTableCell(null, smallFont));
            document.add(table);
        } else {
            int counter = 1;
            for (Referee referee : application.getReferees()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(100f);
                PdfPCell headerCell = newTableCell("Reference (" + counter++ + ")", smallBoldFont);
                headerCell.setColspan(2);
                table.addCell(headerCell);
                table.addCell(newTableCell("First Name", smallBoldFont));
                table.addCell(newTableCell(referee.getFirstname(), smallFont));

                table.addCell(newTableCell("Last Name", smallBoldFont));
                table.addCell(newTableCell(referee.getLastname(), smallFont));

                table.addCell(newTableCell("Employer", smallBoldFont));
                table.addCell(newTableCell(referee.getJobEmployer(), smallFont));

                table.addCell(newTableCell("Position", smallBoldFont));
                table.addCell(newTableCell(referee.getJobTitle(), smallFont));

                table.addCell(newTableCell("Address", smallBoldFont));
                table.addCell(newTableCell(referee.getAddressLocation().getLocationString(), smallFont));
                
                table.addCell(newTableCell("Country", smallBoldFont));
                table.addCell(newTableCell(referee.getAddressLocation().getCountry().getName(), smallFont));

                table.addCell(newTableCell("Email", smallBoldFont));
                table.addCell(newTableCell(referee.getEmail(), smallFont));

                table.addCell(newTableCell("Telephone", smallBoldFont));
                table.addCell(newTableCell(referee.getPhoneNumber(), smallFont));

                table.addCell(newTableCell("Skype", smallBoldFont));
                table.addCell(newTableCell(referee.getMessenger(), smallFont));

                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
                    if (!currentUser.isInRole(Authority.APPLICANT) && currentUser.hasAdminRightsOnApplication(application)) {
                        table.addCell(newTableCell("Reference", smallBoldFont));
                        if (referee.getReference() != null) {
                            table.addCell(newTableCell("See APPENDIX(" + appendixCounter + ")", linkFont, appendixCounter));
                            bookmarkMap.put(appendixCounter++, referee.getReference());
                        } else {
                            table.addCell(newTableCell(null, smallFont));
                        }
                    }
                }
                document.add(table);
                document.add(new Paragraph(" "));
            }
        }
    }

    private void addDocumentsSection(ApplicationForm application, Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100f);
        table.addCell(newTableCell("DOCUMENTS", boldFont, BaseColor.GRAY));
        document.add(table);
        document.add(new Paragraph(" "));

        table = new PdfPTable(2);
        table.setWidthPercentage(100f);

        table.addCell(newTableCell("Personal Statement", smallBoldFont));
        if (application.getPersonalStatement() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            table.addCell(newTableCell("See APPENDIX(" + appendixCounter + ")", linkFont, appendixCounter));
            bookmarkMap.put(appendixCounter++, application.getPersonalStatement());

        }

        table.addCell(newTableCell("CV/Resume", smallBoldFont));
        if (application.getCv() == null) {
            table.addCell(newTableCell(null, smallFont));
        } else {
            table.addCell(newTableCell("See APPENDIX(" + appendixCounter + ")", linkFont, appendixCounter));
            bookmarkMap.put(appendixCounter++, application.getCv());
        }
        document.add(table);

    }

    private void addAdditionalInformationSection(ApplicationForm application, Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100f);
        table.addCell(newTableCell("ADDITIONAL INFORMATION", boldFont, BaseColor.GRAY));
        document.add(table);
        document.add(new Paragraph(" "));

        table = new PdfPTable(2);
        table.setWidthPercentage(100f);

        table = new PdfPTable(2);
        table.setWidthPercentage(100f);
        table.addCell(newTableCell("Do you have any unspent Criminial Convictions?", smallBoldFont));
        if (application.getAdditionalInformation().getConvictions() == null) {
            table.addCell(newTableCell(NOT_PROVIDED, smallGrayFont));
        } else if (BooleanUtils.isTrue(application.getAdditionalInformation().getConvictions())) {
            table.addCell(newTableCell("Yes", smallFont));
        } else {
            table.addCell(newTableCell("No", smallFont));
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
            if (currentUser.isInRole(Authority.SUPERADMINISTRATOR) || currentUser.getId().equals(application.getApplicant().getId())) {
                table.addCell(newTableCell("Description", smallBoldFont));
                table.addCell(newTableCell(application.getAdditionalInformation().getConvictionsText(), smallFont));
            }
        }
        document.add(table);
    }

    private void addSupportingDocuments(ApplicationForm application, Document document, PdfWriter writer, boolean includeAttachments) throws DocumentException, MalformedURLException, IOException {
        if (!includeAttachments) {
            return;
        }
        
        for (Integer integer : bookmarkMap.keySet()) {
            
            document.newPage();
            
            headerEvent.setAddHeaderAndFooter(true);
            
            Object obj = bookmarkMap.get(integer);
            if (obj instanceof com.zuehlke.pgadmissions.domain.Document) {
                com.zuehlke.pgadmissions.domain.Document doc = (com.zuehlke.pgadmissions.domain.Document) obj;
                if (doc != null) {
                    document.add(new Chunk("APPENDIX (" + integer + ")").setLocalDestination(integer.toString()));
                    if (DocumentType.PERSONAL_STATEMENT == doc.getType()) {
                        document.add(new Chunk(" - Personal Statement"));
                    } else if (DocumentType.CV == doc.getType()) {
                        document.add(new Chunk(" - CV"));
                    } else if (DocumentType.SUPPORTING_FUNDING == doc.getType()) {
                        document.add(new Chunk(" - Funding proof of award"));
                    } else if (DocumentType.PROOF_OF_AWARD == doc.getType()) {
                        document.add(new Chunk(" - Qualification Transcript"));
                    }
                    
                    try {
                        readPdf(document, doc, writer);
                    } catch (Exception e) {
                        LOG.warn(String.format("Error in generating pdf while appending supporting document %s for %s", application.getApplicationNumber(), doc.getFileName()), e);
                    }
                }
            } else if (obj instanceof ReferenceComment) {
                ReferenceComment reference = (ReferenceComment) obj;
                document.add(new Chunk("APPENDIX (" + integer + ")").setLocalDestination(integer.toString()));
                
                document.add(new Chunk(" - Reference"));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                PdfPTable table = new PdfPTable(1);
                table.setWidthPercentage(100f);
                table.addCell(newTableCell("REFERENCE", boldFont, BaseColor.GRAY));
                document.add(table);
                document.add(new Paragraph(" "));
                table = new PdfPTable(2);
                table.setWidthPercentage(100f);
                table.addCell(newTableCell("Referee", smallBoldFont));
                table.addCell(newTableCell(reference.getReferee().getFirstname() + " " + reference.getReferee().getLastname(), smallFont));
                table.addCell(newTableCell("Comment", smallBoldFont));
                table.addCell(newTableCell(reference.getComment(), smallFont));
                table.addCell(newTableCell("Is the applicant suitable for postgraduate study at UCL?", smallBoldFont));
                if (BooleanUtils.isTrue(reference.getSuitableForUCL())) {
                    table.addCell(newTableCell("Yes", smallFont));
                } else {
                    table.addCell(newTableCell("No", smallFont));
                }
                table.addCell(newTableCell("Is the applicant suitable for their chosen postgraduate study programme?", smallBoldFont));
                if (BooleanUtils.isTrue(reference.getSuitableForProgramme())) {
                    table.addCell(newTableCell("Yes", smallFont));
                } else {
                    table.addCell(newTableCell("No", smallFont));
                }
                document.add(table);
                for (com.zuehlke.pgadmissions.domain.Document refDocument : reference.getDocuments()) {
                    try {
                        readPdf(document, refDocument, writer);
                    } catch (Exception e) {
                        LOG.warn(String.format("Error in generating pdf while appending supporting document %s for %s",
                                application.getApplicationNumber(), refDocument.getFileName()), e);
                    }
                }
            }
        }
    }

    private void readPdf(Document document, com.zuehlke.pgadmissions.domain.Document doc, PdfWriter writer) throws Exception {
        PdfReader pdfReader = new PdfReader(doc.getContent());
        PdfContentByte cb = writer.getDirectContent();
        for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
            PdfImportedPage page = writer.getImportedPage(pdfReader, i);
            document.setPageSize(new Rectangle(page.getWidth(), page.getHeight()));
            document.newPage();
            headerEvent.setAddHeaderAndFooter(false);
            cb.addTemplate(page, 0, 0);
            document.setPageSize(PageSize.A4);
        }
    }

    private class HeaderEvent extends PdfPageEventHelper {
        private final Chunk programmeHeader;
        private final Chunk applicationHeader;
        private final Chunk submittedDateHeader;
        private boolean addHeaderAndFooter = true;
        private boolean first  = true;
        
        public HeaderEvent(Chunk programmeHeader, Chunk applicationHeader, Chunk submittedDateHeader) {
            this.programmeHeader = programmeHeader;
            this.applicationHeader = applicationHeader;
            this.submittedDateHeader = submittedDateHeader;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            if(first){
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
            Phrase footerPhrase = new Phrase("Page " + (1 + document.getPageNumber() - pageCounter), smallerFont);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, footerPhrase, document.left(), document.bottom() - 25f, 0);
        }

        private void addHeaderToPage(PdfWriter writer, Document document) throws DocumentException, BadElementException, MalformedURLException, IOException {
            PdfPTable table = new PdfPTable(2);
            table.setTotalWidth(0.75f * document.getPageSize().getWidth());
            table.setWidths(new float[] { 25f, 75f });

            PdfPCell c1 = new PdfPCell(new Phrase("Programme", smallerBoldFont));
            c1.setBorder(0);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(programmeHeader));
            c1.setBorder(0);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Application Number", smallerBoldFont));
            c1.setBorder(0);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(applicationHeader));
            c1.setBorder(0);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Submitted", smallerBoldFont));
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
