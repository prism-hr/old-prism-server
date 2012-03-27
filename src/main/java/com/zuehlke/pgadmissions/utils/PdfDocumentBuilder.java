package com.zuehlke.pgadmissions.utils;

import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.LanguageProficiency;
import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.enums.AddressStatus;

public class PdfDocumentBuilder {

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
	}

	private void addSectionSeparators(Document document) throws DocumentException {
		document.add(new Paragraph(" "));
	}

	private void addCorrectOutputDependingOnNull(Document document, String fieldValue, String fieldLabel) throws DocumentException {
		if (fieldValue == null) {
			document.add(new Paragraph(createMessage(fieldLabel.toLowerCase())));
		} else {
			document.add(new Paragraph(fieldLabel+": "+fieldValue));
		}
	}

	private void addProgrammeSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Programme", greyFont));
		document.add(new Paragraph("Programme: "+application.getProject().getProgram().getTitle()));

		if (application.getProgrammeDetails().getStudyOption() == null) {
			document.add(new Paragraph(createMessage("study option")));
		} else {
			document.add(new Paragraph("Study Option: "+application.getProgrammeDetails().getStudyOption().displayValue()));
		}

		addCorrectOutputDependingOnNull(document, application.getProject().getTitle(), "Project");

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


		if (application.getProgrammeDetails().getSupervisors().isEmpty()) {
			document.add(new Paragraph(createMessage("supervisors information")));
		} else {
			document.add(new Paragraph("Supervisor", smallBoldFont));
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
	}

	private void addPersonalDetailsSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Personal Details", greyFont));
		addCorrectOutputDependingOnNull(document, application.getPersonalDetails().getFirstName(), "First Name");
		addCorrectOutputDependingOnNull(document, application.getPersonalDetails().getLastName(), "Last Name");

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

		addGivenNationality(document, "Nationality", application.getPersonalDetails().getCandidateNationalities());
		addGivenNationality(document, "Maternal Guardian Nationality", application.getPersonalDetails().getMaternalGuardianNationalities());
		addGivenNationality(document, "Paternal Guardian Nationality", application.getPersonalDetails().getPaternalGuardianNationalities());

		document.add(new Paragraph("Language", smallBoldFont));
		if (application.getPersonalDetails().getLanguageProficiencies().size() > 0) {
			document.add(new Paragraph(" "));

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
		} else {
			document.add(new Paragraph(createMessage("language")));
		}

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
		addCorrectOutputDependingOnNull(document, application.getPersonalDetails().getEmail(), "Email");

		addTelephones(application, document, application.getPersonalDetails().getPhoneNumbers());

		addCorrectOutputDependingOnNull(document, application.getPersonalDetails().getMessenger(), "Skype");
	}

	private void addTelephones(ApplicationForm application, Document document, List<Telephone> phoneNumbers) throws DocumentException {
		PdfPTable table;
		PdfPCell c1;
		document.add(new Paragraph("Telephone", smallBoldFont));
		if (application.getPersonalDetails().getPhoneNumbers().size() > 0) {
			document.add(new Paragraph(" "));

			table = new PdfPTable(2);
			table.setWidthPercentage (100.0f);

			c1 = new PdfPCell(new Phrase("Type"));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("Number"));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
			table.setHeaderRows(1);

			for (Telephone telephone : phoneNumbers) {
				table.addCell(telephone.getTelephoneType().getDisplayValue());
				table.addCell(telephone.getTelephoneNumber());
			}

			document.add(table);
		} else {
			document.add(new Paragraph(createMessage("telephone")));
		}
	}


	private void addGivenNationality(Document document, String header, java.util.List<Nationality> nationalities) throws DocumentException {
		document.add(new Paragraph(header, smallBoldFont));
		if (!nationalities.isEmpty()) {
			document.add(new Paragraph(" "));

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
		} else {
			document.add(new Paragraph(createMessage("nationality")));
		}

	}

	private void addAddressSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Address", greyFont));

		for (Address address : application.getAddresses()) {
			document.add(new Paragraph("Location: "+address.getLocation()));
			document.add(new Paragraph("Postal Code: "+address.getPostCode()));
			document.add(new Paragraph("Country: "+address.getCountry().getName()));

			document.add(new Paragraph("Residency Period", smallBoldFont));
			document.add(new Paragraph("From: "+address.getStartDate().toString()));
			if (address.getEndDate() != null) {
				document.add(new Paragraph("To: "+address.getEndDate().toString()));
			} else {
				document.add(new Paragraph(createMessage("end date")));
			}

			document.add(new Paragraph("Purpose: "+address.getPurpose().getDisplayValue()));
			if (address.getContactAddress() == AddressStatus.YES) {
				document.add(new Paragraph("This is my contact address."));
			}
			document.add(new Paragraph(" "));
		}

		if (application.getAddresses().isEmpty()) {
			document.add(new Paragraph(createMessage("address information")));
		}

	}

	private void addQualificationSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Qualification", greyFont));
		if (application.getQualifications().isEmpty()) {
			document.add(new Paragraph(createMessage("qualification information")));
		} else {
			
			for (Qualification qualification : application.getQualifications()) {
				document.add(new Paragraph("Provider: " + qualification.getQualificationInstitution()));
				document.add(new Paragraph("Programme: "+ qualification.getQualificationProgramName()));
				document.add(new Paragraph("Start Date: " + qualification.getQualificationStartDate().toString()));
				document.add(new Paragraph("Language: "+ qualification.getQualificationLanguage().getName()));
				document.add(new Paragraph("Level: " + qualification.getQualificationLevel().getDisplayValue()));
				document.add(new Paragraph("Type: " + qualification.getQualificationType()));
				document.add(new Paragraph("Grade: " + qualification.getQualificationGrade()));
				document.add(new Paragraph("Score: " + qualification.getQualificationScore()));
				document.add(new Paragraph("Award Date: " + qualification.getQualificationAwardDate().toString()));

				document.add(new Paragraph(" "));
			}
		}

	}

	private void addEmploymentSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Employment", greyFont));
		if (application.getEmploymentPositions().isEmpty()) {
			document.add(new Paragraph(createMessage("employment information")));
		} else {
			for (EmploymentPosition employment : application.getEmploymentPositions()) {
				document.add(new Paragraph("Employer: " + employment.getPosition_employer()));
				document.add(new Paragraph("Position: " + employment.getPosition_title()));
				document.add(new Paragraph("Remit: " +  employment.getPosition_remit()));
				document.add(new Paragraph("Start Date: " + employment.getPosition_startDate().toString()));
				if (employment.getPosition_endDate() == null) {
					document.add(new Paragraph(createMessage("end date")));
				} else {
					document.add(new Paragraph("End Date: " + employment.getPosition_endDate().toString()));
				}

				document.add(new Paragraph("Language of Work: " + employment.getPosition_language().getName()));

				document.add(new Paragraph(" "));
			}
		}

	}

	private void addFundingSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Funding", greyFont));

		if (application.getFundings().isEmpty()) {
			document.add(new Paragraph(createMessage("funding information")));
		} else {

			for (Funding funding : application.getFundings()) {
				document.add(new Paragraph("Type: " + funding.getType().getDisplayValue()));
				document.add(new Paragraph("Description:" + funding.getDescription()));
				document.add(new Paragraph("Value: " + funding.getValue()));
				document.add(new Paragraph("Award Date: " + funding.getAwardDate().toString()));

				document.add(new Paragraph(" "));
			}
		}

	}

	private void addReferencesSection(ApplicationForm application, Document document) throws DocumentException {

		document.add(new Paragraph("References", greyFont));
		for (Referee reference : application.getReferees()) {
			document.add(new Paragraph("First Name: " + reference.getFirstname()));
			document.add(new Paragraph("Last Name: " + reference.getLastname()));
			document.add(new Paragraph("Relationship: " + reference.getRelationship()));
			document.add(new Paragraph("Position", smallBoldFont));

			document.add(new Paragraph("Employer: " + reference.getJobEmployer()));
			document.add(new Paragraph("Title: " + reference.getJobTitle()));

			document.add(new Paragraph("Address", smallBoldFont));
			document.add(new Paragraph("Location: " + reference.getAddressLocation()));
			document.add(new Paragraph("Postal Code: " + reference.getAddressPostcode()));
			if (reference.getAddressCountry() != null) {
				document.add(new Paragraph("Country: " + reference.getAddressCountry().getName()));
			} else {
				document.add(new Paragraph(createMessage("country")));
			}

			document.add(new Paragraph("Contact Details", smallBoldFont));
			document.add(new Paragraph("Email: " + reference.getEmail()));
			addTelephones(application, document, reference.getPhoneNumbers());
			document.add(new Paragraph("Skype: " + reference.getMessenger()));

			document.add(new Paragraph(" "));
		}

		if (application.getReferees().isEmpty()) {
			document.add(new Paragraph(createMessage("references information")));
		}
	}

	private void addAdditionalInformationSection(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Additional Information", greyFont));
		if (application.getAdditionalInformation() != null) {
			document.add(new Paragraph(application.getAdditionalInformation()));
		} else {
			document.add(new Paragraph(createMessage("additional information")));
		}
	}

	private String createMessage(String fieldName) {
		return "No " + fieldName + " has been specified.";
	}
}
