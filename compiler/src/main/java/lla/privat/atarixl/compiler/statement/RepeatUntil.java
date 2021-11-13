// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.Condition;
import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;

public class RepeatUntil extends Code {
  private static final Logger LOGGER = LoggerFactory.getLogger(RepeatUntil.class);

  private final Source source;

  private Symbol nextSymbol;

  public RepeatUntil(Source source) {
    super(source);

    this.source = source;
  }

  public int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
  }

  public RepeatUntil statement(final Symbol symbol) {
    source.match(symbol, "REPEAT");

    source.incrementLoopCount();
    int condi = source.getLoopCount();
    code("?REPEAT"+condi);

    nextSymbol = source.nextElement();

    final String exitVariable = "?RPTEXIT" + condi;
    source.addBreakVariable(exitVariable);

    boolean isRepeat = true;
    while (isRepeat) {
      String mnemonic = nextSymbol.get();
      if (mnemonic.equals("UNTIL")) {
        isRepeat = false;
        nextSymbol = source.nextElement();
      }
      else {
        nextSymbol = new Statement(source).statement(nextSymbol).build();
      }
    }
    nextSymbol = new Condition(source, exitVariable).condition(nextSymbol).build();
    code(" jmp ?REPEAT"+condi);
    code(exitVariable);
    source.clearBreakVariable();

    return this;
  }

  public Symbol build() {
    return nextSymbol;
  }

}
