// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.expression.multiplication.StarChain;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;

/**
 * PCode to Assembler Generator
 * Most the time it works as follows:
 * - We load a value into register (Y + 256 * X)
 * - If we need to work on,
 * --  move register to akku (TYA or TXA)
 * --  work on it
 * --  move akku back to register (TAY or TAX)
 * - the result is always that the word value stays in register (Y + 256 * X)
 * IMPORTANT:
 * - Do not try to optimize code here, use Peephole Optimizer for that
 */
public class PCodeToAssembler extends Code {

  private static final Logger LOGGER = LoggerFactory.getLogger(PCodeToAssembler.class);

  private final Source source;

  private /*final*/ Type ergebnis;

  private final List<Integer> p_code;

  private final List<String> assemblerCodeList;

  private boolean sonderlocke_fat_byte_array = false;

//  private static int condi;

  public PCodeToAssembler(final Source source, final List<Integer> p_code, Type ergebnis) {
    super(source);

    this.source = source;
    this.p_code = p_code;
    this.ergebnis = ergebnis;

    this.assemblerCodeList = new ArrayList<>();
  }

  public void build() {
    LOGGER.debug("---- PCode to Assembler ----");
    if (p_code.size() >= 3) {
      try {
        p_code_to_assembler();
      } catch (IllegalStateException e) {
        LOGGER.error("caught IllegalStateException {}", e.getMessage());
      } catch (IndexOutOfBoundsException e) {
        // TODO: Warum tritt das hier auf?
        LOGGER.error("Line: {}", source.getLine());
        LOGGER.error("Must not occur {}", e.getMessage());
      }
    }

    for(String assemblerCode: assemblerCodeList) {
      LOGGER.debug(assemblerCode);
    }

    source.code(assemblerCodeList);
  }

  public int code(final String sourcecodeline) {
    return codeGen(sourcecodeline);
  }



