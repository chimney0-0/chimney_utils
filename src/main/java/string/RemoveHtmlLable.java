package string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveHtmlLable {

    public static String removeHTMLLabel(String text){
        StringBuilder textBuilder = new StringBuilder(text);
        removeLabel(textBuilder);
        return textBuilder.toString();
    }

    private static void removeLabel(StringBuilder contextDetail) {
        Pattern p = Pattern.compile("<.+?>");
        Matcher m = p.matcher(contextDetail);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            contextDetail.delete(start, end);
            m = p.matcher(contextDetail);
        }
    }
}
