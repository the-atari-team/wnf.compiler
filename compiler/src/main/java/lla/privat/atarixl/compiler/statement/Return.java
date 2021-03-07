// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.expression.Expression;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;

public class Return extends Code {
  private static final Logger LOGGER = LoggerFactory.getLogger(Return.class);

  private final Source source;

  private Symbol nextSymbol;

  public Return(Source source) {
    super(source);

    this.source = source;
  }

  public int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
  }




  public Return statement(Symbol symbol) {
    source.match(symbol, "RETURN");

    nextSymbol = source.nextElement();
    nextSymbol = new Expression(source).expression(nextSymbol).build();

    if (source.getErgebnis().getBytes() == 1) {
      code(" ldx #0");
    }
    code(" jmp ?RETURN"+source.getReturnCount());
    return this;
  }

  public Symbol build() {
    return nextSymbol;
  }

}
