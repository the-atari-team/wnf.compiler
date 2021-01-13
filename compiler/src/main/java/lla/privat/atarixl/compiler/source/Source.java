// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.source;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.SymbolTokenizer;
import lla.privat.atarixl.compiler.VariableDefinition;
import lla.privat.atarixl.compiler.expression.Type;

/**
 * Source is the "top" class, holder for
 * <li>the wnf-source
 * <li>will be filled with all variables
 * <li>contains the assembler code
 *
 * @author develop
 *
 */
public class Source implements Enumeration<Symbol> {

  private static final Logger LOGGER = LoggerFactory.getLogger(Source.class);

  public static final String STRING_END_MARK = "255";

  private final SymbolTokenizer programTokenizer;
  private String programOrIncludeName;

  // true when we are a program, else we are an include
  private boolean isProgram;

  // Start address of program, default is $4000 (set in class Program)
  private String startAddress;

  private boolean showCode;

  private int verboseLevel;

  private final List<String> includePaths;

  // HashMap zum schnelleren Auffinden von Variablen
  private Map<String, VariableDefinition> variables;

  // Liste der Variablen in immer der selben Reihenfolge, ist für das Erstellen
  // des Codes wichtig!
  private List<String> variableList;

  private List<String> assemblerCodeList;

  // Global Counter
  private int nowinc = 0;

  // Ergebnis der letzten Expression
  private Type ergebnis;

  public Source(final String program) {
    this.showCode = false;
    this.programTokenizer = new SymbolTokenizer(program + " "); // TODO: Das Whitespace am Ende macht vieles einfacher!
    variables = new HashMap<>();

    variableList = new ArrayList<>();

    assemblerCodeList = new ArrayList<>();
    this.verboseLevel = 0;

    this.includePaths = new ArrayList<>();
  }

  /**
   * Der Name des zu erstellenden Programs kommt aus der 1. Zeile PROGRAM <name>
   *
   * @return string
   */
  public String getProgramOrIncludeName() {
    return programOrIncludeName;
  }

  public void setProgramOrIncludeName(String name) {
    this.programOrIncludeName = name;
  }

  /**
   * Der Prefix innerhalb der Includes kommt aus der 1. Zeile INCLUDE
   * <prefix>:<name>
   *
   * @return string
   */
  public String getPrefix() {
    return programTokenizer.getPrefix();
  }

  public void setPrefix(String prefix) {
    programTokenizer.setPrefix(prefix);
  }

  /**
   * die Startadresse kommt aus der 2. Zeile LOMEM=<addresse> Ist LOMEM nicht
   * angegeben, wird $4000 zurückgeliefert (Default)
   *
   * @return start address of Program
   */
  public String getLomem() {
    return startAddress;
  }

  public void setLomem(String startAddress) {
    this.startAddress = startAddress;
  }

  public boolean isProgram() {
    return isProgram;
  }

  public void setProgram(boolean isProgram) {
    this.isProgram = isProgram;
  }

  public String getExtension() {
    return isProgram() == true ? ".ASM" : ".INC";
  }

  public int getVerboseLevel() {
    return verboseLevel;
  }

  public Source setVerboseLevel(int verboseLevel) {
    this.verboseLevel = verboseLevel;
    return this;
  }

//
//                                                   OO        OOO                               OOOOOO                   OO
//                                                   OO         OO                              OO    OO                  OO
//  OOOOOOOO  OOOOOOO   OOOOOOO   OOOOOOO  OOOO OOO  OOOOOOOO   OO   OOOOOOO  OOOOOOOO          OO        OOOOOOO   OOOOOOOO  OOOOOOO
// OO     OO OO        OO        OO     OO OO OOO OO OO     OO  OO  OO     OO OO     OO         OO       OO     OO OO     OO OO     OO
// OO     OO  OOOOOOO   OOOOOOO  OOOOOOOOO OO  O  OO OO     OO  OO  OOOOOOOOO OO                OO       OO     OO OO     OO OOOOOOOOO
// OO     OO        OO        OO OO        OO  O  OO OO     OO  OO  OO        OO                OO    OO OO     OO OO     OO OO
//  OOOOOOOO  OOOOOOO   OOOOOOO   OOOOOOO  OO  O  OO OOOOOOOO  OOOO  OOOOOOO  OO                 OOOOOO   OOOOOOO   OOOOOOOO  OOOOOOO
//

