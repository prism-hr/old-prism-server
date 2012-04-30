package com.zuehlke.pgadmissions.utils;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class PdfDocumentBuilder {

	private Font grayFont = new Font(FontFamily.HELVETICA, 16, Font.BOLD | Font.UNDERLINE, BaseColor.DARK_GRAY);
	private static Font boldFont = new Font(FontFamily.HELVETICA, 16, Font.BOLD);
	private static Font smallBoldFont = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
	private static Font smallerBoldFont = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
	private final PdfWriter writer;
	private final BaseColor grayColor = new BaseColor(220, 220, 220);

	public PdfDocumentBuilder(PdfWriter writer) {
		this.writer = writer;
	}

	public void buildDocument(ApplicationForm application, Document document) throws DocumentException, MalformedURLException, IOException {

		document.add(new Paragraph("Application id: " + application.getId(), boldFont));
		document.add(new Paragraph("Applicant: " + application.getApplicant().getFirstName() + " " + application.getApplicant().getLastName(), boldFont));

		addSectionSeparators(document);

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

		addReferencesSection(application, document);

		addSectionSeparators(document);

		addAdditionalInformationSection(application, document);

		addSectionSeparators(document);

		addSupportingDocuments(application, document);

		addSectionSeparators(document);
		RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if (!currentUser.isInRole(Authority.APPLICANT) && !currentUser.isRefereeOfApplicationForm(application)) {
			addUploadedReferences(application, document);
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
		document.add(new Paragraph("Programme                                                                                          ", grayFont));
		document.add(new Paragraph("Programme: " + application.getProgram().getTitle()));

		if (application.getProgrammeDetails().getStudyOption() == null) {
			document.add(new Paragraph(createMessage("study option")));
		} else {
			document.add(new Paragraph("Study Option: " + application.getProgrammeDetails().getStudyOption().displayValue()));
		}

		addCorrectOutputDependingOnNull(document, application.getProjectTitle(), "Project");

		if (application.getProgrammeDetails().getStartDate() == null) {
			document.add(new Paragraph(createMessage("start date")));
		} else {
			document.add(new Paragraph("Start Date: " + application.getProgrammeDetails().getStartDate().toString()));
		}

		if (application.getProgrammeDetails().getReferrer() == null) {
			document.add(new Paragraph(createMessage("referrer")));
		} else {
			document.add(new Paragraph("How did you find us? " + application.getProgrammeDetails().getReferrer().displayValue()));
		}

		if (application.getProgrammeDetails().getSupervisors().isEmpty()) {
			document.add(new Paragraph(createMessage("supervisors information")));
		} else {
			document.add(new Paragraph("Supervisors", smallBoldFont));
			document.add(new Paragraph(" "));

			PdfPTable table = new PdfPTable(4);
			table.setWidthPercentage(100.0f);

			PdfPCell c1 = new PdfPCell(new Phrase("Supervisor First Name", smallerBoldFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(grayColor);
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("Supervisor Last Name", smallerBoldFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(grayColor);
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("Supervisor Email", smallerBoldFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(grayColor);
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("Is this supervisor aware of your application?", smallerBoldFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(grayColor);
			table.addCell(c1);
			table.setHeaderRows(1);

			for (Supervisor supervisor : application.getProgrammeDetails().getSupervisors()) {
				table.addCell(supervisor.getFirstname());
				table.addCell(supervisor.getLastname());
				table.addCell(supervisor.getEmail());
				table.addCell(supervisor.getAwareSupervisor().displayValue());
			}

			document.add(table);
		}
	}

	private void addPersonalDetailsSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Personal Details                                                                                   ", grayFont));
		addCorrectOutputDependingOnNull(document, application.getPersonalDetails().getFirstName(), "First Name");
		addCorrectOutputDependingOnNull(document, application.getPersonalDetails().getLastName(), "Last Name");

		if (application.getPersonalDetails().getGender() == null) {
			document.add(new Paragraph(createMessage("gender")));
		} else {
			document.add(new Paragraph("Gender: " + application.getPersonalDetails().getGender().getDisplayValue()));
		}

		if (application.getPersonalDetails().getDateOfBirth() == null) {
			document.add(new Paragraph(createMessage("date of birth")));
		} else {
			document.add(new Paragraph("Date of Birth: " + application.getPersonalDetails().getDateOfBirth().toString()));
		}

		document.add(new Paragraph("Nationality", smallBoldFont));
		if (application.getPersonalDetails().getCountry() == null) {
			document.add(new Paragraph(createMessage("country of birth")));
		} else {
			document.add(new Paragraph("Country of Birth: " + application.getPersonalDetails().getCountry().getName()));
		}

		addGivenNationality(document, "My Nationality", application.getPersonalDetails().getCandidateNationalities());
		addGivenNationality(document, "Maternal Guardian Nationality", application.getPersonalDetails().getMaternalGuardianNationalities());
		addGivenNationality(document, "Paternal Guardian Nationality", application.getPersonalDetails().getPaternalGuardianNationalities());

		document.add(new Paragraph("Language", smallBoldFont));
		if (application.getPersonalDetails().isEnglishFirstLanguage()) {
			document.add(new Paragraph("Is English your first language? yes."));
		} else {
			document.add(new Paragraph("Is English your first language? no."));
		}
		document.add(new Paragraph("Residence", smallBoldFont));

		if (application.getPersonalDetails().getResidenceCountry() == null) {
			document.add(new Paragraph(createMessage("country of residence")));
		} else {
			document.add(new Paragraph("Country of Residence: " + application.getPersonalDetails().getResidenceCountry().getName()));
		}

		if (application.getPersonalDetails().isRequiresVisa()) {
			document.add(new Paragraph("Do you require a visa to study in the UK? yes."));
		} else {
			document.add(new Paragraph("Do you require a visa to study in the UK? no."));
		}

		document.add(new Paragraph("Contact Details", smallBoldFont));
		addCorrectOutputDependingOnNull(document, application.getPersonalDetails().getEmail(), "Email");
		addCorrectOutputDependingOnNull(document, application.getPersonalDetails().getPhoneNumber(), "Telephone");
		addCorrectOutputDependingOnNull(document, application.getPersonalDetails().getMessenger(), "Skype Name");

		document.add(new Paragraph("Equal Opportunities Details", smallBoldFont));
		if (application.getPersonalDetails().getEthnicity() == null) {
			document.add(new Paragraph(createMessage("ethnicity")));
		} else {
			document.add(new Paragraph("Ethnicity: " + application.getPersonalDetails().getEthnicity().getName()));
		}
		if (application.getPersonalDetails().getDisability() == null) {
			document.add(new Paragraph(createMessage("disability")));
		} else {
			document.add(new Paragraph("Disability: " + application.getPersonalDetails().getDisability().getName()));
		}
	}

	private void addGivenNationality(Document document, String header, java.util.List<Country> nationalities) throws DocumentException {
		document.add(new Paragraph(header, smallBoldFont));
		if (nationalities.size() > 0) {
			document.add(new Paragraph(" "));

			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage(100.0f);

			PdfPCell c1 = new PdfPCell(new Phrase("Nationality", smallerBoldFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(grayColor);
			table.addCell(c1);

			for (Country nationality : nationalities) {
				table.addCell(nationality.getName());
			}

			document.add(table);
		} else {
			document.add(new Paragraph(createMessage("nationality")));
		}

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

	private void addQualificationSection(ApplicationForm application, Document document) throws DocumentException, IOException {
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
					readPdf(document, qualification.getProofOfAward());
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

	private void addFundingSection(ApplicationForm application, Document document) throws DocumentException, IOException {
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
				readPdf(document, funding.getDocument());
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
				if(reference.isDeclined()){
					document.add(new Paragraph("This referee has declined"));
				}
				document.add(new Paragraph(" "));
			}
		}

	}

	private void addAdditionalInformationSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Additional Information                                                                        ", grayFont));
		if (application.getAdditionalInformation() != null) {
			document.add(new Paragraph(application.getAdditionalInformation().getInformationText()));
		} else {
			document.add(new Paragraph(createMessage("additional information")));
		}
	}

	private void addSupportingDocuments(ApplicationForm application, Document document) throws DocumentException, MalformedURLException, IOException {

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
				readPdf(document, doc);
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
				readPdf(document, doc);
			}
		}
	}

	private void addUploadedReferences(ApplicationForm application, Document document) throws IOException, DocumentException {
		for (Referee referee : application.getReferees()) {
			if (referee.getReference() != null) {
				document.newPage();
				document.add(new Paragraph("Reference from " + referee.getFirstname() + " " + referee.getLastname(), boldFont));
				readPdf(document, referee.getReference().getDocument());
			}
		}
	}

	private void readPdf(Document document, com.zuehlke.pgadmissions.domain.Document doc) throws IOException {
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
}
