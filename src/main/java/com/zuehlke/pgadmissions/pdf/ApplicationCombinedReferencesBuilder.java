package com.zuehlke.pgadmissions.pdf;

import java.io.OutputStream;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;

@Component
public class ApplicationCombinedReferencesBuilder extends AbstractPdfModelBuilder {

    @Value("${xml.export.system.reference}")
    private String noReferenceExplanation;

    public void build(final Application application, final Comment referenceComment, final OutputStream outputStream) {
        try {
            Document document = new Document(PageSize.A4, 50, 50, 100, 50);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setCloseStream(false);
            document.open();

            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
            table.addCell(newGrayTableCell("Referee Comment", BOLD_FONT));
            document.add(table);
            document.add(addSectionSeparators());

            if (referenceComment == null) {
                if (application.getState().getStateGroup().getId() == PrismStateGroup.APPLICATION_APPROVED) {
                    document.add(new Paragraph("Comment:\n" + noReferenceExplanation));
                } else {
                    document.add(new Paragraph("Comment:\nReference not yet provided at time of outcome"));
                }
            } else {
                if (BooleanUtils.isTrue(referenceComment.isDeclinedResponse())) {
                    document.add(new Paragraph("Comment:\nDeclined to provide a reference."));
                } else {
                    document.add(new Paragraph("Comment:\n" + referenceComment.getContent()));
                }

                PdfContentByte cb = writer.getDirectContent();
                for (com.zuehlke.pgadmissions.domain.Document in : referenceComment.getDocuments()) {
                    try {
                        PdfReader reader = new PdfReader(in.getContent());
                        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                            document.newPage();
                            PdfImportedPage page = writer.getImportedPage(reader, i);
                            cb.addTemplate(page, 0, 0);
                        }
                    } catch (IllegalArgumentException e) {
                        throw new Error(e);
                    }
                }
            }

            document.newPage();
            document.close();
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }

}
