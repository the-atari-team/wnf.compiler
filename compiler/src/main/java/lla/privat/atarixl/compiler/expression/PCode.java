// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

import java.util.HashMap;
import java.util.Map;

public enum PCode {
  UNKNOWN(0),
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
  FUNCTION(64),         // followed by function name (number) and parameter count (number)
  FUNCTION_POINTER(65), // followed by function name (number) and parameter count (number)

  ZAHL(160),            // zahl folgt
  PUSH(162),
  PULL(163),
  INT_ZAHL(167),
  WORD(168),
  WORD_ARRAY(169),
  BYTE_ARRAY(170),
  FAT_BYTE_ARRAY(173),   // x[n] mit x = Byte && n > 255
  ADDRESS(171),
  STRING(172),
  WORD_SPLIT_ARRAY(174),
  // very short math routines
  ABSOLUTE_WORD(175),
  TOWORD(176),
  ABSOLUTE_INT8(177),
  HI_BYTE(178),
  NEGATIVE(179),
  
  PARAMETER_START_ADD_TO_HEAP_PTR(180),
  PARAMETER_PUSH(181),
  PARAMETER_END_SUB_FROM_HEAP_PTR(182),
  HSP_PARAMETER_PUSH(183),

//  TYPE_IS_BYTE(191),
//  TYPE_IS_WORD(192),
  
  NOP(254),
  END(9999999)
  ;

  private int value;

  private static final Map<Integer, PCode> intToTypeMap = new HashMap<>();

  static {
    for (PCode type : PCode.values()) {
      intToTypeMap.put(type.value, type);
    }
  }

  private PCode(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static PCode fromInt(int i) {
    PCode type = intToTypeMap.get(Integer.valueOf(i));
    if (type == null)
      return PCode.UNKNOWN;
    return type;
  }
}
