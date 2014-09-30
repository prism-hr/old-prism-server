package com.zuehlke.pgadmissions.services.builders.pdf;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;

@Component
public class PdfDocumentBuilder {
    
    private Logger log = LoggerFactory.getLogger(PdfDocumentBuilder.class);

    public void build(final ModelBuilder builder, final OutputStream outputStream, final Application application) {
        try {
            Document pdfDocument = new Document(PageSize.A4, 50, 50, 100, 50);
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, outputStream);
            pdfWriter.setCloseStream(false);
            pdfDocument.open(); 
            builder.build(application, pdfDocument, pdfWriter);  
            pdfDocument.newPage();
            pdfDocument.close();
        } catch (Exception e) {
            log.error("Error building PDF for application " + application.getCode(), e);
        }
    }
    
    public byte[] build(final ModelBuilder builder, final Application form) {
        HashMap<ModelBuilder, Application> map = new HashMap<ModelBuilder, Application>();
        map.put(builder, form);
        return build(map);
    }
    
    public byte[] build(final Map<ModelBuilder, Application> forms) {
        try {
            Document pdfDocument = new Document(PageSize.A4, 50, 50, 100, 50);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, baos);
            pdfDocument.open();
            
            for (Entry<ModelBuilder, Application> entry : forms.entrySet()) {
                ModelBuilder modelBuilder = entry.getKey();
                Application application = entry.getValue();
                try {
                    modelBuilder.build(application, pdfDocument, pdfWriter);
                } catch (PdfDocumentBuilderException e) {
                    log.warn("Error building PFD for application " + application.getCode(), e);
                }
                pdfDocument.newPage();
            }
            
            pdfDocument.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }
}
