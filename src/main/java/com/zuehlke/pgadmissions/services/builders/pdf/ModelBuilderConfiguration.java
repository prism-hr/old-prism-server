package com.zuehlke.pgadmissions.services.builders.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;

public class ModelBuilderConfiguration {
    
    protected static final BaseColor GREY_COLOR = new BaseColor(220, 220, 220);
    
    protected static final float WIDTH_PERCENTAGE = 100f;
    
    protected static final int LARGE_FONT_SIZE = 12;
    
    protected static final int MEDIUM_FONT_SIZE  = 10;
    
    protected static final int SMALL_FONT_SIZE = 8;
    
    protected static final Font BOLD_FONT = new Font(FontFamily.HELVETICA, LARGE_FONT_SIZE, Font.BOLD);
    
    protected static final Font MEDIUM_BOLD_FONT = new Font(FontFamily.HELVETICA, MEDIUM_FONT_SIZE, Font.BOLD);
    
    protected static final Font MEDIUM_FONT = new Font(FontFamily.HELVETICA, MEDIUM_FONT_SIZE, Font.NORMAL);
    
    protected static final Font MEDIUM_GREY_FONT = new Font(FontFamily.HELVETICA, MEDIUM_FONT_SIZE, Font.NORMAL, GREY_COLOR);
    
    protected static final Font SMALL_BOLD_FONT = new Font(FontFamily.HELVETICA, SMALL_FONT_SIZE, Font.BOLD);
    
    protected static final Font SMALL_FONT = new Font(FontFamily.HELVETICA, SMALL_FONT_SIZE, Font.NORMAL);
    
    protected static final Font MEDIUM_LINK_FONT = new Font(FontFamily.HELVETICA, MEDIUM_FONT_SIZE, Font.UNDERLINE, BaseColor.BLUE);
    
}
