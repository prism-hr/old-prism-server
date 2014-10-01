package com.zuehlke.pgadmissions.services.builders.download;

import java.io.OutputStream;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
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
@Scope("prototype")
public class ApplicationDownloadReferenceBuilder {

    @Value("${xml.export.system.reference}")
    private String noReferenceExplanation;

    @Autowired
    ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    public void build(final Application application, final Comment referenceComment, final OutputStream outputStream) {
        try {
            Document document = applicationDownloadBuilderHelper.startDocument();
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setCloseStream(false);
            document.open();

            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(ApplicationDownloadBuilderConfiguration.PAGE_WIDTH);
            table.addCell(applicationDownloadBuilderHelper.newSectionHeader("Referee Comment"));
            document.add(table);
            document.add(applicationDownloadBuilderHelper.newSectionSeparator());

            if (referenceComment == null) {
                document.add(applicationDownloadBuilderHelper.newContentCellMedium("Comment: "
                        + (application.getState().getId().getStateGroup() == PrismStateGroup.APPLICATION_APPROVED ? noReferenceExplanation
                                : "Reference not yet provided at time of outcome.")));
            } else {
                document.add(applicationDownloadBuilderHelper.newContentCellMedium("Comment: "
                        + (BooleanUtils.isTrue(referenceComment.isDeclinedResponse()) ? "Declined to provide a reference." : referenceComment.getContent())));

                PdfContentByte cb = writer.getDirectContent();
                for (com.zuehlke.pgadmissions.domain.Document input : referenceComment.getDocuments()) {
                    try {
                        PdfReader reader = new PdfReader(input.getContent());
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
