package com.zuehlke.pgadmissions.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.LanguageProficiency;
import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.Telephone;

public class PdfDocumentBuilder {

	private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.RED);
	private static Font purpleFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.MAGENTA);
	private Font greyFont  = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.DARK_GRAY);
	private static Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	private static Font smallBoldFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

	public void buildDocument(ApplicationForm application, Document document) throws DocumentException {

		document.add(new Paragraph("Application id: "+application.getId(), boldFont));
		document.add(new Paragraph("Applicant: "+application.getApplicant().getFirstName()+ " " + application.getApplicant().getLastName(), boldFont));

		addSectionSeparators(document);

		addProgrammeSection(application, document);

		addSectionSeparators(document);

		addPersonalDetailsSection(application, document);
		
		addSectionSeparators(document);
		
		addAddressSection(application, document);
	}

	private void addSectionSeparators(Document document) throws DocumentException {
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
	}

	private void addProgrammeSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Programme", purpleFont));
		document.add(new Paragraph("Programme: "+application.getProject().getProgram().getTitle()));

		if (application.getProgrammeDetails().getStudyOption() == null) {
			document.add(new Paragraph(createMessage("study option")));
		} else {
			document.add(new Paragraph("Study Option: "+application.getProgrammeDetails().getStudyOption().displayValue()));
		}

		document.add(new Paragraph("Project: "+application.getProject().getTitle()));

		if (application.getProgrammeDetails().getStartDate() == null) {
			document.add(new Paragraph(createMessage("start date")));
		} else {
			document.add(new Paragraph("Start Date: "+ application.getProgrammeDetails().getStartDate().toString()));
		}

		if (application.getProgrammeDetails().getReferrer() == null) {
			document.add(new Paragraph(createMessage("referrer")));
		} else {
			document.add(new Paragraph("Referrer: "+application.getProgrammeDetails().getReferrer().displayValue()));
		}

		document.add(new Paragraph(" "));

		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage (100.0f);

		PdfPCell c1 = new PdfPCell(new Phrase("Supervisor Email"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Is primary supervisor?"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Is supervisor aware of your application?"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		table.setHeaderRows(1);

		for (Supervisor supervisor : application.getProgrammeDetails().getSupervisors()) {
			table.addCell(supervisor.getEmail());
			table.addCell(supervisor.getPrimarySupervisor().displayValue());
			table.addCell(supervisor.getAwareSupervisor().displayValue());
		}

		document.add(table);
	}

	private void addPersonalDetailsSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Personal Details", greyFont));
		document.add(new Paragraph("First Name: " + application.getPersonalDetails().getFirstName()));
		document.add(new Paragraph("Last Name: " + application.getPersonalDetails().getLastName()));

		if (application.getPersonalDetails().getGender() == null) {
			document.add(new Paragraph(createMessage("gender")));
		} else {
			document.add(new Paragraph("Gender: "+ application.getPersonalDetails().getGender().getDisplayValue()));
		}

		if (application.getPersonalDetails().getDateOfBirth() == null) {
			document.add(new Paragraph(createMessage("date of birth")));
		} else {
			document.add(new Paragraph("Date of Birth: "+ application.getPersonalDetails().getDateOfBirth().toString()));
		}

		if (application.getPersonalDetails().getCountry() == null) {
			document.add(new Paragraph(createMessage("country of birth")));
		} else {
			document.add(new Paragraph("Country of Birth: "+ application.getPersonalDetails().getCountry().getName()));
		}

		PdfPTable table;
		PdfPCell c1;

		addGivenNationality(application, document, "Nationality", application.getPersonalDetails().getCandidateNationalities());
		addGivenNationality(application, document, "Maternal Guardian Nationality", application.getPersonalDetails().getMaternalGuardianNationalities());
		addGivenNationality(application, document, "Paternal Guardian Nationality", application.getPersonalDetails().getPaternalGuardianNationalities());

		document.add(new Paragraph("Language", smallBoldFont));
		if (application.getPersonalDetails().getLanguageProficiencies().size() > 0) {
			document.add(new Paragraph(" "));
		}

		table = new PdfPTable(2);
		table.setWidthPercentage (100.0f);

		c1 = new PdfPCell(new Phrase("Language"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Aptitude"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		table.setHeaderRows(1);

		for (LanguageProficiency language : application.getPersonalDetails().getLanguageProficiencies()) {
			table.addCell(language.getLanguage().getName());
			table.addCell(language.getAptitude().getDisplayValue());
		}

		document.add(table);

		document.add(new Paragraph("Residence", smallBoldFont));

		if (application.getPersonalDetails().getResidenceCountry() == null) {
			document.add(new Paragraph(createMessage("country")));
		} else {
			document.add(new Paragraph("Country: "+ application.getPersonalDetails().getResidenceCountry().getName()));
		}

		if (application.getPersonalDetails().getResidenceStatus() == null) {
			document.add(new Paragraph(createMessage("status")));
		} else {
			document.add(new Paragraph("Status: "+ application.getPersonalDetails().getResidenceStatus().getDisplayValue()));
		}

		document.add(new Paragraph("Contact Details", smallBoldFont));
		document.add(new Paragraph("Email: " + application.getPersonalDetails().getEmail()));

		document.add(new Paragraph("Telephone", smallBoldFont));
		if (application.getPersonalDetails().getPhoneNumbers().size() > 0) {
			document.add(new Paragraph(" "));
		}
		table = new PdfPTable(2);
		table.setWidthPercentage (100.0f);

		c1 = new PdfPCell(new Phrase("Type"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Number"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		table.setHeaderRows(1);

		for (Telephone telephone : application.getPersonalDetails().getPhoneNumbers()) {
			table.addCell(telephone.getTelephoneType().getDisplayValue());
			table.addCell(telephone.getTelephoneNumber());
		}

		document.add(table);

		document.add(new Paragraph("Skype", smallBoldFont));
		document.add(new Paragraph(application.getPersonalDetails().getMessenger()));
		}
		

	private void addGivenNationality(ApplicationForm application, Document document, String header, java.util.List<Nationality> nationalities) throws DocumentException {
		document.add(new Paragraph(header, smallBoldFont));
		if (application.getPersonalDetails().getCandidateNationalities().size() > 0) {
			document.add(new Paragraph(" "));
		}

		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage (100.0f);

		PdfPCell c1 = new PdfPCell(new Phrase("Nationality"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Is primary nationality?"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		table.setHeaderRows(1);

		for (Nationality nationality : nationalities) {
			table.addCell(nationality.getCountry().getName());
			if (nationality.isPrimary()) {
				table.addCell("Yes");
			} else {
				table.addCell("No");
			}
		}

		document.add(table);
	}

	private void addAddressSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Address", redFont));
		
		List list = new List(true, false, application.getAddresses().size());
		int counter = 1;
		for (Address address : application.getAddresses()) {
			document.add(new Paragraph("Address number "+ counter, smallBoldFont));
			counter++;
		}
		
		document.add(list);
	}
	
	private String createMessage(String fieldName) {
		return "No " + fieldName + " has been specified.";
	}
}
