// cdw by 'The Atari Team' 2020
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

public class For extends Code {
  private static final Logger LOGGER = LoggerFactory.getLogger(For.class);

  private final Source source;

  private Symbol nextSymbol;

  private int step;

  public For(Source source) {
    super(source);

    this.source = source;
    step = 0;
  }

  public void code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    codeGen(sourcecodeline);
  }


  /**
   * Interpretiert eine for - schleife
   * <li>FOR variable := start-expression TO end-expression DO statement
   * <li>FOR variable := start-expression DOWNTO end-expression DO statement
   *
   * @param symbol
   * @return
   */
  public For statement(Symbol symbol) {
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
    code(" sty ?for" + condi);
    if (typ == Type.WORD) {
      if (source.getErgebnis().getBytes() == 1) {
        code(" ldx #0");
      }
      code(" stx ?for" + condi + "+1");
    }

    if (step == 1) {
      code(" ldy ?for" + condi);
      code(" cpy " + variable);
      if (typ == Type.BYTE) {
        code(" bcs ?go" + condi);
      }
      else {
        code(" lda ?for" + condi + "+1");
        code(" sbc " + variable + "+1");
        code(" bcs ?go" + condi);
      }
    }
    else {
      // step == -1
      code(" ldy " + variable);
      code(" cpy ?for" + condi);
      if (typ == Type.BYTE) {
        code(" bcs ?go" + condi);
      }
      else {
        code(" lda " + variable + "+1");
        code(" sbc ?for" + condi + "+1");
        code(" bcs ?go" + condi);
      }
    }
    code(" jmp ?exit" + condi);
    source.addVariable("?FOR" + condi, typ);
    source.getVariable("?FOR" + condi).setRead();
    code("?go" + condi);

    nextSymbol = source.nextElement();
    nextSymbol = new Statement(source).statement(nextSymbol).build();

    if (typ == Type.WORD) {
      code(" lda " + variable + "+1");
      code(" cmp ?for" + condi + "+1");
      code(" bne ?next" + condi);
    }
    if (step == 1) {
      code(" lda " + variable);
      code(" cmp ?for" + condi);
    }
    else {
      code(" LDA ?FOR" + condi);
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
    code("?EXIT" + condi);

    return this;
  }

  public Symbol build() {
    return nextSymbol;
  }
}