  /**
   * Das Arbeitstier, konvertiert p_code aus der Expression in 6502 Assembler
   * Definitiv nicht perfekt, aber besser als selbst in Assembler zu coden.
   */
  private void p_code_to_assembler() {
    int a = 0;
    int shiftValue = 0;
    int operator;
    String mne$ = "";
    String a$ = "";

    do {
      PCode currentPCode = PCode.fromInt(p_code.get(a));
      if (currentPCode == PCode.UNKNOWN) {
        throw new IllegalStateException("unhandled 0") {
          @Override
          public synchronized Throwable fillInStackTrace() {
            return null;
          }
        };
      }

      final int value = p_code.get(a + 1); // wert
      if (currentPCode == PCode.INT_ZAHL && p_code.get(a + 2) == PCode.PUSH.getValue()) {
        // TODO: Reihenfolge umdrehen, Wert in Stack ablegen!
        code(";#2 (1)"); // TEST VORHANDEN
        code(" lda #<" + value); // ;" ;push zahl"
        code(" pha");
        if (ergebnis.getBytes() == 2) {
          code(" lda #>" + value);
          code(" pha");
        }
        a = a + 3;
      }
// -------------------------------
      else {
        if (currentPCode == PCode.WORD && p_code.get(a + 2) == PCode.PUSH.getValue()) {
          a$ = source.getVariableAt(value);
          code(";#2 (2)"); // TEST VORHANDEN
          // TODO: Reihenfolge umdrehen, Wert in Stack ablegen!
          code(" lda " + a$); // ;" ;push var"
          source.incrementRead(a$);
          code(" pha");
          if (ergebnis.getBytes() == 2) {
            Type typ = source.getVariableType(a$);
            if (typ == Type.INT8) {
                code(" cmp #$80");    // switch carry flag
                code(" lda #0");      // ldx will not change carry flag
                code(" bcc *+4");     // jump 2 bytes forward
                code(" lda #$FF");
            }
            else if (typ == Type.BYTE) {
              code(" lda #0");
            }
            else {
              code(" lda " + a$ + "+1");
              source.incrementRead(a$);
            }
            code(" pha");
          }
          a = a + 3;
        }
// -------------------------------
        else {
          operator = p_code.get(a + 2);

          if ((currentPCode == PCode.INT_ZAHL || currentPCode == PCode.WORD)
              && (
              operator == PCode.IADD.getValue() ||
              operator == PCode.ISUB.getValue() ||
              operator == PCode.IOR.getValue()  ||
              operator == PCode.IAND.getValue() ||
              operator == PCode.IXOR.getValue())) {
            // add, sub, or, and, xor
            final int c = p_code.get(a + 4);
            code(";#2 (3)"); // TEST VORHANDEN
            prepareCarryFlag(operator);
            String b$ = "";
            Type typ1;
            // aktuell gibt es INT(Konstante) oder WORD(variable)
            if (currentPCode == PCode.INT_ZAHL) { // low byte
              code(" lda #<" + value); // ;" ;izahl"
// TODO: fehler! falsche Annahme
              typ1 = Type.BYTE;
            }
            else {
              a$ = source.getVariableAt(value);
              b$ = a$;
              typ1 = source.getVariableType(a$);
              code(" lda " + b$);
              source.incrementRead(b$);
            }
            mne$ = mne_is_add_sub_or_eor_and(operator);
            if (p_code.get(a + 3) == PCode.INT_ZAHL.getValue()) {
              code(" " + mne$ + " #<" + c);
            }
            else {
              a$ = source.getVariableAt(c);
              code(" " + mne$ + " " + a$);
            }
            code(" tay");
            if (ergebnis.getBytes() == 2 || typ1.getBytes() == 2) {
              if (currentPCode == PCode.INT_ZAHL) { // high byte
                code(" lda #>" + value);
              }
              else { // ivar links
                if (typ1 == Type.BYTE || typ1 == Type.INT8) {
                  code(" lda #0");
                }
                else {
                  code(" lda " + b$ + "+1");
                  source.incrementRead(b$);
                }
              }
              if (p_code.get(a + 3) == PCode.INT_ZAHL.getValue()) {
                code(" " + mne$ + " #>" + c);
              }
              else { // ivar rechts
                Type typ2 = source.getVariableType(a$);
                if (typ2 == Type.BYTE || typ2 == Type.INT8) {
                  code(" " + mne$ + " #0");
                }
                else {
                  code(" " + mne$ + " " + a$ + "+1");
                  source.incrementRead(a$);
                }
              }
              code(" tax");
            }
            a = a + 5;
          }
// -------------------------------
          else {
            if ((currentPCode == PCode.INT_ZAHL || currentPCode == PCode.WORD)
                && (
                    p_code.get(a + 2) == PCode.IMULT.getValue() ||
                    p_code.get(a + 2) == PCode.IDIV.getValue()  ||
                    p_code.get(a + 2) == PCode.IMOD.getValue())) {
              final int c = p_code.get(a + 4);
              shiftValue = -1;
              code(";#2 (4)"); // TEST FEHLERHAFT

              if (currentPCode == PCode.INT_ZAHL) {
                code(" ldy #<" + value); // ;" ;izahl"
                if (ergebnis.getBytes() == 2) {
                  code(" ldx #>" + value);
                }
              }
              else { // ivar
                a$ = source.getVariableAt(value);
                code(" ldy " + a$); // ;" ;ivar"
                source.incrementRead(a$);

                if (ergebnis.getBytes() == 2) {
                  Type typ = source.getVariableType(a$);
                  if (typ == Type.INT8) {
                    // wir prüfen auf < $80, wenn ja, carry gesetzt
                    // X-Reg muss null sein
                    // sonst X muss $FF sein (-1)
                    // Code frisst 8 Zyklen, wenn >$80
                    // sonst 7, weil bcc springt
                      code(" cpy #$80");    // switch carry flag
                      code(" ldx #0");      // ldx will not change carry flag
                      code(" bcc *+4");     // jump 2 bytes forward
                      code(" ldx #$FF");
                  }
                  else if (typ == Type.BYTE) {
                    code(" ldx #0");
                  }
                  else {
                    code(" ldx " + a$ + "+1");
                    source.incrementRead(a$);
                  }
                }
              }
              if (p_code.get(a + 3) == PCode.INT_ZAHL.getValue()) {
                operator = c;
                PCode pcode = PCode.fromInt(p_code.get(a + 2));
                shiftValue = mul_div_with_shift(operator, pcode);
              }
              else { // ivar
                a$ = source.getVariableAt(c);
                code(" lda " + a$);
                source.incrementRead(a$);
                code(" sta @op");
                if (ergebnis != Type.BYTE) {
                  Type typ = source.getVariableType(a$);
                  if (typ == Type.INT8) {
                      code(" cmp #$80");    // switch carry flag
                      code(" lda #0");      // ldx will not change carry flag
                      code(" bcc *+4");     // jump 2 bytes forward
                      code(" lda #$FF");
                  }
                  else if (typ == Type.BYTE) {
                    code(" lda #0");
                  }
                  else {
                    code(" lda " + a$ + "+1");
                    source.incrementRead(a$);
                  }
                  code(" sta @op+1");
                }
              }
              operator = p_code.get(a + 2);
              mul_div_or_mod(operator, shiftValue);
              a = a + 5;
            }
// -------------------------------
            else {
              if (currentPCode == PCode.INT_ZAHL) {
                code(";#2 (5)"); // TEST VORHANDEN
                code(" ldy #<" + value); // ;" ;izahl"
                if (ergebnis == Type.BYTE && value > 255) {
                  code(" ldx #>" + value); // Sonderlocke für z.B. FAT_BYTE_ARRAY
                  sonderlocke_fat_byte_array = true;
                }
                if (ergebnis == Type.WORD || ergebnis == Type.UINT16) {
                  code(" ldx #>" + value);
                }
                a = a + 2;
              }
// -------------------------------
              else { // ivar
                if (currentPCode == PCode.WORD) {
                  a$ = source.getVariableAt(value);
                  Type typOfa$ = source.getVariableType(a$);
                  code(";#2 (6)");
                  code(" ldy " + a$); // ;" ;ivar"
                  source.incrementRead(a$);

                  if (ergebnis == Type.BYTE && source.getVariableSize(a$) == 2) {
                    code(" ldx " + a$ + "+1"); // Sonderlocke für z.B. FAT_BYTE_ARRAY
                    sonderlocke_fat_byte_array = true;
                    source.incrementRead(a$);
                  }
                  // word a$==INT8
                  // word a$==BYTE
                  // word a$==WORD UINT16

                  if (ergebnis == Type.WORD && typOfa$ == Type.INT8) {
                    code(" cpy #$80");    // switch carry flag
                    code(" ldx #0");      // ldx will not change carry flag
                    code(" bcc *+4");     // jump 2 bytes forward
                    code(" ldx #$FF");
                  }
                  else if (ergebnis == Type.WORD && typOfa$ == Type.BYTE) {
                    code(" ldx #0");
                  }
                  else if (
                      (ergebnis == Type.WORD || ergebnis == Type.UINT16) &&
                      typOfa$.getBytes() == 2) {
                    code(" LDX " + a$ + "+1");
                    source.incrementRead(a$);
                  }
                  else if (ergebnis == Type.BYTE || ergebnis == Type.INT8) {

                  }
                  else {
                    String message = "Ergebnis: " + ergebnis + " typeOfa$ " + typOfa$;
                    throw new IllegalStateException("ivar falsch: " + message) {
                      @Override
                      public synchronized Throwable fillInStackTrace() {
                        return null;
                      }
                    };
                  }
                  a = a + 2;
                }
              }
            }
          }
        }
      }

// -------------------------------
      if (currentPCode == PCode.PULL) {
        code(";#2 (7)"); // TEST VORHANDEN
        code(" sty @op"); // ;movepull"
// TODO: testen mit word + byte + word?
        // TODO: Reihenfolge umdrehen, Wert in Stack ablegen!
        if (ergebnis.getBytes() == 2) {
          code(" stx @op+1");
          code(" pla");
          code(" tax");
        }
        code(" pla");
        code(" tay");
        // -------------------------------
        operator = p_code.get(a + 1);
        shiftValue = -1;
        if (operator == PCode.UPN_ADD.getValue() ||
            operator == PCode.UPN_SUB.getValue() ||
            operator == PCode.UPN_OR.getValue() ||
            operator == PCode.UPN_XOR.getValue() ||
            operator == PCode.UPN_AND.getValue()) {
          prepareCarryFlag(operator);
          mne$ = mne_is_add_sub_or_eor_and(operator);
          add_sub(mne$);
        }
        else { // -------------------------------
          if (operator == PCode.UPN_MUL.getValue() ||
              operator == PCode.UPN_DIV.getValue() ||
              operator == PCode.UPN_MODULO.getValue()) { // mult or div
            operator = operator + 8;
            mul_div_or_mod(operator, shiftValue);
          }
        }
        a = a + 2;
      }
// -------------------------------
      operator = currentPCode.getValue();
      int y = p_code.get(a + 1);
      if (operator == PCode.IADD.getValue() ||
          operator == PCode.ISUB.getValue() ||
          operator == PCode.IOR.getValue() ||
          operator == PCode.IXOR.getValue() ||
          operator == PCode.IAND.getValue()) { // add/sub/or/xor/and
        final int value2 = p_code.get(a + 2);
        code(";#2 (8)"); // TEST VORHANDEN
        if (operator == PCode.IADD.getValue() && value2 == 1 && y == PCode.INT_ZAHL.getValue()) {
          if (operator == PCode.IADD.getValue()) {
            code(" iny");
            if (ergebnis.getBytes() == 2) { // must be WORD or WORD_ARRAY
              code(" bne *+3");
              code(" inx");
              source.incrementNowinc();
            }
          }
          // if (x == 17 ) { ?#kan;" dey" mne$="sbc" }
        }
        else {
          prepareCarryFlag(operator);
          code(" tya");
          mne$ = mne_is_add_sub_or_eor_and(operator);
          if (y == PCode.INT_ZAHL.getValue()) {
            code(" " + mne$ + " #<" + value2);
          }
          else {
            a$ = source.getVariableAt(value2);
            code(" " + mne$ + " " + a$);
          }
          code(" tay");

          if (ergebnis.getBytes() != 1) {
            code(" txa");
            if (y == PCode.INT_ZAHL.getValue()) {
              code(" " + mne$ + " #>" + value2);
            }
            else {
              Type typ = source.getVariableType(a$);
              if (typ.getBytes() == 1 ) {
                code(" " + mne$ + " #0");
              }
              else {
                code(" " + mne$ + " " + a$ + "+1");
              }

            }
            code(" tax");
          }
        }
        a = a + 3;
      }
// -------------------------------
      else {
        if (currentPCode == PCode.IMULT ||
            currentPCode == PCode.IDIV ||
            currentPCode == PCode.IMOD) {
          final int value3 = p_code.get(a + 2);
          shiftValue = -1;
          code(";#2 (9)");
          if (p_code.get(a + 1) == PCode.INT_ZAHL.getValue()) {
            operator = value3;
            shiftValue = mul_div_with_shift(operator, currentPCode);
          }
          else {
            a$ = source.getVariableAt(value3);
            code(" lda " + a$);
            source.incrementRead(a$);

            code(" sta @op");
            if (ergebnis.getBytes() != 1) {
              Type typ = source.getVariableType(a$);
              if (typ == Type.INT8) {
                  code(" cmp #$80");    // switch carry flag
                  code(" lda #0");      // ldx will not change carry flag
                  code(" bcc *+4");     // jump 2 bytes forward
                  code(" lda #$FF");
              }
              else if (typ == Type.BYTE) {
                code(" lda #0");
              }
              else {
                code(" lda " + a$ + "+1");
                source.incrementRead(a$);
              }
              code(" sta @op+1");
            }
          }
          operator = currentPCode.getValue();
          mul_div_or_mod(operator, shiftValue);
          a = a + 3;
        }
      }
// -------------------------------
      if (currentPCode == PCode.NOP) {
        a = a + 1;
      }
// -------------------------------
      if (currentPCode == PCode.PUSH) {
        code(";#2 (10)"); // TEST VORHANDEN
        // TODO: Reihenfolge umdrehen, Wert in Stack ablegen!
        code(" tya"); // ;push"
        code(" pha");
        if (ergebnis.getBytes() == 2) {
          code(" txa");
          code(" pha");
        }
        a = a + 1;
      }
// -------------------------------
      else if (currentPCode == PCode.WORD_ARRAY) {
        code(";#2 (11)"); // TEST VORHANDEN
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);
        if (ergebnis.getBytes() == 1) {
          code(" ldx #0"); // word_array
        }
        code(" tya");
        code(" getarrayw " + a$); // ;" ;word-array"
        code(" tay");
        // y+256+x are set with a value out of getarrayw
        a = a + 2;
      }
      // -------------------------------
      else if (currentPCode == PCode.WORD_SPLIT_ARRAY) {
        code("; (11.2)"); // TEST VORHANDEN
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);
        // Type typ = source.getVariableType(a$);
        // if (typ == Type.BYTE_ARRAY) {
          code(" ldx " + a$ + "_high,y");
          code(" lda " + a$ + "_low,y");
          code(" tay");
        // }
        // y+256*x are set
        a = a + 2;
      }
      // -------------------------------
      else if (currentPCode == PCode.FAT_BYTE_ARRAY) {
        code(";#2 (12.2)"); // TEST VORHANDEN
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);
        if (ergebnis == Type.BYTE && sonderlocke_fat_byte_array == false) {
// TODO: verstehen!
          code(" ldx #0"); // fat_byte_array
        }
        
