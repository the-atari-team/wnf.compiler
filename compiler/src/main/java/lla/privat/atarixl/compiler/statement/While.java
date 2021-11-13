// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.Condition;
import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;

public class While extends Code {
  private static final Logger LOGGER = LoggerFactory.getLogger(While.class);

  private final Source source;

  private Symbol nextSymbol;

  public While(Source source) {
    super(source);

    this.source = source;
  }

  public int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
  }


  public While statement(Symbol symbol) {
    source.match(symbol, "WHILE");

    source.incrementLoopCount();
    int condi = source.getLoopCount();
    code("?while"+condi);

    String conditionStr = "?THEN" + condi;

    nextSymbol = source.nextElement();
    Symbol condition = new Condition(source, conditionStr).condition(nextSymbol).build();
    source.match(condition, "DO");

    final String exitVariable = "?wend" + condi;
    code(" jmp "+exitVariable);
    source.addBreakVariable(exitVariable);
    code(conditionStr);

    nextSymbol = source.nextElement();
    nextSymbol = new Statement(source).statement(nextSymbol).build();

    code(" jmp ?while"+condi);
    code(exitVariable);
    source.clearBreakVariable();
    return this;
  }

  public Symbol build() {
    return nextSymbol;
  }
}
