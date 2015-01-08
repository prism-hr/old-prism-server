package com.zuehlke.pgadmissions.services.builders.download;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_VALUE_NOT_PROVIDED;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.io.Resources;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderColor;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Component
public class ApplicationDownloadBuilderHelper {

    private PropertyLoader propertyLoader;

    @Value("${xml.export.logo.file.location}")
    private String logoFileLocation;

    @Value("${xml.export.logo.file.width.percentage}")
    private Float logoFileWidthPercentage;

    public Document startDocument() {
        return new Document(PageSize.A4, 50, 50, 100, 50);
    }

    public PdfWriter startDocumentWriter(OutputStream outputStream, Document pdfDocument) throws DocumentException {
        PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, outputStream);
        pdfWriter.setCloseStream(false);
        pdfDocument.open();
        return pdfWriter;
    }

    public PdfPTable startSection(Document pdfDocument, String title) throws DocumentException {
        pdfDocument.add(newSectionHeader(title));
        pdfDocument.add(newSectionSeparator());
        return newSectionBody();
    }

    public PdfPTable startSubSection(String title) throws DocumentException {
        PdfPTable subBody = newSectionBody();
        PdfPCell header = new PdfPCell(newSubsectionHeader(title));
        header.setColspan(2);
        subBody.addCell(header);
        return subBody;
    }

    public void addContentRow(String title, String content, ApplicationDownloadBuilderFontSize fontSize, PdfPTable table) {
        String fontSizePostfix = WordUtils.capitalizeFully(fontSize.name());
        table.addCell((PdfPCell) ReflectionUtils.invokeMethod(this, "newTitleCell" + fontSizePostfix, title));
        table.addCell((PdfPCell) ReflectionUtils.invokeMethod(this, "newContentCell" + fontSizePostfix, content == null ? StringUtils.EMPTY : content));
    }

    public void closeSection(Document pdfDocument, PdfPTable body) throws DocumentException {
        pdfDocument.add(body);
        pdfDocument.add(newSectionSeparator());
    }

    public Image newLogoImage() throws BadElementException, MalformedURLException, IOException {
        Image image = Image.getInstance(Resources.getResource(logoFileLocation));
        image.scaleToFit(logoFileWidthPercentage * image.getWidth(), logoFileWidthPercentage * image.getHeight());
        return image;
    }

    public Paragraph newSectionSeparator() {
        return new Paragraph(" ");
    }

    public PdfPTable newSectionHeader(String title) {
        return newSectionHeader(title.toUpperCase(), ApplicationDownloadBuilderColor.GREY);
    }

    public PdfPTable newSubsectionHeader(String title) {
        return newSectionHeader(WordUtils.capitalizeFully(title), ApplicationDownloadBuilderColor.WHITE);
    }

    public PdfPTable newSectionBody() {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(ApplicationDownloadBuilderConfiguration.PAGE_WIDTH);
        return table;
    }

    public PdfPCell newTitleCellMedium(String content) {
        return newTitleCell(content, ApplicationDownloadBuilderFontSize.MEDIUM);
    }

    public PdfPCell newContentCellMedium(String content) {
        return newContentCell(content, ApplicationDownloadBuilderFontSize.MEDIUM);
    }

    public PdfPCell newBookmarkCellMedium(String content, int index) {
        return newBookmarkCell(content, ApplicationDownloadBuilderFontSize.MEDIUM, index);
    }

    public PdfPCell newContentCell(String content, ApplicationDownloadBuilderFontSize fontSize) {
        return newTableCell(content, fontSize, null);
    }

    public PdfPCell newTitleCell(String content, ApplicationDownloadBuilderFontSize fontSize) {
        return newTableCell(content, fontSize, null);
    }

    public PdfPCell newBookmarkCell(String content, ApplicationDownloadBuilderFontSize fontSize, int bookmarkIndex) {
        return newTableCell(content, fontSize, bookmarkIndex);
    }

    public void addContentRowMedium(String title, String content, PdfPTable table) {
        addContentRow(title, content, ApplicationDownloadBuilderFontSize.MEDIUM, table);
    }

    public ApplicationDownloadBuilderHelper localize(PropertyLoader propertyLoader) {
        this.propertyLoader = propertyLoader;
        return this;
    }

    private PdfPTable newSectionHeader(String title, ApplicationDownloadBuilderColor background) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(ApplicationDownloadBuilderConfiguration.PAGE_WIDTH);
        PdfPCell cell = newTableCell(title, ApplicationDownloadBuilderFontSize.LARGE, null);
        cell.setBackgroundColor(background.getColor());
        table.addCell(cell);
        return table;
    }

    private PdfPCell newTableCell(String content, ApplicationDownloadBuilderFontSize fontSize, Integer bookmarkIndex) {
        Phrase phrase;
        if (StringUtils.isBlank(content)) {
            phrase = new Phrase(propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED), ApplicationDownloadBuilderConfiguration.getEmptyFont(fontSize));
        } else if (bookmarkIndex == null) {
            phrase = new Phrase(content, ApplicationDownloadBuilderConfiguration.getFont(fontSize));
        } else {
            Anchor anchor = new Anchor(content, ApplicationDownloadBuilderConfiguration.getLinkFont(fontSize));
            anchor.setReference("#" + bookmarkIndex.toString());
            phrase = new Phrase();
            phrase.add(anchor);
        }

        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

}