        if (a$.equals("@MEM")) {
          code(" sty @GETARRAY");
          code(" stx @GETARRAY+1");          
          code(" LDY #0");
          code(" LDA (@GETARRAY),Y");
        }
        else {
          code(" tya");
  //        code(" getarrayb " + a$); // ;" ;fat-byte-array"
          code(" clc"); // ;  2 Getarrayb MACRO
          code(" adc #<" + a$);
//          code(" STA @GETARRAY");
          code(" TAY");
          code(" TXA");
          code(" ADC #>" + a$);
          code(" STA @GETARRAY0+1");
//          code(" LDY #0");
          code(" LDA (@GETARRAY0),Y");
        }

        if (ergebnis.getBytes() == 2) {
          code(" LDX #0");
        }
        code(" tay");
        sonderlocke_fat_byte_array = false;
        // y (x:=0) are set with a value out of getarrayb
        a = a + 2;
      }
// -------------------------------
      else if (currentPCode == PCode.BYTE_ARRAY) {
        code(";#2 (12)"); // TEST VORHANDEN
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);
        Type typ = source.getVariableType(a$);
        if (typ == Type.BYTE_ARRAY || typ == Type.STRING) {
          code(" lda " + a$ + ",y"); // ;b.array short"
          code(" tay");
        }
