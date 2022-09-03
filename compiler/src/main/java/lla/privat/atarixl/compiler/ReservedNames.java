// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.HashSet;
import java.util.Set;

public enum ReservedNames {
  PROGRAM, INCLUDE, LOMEM,
  PROCEDURE, FUNCTION, RETURN,
  BEGIN, END,
  IF, THEN, ELSE,
  FOR, TO, DOWNTO, STEP, DO, BREAK, WHILE,
  REPEAT, UNTIL,
  ABS, ADR, AND, B2W, DIV, MOD, OR, XOR,
  ASSERT,
  LOCAL, CONST, BYTE, WORD, ARRAY, STRING, INT8, UINT8, INT16, UINT16;

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
