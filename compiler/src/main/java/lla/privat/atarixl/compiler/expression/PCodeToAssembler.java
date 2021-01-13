// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
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

  private final Type ergebnis;

  private final List<Integer> p_code;

  private final List<String> assemblerCodeList;

  private boolean sonderlocke_fat_byte_array = false;

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
        LOGGER.error("caught IllegalStateException {}", e);
      } catch (IndexOutOfBoundsException e) {
        // TODO: Warum tritt das hier auf?
        LOGGER.error("Line: {}", source.getLine());
        LOGGER.error("Must not occur {}", e);
      }
    }

    for(String assemblerCode: assemblerCodeList) {
      LOGGER.debug(assemblerCode);
    }

    source.code(assemblerCodeList);
  }

  public void code(final String sourcecodeline) {
    codeGen(sourcecodeline);
  }



  /**
   * Das Arbeitstier, konvertiert p_code aus der Expression in 6502 Assembler
   * Definitiv nicht perfekt, aber besser als selbst in Assembler zu coden.
   */
  private void p_code_to_assembler() {
    int a = 0;
    int shiftValue = 0;
    int x;
    String mne$ = "";
    String a$ = "";

    do {
      int currentPCode = p_code.get(a);
      if (currentPCode == 0) {
        throw new IllegalStateException("unhandled 0");
      }

      int b = p_code.get(a + 1); // wert
      if (currentPCode == PCode.INT_ZAHL.getValue() && p_code.get(a + 2) == PCode.PUSH.getValue()) {
        code(";#2 (1)"); // TEST VORHANDEN
        code(" lda #<" + b); // ;" ;push zahl"
        code(" pha");
        if (ergebnis != Type.BYTE) {
          code(" lda #>" + b);
          code(" pha");
        }
        a = a + 3;
      }
// -------------------------------
      else {
        if (currentPCode == PCode.WORD.getValue() && p_code.get(a + 2) == PCode.PUSH.getValue()) {
          a$ = source.getVariableAt(b);
          code(";#2 (2)"); // TEST VORHANDEN
          code(" lda " + a$); // ;" ;push var"
          code(" pha");
          if (ergebnis != Type.BYTE) {
            mne$ = "lda";
            get_typ(mne$, a$);
            code(" pha");
          }
          a = a + 3;
        }
// -------------------------------
        else {
          x = p_code.get(a + 2);
          if ((currentPCode == PCode.INT_ZAHL.getValue() || currentPCode == PCode.WORD.getValue())
              && (x == PCode.IADD.getValue() || x == PCode.ISUB.getValue() || x == PCode.IOR.getValue()
                  || x == PCode.IAND.getValue() || x == PCode.IXOR.getValue())) {
            int c = p_code.get(a + 4);
            code(";#2 (3)"); // TEST VORHANDEN
            clc_sec(x);
            String b$ = "";
            Type typ2;
            if (currentPCode == PCode.INT_ZAHL.getValue()) { // low byte
              code(" lda #<" + b); // ;" ;izahl"
              typ2 = Type.BYTE;
            }
            else {
              a$ = source.getVariableAt(b);
              b$ = a$;
              typ2 = source.getVariableType(a$);
              code(" lda " + b$);
            }
            mne$ = mne_is_add_sub_or_eor_and(x);
            if (p_code.get(a + 3) == PCode.INT_ZAHL.getValue()) {
              code(" " + mne$ + " #<" + c);
            }
            else {
              a$ = source.getVariableAt(c);
              code(" " + mne$ + " " + a$);
            }
            code(" tay");
            if (ergebnis != Type.BYTE) {
              if (currentPCode == PCode.INT_ZAHL.getValue()) { // high byte
                code(" lda #>" + b);
              }
              else {
                if (typ2 == Type.BYTE) {
                  code(" lda #0");
                }
                else {
                  code(" lda " + b$ + "+1");
                }
              }
              if (p_code.get(a + 3) == PCode.INT_ZAHL.getValue()) {
                code(" " + mne$ + " #>" + c);
              }
              else {
                get_typ(mne$, a$);
              }
              code(" tax");
            }
            a = a + 5;
          }
// -------------------------------
          else {
            if ((currentPCode == PCode.INT_ZAHL.getValue() || currentPCode == PCode.WORD.getValue())
                && (p_code.get(a + 2) == PCode.IMULT.getValue() || p_code.get(a + 2) == PCode.IDIV.getValue()
                    || p_code.get(a + 2) == PCode.IMOD.getValue())) {
              int c = p_code.get(a + 4);
              shiftValue = -1;
              code(";#2 (4)"); // TEST FEHLERHAFT

              if (currentPCode == PCode.INT_ZAHL.getValue()) {
                code(" ldy #<" + b); // ;" ;izahl"
                if (ergebnis != Type.BYTE) {
                  code(" ldx #>" + b);
                }
              }
              else { // ivar
                a$ = source.getVariableAt(b);
                code(" ldy " + a$); // ;" ;ivar"
                if (ergebnis != Type.BYTE) {
                  mne$ = "ldx";
                  get_typ(mne$, a$);
                }
              }
              if (p_code.get(a + 3) == PCode.INT_ZAHL.getValue()) {
                x = c;
                int y = p_code.get(a + 2);
                shiftValue = mul_div_with_shift(x, y);
              }
              else { // ivar
                a$ = source.getVariableAt(c);
                code(" lda " + a$);
                code(" sta @op");
                if (ergebnis != Type.BYTE) {
                  mne$ = "lda";
                  get_typ(mne$, a$);
                  code(" sta @op+1");
                }
              }
              x = p_code.get(a + 2);
              mul_div_or_mod(x, shiftValue);
              a = a + 5;
            }
// -------------------------------
            else {
              if (currentPCode == PCode.INT_ZAHL.getValue()) {
                code(";#2 (5)"); // TEST VORHANDEN
                code(" ldy #<" + b); // ;" ;izahl"
                if (ergebnis == Type.BYTE && b > 255) {
                  code(" ldx #>" + b); // Sonderlocke für z.B. FAT_BYTE_ARRAY
                  sonderlocke_fat_byte_array = true;
                }
                if (ergebnis != Type.BYTE) {
                  code(" ldx #>" + b);
                }
                a = a + 2;
              }
// -------------------------------
              else { // ivar
                if (currentPCode == PCode.WORD.getValue()) {
                  a$ = source.getVariableAt(b);
                  code(";#2 (6)");
                  code(" ldy " + a$); // ;" ;ivar"
                  if (ergebnis == Type.BYTE && source.getVariableSize(a$) == 2) {
                    code(" ldx " + a$ + "+1"); // Sonderlocke für z.B. FAT_BYTE_ARRAY
                    sonderlocke_fat_byte_array = true;
                  }
                  if (ergebnis != Type.BYTE) {
                    mne$ = "ldx";
                    get_typ(mne$, a$);
                  }
                  a = a + 2;
                }
              }
            }
          }
        }
      }

// -------------------------------
      if (currentPCode == PCode.PULL.getValue()) {
        code(";#2 (7)"); // TEST VORHANDEN
        code(" sty @op"); // ;movepull"
        if (ergebnis != Type.BYTE) {
          code(" stx @op+1");
          code(" pla");
          code(" tax");
        }
        code(" pla");
        code(" tay");
        // -------------------------------
        x = p_code.get(a + 1);
        shiftValue = -1;
        if (x == PCode.UPN_ADD.getValue() ||
            x == PCode.UPN_SUB.getValue() ||
            x == PCode.UPN_OR.getValue() ||
            x == PCode.UPN_XOR.getValue() ||
            x == PCode.UPN_AND.getValue()) {
          clc_sec(x);
          mne$ = mne_is_add_sub_or_eor_and(x);
          add_sub(mne$);
        }
        else { // -------------------------------
          if (x == PCode.UPN_MUL.getValue() ||
              x == PCode.UPN_DIV.getValue() ||
              x == PCode.UPN_MODULO.getValue()) { // mult or div
            x = x + 8;
            mul_div_or_mod(x, shiftValue);
          }
        }
        a = a + 2;
      }
// -------------------------------
      x = currentPCode;
      int y = p_code.get(a + 1);
      if (x == PCode.IADD.getValue() ||
          x == PCode.ISUB.getValue() ||
          x == PCode.IOR.getValue() ||
          x == PCode.IXOR.getValue() ||
          x == PCode.IAND.getValue()) { // add/sub/or/xor/and
        b = p_code.get(a + 2);
        code(";#2 (8)"); // TEST VORHANDEN
        if (x == PCode.IADD.getValue() && b == 1 && y == PCode.INT_ZAHL.getValue()) {
          if (x == PCode.IADD.getValue()) {
            code(" iny");
            if (ergebnis != Type.BYTE) { // must be WORD or WORD_ARRAY
              code(" bne ?nowinc" + source.getNowinc());
              code(" inx");
              code("?nowinc" + source.getNowinc());
              source.incrementNowinc();
            }
          }
          // if (x == 17 ) { ?#kan;" dey" mne$="sbc" }
        }
        else {
          clc_sec(x);
          code(" tya");
          mne$ = mne_is_add_sub_or_eor_and(x);
          if (y == PCode.INT_ZAHL.getValue()) {
            code(" " + mne$ + " #<" + b);
          }
          else {
            a$ = source.getVariableAt(b);
            code(" " + mne$ + " " + a$);
          }
          code(" tay");
          if (ergebnis != Type.BYTE) {
            code(" txa");
            if (y == PCode.INT_ZAHL.getValue()) {
              code(" " + mne$ + " #>" + b);
            }
            else {
              get_typ(mne$, a$);
            }
            code(" tax");
          }
        }
        a = a + 3;
      }
// -------------------------------
      else {
        if (currentPCode == PCode.IMULT.getValue() ||
            currentPCode == PCode.IDIV.getValue() ||
            currentPCode == PCode.IMOD.getValue()) {
          b = p_code.get(a + 2);
          shiftValue = -1;
          code(";#2 (9)");
          if (p_code.get(a + 1) == PCode.INT_ZAHL.getValue()) {
            x = b;
            y = currentPCode;
            shiftValue = mul_div_with_shift(x, y);
          }
          else {
            a$ = source.getVariableAt(b);
            code(" lda " + a$);
            code(" sta @op");
            if (ergebnis != Type.BYTE) {
              mne$ = "lda";
              get_typ(mne$, a$);
              code(" sta @op+1");
            }
          }
          x = currentPCode;
          mul_div_or_mod(x, shiftValue);
          a = a + 3;
        }
      }
// -------------------------------
      if (currentPCode == PCode.NOP.getValue()) {
        a = a + 1;
      }
// -------------------------------
      if (currentPCode == PCode.PUSH.getValue()) {
        code(";#2 (10)"); // TEST VORHANDEN
        code(" tya"); // ;push"
        code(" pha");
        if (ergebnis != Type.BYTE) {
          code(" txa");
          code(" pha");
        }
        a = a + 1;
      }
// -------------------------------
      else if (currentPCode == PCode.WORD_ARRAY.getValue()) {
        code(";#2 (11)"); // TEST VORHANDEN
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);
        if (ergebnis == Type.BYTE) {
          code(" ldx #0");
        }
        code(" tya");
        code(" getarrayw " + a$); // ;" ;word-array"
        code(" tay");
        // y+256+x are set with a value out of getarrayw
        a = a + 2;
      }
      else if (currentPCode == PCode.FAT_BYTE_ARRAY.getValue()) {
        code(";#2 (12.2)"); // TEST VORHANDEN
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);
        if (ergebnis == Type.BYTE && sonderlocke_fat_byte_array == false) {
          code(" ldx #0");
        }
        code(" tya");
        code(" getarrayb " + a$); // ;" ;fat-byte-array"
        code(" tay");
        sonderlocke_fat_byte_array = false;
        // y (x:=0) are set with a value out of getarrayb
        a = a + 2;
      }