// DEAD Code!
//        else {
//          // TODO: diesen Fall unterstützen wir gerade nicht!
//          if (ergebnis == Type.BYTE) {
//            code(" ldx #0"); // dead
//          }
//          code(" getarrayb " + a$); // ;" ;byte-array"
//          code(" tay");
//        }
        if (ergebnis.getBytes() == 2) {
          code(" ldx #0");
        }
        a = a + 2;
      }
      // -------------------------------
      else if (currentPCode == PCode.ADDRESS) {
        code(";#2 (13)");
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);
        code(" ldy #<" + a$); // ;" ;adresse"
        code(" ldx #>" + a$);
        a = a + 2;
      }
      // -------------------------------
      else if (currentPCode == PCode.TOWORD) {
        code(";#2 (19)");
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);
        code(" ldy " + a$);   // ;" ;load byte"
        source.incrementRead(a$);

        Type typ = source.getVariableType(a$);
        if (typ == Type.BYTE) {
            code(" ldx #0");
        }
        else if (typ == Type.INT8) {
          code(" cpy #$80");    // switch carry flag
          code(" ldx #0");      // ldx will not change carry flag
          code(" bcc *+4");
          code(" ldx #$ff");
        }
        else {
        }
        a = a + 2;
      }
      // -------------------------------
      else if (currentPCode == PCode.ABSOLUTE_WORD) {
        code(";#2 (19b)");
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);

        source.incrementNegativeCount();
        
        code(" ldx " + a$ + "+1");
        code(" bpl ?abs_positive"+source.getNegativeCount());
        code(" sec");
        code(" lda #0");
        code(" sbc " + a$);
        code(" tay");
        code(" lda #0");
        code(" sbc " + a$ + "+1");
        code(" tax");
        code(" jmp ?abs_was_negative"+source.getNegativeCount());
        code("?abs_positive"+source.getNegativeCount());
        code(" ldy " + a$);
        code("?abs_was_negative"+source.getNegativeCount());
               
        a = a + 2;
      }
      // -------------------------------
      else if (currentPCode == PCode.ABSOLUTE_INT8) {
        code(";#2 (19c)");
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);

        source.incrementNegativeCount();
        
        code(" ldy " + a$);
        code(" bpl ?abs_positive"+source.getNegativeCount());
        code(" sec");
        code(" lda #0");
        code(" sbc " + a$);
        code(" tay");
        code("?abs_positive"+source.getNegativeCount());
               
        a = a + 2;
      }
      // -------------------------------
      else if (currentPCode == PCode.HI_BYTE) {
        code(";#2 (20)");
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);
        if (source.getVariableSize(a$) == 2) {
          code(" ldy " + a$ + "+1"); //  + " ; hibyte");          
        }
        else if (source.getVariableType(a$) == Type.CONST) {
          code(" ldy #>" + a$); //  + " ; hibyte");
        }
        else {
          // Fehler
          source.error(new Symbol("", SymbolEnum.noSymbol), "HI: with other than WORD/UINT16 or CONST is not supported.");
        }
