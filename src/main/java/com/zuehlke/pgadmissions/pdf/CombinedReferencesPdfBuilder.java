package com.zuehlke.pgadmissions.pdf;

import java.io.OutputStream;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;

@Component
public class CombinedReferencesPdfBuilder extends AbstractPdfModelBuilder {

    public CombinedReferencesPdfBuilder() {
    }

    public void build(final ReferenceComment referenceComment, final OutputStream outputStream) {
        try {
            Document document = new Document(PageSize.A4, 50, 50, 100, 50);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setCloseStream(false); // otherwise we're loosing our ZipOutputstream for calling zos.closeEntry();
            document.open();

            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
            table.addCell(newGrayTableCell("Referee Comment", BOLD_FONT));
            document.add(table);
            document.add(addSectionSeparators());

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
            throw new PdfDocumentBuilderException(e);
        }
    }
}
