package com.zuehlke.pgadmissions.utils;

import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class PrismConversionUtils {

    public static BigDecimal doubleToBigDecimal(Double input, int precision) {
        return input == null ? null : BigDecimal.valueOf(input).setScale(precision, HALF_UP);
    }

    public static BigDecimal decimalObjectToBigDecimal(Object value, int precision) {
        Class<?> valueClass = value.getClass();
        if (valueClass.equals(Double.class)) {
            return doubleToBigDecimal((Double) value, precision);
        } else if (valueClass.equals(BigDecimal.class)) {
            return (BigDecimal) value;
        }
        throw new Error();
    }

    public static Integer longToInteger(Long input) {
        return input == null ? null : input.intValue();
    }

    public static String htmlToPlainText(String html) {
        Document document = Jsoup.parse(html);
        Element body = document.body();
        return htmlToPlainText(body);
    }

    private static String htmlToPlainText(Element element) {
        FormattingVisitor formatter = new FormattingVisitor();
        NodeTraversor traversor = new NodeTraversor(formatter);
        traversor.traverse(element);
        return formatter.toString();
    }

    private static class FormattingVisitor implements NodeVisitor {
        private static final int maxWidth = 80;
        private int width = 0;
        private StringBuilder output = new StringBuilder();

        public void head(Node node, int depth) {
            String name = node.nodeName();
            if (node instanceof TextNode) {
                append(((TextNode) node).text());
            } else if (name.equals("li")) {
                append("\n * ");
            } else if (name.equals("a") && StringUtils.isNotBlank(node.attr("title")) && StringUtils.isNotBlank(node.attr("href"))
                    && !StringUtils.equalsIgnoreCase("http://", node.attr("href"))) {
                append("\n * ");
            }
        }

        public void tail(Node node, int depth) {
            String name = node.nodeName();
            if (name.equals("br")) {
                append("\n");
            } else if (StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5")) {
                append("\n\n");
            } else if (name.equals("a") && StringUtils.isNotBlank(node.attr("href")) && !StringUtils.equalsIgnoreCase("http://", node.attr("href"))) {
                if (StringUtils.isNotBlank(node.attr("title"))) {
                    appendNoLineWrap(String.format(" %s: %s", node.attr("title"), node.absUrl("href").trim()));
                } else if (node.absUrl("href").contains("mailto")) {
                    append(String.format(" <%s>", node.absUrl("href").replace("mailto:", "").trim()));
                } else {
                    append(String.format(" <%s>", node.absUrl("href")));
                }
            }
        }

        private void append(String text) {
            if (text.startsWith("\n")) {
                width = 0;
            }
            if (text.equals(" ") && (output.length() == 0 || StringUtil.in(output.substring(output.length() - 1), " ", "\n"))) {
                return;
            }

            if (text.length() + width > maxWidth) {
                String words[] = text.trim().split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    boolean last = i == words.length - 1;
                    if (!last) {
                        word = word + " ";
                    }
                    if (word.length() + width > maxWidth) {
                        output.append("\n").append(word);
                        width = word.length();
                    } else {
                        output.append(word);
                        width += word.length();
                    }
                }
            } else {
                output.append(text);
                width += text.length();
            }
        }

        private void appendNoLineWrap(String text) {
            output.append(text);
        }

        public String toString() {
            return output.toString().replaceAll("\n\n\n+", "\n\n");
        }

    }

}