//        code(" ldx #0");
        a = a + 2;
      }
      // -------------------------------
      else if (currentPCode == PCode.NEGATIVE) {
        code(";#2 (21)");
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);
        if (source.getVariableSize(a$) == 1) {
          code(" lda " + a$);
          code(" eor #$FF");
          code(" tay");
          code(" iny");

          if (ergebnis.getBytes() == 2) {
            Type typ = source.getVariableType(a$);
            if (typ == Type.INT8) {
              code(" cpy #$80");    // switch carry flag
              code(" ldx #0");      // ldx will not change carry flag
              code(" bcc *+4");
              code(" ldx #$ff");
            }
            else {
              code(" ldx #$ff");              
            }
          }
        }
        else if (source.getVariableSize(a$) == 2) {
          code(" lda " + a$);
          code(" eor #$FF");
          code(" tay");
          code(" lda " + a$ + "+1");
          code(" eor #$FF");
          code(" tax");
          
          code(" iny");
          if (ergebnis.getBytes() == 2) { // must be WORD or WORD_ARRAY
            code(" bne *+3");
            code(" inx");
            source.incrementNowinc();
          }
        }
        else {
          // Fehler
          source.error(new Symbol("", SymbolEnum.noSymbol), "HI: with other than WORD/UINT16 or BYTE/INT8 is not supported.");
        }
