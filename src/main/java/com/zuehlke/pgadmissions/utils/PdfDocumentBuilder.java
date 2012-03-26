package com.zuehlke.pgadmissions.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.zuehlke.pgadmissions.domain.ApplicationForm;

public class PdfDocumentBuilder {
	
	private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
	private static Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

	public void buildDocument(ApplicationForm application, Document document) throws DocumentException {
		document.add(new Paragraph("Application id: "+application.getId(), boldFont));
		document.add(new Paragraph("Program name: " + application.getProject().getProgram().getTitle(), boldFont));
		document.add(new Paragraph("Project name: " + application.getProject().getTitle(), boldFont));
		
	}
}
