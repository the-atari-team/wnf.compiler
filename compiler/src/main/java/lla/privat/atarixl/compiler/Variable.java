// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

/**
 * class Variable dient ausschließlich dem Auslesen der Variablen im
 * eigentlichen Source.
 *
 */
public class Variable {

  private static final Logger LOGGER = LoggerFactory.getLogger(Variable.class);

  private final Source source;

  private Symbol nextSymbol;

  private String name;
  private final List<String> arrayValues;

  public Variable(Source source) {
    this.source = source;
    arrayValues = new ArrayList<>();
  }

  /**
   * interpretieren des Sources, folgendes wird verstanden
   * <li>byte name
   * <li>byte name=1
   * <li>byte array name[1]
   * <li>byte array name[1]=address
   * <li>byte array name[1]=[1,2,-3,$4]
   * <li>byte array name[1] = ['Hallo']
   * <li>string name = ['Hallo']
   * <li>word name
   * <li>word name=1
   * <li>word array name[1]
   * <li>word array name[1]=address
   * <li>word array name[1]=[1,2,3,$4,'Hallo']
   * Sonderfall (n > 255) (fat_byte_array)
   * <li>byte array name[256]
   *
   * @param Symbol next symbol in source
   * @return
   */
  public Variable variable(final Symbol symbol) {
    Type lastType = Type.UNKNOWN;
    String mnemonic = symbol.get();

    if (mnemonic.equals("BYTE")) {
      lastType = Type.BYTE;
    }
    else if (mnemonic.equals("WORD")) {
      lastType = Type.WORD;
    }
    else if (mnemonic.equals("STRING")) {
      lastType = Type.BYTE_ARRAY;
    }
    else {
      source.error(symbol, "unexpected variable type.");
    }

    nextSymbol = source.nextElement();

    boolean isVariables = true;
    while (isVariables) {
      mnemonic = nextSymbol.get();
      if (nextSymbol.getId() == SymbolEnum.variable_name) {
        name = nextSymbol.get();

        source.addVariable(name, lastType);
        nextSymbol = source.nextElement();
      }
      else if (mnemonic.equals("ARRAY")) {
        Symbol array = nextSymbol;
        source.match(array, "ARRAY");

        Symbol nameSymbol = source.nextElement();
        name = nameSymbol.get();
        Symbol arrayCount = arrayCount();
        int n = Integer.parseInt(arrayCount.get());
        lastType = makeArrayType(lastType, n);
        source.addVariable(name, lastType, n);
        nextSymbol = source.nextElement();
      }
      else {
        source.error(nextSymbol, "unexpected variable type.");
        name = null;
      }

      LOGGER.debug(lastType.name() + " " + name);

      mnemonic = nextSymbol.get();
      if (mnemonic.equals("=")) {
        Symbol squaredBrackedOpenOrNumber = source.nextElement();
        if (squaredBrackedOpenOrNumber.getId() == SymbolEnum.number) {
          String number = squaredBrackedOpenOrNumber.get();
          source.setVariableAddress(name, number);
          nextSymbol = source.nextElement();
        }
        else if (squaredBrackedOpenOrNumber.getId() == SymbolEnum.variable_name) {
          String variable = squaredBrackedOpenOrNumber.get();
          source.setVariableAddress(name, variable);

          nextSymbol = source.nextElement();
        }
        else if (squaredBrackedOpenOrNumber.get().equals("[")) {
          source.throwIfNotArrayType(lastType);
          nextSymbol = getValues(squaredBrackedOpenOrNumber, lastType);
        }
        else {
          source.error(squaredBrackedOpenOrNumber,
              "unexpected mnemonic " + squaredBrackedOpenOrNumber.get() + " only number or [ supported.");
        }
      }

      mnemonic = nextSymbol.get();
      if (mnemonic.equals(",")) {
        nextSymbol = source.nextElement();
      }
      else {
        isVariables = false;
      }
    }
    return this;
  }

  public Symbol build() {
	  // generate equates Variables (x=1)
      // or it will came to an error like "transitory"
      source.generateAllNotAlreadyGeneratedEquatesVariables();
      return nextSymbol;
	}

  private Type makeArrayType(Type type, int arrayCount) {
    if (type == Type.BYTE) {
      if (arrayCount > 255) {
        return Type.FAT_BYTE_ARRAY;
      }
      return Type.BYTE_ARRAY;
    }
    if (type == Type.WORD) {
      return Type.WORD_ARRAY;
    }
    source.error(new Symbol("", SymbolEnum.noSymbol), "unexpected array assignment");
    return null;
  }

  private Symbol getValues(final Symbol symbol, Type lastType) {
    source.match(symbol, "[");

    boolean hasValues = true;
    int localValues = 0;
    Symbol valueSymbol = source.nextElement();

    while (hasValues) {
      SymbolEnum id = valueSymbol.getId();
      if (id == SymbolEnum.variable_name) {
        ++localValues;
        String name = valueSymbol.get();
        if (name.equals("ADR")) {
          // TODO: give a hint, adr is not need here!
          Symbol doppelpunkt = source.nextElement();
          source.match(doppelpunkt, ":");
          Symbol variable = source.nextElement();
          name = variable.get();
        }
        source.throwIfVariableUndefined(name);
        source.getVariable(name).setRead();
        arrayValues.add(name);
        valueSymbol = source.nextElement();
      }
      else if (id == SymbolEnum.symbol && valueSymbol.get().equals("-")) {
        ++localValues;
        valueSymbol = source.nextElement();
        String number = "-" + valueSymbol.get();
        arrayValues.add(number);
        valueSymbol = source.nextElement();
      }
      else if (id == SymbolEnum.number) {
        ++localValues;
        String number = valueSymbol.get();
        arrayValues.add(number);
        valueSymbol = source.nextElement();
      }
      else if (id == SymbolEnum.string) {
        ++localValues;
        String string = valueSymbol.get();
        if (lastType == Type.WORD_ARRAY) {
          source.addVariable(string, Type.STRING);
          String name = "?STRING" + source.getVariablePosition(string);
          arrayValues.add(name);
        }
        else if (lastType == Type.BYTE_ARRAY) {
          arrayValues.add(string);
          arrayValues.add("255");
        }
        else {
          throw new IllegalStateException("Direct String is not allowed here.");
        }
        valueSymbol = source.nextElement();
      }

      String mnemonic = valueSymbol.get();
      if (mnemonic.equals(",")) {
        valueSymbol = source.nextElement();
      }
      else {
        hasValues = false;
      }
    }
    if (localValues > 0) {
      source.setVariableArray(name, arrayValues);
      return source.nextElement();
    }
    return valueSymbol;
  }

  private Symbol arrayCount() {
    Symbol squaredBracketOpen = source.nextElement();
    source.match(squaredBracketOpen, "[");

    Symbol arrayCount = source.nextElement();

    Symbol squaredBracketClose = source.nextElement();
    source.match(squaredBracketClose, "]");
    return arrayCount;
  }

}
