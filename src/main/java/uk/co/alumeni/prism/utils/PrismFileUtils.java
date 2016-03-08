package uk.co.alumeni.prism.utils;

import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Resources;

public class PrismFileUtils {

    public static String getContent(String filePath) {
        try {
            return Joiner.on(System.getProperty("line.separator")).join(Resources.readLines(Resources.getResource(filePath), Charsets.UTF_8));
        } catch (IOException e) {
            throw new Error(e);
        }
    }

}
