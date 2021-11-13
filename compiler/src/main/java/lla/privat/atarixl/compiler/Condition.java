// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.expression.Expression;
import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;

public class Condition extends Code {
  private static final Logger LOGGER = LoggerFactory.getLogger(Condition.class);

  private final Source source;

  private Symbol nextSymbol;
  private final String conditionStr;

  public Condition(Source source, final String conditionStr) {
    super(source);
    this.source = source;
    this.conditionStr = conditionStr;
  }

  public int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
  }




  public Condition condition(Symbol symbol) {

    boolean hasMoreCondition = true;
    int ct = source.getConditionCount();
    int ctf = ct;

    nextSymbol = symbol;

    while (hasMoreCondition) {
      Symbol condition = new Expression(source).setType(Type.BYTE).expression(nextSymbol).build();

      code(" STY @ERG");
      int ergebnisBytes1 = source.getTypeOfLastExpression().getBytes();
      Type ergebnis1Type = source.getTypeOfLastExpression();
      if (ergebnisBytes1 == 2) {
        code(" STX @ERG+1");
      }

      if (condition.getId() == SymbolEnum.condition_symbol) {
        String currentCondition = condition.get();

        nextSymbol = source.nextElement();
        nextSymbol = new Expression(source).setType(ergebnis1Type).expression(nextSymbol).build();

        int ergebnisBytes2 = source.getTypeOfLastExpression().getBytes();

        code("; Bedingung (a" + currentCondition + "b)");
        if (ergebnisBytes1 == 1 && ergebnisBytes2 != 1) {
          code(" lda #0");
          code(" sta @erg+1");
        }
        else if (ergebnisBytes1 != 1 && ergebnisBytes2 == 1) {
          code(" ldx #0");
        }


        if (ergebnisBytes1 == 1 && ergebnisBytes2 == 1) {
          // links und rechts jeweils 1 Byte
          boolean signed = false;
          Type ergebnis2Type = source.getTypeOfLastExpression();
          if (ergebnis1Type == ergebnis2Type && ergebnis1Type==Type.INT8) {
            signed = true;
          }

          switch (currentCondition) {
          // <> != (ne)
          // = == (eq) are signed and unsigned right
          case "<>":
          case "!=":
            code(" cpy @erg");
            code(" beq ?fa" + ctf);
            break;

          case "=":
          case "==":
            code(" cpy @erg");
            code(" bne ?fa" + ctf);
            break;

          // this is the unsigned way
          case "<":
            if (signed) {
              code(" tya");
              code(" clc"); // prepare carry for SBC
              code(" sbc @erg"); // A-NUM
              code(" bvc *+4"); // if V is 0, N eor V = N, otherwise N eor V = N eor 1
              code(" eor #$80"); // A = A eor $80, and N = N eor 1
              code(" bmi ?fa" + ctf); // If the N flag is 1, then A (signed) <= NUM (signed) and BMI will branch
            }
            else {
              // unsigned '<'
              code(" cpy @erg");
              code(" beq ?fa" + ctf);
              code(" bcc ?fa" + ctf);
            }
            break;

          case "<=":
            if (signed) {
              code(" tya");
              code(" sec"); // prepare carry for SBC
              code(" sbc @erg"); // A-NUM
              code(" bvc *+4"); // if V is 0, N eor V = N, otherwise N eor V = N eor 1
              code(" eor #$80"); // A = A eor $80, and N = N eor 1
              code(" bmi ?fa" + ctf); // If the N flag is 1, then A (signed) < NUM (signed) and BMI will branch
            }
            else {
              code(" cpy @erg");
              code(" bcc ?fa" + ctf);
            }
            break;
          case ">":
            if (signed) {
              code(" tya");
              code(" sec"); // prepare carry for SBC
              code(" sbc @erg"); // A-NUM
              code(" bvc *+4"); // if V is 0, N eor V = N, otherwise N eor V = N eor 1
              code(" eor #$80"); // A = A eor $80, and N = N eor 1
              code(" bpl ?fa" + ctf); // If the N flag is 0, then A (signed) > NUM (signed) and BPL will branch
            }
            else {
              code(" cpy @erg");
              code(" bcs ?fa" + ctf);
            }
            break;
          case ">=":
            if (signed) {
              code(" tya");
              code(" clc"); // prepare carry for SBC
              code(" sbc @erg"); // A-NUM
              code(" bvc *+4"); // if V is 0, N eor V = N, otherwise N eor V = N eor 1
              code(" eor #$80"); // A = A eor $80, and N = N eor 1
              code(" bpl ?fa" + ctf); // If the N flag is 0, then A (signed) >= NUM (signed) and BPL will branch
            }
            else {
              code(" cpy @erg");
              code(" beq ?tr" + ct);
              code(" bcs ?fa" + ctf);
              code("?tr" + ct);
            }
            break;
          }
        }
        else {
          switch (currentCondition) {
          case "<>":
          case "!=":
            code(" cpy @erg");
            code(" bne ?tr" + ct);
            code(" cpx @erg+1");
            code(" beq ?fa" + ctf);
            code("?tr" + ct);
            break;
          case "=":
          case "==":
            code(" cpy @erg");
            code(" bne ?fa" + ctf);
            code(" cpx @erg+1");
            code(" bne ?fa" + ctf);
            break;
          case "<=":
            code(" cpy @erg");
            code(" txa");
            code(" sbc @erg+1");
            code(" bvc ?vc" + ct);
            code(" eor #$80");
            code("?vc" + ct);
            code(" bmi ?fa" + ctf);
            break;
          case ">":
            code(" cpy @erg");
            code(" txa");
            code(" sbc @erg+1");
            code(" bvc ?vc" + ct);
            code(" eor #$80");
            code("?vc" + ct);
            code(" bpl ?fa" + ctf);
            break;
          case "<":
            /*
Example 6.3: a 16-bit signed comparison that branches to LABEL4 if NUM1 < NUM2
 (similar to Example 4.1.1 in Section 4.1)

           code(" SEC");
           code(" TXA"); //           ; compare high bytes
           code(" SBC @erg+1");
           code(" BVC ?vc1"+ct); //  ; the equality comparison is in the Z flag here
           code(" EOR #$80"); //      ; the Z flag is affected here
           code("?vc1" + ct);
           code(" BMI ?tr" + ct);//  ; if NUM1H < NUM2H then NUM1 < NUM2
           code(" BVC ?vc2" + ct); // ; the Z flag was affected only if V is 1
           code(" EOR #$80"); //      ; restore the Z flag to the value it had after SBC NUM2H
           code("?vc2" + ct);
           code(" BNE ?fa" + ct); //  ; if NUM1H <> NUM2H then NUM1 > NUM2 (so NUM1 >= NUM2)
           code(" tya"); //           ; compare low bytes
           code(" SBC @erg");
           code(" BCS ?fa" + ct); //  ; if NUM1L < NUM2L then NUM1 < NUM2
           code("?tr" + ct);
             */

            code(" cpy @erg");
            code(" bne ?ne"+ct);
            code(" cpx @erg+1");
            code(" beq ?fa" + ctf);
            code("?ne"+ct);
            code(" cpy @erg");
            code(" txa");
            code(" sbc @erg+1");
            code(" bvc ?vc" + ct);
            code(" eor #$80");
            code("?vc" + ct);
            code(" bmi ?fa" + ctf);
            break;
          case ">=":
            code(" cpy @erg");
            code(" bne ?ne"+ct);
            code(" cpx @erg+1");
            code(" beq ?tr" + ct);
            code("?ne"+ct);
            code(" cpy @erg");
            code(" txa");
            code(" sbc @erg+1");
            code(" bvc ?vc" + ct);
            code(" eor #$80");
            code("?vc" + ct);
            code(" bpl ?fa" + ctf);
            code("?tr" + ct);
            break;
          }

        }
      }
      else {
        source.error(condition, "condition expected");
      }

      String mnemonic = nextSymbol.get();
      if (mnemonic.equals("OR")) {
        code(" jmp "+conditionStr); // fuer OR
        code("?fa"+ctf);
        source.incrementConditionCount();
        ++ct;
        ++ctf;

        nextSymbol = source.nextElement();
      }
      else if (mnemonic.equals("AND")) {
        source.incrementConditionCount();
        ++ct;
        nextSymbol = source.nextElement();
      }
      else {
        hasMoreCondition = false;
      }
    }
    code(" jmp "+conditionStr);
    code("?fa"+ctf);

    source.incrementConditionCount();
    return this;
  }

  public Symbol build() {
    return nextSymbol;
  }

}
