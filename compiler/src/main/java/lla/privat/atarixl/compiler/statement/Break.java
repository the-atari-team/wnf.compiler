// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;

public class Break extends Code {
  private static final Logger LOGGER = LoggerFactory.getLogger(Break.class);

  private final Source source;

  private Symbol nextSymbol;

  public Break(Source source) {
    super(source);

    this.source = source;
  }

  public int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
  }

  public Break statement(Symbol symbol) {
    source.match(symbol, "BREAK");

    code(" jmp "+ source.getBreakVariable());
    nextSymbol = source.nextElement();
    return this;
  }

  public Symbol build() {
    return nextSymbol;
  }

}
