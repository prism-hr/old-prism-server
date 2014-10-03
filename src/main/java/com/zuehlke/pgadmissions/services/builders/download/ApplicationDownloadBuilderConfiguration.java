package com.zuehlke.pgadmissions.services.builders.download;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;

public class ApplicationDownloadBuilderConfiguration {

    public static final FontFamily FONT_FAMILY = FontFamily.HELVETICA;

    public static final float PAGE_WIDTH = 100f;

    public static final Font LARGE_FONT = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.LARGE.getSize(), Font.NORMAL);

    public static final Font LARGE_FONT_BOLD = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.LARGE.getSize(), Font.BOLD);

    public static final Font MEDIUM_FONT = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.MEDIUM.getSize(), Font.NORMAL);

    public static final Font MEDIUM_FONT_BOLD = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.MEDIUM.getSize(), Font.BOLD);

    public static final Font MEDIUM_FONT_GREY = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.MEDIUM.getSize(), Font.NORMAL,
            ApplicationDownloadBuilderColor.GREY.getColor());

    public static final Font MEDIUM_FONT_BLUE_UNDERLINE = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.MEDIUM.getSize(), Font.UNDERLINE,
            BaseColor.BLUE);

    public static final Font SMALL_FONT = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.SMALL.getSize(), Font.NORMAL);

    public static final Font SMALL_FONT_BOLD = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.SMALL.getSize(), Font.BOLD);

    public static final Font getFont(ApplicationDownloadBuilderFontSize size) {
        return getFont(size, null, false, false);
    }

    public static final Font getEmptyFont(ApplicationDownloadBuilderFontSize size) {
        return getFont(size, ApplicationDownloadBuilderColor.GREY, false, false);
    }

    public static final Font getBoldFont(ApplicationDownloadBuilderFontSize size) {
        return getFont(size, null, true, false);
    }

    public static final Font getLinkFont(ApplicationDownloadBuilderFontSize size) {
        return getFont(size, ApplicationDownloadBuilderColor.BLUE, false, true);
    }

    private static final Font getFont(ApplicationDownloadBuilderFontSize size, ApplicationDownloadBuilderColor color, boolean bold, boolean underline) {
        String property = size.name() + "_FONT" + (color == null ? "" : "_" + color.name()) + (bold ? "_BOLD" : "") + (underline ? "_UNDERLINE" : "");
        try {
            return (Font) ApplicationDownloadBuilderConfiguration.class.getField(property).get(null);
        } catch (Exception e) {
            throw new Error("No such font definition: " + property, e);
        }
    }

    public static enum ApplicationDownloadBuilderFontSize {

        LARGE(12), MEDIUM(10), SMALL(8);

        private int size;

        private ApplicationDownloadBuilderFontSize(int size) {
            this.size = size;
        }

        public final int getSize() {
            return size;
        }

    }

    public static enum ApplicationDownloadBuilderColor {

        WHITE(BaseColor.WHITE), GREY(new BaseColor(220, 220, 220)), BLUE(BaseColor.BLUE);

        private BaseColor color;

        private ApplicationDownloadBuilderColor(BaseColor color) {
            this.color = color;
        }

        public final BaseColor getColor() {
            return color;
        }

    }

}