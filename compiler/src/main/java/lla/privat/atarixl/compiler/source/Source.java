// cdw by 'The Atari Team' 2021
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

import lla.privat.atarixl.compiler.Options;
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

  private Options options;
  
  // true when we are a program, else we are an include
  private boolean isProgram;

  // Start address of program, default is $4000 (set in class Program)
  private String startAddress;

  private boolean runAddress;

  private boolean showCode;

  private final List<String> includePaths;

  // HashMap zum schnelleren Auffinden von Variablen
  private Map<String, VariableDefinition> variables;

  // Liste der Variablen in immer der selben Reihenfolge, ist für das Erstellen
  // des Codes wichtig!
  private List<String> variableList;

  private List<String> assemblerCodeList;

  // Global Counter
  private int nowinc;

  // Ergebnis der letzten Expression
  private Type typeOfLastExpression;

  private int countOfAsserts;

  private String filename;

//  private boolean starChainMult;
  
  public Source(final String sourceCode) {
    this.showCode = false;
    this.options = new Options();
    
    this.programTokenizer = new SymbolTokenizer(sourceCode + " "); // TODO: Das Whitespace am Ende macht vieles einfacher!
    variables = new HashMap<>();

    variableList = new ArrayList<>();

    assemblerCodeList = new ArrayList<>();

    this.includePaths = new ArrayList<>();
    this.runAddress = false;
    countOfAsserts=0;
    this.filename = "";
    nowinc = 0;
  }

  public void setOptions(Options options) {
    this.options = options;
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
    if (name.length() > 8) {
      error(new Symbol(name, SymbolEnum.variable_name), "Program or Include name too long. 8 char max.");
    }
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }
  public String getFilename() {
    return filename;
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

  public void setRunAd() {
    this.runAddress = true;
  }

  public boolean isRunAd() {
    return runAddress;
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
    return options.getVerboseLevel();
  }
  public Source setVerboseLevel(int level) {
    this.options.setVerboseLevel(level);
    return this;
  }
  
  public boolean isSelfModifiedCode() {
    return options.isSelfModifiedCode();
  }

  public void setSelfModifiedCode(boolean selfModifiedCode) {
    this.options.setSelfModifiedCode(selfModifiedCode);
  }

  public boolean isStarChainMult() {
    return options.isStarChainMult();
  }

  public void setStarChainMult(boolean starChainMult) {
    this.options.setStarChainMult(starChainMult);
  }

  public boolean isShiftMultDiv() {
    return options.isShiftMultDiv();
  }
  
  public void setShiftMultDiv(boolean shiftMultDiv) {
    this.options.setShiftMultDiv(shiftMultDiv);
  }
  
  public boolean showPeepholeOptimize() {
    return options.isShowPeepholeOptimize();
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
  public int code(String sourcecode) {
    int n = assemblerCodeList.size();
    assemblerCodeList.add(sourcecode);
    return n;
  }

  // add a bunch of lines to the assembler code
  public int code(List<String> sourcecodeList) {
    int n = assemblerCodeList.size();
    for (String item : sourcecodeList) {
      assemblerCodeList.add(item);
    }
    return n;
  }

  public void replaceCode(int index, String codeToReplace) {
    if (index != 0) {
      assemblerCodeList.set(index, codeToReplace);
    }
  }

//  private int heapPtrCheck = 0;
  
  public void sub_from_heap_ptr(int count) {
    code(" SEC"); //              ;2
    code(" LDA @HEAP_PTR"); //    ;3
    code(" SBC #"+count);   // %1 ;2
    code(" STA @HEAP_PTR"); //    ;3
    if (!options.isSmallAddSubHeapPtr()) {
      code(" BCS *+4");       // das geht nur, weil HEAP_PTR in der Zero Page ist
//    code(" BCS ?NO_DEC_HIGH_HEAP_PTR"); // ;3 ; => 13 Takte statt 30 mit jsr
      code(" DEC @HEAP_PTR+1"); //  ;5
//    code("?NO_DEC_HIGH_HEAP_PTR");
    }
  }
  
  public void add_to_heap_ptr(int count) {
    code(" CLC"); //              ;2 (Takte)
    code(" LDA @HEAP_PTR"); //    ;3
    code(" ADC #" + count); //    ;2
    code(" STA @HEAP_PTR"); //    ;3
    if (!options.isSmallAddSubHeapPtr()) {
      code(" BCC *+4");
//    code(" BCC NO_INC_HIGH_HEAP_PTR"); // ;3 ; => 13 Takte statt 24 mit jsr
      code(" INC @HEAP_PTR+1"); //  ;5
//    code("NO_INC_HIGH_HEAP_PTR");
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

  public void showSourceCodeLine() {
    code(";");
    final int lineNumber = programTokenizer.getLineNumber();
    final String sourceCodeLine = programTokenizer.getCodeLine();
    StringBuilder codelineBuffer = new StringBuilder();
    codelineBuffer.append("; ");
    codelineBuffer.append('[');
    codelineBuffer.append(lineNumber);
    codelineBuffer.append("]  ");
    codelineBuffer.append(sourceCodeLine);    
    code(codelineBuffer.toString());
    code(";");
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
      showSourceCodeLine();
      // code(";");
      // code("; " + programTokenizer.getSourceCodeLine());
      // code(";");
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
      if (type == Type.STRING_ANONYM) {
        return;
      }
      error(new Symbol(name, SymbolEnum.noSymbol), "variable already defined");
    }
    final VariableDefinition newVariable = new VariableDefinition(name, type, countArray, filename);
    variables.put(name, newVariable);
    variableList.add(name);

//    if (name.endsWith("_LENGTH")) return;
//
    if (isArrayType(type)) {
      if (! StringHelper.isSingleQuotedString(name)) {
        final String nameWithLength = name + "_LENGTH";
        final VariableDefinition newConstVariable = new VariableDefinition(nameWithLength, Type.CONST, 0, filename);
        variables.put(nameWithLength, newConstVariable);
        variableList.add(nameWithLength);
        setVariableAddress(nameWithLength, String.valueOf(countArray));
      }
    }
  }

  // Helperfunction to add Variables out of header file
  // reset prefix by given from INCLUDE Header
  public void addVariableResetPrefix(VariableDefinition variableDefinition) {
    variableDefinition.resetNameWithPrefix(getPrefix());
    variableDefinition.setGenerated(false);
    final String newName = variableDefinition.getName();
    if (variables.containsKey(newName)) {
      error(new Symbol(newName, SymbolEnum.noSymbol), "variable already defined in file:" + variableDefinition.getFilename());
    }
    if (options.isShowVariableUsage()) {
      LOGGER.info("set variable {} from header.", newName);
    }
    variables.put(newName, variableDefinition);
    variableList.add(newName);
  }
  
  public boolean isArrayType(Type type) {
    return (type.equals(Type.BYTE_ARRAY) ||
        type.equals(Type.STRING) ||
        type.equals(Type.FAT_BYTE_ARRAY) ||
        type.equals(Type.FAT_WORD_ARRAY) ||
        type.equals(Type.WORD_ARRAY) ||
        type.equals(Type.WORD_SPLIT_ARRAY));
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
    return new VariableDefinition("", Type.UNKNOWN, filename);
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

  public Type getVariableOverType(String name) {
    switch(getVariable(name).getType()) {
    case BYTE:
    case BYTE_ARRAY:
    case FAT_BYTE_ARRAY:
    case UINT8:
      return Type.BYTE;

    case INT8:
      return Type.INT8;

    case WORD:
    case WORD_ARRAY:
    case WORD_SPLIT_ARRAY:
    case FAT_WORD_ARRAY:
    case UINT16:
      return Type.WORD;

    case CONST:
      return Type.CONST;
      
    default:
        return Type.WORD;
    }
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
    if (getVariable(variableName).getType() == Type.STRING_ANONYM) {
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

  public void incrementRead(String name) {
    getVariable(name).incrementRead();
  }
  public void incrementWrite(String name) {
    getVariable(name).incrementWrites();
  }
  public void incrementCall(String name) {
    getVariable(name).incrementCalls();
  }
  
  public String generateFunctionNameWithParameters(String name, int countOfParameters) {
    if (countOfParameters > 0) {
      StringBuilder nameBuilder = new StringBuilder();
      nameBuilder.append(name);
      nameBuilder.append('_');
      for (int i=0;i<countOfParameters;i++) {
        nameBuilder.append("I");
      }
      return nameBuilder.toString();
    }
    return name;
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
    }
    variableStatistics();
  }
  
  
  public void variableStatistics() {
    int maxWrites = 0;
    int maxReads = 0;
    for (String name : variableList) {
      VariableDefinition definition = getVariable(name);

      if (getVariableType(name) != Type.STRING_ANONYM) {
        if (!definition.hasAnyAccess()) {
          if (options.isShowVariableUnused()) {
            warn("Variable: '{}' is not used.", name);
          }
        }
        if (options.isShowVariableUsage()) {
          maxWrites = Math.max(maxWrites, definition.getWrites());
          maxReads = Math.max(maxReads, definition.getReads());
        }
      }
    }
    
    // Show Variable usage, if parameter -varusage is given
    // But only the variables which use more then 80% of all usage.
    // So we can move such variables in zero page register 128-211
    // this can save some memory.
    if (options.isShowVariableUsage()) {
      LOGGER.info("Variable usage count:");

      int showReadsIfGreater = maxReads * 80 / 100;
      int showWritesIfGreater = maxWrites * 80 / 100;
      LOGGER.info("show only variables with read usage of at least {} times.", showReadsIfGreater);
      LOGGER.info("show only variables with write usage of at least {} times.", showWritesIfGreater);

      for (String name : variableList) {
        if (getVariableType(name) != Type.STRING_ANONYM) {
          VariableDefinition definition = getVariable(name);

          if (options.isShowVariableUsage()) {
            if (definition.getReads() > showReadsIfGreater) {
              LOGGER.info("Variable: '{}' has {} reads", name, definition.getReads());
            }
            if (definition.getWrites() > showWritesIfGreater) {
              LOGGER.info("Variable: '{}' has {} writes", name, definition.getWrites());
            }
          }
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
          if (definition.getType() == Type.WORD_SPLIT_ARRAY) {
            code(name + "_LOW = " + definition.getAddress() + "_LOW");
            code(name + "_HIGH = " + definition.getAddress() + "_HIGH");            
          }
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
    case FUNCTION_POINTER:
      // FUNCTION and PROCEDURE are already defined as Sourcecode
      break;

    case BYTE:
    case UINT8:
    case INT8:
      if (definition.getAddress() != null) {
        code(name + " = " + definition.getAddress());
      }
      else {
        code(name + " .BYTE 0");
      }
      break;

    case FAT_BYTE_ARRAY:
    case BYTE_ARRAY:
    case STRING:
      generateByteArray(name, definition);
      break;

    case WORD:
    case UINT16:
    case INT16:
      if (definition.getAddress() != null) {
        code(name + " = " + definition.getAddress());
      }
      else {
        code(name + " .WORD 0");
      }
      break;

    case WORD_ARRAY:
    case FAT_WORD_ARRAY:
      generateWordArray(name, definition);
      break;
    case WORD_SPLIT_ARRAY:
      generateWordSplitArray(name, definition);
      break;
    case STRING_ANONYM:
      String stringName = "?STRING" + getVariablePosition(name);
      code(stringName);
      code(" .BYTE " + StringHelper.makeDoubleQuotedString(name) + "," + STRING_END_MARK);
      break;
    case UNKNOWN:
    case CONST:
      break;
//    default:
    }
  }

  private void generateWordArray(String name, VariableDefinition definition) {
    if (definition.getAddress() != null) {
      // code(name + " = " + definition.getAddress());
      error(new Symbol(name, SymbolEnum.noSymbol), "there is a definition problem with array: " + name + ".");
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

  private void generateWordSplitArray(String name, VariableDefinition definition) {
    if (definition.getAddress() != null) {
      code(name + " = " + definition.getAddress());
    }
    else if (!definition.getArrayContent().isEmpty()) {
      code(name); // we will also generate a variable without '_low' and '_high'
      code(name+"_LOW");
      ByteListGenerator generatorlow = new ByteListGenerator(this) {
        @Override
        public String getElement(int index) {
          String element = definition.getArrayElement(index);
          if (StringHelper.isSingleQuotedString(element)) {
            element = "?STRING" + getVariablePosition(element); 
          }
          return "<" + element;
        }
      };
      generatorlow.generateCode(definition.getArrayContent().size());

      code(name+"_HIGH");
      ByteListGenerator generatorhigh = new ByteListGenerator(this) {
        @Override
        public String getElement(int index) {
          String element = definition.getArrayElement(index);
          if (StringHelper.isSingleQuotedString(element)) {
            element = "?STRING" + getVariablePosition(element); 
          }
          return ">" + element;
        }
      };
      generatorhigh.generateCode(definition.getArrayContent().size());
    }
    else {
      code(name); // we will also generate a variable without '_low' and '_high'
      code(name+"_LOW");
      code(" *=*+" + definition.getSizeOfArray());
      code(name+"_HIGH");
      code(" *=*+" + definition.getSizeOfArray());
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
      
      if (definition.getType() == Type.STRING) {
        if (arrayValues.size() != 2) {
          error(new Symbol(name, SymbolEnum.noSymbol), "byte array " + name + " must contain more than one element");
        }
      }
      // wirkliche Anzahl der Elemente in neuer Variable ablegen
      final String nameWithLength = name + "_ELEMENTS";

      final VariableDefinition newConstVariable = new VariableDefinition(nameWithLength, Type.CONST, 0, filename);
      variables.put(nameWithLength, newConstVariable);
      variableList.add(nameWithLength);
      setVariableAddress(nameWithLength, String.valueOf(arrayValues.size()));
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

  public void throwIfFunctionUndefined(String functionName) {
    if (!hasVariable(functionName)) {
      error(new Symbol(functionName, SymbolEnum.noSymbol), "function " + functionName + "() is not defined");
    }
  }

  public void throwIfVariableAlreadyDefined(String name) {
    if (hasVariable(name)) {
      VariableDefinition definition = getVariable(name);
      String filename = definition.getFilename();
      error(new Symbol(name, SymbolEnum.noSymbol), "variable " + name + " is already defined in file:"+filename);
    }
  }

  public void throwIfNotArrayType(Type type) {
    if (!isArrayType(type)) {
      error(Symbol.noSymbol(), "variable must from type array");
    }
  }

  public void throwIfArrayType(Type type) {
    if (isArrayType(type)) {
      error(Symbol.noSymbol(), "variable must NOT from type array, Maybe prefix 'ADR:' forgotten?");
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

  public void throwIfConstVariableHasNoValue(String name) {
    if (getVariableAddress(name) == null) {
      error(new Symbol(name, SymbolEnum.noSymbol), "Const Variable '" + name + "' must set with variable or integer");
    }
  }
  
  public void error(Symbol symbol, String message) {
    programTokenizer.error(symbol, message);
  }

  /**
   * give out warning
   */

  public void warning(Symbol symbol, String message) {
    programTokenizer.warning(symbol, message);
  }

  public void warn(String message) {
    LOGGER.warn(message);
  }

  public void warn(String message, Object arg) {
    LOGGER.warn(message, arg);
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
  public Type getTypeOfLastExpression() {
    return typeOfLastExpression;
  }

  public void setTypeOfLastExpression(Type ergebnis) {
    this.typeOfLastExpression = ergebnis;
  }

  private int negativeCount = 0;

  public int getNegativeCount() {
    return negativeCount;
  }

  public void incrementNegativeCount() {
    ++negativeCount;
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

  /**
   * return count of used asserts, helpful for more tests
   * @return count of used asserts
   */
  public int getCountOfAsserts() {
    return countOfAsserts;
  }

  public void incrementAsserts() {
    ++countOfAsserts;
  }
}
