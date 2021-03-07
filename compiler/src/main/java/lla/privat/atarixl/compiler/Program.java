// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;

public class Program extends Code {

  private static final Logger LOGGER = LoggerFactory.getLogger(Program.class);

  private Source source;

  private Symbol nextSymbol;

  public Program(Source source) {
    super(source);

    this.source = source;
  }

  public Program program(Symbol symbol) {
    String first = symbol.get();

    if (first.equals("PROGRAM")) {
      Symbol name = source.nextElement(); // name
      source.setProgramOrIncludeName(name.get());
      source.setLomem("$4000"); // default start address
      source.setProgram(true);

      if (source.peekSymbol().get().equals("LOMEM")) {
        /* Symbol lomem = */ source.nextElement();
        Symbol assign = source.nextElement();
        source.match(assign, "=");

        Symbol address = source.nextElement(); // address
        source.throwIsValueNotANumber(address);
        source.setLomem(address.get());
      }
    }
    else if (first.equals("INCLUDE")) {
      Symbol prefix = source.nextElement(); // prefix
      Symbol name;
      source.setProgram(false);

      if (source.peekSymbol().get().equals(":")) {
        Symbol colon = source.nextElement(); // :
        source.match(colon, ":");

        name = source.nextElement(); // name
        source.throwIfNotVariableName(name);
        source.setPrefix(prefix.get());
      }
      else {
        name = prefix;
        source.setPrefix("");
      }
      source.setProgramOrIncludeName(name.get());
    }
    else {
      source.error(symbol, "PROGRAM or INCLUDE expected");
    }

    nextSymbol = source.nextElement();

    return this;
  }

  public int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
  }

  public Symbol build() {
    return nextSymbol;
  }

}
