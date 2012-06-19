package com.zuehlke.pgadmissions.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.PDFException;

@Component
public class PdfDocumentBuilder {

	private Font grayFont = new Font(FontFamily.HELVETICA, 14, Font.BOLD | Font.UNDERLINE, BaseColor.DARK_GRAY);
	private static Font boldFont = new Font(FontFamily.HELVETICA, 14, Font.BOLD);
	private static Font smallBoldFont = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
	private static Font smallFont = new Font(FontFamily.HELVETICA, 12, Font.NORMAL);
	private static Font smallGrayFont = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.LIGHT_GRAY);
	private static Font smallerBoldFont = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
	private static Font smallerFont = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);

	private final BaseColor grayColor = new BaseColor(220, 220, 220);
	private Chunk programmeHeader;
	private Chunk applicationHeader;
	private Chunk submittedDateHeader;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");

	public byte[] buildPdf(ApplicationForm... applications) {
		try {
			Document document = new Document(PageSize.A4, 50, 50, 100, 50);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, baos);
			document.open();
			for (ApplicationForm applicationForm : applications) {
				buildDocument(applicationForm, document, writer);
				document.newPage();
			}
			document.close();
			return baos.toByteArray();
		} catch (Exception e) {
			throw new PDFException(e);
		}
	}

	private void buildDocument(ApplicationForm application, Document document, PdfWriter writer) throws DocumentException, MalformedURLException, IOException {

		programmeHeader = new Chunk(application.getProgram().getTitle(), smallerFont);
		applicationHeader = new Chunk(application.getApplicationNumber(), smallerFont);
		if(application.getSubmittedDate() != null){
			submittedDateHeader = new Chunk(new SimpleDateFormat("dd MMMM yyyy").format(application.getSubmittedDate()), smallerFont);
		}else{
			submittedDateHeader = new Chunk("", smallerFont);
		}

		writer.setPageEvent(new HeaderEvent());

		addProgrammeSection(application, document);

		addSectionSeparators(document);

		addPersonalDetailsSection(application, document);

		addSectionSeparators(document);

		addAddressSection(application, document);

		addSectionSeparators(document);

		addQualificationSection(application, document, writer);

		addSectionSeparators(document);

		addEmploymentSection(application, document);

		addSectionSeparators(document);

		addFundingSection(application, document, writer);

		addSectionSeparators(document);

		addReferencesSection(application, document);

		addSectionSeparators(document);

		addAdditionalInformationSection(application, document);

		addSectionSeparators(document);

		addSupportingDocuments(application, document, writer);

		addSectionSeparators(document);
		RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if (!currentUser.isInRole(Authority.APPLICANT) && !currentUser.isRefereeOfApplicationForm(application)) {
			addUploadedReferences(application, document, writer);
		}

	}

	private void addSectionSeparators(Document document) throws DocumentException {
		document.add(new Paragraph(" "));
	}

	private void addCorrectOutputDependingOnNull(Document document, String fieldValue, String fieldLabel) throws DocumentException {
		if (StringUtils.isBlank(fieldValue)) {
			document.add(new Paragraph(createMessage(fieldLabel.toLowerCase())));
		} else {
			document.add(new Paragraph(fieldLabel + ": " + fieldValue));
		}
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
		if(application.getProgrammeDetails().getStudyOption() != null){
			table.addCell(newTableCell(application.getProgrammeDetails().getStudyOption().displayValue(), smallFont));
		}else{
			table.addCell(newTableCell("Not Provided", smallGrayFont));
		}
				
		table.addCell(newTableCell("Project", smallBoldFont));
		if(!StringUtils.isBlank(application.getProgrammeDetails().getProjectName())){
			table.addCell(newTableCell(application.getProgrammeDetails().getProjectName(), smallFont));			
		}else{
			table.addCell(newTableCell("Not Provided", smallGrayFont));
		}
				
		table.addCell(newTableCell("Start Date", smallBoldFont));	
		if(application.getProgrammeDetails().getStartDate() != null){
			table.addCell(newTableCell(simpleDateFormat.format(application.getProgrammeDetails().getStartDate()), smallFont));
		}else{
			table.addCell(newTableCell("Not Provided", smallGrayFont));
		}
		
		table.addCell(newTableCell("How did you find us?", smallBoldFont));
		if(application.getProgrammeDetails().getReferrer() != null){
			table.addCell(newTableCell(application.getProgrammeDetails().getReferrer().displayValue(), smallFont));
		}else{
			table.addCell(newTableCell("Not Provided", smallGrayFont));
		}
		
		document.add(table);
		document.add(new Paragraph(" "));

		if (application.getProgrammeDetails().getSuggestedSupervisors().isEmpty()) {
			table = new PdfPTable(2);	
			table.setWidthPercentage(100f);
			table.addCell(newTableCell("Supervisor", smallBoldFont));
			table.addCell(newTableCell("Not Provided", smallGrayFont));
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
			
				if (supervisor.isAware()) {
					table.addCell(newTableCell("Yes", smallerFont));			
				} else {
					table.addCell(newTableCell("No", smallerFont));
				}
				document.add(table);
				document.add(new Paragraph(" "));

			}

			
		}
	}

	private PdfPCell newTableCell(String content , Font font) {

		PdfPCell cell =null;
		if(StringUtils.isNotBlank(content)){
			cell = new PdfPCell(new Phrase(content, font));
		}else{
			cell = new PdfPCell(new Phrase("Not Provided", smallGrayFont));
		}
		cell.setPaddingBottom(5);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	
		return cell;
	}
	
	private PdfPCell newTableCell(String content , Font font, BaseColor backgrounColor) {

		PdfPCell c1 = newTableCell(content, font);
		c1.setBackgroundColor(grayColor);
		return c1;
	}
	private void addPersonalDetailsSection(ApplicationForm application, Document document) throws DocumentException {
		PdfPTable table = new PdfPTable(1);	
		table.setWidthPercentage(100f);
		table.addCell(newTableCell("PERSONAL DETAILS", boldFont, BaseColor.GRAY));
		document.add(table);
		document.add(new Paragraph(" "));

		table = new PdfPTable(2);		
		table.setWidthPercentage(100f);		
		table.addCell(newTableCell("First Name", smallBoldFont));
		table.addCell(newTableCell(application.getPersonalDetails().getFirstName(), smallFont));
		
		table.addCell(newTableCell("Last Name", smallBoldFont));
		table.addCell(newTableCell(application.getPersonalDetails().getLastName(), smallFont));

		table.addCell(newTableCell("Gender", smallBoldFont));
		if (application.getPersonalDetails().getGender() == null) {
			table.addCell(newTableCell(null, smallFont));
		} else {
			table.addCell(newTableCell( application.getPersonalDetails().getGender().getDisplayValue(), smallFont));
		}
		
		table.addCell(newTableCell("Date of Birth", smallBoldFont));
		if (application.getPersonalDetails().getDateOfBirth() == null) {
			table.addCell(newTableCell(null, smallFont));
		} else {
			table.addCell(newTableCell(simpleDateFormat.format(application.getPersonalDetails().getDateOfBirth()), smallFont));
		}
		table.addCell(newTableCell("Country of Birth", smallBoldFont));
		if(application.getPersonalDetails().getCountry() == null){
			table.addCell(newTableCell(null, smallFont));
		}else{
			table.addCell(newTableCell(application.getPersonalDetails().getCountry().getName(), smallFont));	
		}
		
		table.addCell(newTableCell("Nationality", smallBoldFont));
		StringBuilder sb = new StringBuilder();
		for (Country country : application.getPersonalDetails().getCandidateNationalities()) {
			if(sb.length() > 0){
				sb.append(", ");				
			}
			sb.append(country.getName());
		}
		table.addCell(newTableCell(sb.toString(), smallFont));
		
		table.addCell(newTableCell("Mother's Nationality", smallBoldFont));
		sb = new StringBuilder();
		for (Country country : application.getPersonalDetails().getMaternalGuardianNationalities()) {
			if(sb.length() > 0){
				sb.append(", ");				
			}
			sb.append(country.getName());
		}
		table.addCell(newTableCell(sb.toString(), smallFont));
		
		table.addCell(newTableCell("Father's Nationality", smallBoldFont));
		sb = new StringBuilder();
		for (Country country : application.getPersonalDetails().getPaternalGuardianNationalities()) {
			if(sb.length() > 0){
				sb.append(", ");				
			}
			sb.append(country.getName());
		}
		table.addCell(newTableCell(sb.toString(), smallFont));
		
		table.addCell(newTableCell("Is English your first language?", smallBoldFont));
		if(application.getPersonalDetails().getEnglishFirstLanguage() == null){
			table.addCell(newTableCell(null, smallFont));	
		}else{
			if(application.getPersonalDetails().getEnglishFirstLanguage()){
				table.addCell(newTableCell("Yes", smallFont));
			}else{
				table.addCell(newTableCell("No", smallFont));
			}
		}

		table.addCell(newTableCell("Country of Residence", smallBoldFont));
		if(application.getPersonalDetails().getResidenceCountry() == null){
			table.addCell(newTableCell(null, smallFont));
		}else{
			table.addCell(newTableCell(application.getPersonalDetails().getResidenceCountry().getName(), smallFont));	
		}
		
		table.addCell(newTableCell("Do you Require a Visa to Study in the UK?", smallBoldFont));
		if(application.getPersonalDetails().getRequiresVisa() == null){
			table.addCell(newTableCell(null, smallFont));	
		}else{
			if(application.getPersonalDetails().getRequiresVisa()){
				table.addCell(newTableCell("Yes", smallFont));
			}else{
				table.addCell(newTableCell("No", smallFont));
			}
		}

		
		table.addCell(newTableCell("Email", smallBoldFont));
		table.addCell(newTableCell(application.getPersonalDetails().getEmail(), smallFont));

		table.addCell(newTableCell("Telephone", smallBoldFont));
		table.addCell(newTableCell(application.getPersonalDetails().getPhoneNumber(), smallFont));
		
		table.addCell(newTableCell("Skype", smallBoldFont));
		table.addCell(newTableCell(application.getPersonalDetails().getMessenger(), smallFont));
		document.add(table);
	}


	private void addAddressSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Address                                                                                                ", grayFont));

		Address currentAddress = application.getCurrentAddress();
		if (currentAddress != null) {
			document.add(new Paragraph("Current Address", smallBoldFont));
			document.add(new Paragraph("Address: " + currentAddress.getLocation()));
			document.add(new Paragraph("Country: " + currentAddress.getCountry().getName()));

			document.add(new Paragraph(" "));
		}

		Address contactAddress = application.getContactAddress();
		if (contactAddress != null) {
			document.add(new Paragraph("Contact Address", smallBoldFont));
			document.add(new Paragraph("Address: " + contactAddress.getLocation()));
			document.add(new Paragraph("Country: " + contactAddress.getCountry().getName()));

			document.add(new Paragraph(" "));
		}

	}

	private void addQualificationSection(ApplicationForm application, Document document, PdfWriter writer) throws DocumentException, IOException {
		document.add(new Paragraph("Qualification                                                                                         ", grayFont));
		if (application.getQualifications().isEmpty()) {
			document.add(new Paragraph(createMessage("qualification information")));
		} else {

			for (Qualification qualification : application.getQualifications()) {
				document.add(new Paragraph("Institution Country: " + qualification.getInstitutionCountry().getName()));
				document.add(new Paragraph("Institution / Provider Name: " + qualification.getQualificationInstitution()));
				document.add(new Paragraph("Qualification Type: " + qualification.getQualificationType()));
				document.add(new Paragraph("Title / Subject: " + qualification.getQualificationSubject()));
				document.add(new Paragraph("Language of Study: " + qualification.getQualificationLanguage().getName()));
				document.add(new Paragraph("Start Date: " + qualification.getQualificationStartDate().toString()));
				document.add(new Paragraph("Has this qualification been awarded? " + qualification.getCompleted().displayValue()));
				document.add(new Paragraph("Grade / Result / GPA: " + qualification.getQualificationGrade()));
				if (qualification.getQualificationAwardDate() != null) {
					document.add(new Paragraph("Award Date: " + qualification.getQualificationAwardDate().toString()));
				}

				if (qualification.getProofOfAward() != null) {
					document.newPage();
					document.add(new Paragraph("Proof of award(PDF)", smallBoldFont));
					readPdf(document, qualification.getProofOfAward(), writer);
					document.newPage();
				}

				document.add(new Paragraph(" "));
			}
		}

	}

	private void addEmploymentSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Employment                                                                                         ", grayFont));
		if (application.getEmploymentPositions().isEmpty()) {
			document.add(new Paragraph(createMessage("employment information")));
		} else {
			for (EmploymentPosition employment : application.getEmploymentPositions()) {
				document.add(new Paragraph("Country: " + employment.getEmployerCountry().getName()));
				document.add(new Paragraph("Employer Name: " + employment.getEmployerName()));
				document.add(new Paragraph("Employer Address: " + employment.getEmployerAddress()));
				document.add(new Paragraph("Position: " + employment.getPosition()));
				document.add(new Paragraph("Roles and Resposibilities: " + employment.getRemit()));
				document.add(new Paragraph("Language of work: " + employment.getLanguage().getName()));
				document.add(new Paragraph("Start Date: " + employment.getStartDate().toString()));
				if (employment.isCurrent()) {
					document.add(new Paragraph("Is this your current position? yes."));
				} else {
					document.add(new Paragraph("Is this your current position? no."));
				}
				if (employment.getEndDate() == null) {
					document.add(new Paragraph(createMessage("end date")));
				} else {
					document.add(new Paragraph("End Date: " + employment.getEndDate().toString()));
				}

				document.add(new Paragraph(" "));
			}
		}

	}

	private void addFundingSection(ApplicationForm application, Document document, PdfWriter writer) throws DocumentException, IOException {
		document.add(new Paragraph("Funding                                                                                                ", grayFont));

		if (application.getFundings().isEmpty()) {
			document.add(new Paragraph(createMessage("funding information")));
		} else {

			for (Funding funding : application.getFundings()) {
				document.add(new Paragraph("Funding Type: " + funding.getType().getDisplayValue()));
				document.add(new Paragraph("Description:" + funding.getDescription()));
				document.add(new Paragraph("Value of Award: " + funding.getValue()));
				document.add(new Paragraph("Award Date: " + funding.getAwardDate().toString()));

				document.newPage();
				document.add(new Paragraph("Proof of award(PDF)", smallBoldFont));
				readPdf(document, funding.getDocument(), writer);
				document.newPage();

				document.add(new Paragraph(" "));
			}
		}

	}

	private void addReferencesSection(ApplicationForm application, Document document) throws DocumentException {

		document.add(new Paragraph("References                                                                                           ", grayFont));

		if (application.getReferees().isEmpty()) {
			document.add(new Paragraph(createMessage("references information")));
		} else {

			for (Referee reference : application.getReferees()) {
				document.add(new Paragraph("First Name: " + reference.getFirstname()));
				document.add(new Paragraph("Last Name: " + reference.getLastname()));

				addCorrectOutputDependingOnNull(document, reference.getJobEmployer(), "Employer");
				addCorrectOutputDependingOnNull(document, reference.getJobTitle(), "Position");

				document.add(new Paragraph("Contact Details", smallBoldFont));
				addCorrectOutputDependingOnNull(document, reference.getAddressLocation(), "Address");

				if (reference.getAddressCountry() != null) {
					document.add(new Paragraph("Country: " + reference.getAddressCountry().getName()));
				} else {
					document.add(new Paragraph(createMessage("country")));
				}

				document.add(new Paragraph("Contact Details", smallBoldFont));
				document.add(new Paragraph("Email: " + reference.getEmail()));
				document.add(new Paragraph("Telephone: " + reference.getPhoneNumber()));
				addCorrectOutputDependingOnNull(document, reference.getMessenger(), "Skype Name");
				if (reference.isDeclined()) {
					document.add(new Paragraph("This referee has declined"));
				}
				document.add(new Paragraph(" "));
			}
		}

	}

	private void addAdditionalInformationSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Additional Information                                                                        ", grayFont));
		AdditionalInformation addInfo = application.getAdditionalInformation();
		if (addInfo != null) {
			document.add(new Paragraph(addInfo.getInformationText()));
			if (addInfo.getConvictions() != null && addInfo.getConvictions()) {
				document.add(new Paragraph("Convictions", smallBoldFont));
				document.add(new Paragraph("Details of convictions: " + addInfo.getConvictionsText()));
			}
		} else {
			document.add(new Paragraph(createMessage("additional information")));
		}
	}

	private void addSupportingDocuments(ApplicationForm application, Document document, PdfWriter writer) throws DocumentException, MalformedURLException,
			IOException {

		com.zuehlke.pgadmissions.domain.Document doc = application.getCv();
		if (doc != null) {
			document.newPage();
			document.add(new Paragraph(doc.getType().getDisplayValue(), smallBoldFont));
			if (doc.getFileName().endsWith(".jpg") || doc.getFileName().endsWith("bmp") || doc.getFileName().endsWith("jpeg")
					|| doc.getFileName().endsWith("png") || doc.getFileName().endsWith(".tiff") || doc.getFileName().endsWith(".tif")) {
				Image image = Image.getInstance(doc.getContent());
				document.add(image);
			} else if (doc.getFileName().endsWith(".txt")) {
				String content = new String(doc.getContent());
				document.add(new Chunk(content));
			} else if (doc.getFileName().endsWith(".pdf")) {
				readPdf(document, doc, writer);
			}
		}
		doc = application.getPersonalStatement();
		if (doc != null) {
			document.newPage();
			document.add(new Paragraph(doc.getType().getDisplayValue(), smallBoldFont));
			if (doc.getFileName().endsWith(".jpg") || doc.getFileName().endsWith("bmp") || doc.getFileName().endsWith("jpeg")
					|| doc.getFileName().endsWith("png") || doc.getFileName().endsWith(".tiff") || doc.getFileName().endsWith(".tif")) {
				Image image = Image.getInstance(doc.getContent());
				document.add(image);
			} else if (doc.getFileName().endsWith(".txt")) {
				String content = new String(doc.getContent());
				document.add(new Chunk(content));
			} else if (doc.getFileName().endsWith(".pdf")) {
				readPdf(document, doc, writer);
			}
		}
	}

	private void addUploadedReferences(ApplicationForm application, Document document, PdfWriter writer) throws IOException, DocumentException {
		for (Referee referee : application.getReferees()) {
			if (referee.getReference() != null) {
				document.newPage();
				document.add(new Paragraph("Reference from " + referee.getFirstname() + " " + referee.getLastname(), boldFont));
				readPdf(document, referee.getReference().getDocument(), writer);
			}
		}
	}

	private void readPdf(Document document, com.zuehlke.pgadmissions.domain.Document doc, PdfWriter writer) throws IOException {
		PdfReader pdfReader = new PdfReader(doc.getContent());
		PdfContentByte cb = writer.getDirectContent();
		for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
			document.newPage();
			PdfImportedPage page = writer.getImportedPage(pdfReader, i);
			cb.addTemplate(page, 0, 0);
		}
	}

	private String createMessage(String fieldName) {
		return "No " + fieldName + " has been specified.";
	}

	private class HeaderEvent extends PdfPageEventHelper {
		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			try {
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

				table.writeSelectedRows(0, -1, document.left(), document.top() + 50f, writer.getDirectContent());

				Image image = Image.getInstance(this.getClass().getResource("/logo.jpg"));
				image.scalePercent(30f);

				image.setAbsolutePosition(document.right() - image.getWidth() * 0.3f, document.top());
				document.add(image);
				LineSeparator lineSeparator = new LineSeparator();
				lineSeparator.drawLine(writer.getDirectContent(), document.left(), document.right(), document.top() + 5f);

				lineSeparator.drawLine(writer.getDirectContent(), document.left(), document.right(), document.bottom() - 5f);
				Phrase footerPhrase = new Phrase("Page " + document.getPageNumber(), smallerFont);
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, footerPhrase, document.left(), document.bottom() - 15f, 0);
			} catch (DocumentException e) {

				throw new RuntimeException(e);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
