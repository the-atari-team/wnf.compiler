// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

public enum Type {
  UNKNOWN(0, false),
  CONST(0, true),
  BYTE(1, false),
  UINT8(1, false), // is byte (unsigned)
  INT8(1, true),
  WORD(2, true),
  INT16(2, true), // is word (signed)
  UINT16(2, false),
  BYTE_ARRAY(1, false),
  FAT_BYTE_ARRAY(1, false),
  WORD_ARRAY(2, true),
  FAT_WORD_ARRAY(2, true),
  WORD_SPLIT_ARRAY(2, true),
  FUNCTION(2, false),
  FUNCTION_POINTER(2, false),
  PROCEDURE(2, false),
  STRING(2, false),
  STRING_ANONYM(2, false)
  ;

  private final int countOfBytes;

  private final boolean signed;

  private Type(int bytes, boolean signed) {
    countOfBytes = bytes;
    this.signed = signed;
  }

  public int getBytes() {
    return countOfBytes;
  }

  public boolean isSigned() {
    return signed;
  }

  public String getName() {
    return name().toLowerCase();
  }

}

