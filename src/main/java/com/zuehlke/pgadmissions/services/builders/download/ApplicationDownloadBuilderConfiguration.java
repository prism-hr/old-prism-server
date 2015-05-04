package com.zuehlke.pgadmissions.services.builders.download;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

public class ApplicationDownloadBuilderConfiguration {

    public static final FontFamily FONT_FAMILY = FontFamily.HELVETICA;

    public static final float PAGE_WIDTH = 100f;

    public static final Font LARGE_FONT = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.LARGE.getSize(), Font.NORMAL);

    public static final Font LARGE_FONT_BOLD = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.LARGE.getSize(), Font.BOLD);

    public static final Font MEDIUM_FONT = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.MEDIUM.getSize(), Font.NORMAL);

    public static final Font MEDIUM_FONT_BOLD = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.MEDIUM.getSize(), Font.BOLD);

    public static final Font MEDIUM_FONT_GREY = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.MEDIUM.getSize(), Font.NORMAL,
            ApplicationDownloadBuilderColor.GREY.getColor());

    public static final Font MEDIUM_FONT_BLUE = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.MEDIUM.getSize(), Font.NORMAL, BaseColor.BLUE);

    public static final Font SMALL_FONT = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.SMALL.getSize(), Font.NORMAL);

    public static final Font SMALL_FONT_BOLD = new Font(FONT_FAMILY, ApplicationDownloadBuilderFontSize.SMALL.getSize(), Font.BOLD);

    public static final Font getFont(ApplicationDownloadBuilderFontSize size) {
        return getFont(size, null, false);
    }

    public static final Font getEmptyFont(ApplicationDownloadBuilderFontSize size) {
        return getFont(size, ApplicationDownloadBuilderColor.GREY, false);
    }

    public static final Font getBoldFont(ApplicationDownloadBuilderFontSize size) {
        return getFont(size, null, true);
    }

    public static final Font getLinkFont(ApplicationDownloadBuilderFontSize size) {
        return getFont(size, ApplicationDownloadBuilderColor.BLUE, false);
    }

    private static final Font getFont(ApplicationDownloadBuilderFontSize size, ApplicationDownloadBuilderColor color, boolean bold) {
        String property = size.name() + "_FONT" + (color == null ? "" : "_" + color.name()) + (bold ? "_BOLD" : "");
        return (Font) PrismReflectionUtils.getStaticProperty(ApplicationDownloadBuilderConfiguration.class, property);
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

        WHITE(BaseColor.WHITE), GREY(BaseColor.GRAY), BLUE(BaseColor.BLUE);

        private BaseColor color;

        private ApplicationDownloadBuilderColor(BaseColor color) {
            this.color = color;
        }

        public final BaseColor getColor() {
            return color;
        }

    }

}
