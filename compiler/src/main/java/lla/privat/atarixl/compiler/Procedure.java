// cdw by 'The Atari Team' 2022
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
  private int innerCallParameterPosition; // position of current parameter, will start at 1 and +=2 for every parameter
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
    LOGGER.debug("{} {}", procedureType.name(), procedureName.get());
    source.throwIfVariableAlreadyDefined(procedureName.get());

    this.procedureType = procedureType;
    source.addVariable(procedureName.get(), procedureType);

    Symbol leftParenthesis = source.nextElement(); // (
    source.match(leftParenthesis, "(");

    final String name = procedureName.get();
    // the simple name
    code(name);

    // the name with '_i*' if the procedure contains parameters
    int namePosition = code("; " + name);

    source.incrementReturnCount();

    Symbol rightParenthesisOrVar = source.nextElement(); // ) or variable_name

    innerCallParameterPosition = 1;
    symbol = getCallParameter(source, rightParenthesisOrVar, name, namePosition);

    if (symbol.get().equals("LOCAL")) {
      localVariableCount = 1;
      symbol = getLocalVariables(symbol);
    }
    code(";#1 " + procedureType.getName() + " body");

    nextSymbol = new Statement(source).statement(symbol).build();

    code(";#1 " + procedureType.getName() + " end");
    code("?RETURN" + source.getReturnCount());

    freeLocalVariables();
    freeCallVariables();

    if (yregistersafe) {
      code(" ldy @reg+2");
    }
    code(" rts");

    return this;
  }

  private boolean isSaveLocalToStack() {
    return source.getOptions().isSaveLocalToStack();
  }

  private void freeLocalVariables() {
    if (localVariableCount > 1) {
      yregistersafe = false;
      if (procedureType == Type.FUNCTION) {
        if (!isSaveLocalToStack()) {
          code(" sty @reg+2");
          yregistersafe = true;
        }
      }
      if (!isSaveLocalToStack()) {
        source.sub_from_heap_ptr(localVariableCount);
      }

      while (localVariableCount > 1) {
        String variable = localVariables.pop();
        Type typ = source.getVariableType(variable);
        localVariableCount -= 2;
        if (isSaveLocalToStack()) {
          if (typ.getBytes() == 2) {
            code(" pla");
            code(" sta " + variable + "+1");
          }          
          code(" pla");          
          code(" sta " + variable);
        }
        else {
          code(" ldy #" + localVariableCount);
          code(" lda (@heap_ptr),y");
          code(" sta " + variable);
          if (typ.getBytes() == 2) {
            code(" iny");
            code(" lda (@heap_ptr),y");
            code(" sta " + variable + "+1");
          }
        }
      }
    }
  }

  private void freeCallVariables() {
    if (innerCallParameterPosition > 1) {
      if (procedureType == Type.FUNCTION && yregistersafe == false) {
        code(" sty @reg+2");
        yregistersafe = true;
      }
      source.sub_from_heap_ptr(innerCallParameterPosition);

      while (innerCallParameterPosition > 1) {
        String variable = callVariables.pop();
        Type typ = source.getVariableType(variable);
        innerCallParameterPosition -= 2;
        code(" ldy #" + innerCallParameterPosition);
        code(" lda (@heap_ptr),y");
        code(" sta " + variable);
        if (typ.getBytes() == 2) {
          code(" iny");
          code(" lda (@heap_ptr),y");
          code(" sta " + variable + "+1");
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
        if (typ.getBytes() > 2) {
          source.error(nextSymbol, "LOCAL variable " + variable + " must be 1 or 2 bytes long, no arrays");
        }
        if (isSaveLocalToStack()) {
          code(" lda " + variable);
          code(" pha");          
          if (typ.getBytes() == 2) {
            code(" lda " + variable + "+1");
            code(" pha");
          }          
        }
        else {
          code(" lda " + variable);
          code(" ldy #" + localVariableCount);
          code(" sta (@heap_ptr),y");
          if (typ.getBytes() == 2) {
            code(" iny");
            code(" lda " + variable + "+1");
            code(" sta (@heap_ptr),y");
          }
        }
        localVariableCount += 2;
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
    if (!isSaveLocalToStack()) {
      if (localVariableCount > 1) {
          source.add_to_heap_ptr(localVariableCount);
      }
    }
    return nextSymbol;
  }

  private Symbol getCallParameter(Source source, final Symbol symbol, String name, int namePosition) {
    boolean isMoreParameter = true;
    Symbol nextSymbol = symbol;
    int countOfParameters = 0;

    while (isMoreParameter) {
      SymbolEnum id = nextSymbol.getId();
      if (id == SymbolEnum.variable_name) {
        Symbol variableName = nextSymbol;

        source.throwIfVariableUndefined(variableName.get());
        ++countOfParameters;

        String variable = variableName.get();
        code(" ldx " + variable);
        code(" ldy #" + innerCallParameterPosition);
        code(" lda (@heap_ptr),y");
        code(" sta " + variable);
        code(" txa");
        code(" sta (@heap_ptr),y");
        if (source.getVariableType(variable).getBytes() == 2) {
          code(" iny");
          code(" ldx " + variable + "+1");
          code(" lda (@heap_ptr),y");
          code(" sta " + variable + "+1");
          code(" txa");
          code(" sta (@heap_ptr),y");
        }
        innerCallParameterPosition += 2;
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
    if (innerCallParameterPosition > 1) {
        source.add_to_heap_ptr(innerCallParameterPosition);
    }

    if (countOfParameters > 0) {
      final String nameWithParameters = source.generateFunctionNameWithParameters(name, countOfParameters);
      source.replaceCode(namePosition, nameWithParameters);
    }
    return source.nextElement();
  }

  public int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
  }

  public Symbol build() {

    return nextSymbol;
  }
}
