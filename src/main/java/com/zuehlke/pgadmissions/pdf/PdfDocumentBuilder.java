package com.zuehlke.pgadmissions.pdf;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;

@Component
public class PdfDocumentBuilder {
    
    private Logger log = LoggerFactory.getLogger(PdfDocumentBuilder.class);

    public void build(final PdfModelBuilder builder, final OutputStream outputStream, final ApplicationForm form) {
        try {
            Document pdfDocument = new Document(PageSize.A4, 50, 50, 100, 50);
            
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, outputStream);
            
            pdfWriter.setCloseStream(false); // otherwise we're loosing our ZipOutputstream for calling zos.closeEntry();
            
            pdfDocument.open();
            
            builder.build(form, pdfDocument, pdfWriter);
            
            pdfDocument.newPage();
    
            pdfDocument.close();
        } catch (DocumentException e) {
            log.error(e.getMessage(), e);
        } catch (PdfDocumentBuilderException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    
    public byte[] build(final PdfModelBuilder builder, final ApplicationForm form) {
        HashMap<PdfModelBuilder, ApplicationForm> map = new HashMap<PdfModelBuilder, ApplicationForm>();
        map.put(builder, form);
        return build(map);
    }
    
    public byte[] build(final Map<PdfModelBuilder, ApplicationForm> forms) {
        try {
            Document pdfDocument = new Document(PageSize.A4, 50, 50, 100, 50);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, baos);
            
            pdfDocument.open();
            
            for (Entry<PdfModelBuilder, ApplicationForm> entry : forms.entrySet()) {
                PdfModelBuilder modelBuilder = entry.getKey();
                ApplicationForm form = entry.getValue();
                try {
                    modelBuilder.build(form, pdfDocument, pdfWriter);
                } catch (PdfDocumentBuilderException e) {
                    log.warn("Error in generating pdf for application " + form.getApplicationNumber(), e);
                }
                pdfDocument.newPage();
            }
            
            pdfDocument.close();
            
            return baos.toByteArray();
        } catch (DocumentException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
