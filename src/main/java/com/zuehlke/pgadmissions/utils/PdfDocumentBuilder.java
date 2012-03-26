package com.zuehlke.pgadmissions.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Supervisor;

public class PdfDocumentBuilder {

	private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.RED);
	private static Font purpleFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.MAGENTA);
	private static Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

	public void buildDocument(ApplicationForm application, Document document) throws DocumentException {

		document.add(new Paragraph("Application id: "+application.getId(), boldFont));
		document.add(new Paragraph("Applicant: "+application.getApplicant().getFirstName()+ " " + application.getApplicant().getLastName(), boldFont));

		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		
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
//
//		List list = new List(true, false, 10);
//		list.add(new ListItem("First point"));
//		list.add(new ListItem("Second point"));
//		list.add(new ListItem("Third point"));

//		catPart.add(list);


	}

	private String createMessage(String fieldName) {
		return "No " + fieldName + " has been specified.";
	}
}
