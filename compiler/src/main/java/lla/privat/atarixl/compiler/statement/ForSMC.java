// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.Assignment;
import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.expression.Expression;
import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;

/**
 * This is the for implementation with self modified code
 * empty byte loop takes ~21 to (24) 1/50s 14% in 32768 loops variable in zeropage
 * empty byte loop takes ~24 to (26) 1/50s  8% in 32768 loops
 * empty word loop takes ~35 to (41) 1/50s 17% in 32768 loops variable in zeropage
 * empty word loop takes ~39 to (45) 1/50s 15% in 32768 loops
 *
 * Self modified code and zero page is up to 28% faster (empty loops word value)
 * Self modified code and zero page is up to 24% faster (empty loops byte value)
 */
public class ForSMC extends Code {
  private static final Logger LOGGER = LoggerFactory.getLogger(ForSMC.class);

  private final Source source;

  private Symbol nextSymbol;

  private int step;

  public ForSMC(Source source) {
    super(source);

    this.source = source;
    step = 0;
  }

  public int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
  }


  /**
   * Interpretiert eine for - schleife
   * <li>FOR variable := start-expression TO end-expression DO statement
   * <li>FOR variable := start-expression DOWNTO end-expression DO statement
   *
   * @param symbol
   * @return
   */
  public ForSMC statement(Symbol symbol) {
    source.match(symbol, "FOR");

    Symbol variableSymbol = source.nextElement();

    String variable = variableSymbol.get();
    source.throwIfVariableUndefined(variable);

    Type typ = source.getVariableType(variable);

    Symbol toOrDownto = new Assignment(source).assign(variableSymbol).build();

    if (toOrDownto.get().equals("TO")) {
      nextSymbol = source.nextElement();
      step = 1;
    }
    else if (toOrDownto.get().equals("DOWNTO")) {
      nextSymbol = source.nextElement();
      step = -1;
    }
    else {
      source.error(toOrDownto, "TO or DOWNTO expected");
    }
    nextSymbol = new Expression(source).expression(nextSymbol).build();
    source.match(nextSymbol, "DO");

    source.incrementLoopCount();
    int condi = source.getLoopCount();

    // Zuweisung an y,x variable
    code(" sty ?smcfor1lo" + condi + "+1");
    code(" sty ?smcfor2lo" + condi + "+1");
    if (typ == Type.WORD) {
      if (source.getTypeOfLastExpression().getBytes() == 1) {
        code(" ldx #0");
      }
      code(" stx ?smcfor1hi" + condi + "+1");
      code(" stx ?smcfor2hi" + condi + "+1");
    }

    if (step == 1) {
      code("?smcfor1lo"+condi);
      code(" ldy #0");
      code(" cpy " + variable);
      if (typ == Type.BYTE) {
        code(" bcs ?go" + condi);
      }
      else {
        code("?smcfor1hi"+condi);
        code(" lda #0");
        code(" sbc " + variable + "+1");
        code(" bcs ?go" + condi);
      }
    }
    else {
      // step == -1
      code(" ldy " + variable);
      code("?smcfor1lo"+condi);
      code(" cpy #0");
      if (typ == Type.BYTE) {
        code(" bcs ?go" + condi);
      }
      else {
        code(" lda " + variable + "+1");
        code("?smcfor1hi"+condi);
        code(" sbc #0");
        code(" bcs ?go" + condi);
      }
    }
    final String exitVariable = "?exit" + condi;
    code(" jmp "+exitVariable);
    source.addBreakVariable(exitVariable);
//    source.addVariable("?FOR" + condi, typ);
//    source.getVariable("?FOR" + condi).setRead();
    code("?go" + condi);

    nextSymbol = source.nextElement();
    nextSymbol = new Statement(source).statement(nextSymbol).build();

    if (typ == Type.WORD) {
      code(" lda " + variable + "+1");
      code("?smcfor2hi"+condi);
      code(" cmp #0");
      code(" bne ?next" + condi);
    }
    if (step == 1) {
      code(" lda " + variable);
      code("?smcfor2lo"+condi);
      code(" cmp #0");
    }
    else {
      code("?for2lo"+condi);
      code(" LDA #0");
      code(" CMP " + variable);
    }
    code(" BCS ?EXIT" + condi);
    if (typ == Type.WORD) {
      code("?NEXT" + condi);
    }
//    REM  ? #KAN;"; Richtung der Schleife ";STEP
    if (step == 1) {
      code(" INC " + variable);
      if (typ == Type.WORD) {
        code(" BNE ?LOOP" + condi);
        code(" INC " + variable + "+1");
        code("?LOOP" + condi);
      }
    }
    else {
      if (typ == Type.WORD) {
        code(" LDA " + variable);
        code(" BNE ?LOOP" + condi);
        code(" DEC " + variable + "+1");
        code("?LOOP" + condi);
      }
      code(" DEC " + variable);
    }
    code(" JMP ?GO" + condi);
//    REM  ? #KAN;"; For ende"
    code(exitVariable);
    source.clearBreakVariable();

    return this;
  }

  public Symbol build() {
    return nextSymbol;
  }
}
