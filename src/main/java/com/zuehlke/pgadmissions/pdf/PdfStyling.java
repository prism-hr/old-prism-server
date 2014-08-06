package com.zuehlke.pgadmissions.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPageEventHelper;

public abstract class PdfStyling extends PdfPageEventHelper {

    protected static final float MAX_WIDTH_PERCENTAGE = 100f;
    
    protected static final int NORMAL_FONT_SIZE = 12;
    
    protected static final int SMALL_FONT_SIZE  = 10;
    
    protected static final int SMALLER_FONT_SIZE = 8;
    
    protected static final Font BOLD_FONT = new Font(FontFamily.HELVETICA, NORMAL_FONT_SIZE, Font.BOLD);
    
    protected static final Font SMALL_BOLD_FONT = new Font(FontFamily.HELVETICA, SMALL_FONT_SIZE, Font.BOLD);
    
    protected static final Font SMALL_FONT = new Font(FontFamily.HELVETICA, SMALL_FONT_SIZE, Font.NORMAL);
    
    protected static final Font SMALL_GREY_FONT = new Font(FontFamily.HELVETICA, SMALL_FONT_SIZE, Font.NORMAL, BaseColor.LIGHT_GRAY);
    
    protected static final Font SMALLER_BOLD_FONT = new Font(FontFamily.HELVETICA, SMALLER_FONT_SIZE, Font.BOLD);
    
    protected static final Font SMALLER_FONT = new Font(FontFamily.HELVETICA, SMALLER_FONT_SIZE, Font.NORMAL);
    
    protected static final Font LINK_FONT = new Font(FontFamily.HELVETICA, SMALL_FONT_SIZE, Font.UNDERLINE, BaseColor.BLUE);
    
    protected static final BaseColor GRAY_COLOR = new BaseColor(220, 220, 220);
    
}
