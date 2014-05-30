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
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;

@Component
public class CombinedReferencesPdfBuilder extends AbstractPdfModelBuilder {

    @Value("${email.address.to}")
    private String emailAddressTo;
    
    public void build(final Comment referenceComment, final OutputStream outputStream) {
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

            if (BooleanUtils.isTrue(referenceComment.getDeclinedResponse())) {
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
                    document.newPage();
                    document.add(new Paragraph(
                            "We are sorry but we were unable to read and merge the contents of this document. " +
                                    "Please contact us at " + emailAddressTo + " to obtain an original copy, " +
                                    "quoting our application reference number: " + referenceComment.getApplication().getCode() + " " +
                                    "and document identifier: " + in.getId().toString() + "."));    
                }
            }
            document.newPage();
            document.close();
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }
    
}