//        code(" ldx #0");
        a = a + 2;
      }
// -------------------------------
      else if (currentPCode == PCode.FUNCTION) {
        code(";#2 (14)");
        int num = p_code.get(a + 1); // todo was enthaelt num, wenn es keinen parameter gibt?
        int parameterCount = p_code.get(a+2);
        a$ = source.getVariableAt(num);
        String functionName = source.generateFunctionNameWithParameters(a$, parameterCount);
        code(" jsr " + functionName); // ;" ; -->func"
        a = a + 3;
      }
      else if (currentPCode == PCode.FUNCTION_POINTER) {
        code(";#2 (14 ptr)");
        int num = p_code.get(a + 1); // todo was enthaelt num, wenn es keinen parameter gibt?
        a$ = source.getVariableAt(num);
        code(" ldy " + a$); // ;" ;Inhalt der Variable nach y,x"
        code(" ldx " + a$ + "+1");
        code(" jsr @function_pointer"); // ;" ; --> func ptr"
        source.incrementRead(a$);
        source.incrementRead(a$);
        a = a + 3;
      }
      else if (currentPCode == PCode.STRING) {
        code(";#2 (15)");
        int num = p_code.get(a + 1); // todo was enthaelt num, wenn es keinen parameter gibt?
        a$ = source.getVariableAt(num);
        code(" ldy #<" + a$); // ;" ;adresse"
        code(" ldx #>" + a$);
        a = a + 2;
      }
      else if (currentPCode == PCode.PARAMETER_PUSH) {
        int num = p_code.get(a + 1); // todo was enthaelt num, wenn es keinen parameter gibt?
        if (source.getOptions().isUseStoreHeapPtrFunction()) {
          code(";#2 (16c)");
          if (ergebnis == Type.WORD || ergebnis == Type.UINT16) {  
          }
          else {
            code(" cpx #$80");
            code(" ldx #0");
            code(" bcs *+4");
            code(" ldx #$ff");           
          }
          code(" jsr @storeToHeap" + String.valueOf(num * 2 + 1));
        }
        else {
          code(";#2 (16)");
          // Wert als Parameter auf dem Heap ablegen
          //
          code(" tya"); // ; ummodeln x,y in parameter->heap"
          code(" ldy #" + String.valueOf(num * 2 + 1));
          code(" sta (@heap_ptr),y");
          if (ergebnis == Type.WORD || ergebnis == Type.UINT16) {
            code(" txa");
          }
          else {
            // TODO Kontrolle!!!!!
            code(" cmp #$80");
            code(" lda #0");
            code(" bcs *+4");
            code(" lda #$ff");
          }
          code(" iny");
          code(" sta (@heap_ptr),y");
        }
        a = a + 2;
      }
      else if (currentPCode == PCode.HSP_PARAMETER_PUSH) {
        code(";#2 (16b)");
        int num = p_code.get(a + 1); // todo was enthaelt num, wenn es keinen parameter gibt?
        a$ = source.getVariableAt(num);
        code(" sty @hsp_param+"+ String.valueOf(num * 2 + 1));
// TODO: type of current variable
        if (ergebnis == Type.WORD || ergebnis == Type.UINT16) {
          code(" txa");
        }
        else {
          // TODO Kontrolle!!!!!
          code(" cmp #$80");
          code(" lda #0");
          code(" bcs *+4");
          code(" lda #$ff");
        }
        code(" sta @hsp_param+"+ String.valueOf(num * 2 + 2));
        a = a + 2;
      }
      else if (currentPCode == PCode.PARAMETER_START_ADD_TO_HEAP_PTR) {
        int num = p_code.get(a + 1); // todo was enthaelt num, wenn es keinen parameter gibt?
        if (num != 0) {
          code(";#2 (17)");
          // code(" ADD_TO_HEAP_PTR " + String.valueOf(num * 2 + 1));
          source.add_to_heap_ptr(num*2+1);
        }
        a = a + 2;
      }
      else if (currentPCode == PCode.PARAMETER_END_SUB_FROM_HEAP_PTR) {
        int num = p_code.get(a + 1); // todo was enthaelt num, wenn es keinen parameter gibt?
        if (num != 0) {
          code(";#2 (18)");
          // code(" SUB_FROM_HEAP_PTR " + String.valueOf(num * 2 + 1));
          source.sub_from_heap_ptr(num*2+1);
        }
        a = a + 2;
      }