// -------------------------------
      else if (currentPCode == PCode.BYTE_ARRAY.getValue()) {
        code(";#2 (12)"); // TEST VORHANDEN
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);
        Type typ = source.getVariableType(a$);
        if (typ == Type.BYTE_ARRAY) {
          code(" lda " + a$ + ",y"); // ;b.array short"
          code(" tay");
        }
// DEAD Code!        
//        else {
//          // TODO: diesen Fall unterstützen wir gerade nicht!
//          if (ergebnis == Type.BYTE) {
//            code(" ldx #0");
//          }
//          code(" getarrayb " + a$); // ;" ;byte-array"
//          code(" tay");
//        }
        if (ergebnis == Type.WORD) {
          code(" ldx #0");
        }
        a = a + 2;
      }
// -------------------------------
      else if (currentPCode == PCode.ADDRESS.getValue()) {
        code(";#2 (13)");
        int num = p_code.get(a + 1);
        a$ = source.getVariableAt(num);
        code(" ldy #<" + a$); // ;" ;adresse"
        code(" ldx #>" + a$);
        a = a + 2;
      }
// -------------------------------
      else if (currentPCode == PCode.FUNCTION.getValue()) {
        code(";#2 (14)");
        int num = p_code.get(a + 1); // todo was enthaelt num, wenn es keinen parameter gibt?
        a$ = source.getVariableAt(num);
        code(" jsr " + a$); // ;" ; -->func"
        a = a + 2;
      }
      else if (currentPCode == PCode.FUNCTION_POINTER.getValue()) {
        code(";#2 (14 ptr)");
        int num = p_code.get(a + 1); // todo was enthaelt num, wenn es keinen parameter gibt?
        a$ = source.getVariableAt(num);
        code(" ldy " + a$); // ;" ;Inhalt der Variable nach y,x"
        code(" ldx " + a$ + "+1");
        code(" jsr @function_pointer"); // ;" ; --> func ptr"
        a = a + 2;
      }
      else if (currentPCode == PCode.STRING.getValue()) {
        code(";#2 (15)");
        int num = p_code.get(a + 1); // todo was enthaelt num, wenn es keinen parameter gibt?
        a$ = source.getVariableAt(num);
        code(" ldy #<" + a$); // ;" ;adresse"
        code(" ldx #>" + a$);
        a = a + 2;
      }
      else if (currentPCode == PCode.PARAMETER_PUSH.getValue()) {
        code(";#2 (16)");
        int num = p_code.get(a + 1); // todo was enthaelt num, wenn es keinen parameter gibt?
        code(" tya"); // ; ummodeln x,y in parameter->heap"
        code(" ldy #" + String.valueOf(num * 2 + 1));
        code(" sta (@heap_ptr),y");
        if (ergebnis == Type.WORD) {
          code(" txa");
        }
        else {
          code(" lda #0");
        }
        code(" iny");
        code(" sta (@heap_ptr),y");
        a = a + 2;
      }
      else if (currentPCode == PCode.PARAMETER_START_ADD_TO_HEAP_PTR.getValue()) {
        int num = p_code.get(a + 1); // todo was enthaelt num, wenn es keinen parameter gibt?
        if (num != 0) {
          code(";#2 (17)");
          code(" ADD_TO_HEAP_PTR " + String.valueOf(num * 2 + 1));
        }
        a = a + 2;
      }
      else if (currentPCode == PCode.PARAMETER_END_SUB_FROM_HEAP_PTR.getValue()) {
        int num = p_code.get(a + 1); // todo was enthaelt num, wenn es keinen parameter gibt?
        if (num != 0) {
          code(";#2 (18)");
          code(" SUB_FROM_HEAP_PTR " + String.valueOf(num * 2 + 1));
        }
        a = a + 2;
      }
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

  private static int condi;