  // add one line to the assembler code
  public void code(String sourcecode) {
    assemblerCodeList.add(sourcecode);
  }

  // add a bunch of lines to the assembler code
  public void code(List<String> sourcecodeList) {
    for (String item : sourcecodeList) {
      assemblerCodeList.add(item);
    }
  }

  public List<String> getCode() {
    return assemblerCodeList;
  }

  public void resetCode(List<String> newList) {
    assemblerCodeList = newList;
  }

  public boolean isShowCode() {
    return showCode;
  }

  public void setShowCode(boolean showCode) {
    this.showCode = showCode;
  }

  public void showCodeLine() {
    code(";#1 ");
    final String codeLine = programTokenizer.getCodeLine();
    code(";#1 " + codeLine);
    code(";#1 ");
  }

  public void show() {
    assemblerCodeList.forEach(x -> LOGGER.info(x));
  }

//
//  OOOOOO                      OO                  OOO
// OO                           OO                   OO
// OO       OO     OO OOOO OOO  OOOOOOOO   OOOOOOO   OO
//  OOOOOO  OO     OO OO OOO OO OO     OO OO     OO  OO
//       OO OO     OO OO  O  OO OO     OO OO     OO  OO
//       OO  OOOOOOOO OO  O  OO OO     OO OO     OO  OO
//  OOOOOO         OO OO  O  OO OOOOOOOO   OOOOOOO  OOOO
//          OOOOOOOO

  /**
   * returns the next Symbol, without increment the internal index
   *
   * @return Symbol next possible Symbol
   */
  public Symbol peekSymbol() {
    return programTokenizer.peekSymbol();
  }

  /**
   * getLine() returns the current source line in which the internal index stays
   * maybe helpful for errors
   *
   * @return int current source line
   */
  public int getLine() {
    return programTokenizer.getLineNumber();
  }

  /**
   * Checks the Symbol, if it is expected
   *
   * @param symbol
   * @param expectedSymbol as String
   */

  public void match(Symbol symbol, String expectedSymbol) {
    if (!symbol.get().equals(expectedSymbol)) {
      error(symbol, "current Symbol is not the expected " + expectedSymbol);
    }
  }

  /**
   * nextElement() returns the next Symbol after the internal index. The internal
   * index will move after this symbol.
   *
   * @return next Symbol
   */
  @Override
  public Symbol nextElement() {
    boolean showSourceCode = showCode;

    final Symbol nextSymbol = programTokenizer.handleShowCode(showCode).nextElement();

    if (showSourceCode) {
      code(";");
      code("; " + programTokenizer.getSourceCodeLine());
      code(";");
      showCode = false;
    }
    return nextSymbol;
  }

  @Override
  public boolean hasMoreElements() {
    return this.programTokenizer.hasMoreElements();
  }

//
// OO    OO                      OO            OO        OOO
// OO    OO                                    OO         OO
// OO    OO  OOOOOOOO OOOOOOOO  OOO   OOOOOOOO OOOOOOOO   OO   OOOOOOO   OOOOOOO
// OO    OO OO     OO OO     OO  OO  OO     OO OO     OO  OO  OO     OO OO
// OO    OO OO     OO OO         OO  OO     OO OO     OO  OO  OOOOOOOOO  OOOOOOO
//  OO  OO  OO     OO OO         OO  OO     OO OO     OO  OO  OO               OO
//   OOOO    OOOOOOOO OO        OOOO  OOOOOOOO OOOOOOOO  OOOO  OOOOOOO   OOOOOOO
//

  /**
   * Fügt der internen Liste der genutzten Variablen eine neue hinzu Gibt es die
   * Variable schon, wird ein Fehler erzeugt Die Variable wird intern in 2 Listen
   * gehalten, einem Hash zum schnellen wiederfinden und einer Liste
   *
   * @param name
   * @param type
   */
  public void addVariable(String name, Type type) {
    addVariable(name, type, 0);
  }