//      else if (currentPCode == PCode.TYPE_IS_BYTE) {
//        ergebnis = Type.BYTE;
//        a = a + 1;
//      }
//      else if (currentPCode == PCode.TYPE_IS_WORD) {
//        ergebnis = Type.WORD;
//        a = a + 1;
//      }
    }
// -------------------------------
    while (!(p_code.get(a) == PCode.END.getValue()));
  }

// -------------------------------
  public void add_sub(String mne$) {
    code(" tya"); // ;add/sub"
    code(" " + mne$ + " @op");
    code(" tay");
    if (ergebnis != Type.BYTE) {
      code(" txa");
      code(" " + mne$ + " @op+1");
      code(" tax");
    }
  }

// -------------------------------
  public int mul_div_with_shift(int value, PCode pcode) {
      // ++condi;
    int exponent;
    if (value > 1) {
      exponent = Long.valueOf(Math.round(Math.log(value) / 0.69314718 + 1.0e-05)).intValue(); // log(x)/log(2)
    }
    else {
      exponent = -1;
    }
    if  (!source.isShiftMultDiv()) {
      exponent = -1;
    }

    if (exponent > 0 && Math.pow(2, exponent) == value && pcode != PCode.IMOD) { // nicht modulo
//    code("; shiften um ";d;" bits"
      if (exponent > 7) {
        if (pcode == PCode.IDIV) {
          // TODO: make shifts negativable

          code(" txa"); // ;hohe zahl in kleine zahl"
          code(" tay");
//          code(" cpy #$80");
          code(" ldx #0");
//          code(" bcc *+4");
//          code(" ldx #$ff");
        }
        if (pcode == PCode.IMULT) {
          code(" tya"); // ;kleine zahl in hohe zahl"
          code(" tax");
          code(" ldy #0");
        }
        exponent = exponent - 8;
      }

      if (exponent > 0) {
        if (pcode == PCode.IMULT) {
          if (ergebnis == Type.BYTE) { // ;mul shiften"
            code(" tya");
            for (int z = 0; z < exponent; z++) {
              code(" asl a");
            }
            code(" tay");
          }
          else {
            // Type WORD, IMULT
            code(" tya");
            code(" stx @op+1");

            for (int z = 0; z < exponent; z++) {
              code(" asl a");
              code(" rol @op+1");
            }
            code(" tay");
            code(" ldx @op+1");
          }
        }
        else if (pcode == PCode.IDIV) {
          if (ergebnis == Type.BYTE) { // ;div shiften"
            code(" tya");
            for (int z = 0; z < exponent; z++) {
                code(" lsr a");
            }
            code(" tay");
          }

          else {
            code(" sty @op");
            code(" txa");

            for (int z = 0; z < exponent; z++) {
              code(" cmp #$80");
              code(" ror a");
              code(" ror @op");
            }
            code(" ldy @op");
            code(" tax");
          }
        }
        else {
          source.error(new Symbol("", SymbolEnum.noSymbol), "Other than IMULT or IDIV not supported with shift.");
        }
      }
    }
    else {
      if (pcode == PCode.IMULT && source.isStarChainMult()) {
        new StarChain(source).domult(value).build();
        exponent = 47;
      }
      else {
        exponent = -1; // keine rotation
        code(" lda #<" + value); // ;" ;div/modulo"
        code(" sta @op");
        if (ergebnis != Type.BYTE) {
          code(" lda #>" + value);
          code(" sta @op+1");
        }
      }
    }
    return exponent;
  }

