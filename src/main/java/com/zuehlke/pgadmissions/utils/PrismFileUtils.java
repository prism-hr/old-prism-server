package com.zuehlke.pgadmissions.utils;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Resources;

import java.io.IOException;

public class PrismFileUtils {

    public static String getContent(String filePath) {
        try {
            return Joiner.on(System.getProperty("line.separator")).join(Resources.readLines(Resources.getResource(filePath), Charsets.UTF_8));
        } catch (IOException e) {
            throw new Error(e);
        }
    }

}
