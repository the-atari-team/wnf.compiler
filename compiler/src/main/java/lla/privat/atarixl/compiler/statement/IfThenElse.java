// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.Condition;
import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;

public class IfThenElse extends Code {
  private static final Logger LOGGER = LoggerFactory.getLogger(IfThenElse.class);

  private final Source source;

  private Symbol nextSymbol;

  public IfThenElse(Source source) {
    super(source);

    this.source = source;
  }

  public int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
  }



  public IfThenElse statement(final Symbol symbol) {
    source.match(symbol, "IF");
    // if condition * then statement else statement
    source.incrementLoopCount();
    int condi = source.getLoopCount();
    String conditionStr = "?THEN" + condi;

    nextSymbol = source.nextElement();
    Symbol condition = new Condition(source, conditionStr).condition(nextSymbol).build();
    source.match(condition, "THEN");

    // Due to the fact this is a single pass Compiler, we need such lines for the assembler
    code(" .if .not .def ?else" + condi);
    code(" jmp ?endif" + condi);
    code(" .else");
    code(" jmp ?else" + condi);
    code(" .endif");
    code(conditionStr);

    nextSymbol = source.nextElement();
    nextSymbol = new Statement(source).statement(nextSymbol).build();

    if (nextSymbol.get().equals("ELSE")) {
      code(" jmp ?endif" + condi);
      code("?else"+condi);

      nextSymbol = source.nextElement();
      nextSymbol = new Statement(source).statement(nextSymbol).build();
    }
    code("?endif"+condi);
    return this;
  }

  public Symbol build() {
    return nextSymbol;
  }

}