// -------------------------------
  public void mul_div_or_mod(int x, int d) {
    if (d < 0) { // rotation?
      if (ergebnis == Type.BYTE) {
        code(" ldx #0");
        code(" stx @op+1");
      }
      if (x == PCode.IMULT.getValue()) {
        code(" jsr @imult");
      }
      else {
        if (x == PCode.IDIV.getValue()) {
          code(" jsr @idiv");
        }
        else { // imod
          code(" jsr @imod");
        }
      }
    }
  }

// -------------------------------
  public String mne_is_add_sub_or_eor_and(int x) {
    String mne$ = "";
    switch (x & 7) {
    case 0:
      mne$ = "adc";
      break;
    case 1:
      mne$ = "sbc";
      break;
    case 4:
      mne$ = "ora";
      break;
    case 5:
      mne$ = "eor";
      break;
    case 6:
      mne$ = "and";
      break;
    default:
      source.throwUnsupportedFeature("mne_is_add_sub_or_eor_and(), x&7 not handled");
    }
    return mne$;
  }

// -------------------------------
  public void prepareCarryFlag(int x) { // carry
    switch (x & 7) {
    case 0:
      code(" clc"); // ;addition"
      break;

    case 1:
      code(" sec"); // ;subtraktion"
      break;
    default:
      // This is not an error, we need no clc nor sec 
      // source.throwUnsupportedFeature("prepareCarryFlag(), x&7 not handled");
    }
  }

}