// -------------------------------
  public int mul_div_with_shift(int value, int pcode) {
    ++condi;
    int exponent;
    if (value > 1) {
      exponent = Long.valueOf(Math.round(Math.log(value) / 0.69314718 + 1.0e-05)).intValue(); // log(x)/log(2)
    }
    else {
      exponent = -1;
    }
    if (exponent > 0 && Math.pow(2, exponent) == value && pcode != 23) { // nicht modulo
//    code("; shiften um ";d;" bits"
      if (exponent > 7) {
        if (pcode == PCode.IDIV.getValue()) {
          // TODO: make shifts negativable              
          
          code(" txa"); // ;hohe zahl in kleine zahl"
          code(" tay");
          code(" cpy #$80");
          code(" ldx #0");
          code(" bcc ?notneg"+condi);
          code(" ldx #$ff");
          code("?notneg"+condi);
        }
        if (pcode == PCode.IMULT.getValue()) {
          code(" tya"); // ;kleine zahl in hohe zahl"
          code(" tax");
          code(" ldy #0");
        }
        exponent = exponent - 8;
      }

      if (exponent > 0) {
        if (pcode == PCode.IMULT.getValue()) {
          if (ergebnis == Type.BYTE) { // ;mul shiften"
            code(" tya");
            for (int z = 0; z < exponent; z++) {
              code(" asl a");
            }         
            code(" tay");
          }
          else {
            code(" tya"); 
            code(" stx @op");
            
            for (int z = 0; z < exponent; z++) {
              code(" asl a");
              code(" rol @op");
            }
            code(" tay");
            code(" ldx @op");            
          }
        }
        else if (pcode == PCode.IDIV.getValue()) {
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
      exponent = -1; // keine rotation
      code(" lda #<" + value); // ;" ;mul/div/modulo"
      code(" sta @op");
      if (ergebnis != Type.BYTE) {
        code(" lda #>" + value);
        code(" sta @op+1");
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
  public void get_typ(String mne$, String a$) {
    Type typ = source.getVariableType(a$);
    if (typ == Type.BYTE) {
      code(" " + mne$ + " #0");
    }
    else {
      code(" " + mne$ + " " + a$ + "+1");
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
    }
    return mne$;
  }

// -------------------------------
  public void clc_sec(int x) { // carry
    switch (x & 7) {
    case 0:
      code(" clc"); // ;addition"
      break;

    case 1:
      code(" sec"); // ;subtraktion"
      break;
    }
  }

}
