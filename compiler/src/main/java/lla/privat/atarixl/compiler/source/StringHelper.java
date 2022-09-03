// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.source;

public class StringHelper {
  
  // private static final Logger LOGGER = LoggerFactory.getLogger(StringHelper.class);
  
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

  // TODO: is there a better way to check for a float value?
  public static boolean isFloatValue(String string) {
    int countOfPoint = 0;
    for (int i=0;i<string.length();i++) { // check if a dot exists
      if (string.charAt(i) == '.') {
        countOfPoint++;
      }
    }
    if (countOfPoint == 1) {
      boolean onlyDigitsOrDot = true;     // check if only digits but one dot
      for (int i=0;i<string.length();i++) {
        if (string.charAt(i) == '.' || (string.charAt(i) >= '0' && string.charAt(i) <= '9')) {
        }
        else {
          onlyDigitsOrDot = false;
        }
      }
      return onlyDigitsOrDot;
    }
    return false;
  }
  
  public static int convertToInteger(String element) {
    Double value = Double.parseDouble(element);
    return (int)Math.round(value);
  }
}
