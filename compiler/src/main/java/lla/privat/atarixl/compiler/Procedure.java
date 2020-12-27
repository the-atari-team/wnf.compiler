// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;
import lla.privat.atarixl.compiler.statement.Statement;

public class Procedure extends Code {

  private static final Logger LOGGER = LoggerFactory.getLogger(Procedure.class);

  private final Source source;

  private Symbol nextSymbol;

  private Type procedureType;
  private int callParameterCount; // counter for parameters
  private int localVariableCount; // counter for local variables

  boolean yregistersafe = false;

  Stack<String> callVariables;
  Stack<String> localVariables;

  public Procedure(Source source) {
    super(source);

    this.source = source;
    callVariables = new Stack<>();
    localVariables = new Stack<>();
  }

  public Procedure procedure(Symbol symbol, Type procedureType) {
    source.setShowCode(true);
    Symbol procedureName = source.nextElement(); // name
    LOGGER.debug("{} {}",procedureType.name(), procedureName.get());
    source.throwIfVariableAlreadyDefined(procedureName.get());

    this.procedureType = procedureType;
    source.addVariable(procedureName.get(), procedureType);

    Symbol leftParenthesis = source.nextElement(); // (
    source.match(leftParenthesis, "(");

    code(procedureName.get());

    source.incrementReturnCount();

    Symbol rightParenthesisOrVar = source.nextElement(); // ) or variable_name

    callParameterCount = 1;
    symbol = getCallParameter(source, rightParenthesisOrVar);

    if (symbol.get().equals("LOCAL")) {
      localVariableCount = 1;
      symbol = getLocalVariables(symbol);
    }
    code(";#1 " + procedureType.getName() + " body");

    nextSymbol = new Statement(source).statement(symbol).build();

    code(";#1 " + procedureType.getName() + " end");
    code("?RETURN"+source.getReturnCount());

    freeLocalVariables();
    freeCallVariables();

    if (yregistersafe) {
      code(" ldy @reg+2");
    }
    code(" rts");

    return this;
  }

  private void freeLocalVariables() {
    if (localVariableCount > 1) {
      yregistersafe = false;
      if (procedureType == Type.FUNCTION) {
        code(" sty @reg+2");
        yregistersafe = true;
      }
      code(" sub_from_heap_ptr "+localVariableCount);
      while (localVariableCount > 1) {
        String variable = localVariables.pop();
        Type typ = source.getVariableType(variable);
        localVariableCount -= 2;
        code(" ldy #"+localVariableCount);
        code(" lda (@heap_ptr),y");
        code(" sta "+variable);
        if (typ == Type.WORD) {
          code(" iny");
          code(" lda (@heap_ptr),y");
          code(" sta "+variable+"+1");
        }
      }
    }
  }

  private void freeCallVariables() {
    if (callParameterCount > 1) {
      if (procedureType == Type.FUNCTION && yregistersafe==false) {
        code(" sty @reg+2");
        yregistersafe = true;
      }
      code(" sub_from_heap_ptr "+callParameterCount);

      while (callParameterCount > 1) {
        String variable = callVariables.pop();
        Type typ = source.getVariableType(variable);
        callParameterCount -= 2;
        code(" ldy #"+callParameterCount);
        code(" lda (@heap_ptr),y");
        code(" sta "+variable);
        if (typ == Type.WORD) {
          code(" iny");
          code(" lda (@heap_ptr),y");
          code(" sta "+variable+"+1");
        }
      }
    }
  }

  private Symbol getLocalVariables(final Symbol symbol) {
    boolean hasLocalVariables = true;
    Symbol nextSymbol = source.nextElement();
    while (hasLocalVariables) {
      SymbolEnum id = nextSymbol.getId();
      if (id == SymbolEnum.variable_name) {
        String variable = nextSymbol.get();
        source.throwIfVariableUndefined(variable);

        Type typ = source.getVariableType(variable);
        if (typ != Type.BYTE && typ != Type.WORD) {
          source.error(nextSymbol, "in LOCAL variable "+variable+" must be BYTE of WORD");
        }
        code(" lda " + variable);
        code(" ldy #"+localVariableCount);
        code(" sta (@heap_ptr),y");
        if (typ == Type.WORD) {
          code(" iny");
          code(" lda " + variable + "+1");
          code(" sta (@heap_ptr),y");
        }
        localVariableCount+=2;
        localVariables.push(variable);
        nextSymbol = source.nextElement();
      }
      String mnemonic = nextSymbol.get();
      if (mnemonic.equals(",")) {
        nextSymbol = source.nextElement();
      }
      else {
        hasLocalVariables = false;
      }
    }
    if (localVariableCount > 1) {
      code(" add_to_heap_ptr "+localVariableCount);
    }
    return nextSymbol;
  }

  private Symbol getCallParameter(Source source, final Symbol symbol) {
    boolean isMoreParameter = true;
    Symbol nextSymbol = symbol;
    while (isMoreParameter) {
      SymbolEnum id = nextSymbol.getId();
      if (id == SymbolEnum.variable_name) {
        Symbol name = nextSymbol;

        source.throwIfVariableUndefined(name.get());

        String variable = name.get();
        code(" ldx " + variable);
        code(" ldy #" + callParameterCount);
        code(" lda (@heap_ptr),y");
        code(" sta " + variable);
        code(" txa");
        code(" sta (@heap_ptr),y");
        if (source.getVariableType(variable) == Type.WORD) {
          code(" iny");
          code(" ldx " + variable + "+1");
          code(" lda (@heap_ptr),y");
          code(" sta " + variable + "+1");
          code(" txa");
          code(" sta (@heap_ptr),y");
        }
        callParameterCount+=2;
        callVariables.push(variable);
        nextSymbol = source.nextElement(); // , )
      }

      String mnemonic = nextSymbol.get();
      if (mnemonic.equals(",")) {
        nextSymbol = source.nextElement();
      }
      else {
        source.match(nextSymbol, ")");
        isMoreParameter = false;
      }
    }
    if (callParameterCount > 1) {
      code(" add_to_heap_ptr "+callParameterCount);
    }
    return source.nextElement();
  }

  public void code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    codeGen(sourcecodeline);
  }





  public Symbol build() {

    return nextSymbol;
  }
}
