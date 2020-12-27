// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

public enum PCode {
// 0000_1xxx
// load zahl push, load zahl2 pull add
  UPN_ADD(8),
  UPN_SUB(9),
  UPN_MUL(10),
  UPN_DIV(11),
  UPN_OR(12),
  UPN_XOR(13),
  UPN_AND(14),
  UPN_MODULO(15),

//0001_0xxx
// load zahl add load zahl2
  IADD(16),
  ISUB(17),
  IMULT(18),
  IDIV(19),
  IOR(20),
  IXOR(21),
  IAND(22),
  IMOD(23),

//
  FUNCTION(64),
  FUNCTION_POINTER(65),

  ZAHL(160), // zahl folgt
  PUSH(162),
  PULL(163),
  INT_ZAHL(167),
  WORD(168),
  WORD_ARRAY(169),
  BYTE_ARRAY(170),
  FAT_BYTE_ARRAY(173),   // x[n] mit x = Byte && n > 255
  ADDRESS(171),
  STRING(172),
  PARAMETER_START_ADD_TO_HEAP_PTR(180),
  PARAMETER_PUSH(181),
  PARAMETER_END_SUB_FROM_HEAP_PTR(182),

  NOP(254),
  END(9999999)
  ;

  private int value;

  private PCode(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
