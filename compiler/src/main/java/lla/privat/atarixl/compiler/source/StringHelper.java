// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.source;

public class StringHelper {
  public static String maybeString(String text) {
    if (isSingleQuotedString(text)) {
      return "\"" + text.substring(1, text.length() - 1) + "\"";
    }
    return text;
  }

  public static String makeDoubleQuotedString(String text) {
    if (isSingleQuotedString(text)) {
      return "\"" + text.substring(1, text.length() - 1) + "\"";
    }
    if (isDoubleQuotedString(text)) {
      return text;
    }
    return "\"" + text + "\"";
  }

  public static String removeQuotes(final String text) {
    if (isSingleQuotedString(text) || isDoubleQuotedString(text)) {
      return text.substring(1, text.length() - 1);
    }
    return text;
  }

  public static boolean isSingleQuotedString(String string) {
    return string.startsWith("'") && string.endsWith("'");
  }

  public static boolean isDoubleQuotedString(String string) {
    return string.startsWith("\"") && string.endsWith("\"");
  }

}
