package idv.common.string;

public class Trim {

    private String upgradedTrim(String textContent) {
        while (textContent.startsWith("　") || textContent.startsWith("、") || textContent.startsWith("\n")) {
            textContent = textContent.substring(1, textContent.length()).trim();
        }
        while (textContent.endsWith("　") || textContent.endsWith("\n")) {
            textContent = textContent.substring(0, textContent.length() - 1).trim();
        }
        return textContent;
    }
}
