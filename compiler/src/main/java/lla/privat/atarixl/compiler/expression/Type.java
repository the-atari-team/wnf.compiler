// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

public enum Type {
  UNKNOWN(0),
  BYTE(1),
  WORD(2),
  BYTE_ARRAY(1),
  FAT_BYTE_ARRAY(1),
  WORD_ARRAY(2),
  FUNCTION(2),
  FUNCTION_POINTER(2),
  PROCEDURE(2),
  STRING(2);

  private final int countOfBytes;

  private Type(int bytes) {
    countOfBytes = bytes;
  }

  public int getBytes() {
    return countOfBytes;
  }
  
  public String getName() {
    return name().toLowerCase();
  }
}