  /**
   * Fügt der internen Liste der genutzten Variablen eine neue hinzu
   *
   * @param name
   * @param type
   */
  public void addVariable(String name, Type type, int countArray) {
    if (variables.containsKey(name)) {
      if (name.startsWith("@") && type == Type.FUNCTION) {
        return;
      }
      if (type == Type.STRING) {
        return;
      }
      error(new Symbol(name, SymbolEnum.noSymbol), "variable already defined");
    }
    final VariableDefinition newVariable = new VariableDefinition(name, type, countArray);
    variables.put(name, newVariable);
    variableList.add(name);
  }

  /**
   * liefert true, falls die Variable schon existiert
   *
   * @param variable
   * @return true if already exist
   */
  public boolean hasVariable(String variable) {
    return variables.containsKey(variable);
  }

  /**
   * liefert die Position der Variable innerhalb der Liste, diese ist immer gleich
   * wird für die Expressions gebraucht
   *
   * @param name
   * @return position innerhalb der Liste
   */
  public int getVariablePosition(String name) {
    for (int i = 0; i < variableList.size(); i++) {
      if (variableList.get(i).equals(name)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * liefert zu einer Variablen dessen Definition
   *
   * @param name
   * @return
   */
  public VariableDefinition getVariable(String name) {
    if (variables.containsKey(name)) {
      return variables.get(name);
    }
    return new VariableDefinition("", Type.UNKNOWN);
  }

  /**
   * liefert zu einer Variablen dessen Type
   *
   * @param name
   * @return
   */
  public Type getVariableType(String name) {
    return getVariable(name).getType();
  }

  /**
   * liefert zu einer Variablen dessen sizeof
   *
   * @param name
   * @return
   */

  public int getVariableSize(String name) {
    return getVariable(name).getType().getBytes();
  }

  /**
   * liefert zu einer Position den Variablennamen
   *
   * @param position
   * @return
   */
  public String getVariableAt(int position) {
    String variableName = variableList.get(position);
    if (getVariable(variableName).getType() == Type.STRING) {
      return "?STRING" + String.valueOf(position);
    }
    return variableName;
  }

  public int countOfVariables() {
    return variables.size();
  }

  public int getVariableArraySize(String name) {
    if (variables.containsKey(name)) {
      VariableDefinition definition = variables.get(name);
      return definition.getSizeOfArray();
    }
    return 0;
  }

  //
//  OOOOOO                                                      OO                                                  OO            OO        OOO
// OO    OO                                                     OO                                                                OO         OO
// OO        OOOOOOO  OOOOOOOO   OOOOOOO  OOOOOOOO   OOOOOOOO OOOOOO  OOOOOOO          OO   OO  OOOOOOOO OOOOOOOO  OOO   OOOOOOOO OOOOOOOO   OO   OOOOOOO   OOOOOOO
// OO       OO     OO OO     OO OO     OO OO     OO OO     OO   OO   OO     OO         OO   OO OO     OO OO     OO  OO  OO     OO OO     OO  OO  OO     OO OO
// OO  OOOO OOOOOOOOO OO     OO OOOOOOOOO OO        OO     OO   OO   OOOOOOOOO         OO   OO OO     OO OO         OO  OO     OO OO     OO  OO  OOOOOOOOO  OOOOOOO
// OO    OO OO        OO     OO OO        OO        OO     OO   OO   OO                 OO OO  OO     OO OO         OO  OO     OO OO     OO  OO  OO               OO
//  OOOOOO   OOOOOOO  OO     OO  OOOOOOO  OO         OOOOOOOO    OOO  OOOOOOO            OOO    OOOOOOOO OO        OOOO  OOOOOOOO OOOOOOOO  OOOO  OOOOOOO   OOOOOOO
//

  // sämtlichen bekannten Variablen erzeugen
  public void generateVariables() {
    for (String name : variableList) {
      VariableDefinition definition = getVariable(name);
      generateVariable(definition);

      if (getVariableType(name) != Type.STRING) {
        if (!definition.hasAnyAccess()) {
          LOGGER.warn("Variable: '{}' is not used.", name);
        }
      }
    }
  }

  public void generateAllNotAlreadyGeneratedEquatesVariables() {
    for (String name : variableList) {
      VariableDefinition definition = getVariable(name);
      generateEquatesVariable(definition);
    }
  }

  public List<String> getAllVariables(){
    return variableList;
  }

  public void generateEquatesVariable(VariableDefinition definition) {
    if (!definition.isGenerated()) {
      String name = definition.getName();
      String address = definition.getAddress();
      
      if (address != null) {
        if (!address.equals("@")) {
          code(name + " = " + address);
        }
        definition.setGenerated(true);
      }
    }
  }

  public void generateVariable(final VariableDefinition definition) {
    if (definition.isGenerated()) {
      // Variable already, do nothing here!
      return;
    }

    String name = definition.getName();
    switch (definition.getType()) {

    case FUNCTION:
    case PROCEDURE:
      // FUNCTION and PROCEDURE are already defined as Sourcecode
      break;

    case BYTE:
      if (definition.getAddress() != null) {
        code(name + " = " + definition.getAddress());
      }
      else {
        code(name + " .BYTE 0");
      }
      break;

    case FAT_BYTE_ARRAY:
    case BYTE_ARRAY:
      generateByteArray(name, definition);
      break;

    case WORD:
      if (definition.getAddress() != null) {
        code(name + " = " + definition.getAddress());
      }
      else {
        code(name + " .WORD 0");
      }
      break;

    case WORD_ARRAY:
      generateWordArray(name, definition);
      break;

    case STRING:
      String stringName = "?STRING" + getVariablePosition(name);
      code(stringName);
      code(" .BYTE " + StringHelper.makeDoubleQuotedString(name) + "," + STRING_END_MARK);
      break;
    default:
    }
  }

  private void generateWordArray(String name, VariableDefinition definition) {
    if (definition.getAddress() != null) {
      code(name + " = " + definition.getAddress());
    }
    else if (!definition.getArrayContent().isEmpty()) {
      code(name);
      WordListGenerator generator = new WordListGenerator(this) {
        @Override
        public String getElement(int index) {
          return definition.getArrayElement(index);
        }
      };

      generator.generateCode(definition.getArrayContent().size());
    }
    else {
      code(name);
      code(" *=*+" + (definition.getSizeOfArray() * 2));
    }

  }

  private void generateByteArray(String name, VariableDefinition definition) {
    if (definition.getAddress() != null) {
      code(name + " = " + definition.getAddress());
    }
    else if (!definition.getArrayContent().isEmpty()) {
      code(name);
      ByteListGenerator generator = new ByteListGenerator(this) {
        @Override
        public String getElement(int index) {
          return definition.getArrayElement(index);
        }
      };
      generator.generateCode(definition.getArrayContent().size());

    }
    else {
      code(name);
      code(" *=*+" + definition.getSizeOfArray());
    }

  }

  public void setVariableArray(String name, List<String> arrayValues) {
    if (variables.containsKey(name)) {
      VariableDefinition definition = variables.get(name);
      definition.setArray(arrayValues);
    }
  }

  public void setVariableAddress(String name, String address) {
    if (variables.containsKey(name)) {
      VariableDefinition definition = variables.get(name);
      definition.setAddress(address);
    }
  }

  public String getVariableAddress(String name) {
    if (variables.containsKey(name)) {
      VariableDefinition definition = variables.get(name);
      return definition.getAddress();
    }
    return "";
  }

//
//                                                             OO   OO
//                                                             OO   OO
//  OOOOOOO  OOOOOOOO  OOOOOOOO   OOOOOOO  OOOOOOOO          OOOOOO OOOOOOOO  OOOOOOOO   OOOOOOO  OO     OO  OOOOOOO  OOOOOOOO   OOOOOOO
// OO     OO OO     OO OO     OO OO     OO OO     OO           OO   OO     OO OO     OO OO     OO OO     OO OO     OO OO     OO OO
// OOOOOOOOO OO        OO        OO     OO OO                  OO   OO     OO OO        OO     OO OO  O  OO OOOOOOOOO OO         OOOOOOO
// OO        OO        OO        OO     OO OO                  OO   OO     OO OO        OO     OO OO  O  OO OO        OO               OO
//  OOOOOOO  OO        OO         OOOOOOO  OO                   OOO OO     OO OO         OOOOOOO   OOO OOO   OOOOOOO  OO         OOOOOOO
//

  /**
   * hilfsfunktion für das Werfen von Fehlermeldungen
   *
   * @param name
   */
  public void throwIfVariableUndefined(String name) {
    if (!hasVariable(name)) {
      error(new Symbol(name, SymbolEnum.noSymbol), "variable " + name + " is unknown");
    }
  }

  public void throwIfVariableAlreadyDefined(String name) {
    if (hasVariable(name)) {
      error(new Symbol(name, SymbolEnum.noSymbol), "variable " + name + " is already defined");
    }
  }

  public void throwIfNotArrayType(Type type) {
    if (type == Type.BYTE || type == Type.WORD) {
      error(new Symbol("", SymbolEnum.noSymbol), "variable must from type array");
    }
  }

  public void throwIsValueNotANumber(Symbol address) {
    if (address.getId() != SymbolEnum.number) {
      error(address, "Number expected");
    }
  }

  public void throwIfNotVariableName(Symbol name2) {
    if (name2.getId() != SymbolEnum.variable_name) {
      error(name2, "Variable name expected");
    }
  }

  public void error(Symbol symbol, String message) {
    programTokenizer.error(symbol, message);
  }

//
//                                           OO
//                                           OO
//  OOOOOOO   OOOOOOO  OO     OO OOOOOOOO  OOOOOO  OOOOOOO  OOOOOOOO
// OO     OO OO     OO OO     OO OO     OO   OO   OO     OO OO     OO
// OO        OO     OO OO     OO OO     OO   OO   OOOOOOOOO OO
// OO     OO OO     OO OO     OO OO     OO   OO   OO        OO
//  OOOOOOO   OOOOOOO   OOOOOOOO OO     OO    OOO  OOOOOOO  OO
//

  /**
   * Counter für die Codeerstellung von eindeutigen Sprungzielen
   *
   * @return
   */
  public int getNowinc() {
    return nowinc;
  }

  public void incrementNowinc() {
    ++nowinc;
  }

  private int forCount = 0;

  public int getLoopCount() {
    return forCount;
  }

  public void incrementLoopCount() {
    ++forCount;
  }

  private int returnCount = 0;

  public int getReturnCount() {
    return returnCount;
  }

  public void incrementReturnCount() {
    ++returnCount;
  }

  private int conditionCount = 1;

  public int getConditionCount() {
    return conditionCount;
  }

  public void incrementConditionCount() {
    ++conditionCount;
  }

  // TODO: rename to getLastErgebnis!
  // TODO: rename to getResultType()
  public Type getErgebnis() {
    return ergebnis;
  }

  public void setErgebnis(Type ergebnis) {
    this.ergebnis = ergebnis;
  }

  public List<String> getIncludePaths() {
    return includePaths;
  }

  public void setIncludePaths(final List<String> includePaths) {
    this.includePaths.addAll(includePaths);
  }

  /**
   * BREAK Statement is implemented as a stack,
   * every loop add a new breakable stage
   * at end of loop, this stage will remove, so the old stage come back
   */
  private Stack<String> breakVariable = new Stack<>();
  public void addBreakVariable(final String breakVariable) {
    this.breakVariable.push(breakVariable);
  }
  
  public void clearBreakVariable() {
    breakVariable.pop();
  }
  
  public String getBreakVariable() {
    if (breakVariable.isEmpty()) {
      error(new Symbol("BREAK", SymbolEnum.variable_name), "We are not inside a breakable stage.");
    }
    return breakVariable.peek();
  }
}
