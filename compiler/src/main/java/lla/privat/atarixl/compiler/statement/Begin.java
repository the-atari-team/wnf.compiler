// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.source.Source;

public class Begin {
  private final Source source;

  private Symbol nextSymbol;

  public Begin(Source source) {
    this.source = source;
  }

  public Begin statement(Symbol symbol) {
    source.match(symbol, "BEGIN");

    nextSymbol = source.nextElement();
    do {
      if (!nextSymbol.get().equals("END")) {
        nextSymbol = new Statement(source).statement(nextSymbol).build();
      }
    } while (!nextSymbol.get().equals("END"));

    nextSymbol = source.nextElement();
    return this;
  }

  public Symbol build() {
    return nextSymbol;
  }
}
