package com.zuehlke.pgadmissions.services.builders.download;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
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
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderColor;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Component
public class ApplicationDownloadBuilderHelper {

    @Value("${xml.export.logo.file.location}")
    private String logoFileLocation;

    @Value("${xml.export.logo.file.width.percentage}")
    private Float logoFileWidthPercentage;

    @Autowired
    private ApplicationContext applicationContext;

    public Document startDocument() {
        return new Document(PageSize.A4, 50, 50, 100, 50);
    }

    public PdfWriter startDocumentWriter(final OutputStream outputStream, Document pdfDocument) throws DocumentException {
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

    public PdfPTable startSubection(Document pdfDocument, String title) throws DocumentException {
        PdfPTable subBody = newSectionBody();
        PdfPCell header = new PdfPCell(newSubsectionHeader(title));
        header.setColspan(2);
        subBody.addCell(header);
        return subBody;
    }

    public void addContentRow(String title, String content, ApplicationDownloadBuilderFontSize fontSize, PdfPTable table) {
        String fontSizePostfix = WordUtils.capitalizeFully(fontSize.name());
        table.addCell((PdfPCell) ReflectionUtils.invokeMethod(this, "newTitleCell" + fontSizePostfix, title));
        table.addCell((PdfPCell) ReflectionUtils.invokeMethod(this, "newContentCell" + fontSizePostfix,
                content == null ? applicationContext.getBean(PropertyLoader.class).load(PrismDisplayProperty.SYSTEM_VALUE_NOT_PROVIDED) : content));
    }

    public void closeSection(Document pdfDocument, PdfPTable body) throws DocumentException {
        pdfDocument.add(body);
        pdfDocument.add(newSectionSeparator());
    }

    public Image newLogoImage() throws BadElementException, MalformedURLException, IOException {
        Image image = Image.getInstance(this.getClass().getResource(logoFileLocation));
        image.setWidthPercentage(logoFileWidthPercentage);
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

    public PdfPCell newTitleCellSmall(final String content) {
        return newTitleCell(content, ApplicationDownloadBuilderFontSize.SMALL);
    }

    public PdfPCell newTitleCellMedium(final String content) {
        return newTitleCell(content, ApplicationDownloadBuilderFontSize.MEDIUM);
    }

    public PdfPCell newTitleCellLarge(final String content) {
        return newTitleCell(content, ApplicationDownloadBuilderFontSize.LARGE);
    }

    public PdfPCell newContentCellSmall(final String content) {
        return newContentCell(content, ApplicationDownloadBuilderFontSize.SMALL);
    }

    public PdfPCell newContentCellMedium(final String content) {
        return newContentCell(content, ApplicationDownloadBuilderFontSize.MEDIUM);
    }

    public PdfPCell newContentCellLarge(final String content) {
        return newContentCell(content, ApplicationDownloadBuilderFontSize.LARGE);
    }

    public PdfPCell newBookmarkCellSmall(final String content, int index) {
        return newBookmarkCell(content, ApplicationDownloadBuilderFontSize.SMALL, index);
    }

    public PdfPCell newBookmarkCellMedium(final String content, int index) {
        return newBookmarkCell(content, ApplicationDownloadBuilderFontSize.MEDIUM, index);
    }

    public PdfPCell newBookmarkCellLarge(final String content, int index) {
        return newBookmarkCell(content, ApplicationDownloadBuilderFontSize.LARGE, index);
    }

    public PdfPCell newContentCell(final String content, final ApplicationDownloadBuilderFontSize fontSize) {
        return newTableCell(content, fontSize, null);
    }

    public PdfPCell newTitleCell(final String content, final ApplicationDownloadBuilderFontSize fontSize) {
        if (content == null) {
            throw new Error("Title cell must have content");
        }
        return newTableCell(content, fontSize, null);
    }

    public PdfPCell newBookmarkCell(final String content, final ApplicationDownloadBuilderFontSize fontSize, int bookmarkIndex) {
        return newTableCell(content, fontSize, bookmarkIndex);
    }

    public void addContentRowSmall(String title, String content, PdfPTable table) {
        addContentRow(title, content, ApplicationDownloadBuilderFontSize.SMALL, table);
    }

    public void addContentRowMedium(String title, String content, PdfPTable table) {
        addContentRow(title, content, ApplicationDownloadBuilderFontSize.MEDIUM, table);
    }

    private PdfPTable newSectionHeader(String title, ApplicationDownloadBuilderColor background) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(ApplicationDownloadBuilderConfiguration.PAGE_WIDTH);
        PdfPCell cell = newTableCell(title, ApplicationDownloadBuilderFontSize.LARGE, null);
        cell.setBackgroundColor(background.getColor());
        table.addCell(cell);
        return table;
    }

    private PdfPCell newTableCell(final String content, final ApplicationDownloadBuilderFontSize fontSize, final Integer bookmarkIndex) {
        if (fontSize == null) {
            throw new Error("Cell must have font size");
        }

        Phrase phrase;
        if (StringUtils.isBlank(content)) {
            phrase = new Phrase(applicationContext.getBean(PropertyLoader.class).load(PrismDisplayProperty.SYSTEM_VALUE_NOT_PROVIDED),
                    ApplicationDownloadBuilderConfiguration.getEmptyFont(fontSize));
        } else if (bookmarkIndex == null) {
            phrase = new Phrase(content, ApplicationDownloadBuilderConfiguration.getFont(fontSize));
        } else {
            phrase = new Phrase(new Chunk(content, ApplicationDownloadBuilderConfiguration.getLinkFont(fontSize)).setLocalDestination(bookmarkIndex.toString()));
        }

        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

}
