// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.Assignment;
import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.VariableDefinition;
import lla.privat.atarixl.compiler.expression.Expression;
import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class Statement {
  private static final Logger LOGGER = LoggerFactory.getLogger(Statement.class);

  private final Source source;

  private Symbol nextSymbol;

  public Statement(Source source) {
    this.source = source;
  }

  public Statement statement(final Symbol symbol) {
    source.setShowCode(true);
    String mnemonic = symbol.get();
    LOGGER.debug("Mnemonic: " + mnemonic);

    if (mnemonic.equals("BEGIN")) {
      source.setShowCode(false);
      nextSymbol = new Begin(source).statement(symbol).build();
    }
    else if (mnemonic.equals("IF")) {
      nextSymbol = new IfThenElse(source).statement(symbol).build();
    }
    else if (mnemonic.equals("WHILE")) {
      nextSymbol = new While(source).statement(symbol).build();
    }
    else if (mnemonic.equals("REPEAT")) {
      nextSymbol = new RepeatUntil(source).statement(symbol).build();
    }
    else if (mnemonic.equals("FOR")) {
      if (source.isSelfModifiedCode()) {
        nextSymbol = new ForSMC(source).statement(symbol).build();
      }
      else {
        nextSymbol = new For(source).statement(symbol).build();
      }
    }
    else if (mnemonic.equals("ASSERT")) {
      source.incrementAsserts();
      nextSymbol = new Assert(source).statement(symbol).build();
    }
    else if (mnemonic.equals("RETURN")) {
      nextSymbol = new Return(source).statement(symbol).build();
    }
    else if (mnemonic.equals("BREAK")) {
      nextSymbol = new Break(source).statement(symbol).build();
    }
    else if (mnemonic.equals("@(") && symbol.getId() == SymbolEnum.symbol) {
      LOGGER.debug("function pointer call");
      nextSymbol = functionCall(symbol);
    }
    else if (symbol.getId() == SymbolEnum.variable_name) {
      String name = symbol.get();

      VariableDefinition variable = source.getVariable(symbol.get());
      if (name.startsWith("@")) {
        // void call
        LOGGER.debug("is a function call to {}", name);
        nextSymbol = functionCall(symbol);
      }
      else if (variable.getType() == Type.PROCEDURE) {
        // procedure (void) call
        LOGGER.debug("is a procedure call to {}", name);
        nextSymbol = functionCall(symbol);
      }
      else {
        source.throwIfVariableUndefined(name);

        LOGGER.debug("is Variable assignment to {}", name);
        nextSymbol = new Assignment(source).assign(symbol).build();
      }
    }
    else {
      if (symbol.getId() == SymbolEnum.noSymbol) {
        source.error(symbol, "statement expected");
      }
      source.error(symbol, "unknown statement");
    }
    return this;
  }

  private Symbol functionCall(Symbol symbol) {
    Symbol nextSymbol = new Expression(source).expression(symbol).build();
    return nextSymbol;
  }

  public Symbol build() {

    return nextSymbol;
  }
}
