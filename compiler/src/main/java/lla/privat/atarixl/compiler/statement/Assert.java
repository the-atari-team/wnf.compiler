// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.Condition;
import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;
import lla.privat.atarixl.compiler.source.StringHelper;

public class Assert extends Code {
  private static final Logger LOGGER = LoggerFactory.getLogger(Assert.class);

  private final Source source;

  private Symbol nextSymbol;

  public Assert(Source source) {
    super(source);

    this.source = source;
  }

  public void code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    codeGen(sourcecodeline);
  }



  public Assert statement(final Symbol symbol) {
    source.match(symbol, "ASSERT");

    source.incrementConditionCount();
    int condi = source.getConditionCount();
    String conditionStr = "?ASSERTTRUE"+condi;

    nextSymbol = source.nextElement();
    source.match(nextSymbol, "(");

    nextSymbol = source.nextElement();
    nextSymbol = new Condition(source, conditionStr).condition(nextSymbol).build();

    if (nextSymbol.get().equals(",")) {

      Symbol falseString = source.nextElement();
      code(" jsr @print_string");
      code(" .BYTE " + StringHelper.makeDoubleQuotedString(falseString.get()) + "," + Source.STRING_END_MARK);
      code(" jsr @eolout");

      nextSymbol = source.nextElement();
    }
    source.match(nextSymbol, ")");
    code(" winc @assert_fehler");
    code(conditionStr);

    nextSymbol = source.nextElement();
    return this;
  }

  public Symbol build() {
    return nextSymbol;
  }

}
