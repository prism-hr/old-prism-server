package com.zuehlke.pgadmissions.services.builders.download;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

public class ApplicationDownloadBuilderConfiguration {

    public static final float PAGE_WIDTH = 100f;

    public static final Font getFont(ApplicationDownloadBuilderFontSize size) {
        return getFont(size, null, false);
    }

    public static final Font getEmptyFont(ApplicationDownloadBuilderFontSize size) {
        return getFont(size, ApplicationDownloadBuilderColor.GREY, false);
    }

    public static final Font getLinkFont(ApplicationDownloadBuilderFontSize size) {
        return getFont(size, ApplicationDownloadBuilderColor.BLUE, false);
    }

    private static final Font getFont(ApplicationDownloadBuilderFontSize size, ApplicationDownloadBuilderColor color, boolean bold) {
        String property = size.name() + "_FONT" + (color == null ? "" : "_" + color.name()) + (bold ? "_BOLD" : "");
        return (Font) PrismReflectionUtils.getStaticProperty(ApplicationDownloadBuilderConfiguration.class, property);
    }

    public enum ApplicationDownloadBuilderFontSize {

        LARGE(12), MEDIUM(10), SMALL(8);

        private int size;

        ApplicationDownloadBuilderFontSize(int size) {
            this.size = size;
        }

        public final int getSize() {
            return size;
        }

    }

    public enum ApplicationDownloadBuilderColor {

        WHITE(BaseColor.WHITE), GREY(BaseColor.GRAY), BLUE(BaseColor.BLUE);

        private BaseColor color;

        ApplicationDownloadBuilderColor(BaseColor color) {
            this.color = color;
        }

        public final BaseColor getColor() {
            return color;
        }

    }

}
