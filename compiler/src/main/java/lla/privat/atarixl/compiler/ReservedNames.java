// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.HashSet;
import java.util.Set;

public enum ReservedNames {
  ADR, AND, ARRAY, ASSERT, BEGIN, BYTE, DIV, DO, DOWNTO, ELSE, END, FOR, FUNCTION, IF, INCLUDE, LOCAL, LOMEM, MOD, OR,
  PROCEDURE, PROGRAM, REPEAT, RETURN, STRING, THEN, TO, UNTIL, WHILE, WORD, XOR;

  private static Set<String> map;

  public static boolean isReservedWord(String word) {
    if (map == null) {
      initializeMapping();
    }
    return map.contains(word);
  }

  private static void initializeMapping() {
    map = new HashSet<String>();
    for (ReservedNames s : ReservedNames.values()) {
      map.add(s.name());
    }

  }
}
