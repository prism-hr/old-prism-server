package com.zuehlke.pgadmissions.validators;

import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.Document;

@Component
public class DocumentValidator extends AbstractValidator {

    private static final int TWO_MB_IN_BYTES = 2097152;
    private static final String[] EXTENSION_WHITE_LIST = { "PDF" };

    @Override
    public boolean supports(Class<?> clazz) {
        return Document.class.isAssignableFrom(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        Document document = (Document) target;

        if (isEmptyFilename(document)) {
            errors.rejectValue("fileName", "file.upload.empty");
            return;
        }

        if (!hasValidExtension(document)) {
            errors.rejectValue("fileName", "file.upload.notPDF");
            return;
        }

        if (isFilenameLongerThan200Chars(document)) {
            errors.rejectValue("fileName", "upload.file.toolong");
            return;
        }

        if (isLargerThan2Mb(document)) {
            errors.rejectValue("content", "file.upload.large");
            return;
        }

        PdfReader pdfReader = null;
        try {
            pdfReader = new PdfReader(document.getContent());
        } catch (IOException e) {
            errors.rejectValue("content", "file.upload.corrupted");
            return;
        }

        int permissions = pdfReader.getPermissions();
        if (pdfReader.isEncrypted() && (permissions & PdfWriter.ALLOW_COPY) == 0) {
            errors.rejectValue("content", "file.upload.encrypted");
        }
    }

    private boolean isEmptyFilename(final Document document) {
        return StringUtils.isBlank(document.getFileName());
    }

    private boolean hasValidExtension(final Document document) {
        if (StringUtils.isBlank(document.getFileName())) {
            return false;
        }
        for (String extension : EXTENSION_WHITE_LIST) {
            if (FilenameUtils.isExtension(document.getFileName().toUpperCase(), extension.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean isFilenameLongerThan200Chars(final Document document) {
        return document.getFileName().length() > 200;
    }

    private boolean isLargerThan2Mb(final Document document) {
        if (document.getFileData() != null) {
            return document.getFileData().getSize() > TWO_MB_IN_BYTES;
        } else if (document.getContent() != null) {
            return document.getContent().length > TWO_MB_IN_BYTES;
        }
        return true;
    }
}
