// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.VariableDefinition;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;

public class Expression extends Code {

  private static final Logger LOGGER = LoggerFactory.getLogger(Expression.class);

  private final Source source;
  private ArrayList<Integer> p_code;

  private Symbol lastSymbol;

  private Type ergebnis;
  private Type ergebnisMayBe;
  
  private boolean precalculationPossible;
  private int countArithmeticSymbols;

  public Expression(Source source) {
    super(source);

    this.source = source;
    p_code = new ArrayList<>();
    parameterCount = 0;
    ergebnis = Type.BYTE;
    ergebnisMayBe = Type.UNKNOWN;
    precalculationPossible = false;
    countArithmeticSymbols = 0;
  }

  public List<Integer> getPCode() {
    return p_code;
  }

  /**
   * If we would like a byte expression, set width to 1, all other is 2
   *
   * @param width
   */
  public Expression setType(Type type) {
    ergebnis = type;
    return this;
  }

  public Type getType() {
    return ergebnis;
  }

  /**
   * Unary add or sub symbol
   * -1 is a negative digit
   * +1 is a positiv digit
   *
   * 1 - -1 == 2
   * 1 - 1 == 0
   * 1 - +1 == 0
   *
   * @param symbol
   * @return
   */
//  private boolean isUnaryAddOrSub(Symbol symbol) {
//    final String mayBeUnaryOperator = symbol.get();
//    return mayBeUnaryOperator.equals("+") || mayBeUnaryOperator.equals("-");
//  }

  public int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
  }

  private void calculatePrecalculation() {
    precalculationPossible = true;
//    System.out.print("PCode: ");
//    try {
      for (int i = 0; i < p_code.size(); i++) {
        int pcode = p_code.get(i);
//        System.out.print(pcode);
        
        if (pcode == PCode.ZAHL.getValue()) {
          ++i; // naechste Zahl interessiert nicht
//          System.out.print(" <" + p_code.get(i) + ">");
        }
        else if (pcode == PCode.PUSH.getValue() || pcode == PCode.PULL.getValue()) {        
        }
        else if (pcode >= PCode.UPN_ADD.getValue() && pcode <= PCode.UPN_MODULO.getValue()) {
        }
        else if (pcode == PCode.NOP.getValue()) {          
        }
        else if (pcode == PCode.ADDRESS.getValue()) {
          ++i; // naechste Zahl interessiert nicht
        }
        else {
          precalculationPossible = false;
        }
//        System.out.print(" ");
      }
//      System.out.println();
    
      // Sonderlocke, single Value
      if (p_code.size() == 2 && p_code.get(0) == PCode.ZAHL.getValue()) {
        precalculationPossible = false;
      }
      // Sonderlocke, single address
      if (p_code.size() == 3 && p_code.get(0) == PCode.NOP.getValue() && p_code.get(1) == PCode.ADDRESS.getValue()) {
        precalculationPossible = false;
      }
//    }
//    catch (IndexOutOfBoundsException e) {
//      System.out.println("ERROR: " + p_code.size());
//      source.error(new Symbol(null, null), "UNKNOWN ERROR!");
//    }
  } 
  
  public Symbol build() {
    // TODO: ergebnis im PCode hinterlegen?
    code(";#3 vor optimierung");
    code(";#3 <P-Code> " + joinedPCode());

//    if (getCountArithmeticSymbols() > 0) {
//      if (isPrecalculationPossible()) {
//        source.warning(Symbol.noSymbol(), "Expression could be precalculated!");
//      }
//    }

    // If parameter -errorIfPrecalculatable is given and an expression contains only fix digits
    // it should precalculated by the developer. Due to the fact it is a single path compiler
    // the compiler is NOT be able to help here in precalculate itself.
    
    if (precalculationPossible) {
      if (source.getOptions().isErrorPrecalculate()) {
        source.error(Symbol.noSymbol(), "Error: Precalculation is possible here. Fix it!");
      }
      if (source.getOptions().isWarningPrecalculate()) {
        source.warn("Warning: Precalculation is possible here.");
      }
    }
    
    optimisation();

    LOGGER.debug("PCode is " + joinedPCode());
    code(";#3 <P-Code> " + joinedPCode());

    new PCodeToAssembler(source, p_code, ergebnis).build();

    return lastSymbol;
  }

  /**
   * @return true, if compiler could calculate the result completely
   */
  boolean isPrecalculationPossible() {
    return precalculationPossible;
  }

  int getCountArithmeticSymbols() {
    return countArithmeticSymbols;
  }

  /**
   * Expression Etwas, das wir addieren oder subtrahieren, die Strichrechnung
   *
   * @param symbol enthält erstes Symbol
   * @return nextSymbol
   */
  public Expression expression(Symbol symbol) {
    Symbol nextSymbol = new Symbol("", SymbolEnum.noSymbol);

    // Strings with length or 1 ('a') has intern length of 3 can act like a 'a Value
    // 97
    if (symbol.getId() == SymbolEnum.string && symbol.get().length() > 3) {
      p_code.add(PCode.STRING.getValue());
      source.addVariable(symbol.get(), Type.STRING_ANONYM);
      p_code.add(source.getVariablePosition(symbol.get()));
      ergebnis = Type.WORD;
      nextSymbol = source.nextElement();
    }
    else {
      // Mathematischer Ausdruck
//      if (isUnaryAddOrSub(symbol)) { // unary - or + => 0 - expression oder 0 + expression
//        p_code.add(PCode.ZAHL.getValue());
//        p_code.add(0);
//        nextSymbol = symbol; // kein Symbol neu laden, einfach als Operator verwenden!
//      }
//     else {
        nextSymbol = term(symbol);
//      }

      String operator = nextSymbol.get();
      while (operator.equals("+") || operator.equals("-") || operator.equals("&") || operator.equals("!")
          || operator.equals("XOR")) {
        p_code.add(PCode.PUSH.getValue());
        switch (operator) {
        case "+":
          nextSymbol = add(nextSymbol);
          break;
        case "-":
          nextSymbol = substract(nextSymbol);
          break;
        case "&":
          nextSymbol = and(nextSymbol);
          break;
        case "!":
          nextSymbol = or(nextSymbol);
          break;
        case "XOR":
          nextSymbol = xor(nextSymbol);
          break;
        }
        operator = nextSymbol.get();
        ++countArithmeticSymbols;
      }
    }
    lastSymbol = nextSymbol;

    calculatePrecalculation();
    // fluid interface
    return this;
  }

  /**
   * Ein Term ist etwas, das wir multiplizieren oder dividieren, die Punktrechnung
   *
   * @param symbol
   * @return
   */

  public Symbol term(Symbol symbol) {
    Symbol nextSymbol = factor(symbol);

    String operator = nextSymbol.get();
    while (operator.equals("*") || operator.equals("/") || operator.equals("MOD")) {
      p_code.add(PCode.PUSH.getValue());
      switch (operator) {
      case "*":
        ergebnisMayBe = Type.WORD;
        nextSymbol = mul(nextSymbol);
        break;
      case "/":
        ergebnisMayBe = Type.WORD;
        nextSymbol = divide(nextSymbol);
        break;
      case "MOD":
        ergebnis = Type.WORD;
        nextSymbol = modulo(nextSymbol);
        break;
      }
      if (ergebnisMayBe == Type.WORD) {
        ergebnisMayBe = Type.UNKNOWN;
        ergebnis = Type.WORD;
      }
      operator = nextSymbol.get();
      ++countArithmeticSymbols;
    }
    return nextSymbol;
  }

  public Symbol getLastSymbol() {
    return lastSymbol;
  }

  /**
   * Ein factor, eine Zahl (normal, hexadezimal($) oder binär(%)) oder ein
   * Klammerausdruck, oder ein eckiger Klammerausdruck
   *
   * @param symbol
   * @return nextSymbol
   */
  public Symbol factor(Symbol symbol) {
    SymbolEnum id = symbol.getId();
    Symbol nextSymbol;
    if (id == SymbolEnum.number) {
      nextSymbol = number(symbol);
    }
    else if (id == SymbolEnum.symbol && symbol.get().equals("-")) {
      nextSymbol = source.peekSymbol();
      if (nextSymbol.getId() == SymbolEnum.number) {
        nextSymbol = source.nextElement();
        Symbol negativValue = new Symbol("-" + nextSymbol.get(), SymbolEnum.number);
        nextSymbol = number(negativValue);
      }
      else if (nextSymbol.getId() == SymbolEnum.variable_name) {
        p_code.add(PCode.ZAHL.getValue());
        p_code.add(0);
        p_code.add(PCode.PUSH.getValue());
        nextSymbol = substract(symbol);
      }
    }
    else if (id == SymbolEnum.string && symbol.get().length() == 3) {
      nextSymbol = numberFromString(symbol);
    }
    else if (id == SymbolEnum.symbol && symbol.get().equals("@(")) {
      nextSymbol = identifier(symbol);
    }
    else if (id == SymbolEnum.variable_name) {
      nextSymbol = identifier(symbol);
    }
    else if (id == SymbolEnum.reserved_word) {
      Symbol peekSymbol = source.peekSymbol();
      if (symbol.get().equals("BYTE") && peekSymbol.get().equals("(")) {
        nextSymbol = source.nextElement(); // (
        nextSymbol = source.nextElement(); // ...
        nextSymbol = expression(nextSymbol).getLastSymbol();
        match(nextSymbol, ")");
        nextSymbol = source.nextElement();
        ergebnis = Type.BYTE;
      }
      else {
        source.error(symbol, "Cast only with BYTE(...) supported");
        nextSymbol = null;
      }
    }
    // runde Klammern signalisieren eine innere Berechnung, die vorzuziehen ist
    else if (id == SymbolEnum.symbol && symbol.get().equals("(")) {
      nextSymbol = source.nextElement();
      nextSymbol = expression(nextSymbol).getLastSymbol();
      match(nextSymbol, ")");
      nextSymbol = source.nextElement();
    }
    // eckige Klammern signalisieren eine innere Berechnung die vorzuziehen ist,
    // kommt eigentlich nur in Array Zugriff vor
    else if (id == SymbolEnum.symbol && symbol.get().equals("[")) {
      // TODO: Hier sollten wir einen Typen setzen im pcode, dann können wir im pcode
      // entscheiden, welcher Type gefordert wird!
      // TODO: innerhalb eckiger Klammern sollten wir unsigned rechnen!
      nextSymbol = source.nextElement();
      nextSymbol = expression(nextSymbol).getLastSymbol();
      match(nextSymbol, "]");
    }
    else {
      source.error(symbol, "unknown factor value");
      nextSymbol = null;
    }
    return nextSymbol;
  }

  public Symbol number(Symbol symbol) {
    p_code.add(PCode.ZAHL.getValue());
    int value = convertNumberValue(symbol.get());
    p_code.add(Integer.valueOf(value));
    if (ergebnis == Type.INT8) {
      if (value < -128 || value > 127) {
        source.warning(symbol, "We leave INT8 value range [-128, 127]");
        // TODO: konversion gefordert!
        ergebnis = Type.WORD;
      }
    }
    else if (ergebnis == Type.BYTE) {
      if (value < 0 || value > 255) {
        source.warning(symbol, "We leave BYTE value range [0, 255]");
        ergebnis = Type.WORD;
      }
      if (ergebnisMayBe == Type.WORD) {
        if (!source.isShiftMultDiv()) {
          ergebnis = Type.WORD;
          ergebnisMayBe = Type.UNKNOWN;
        } 
        else if (value == 2 || value == 4 || value == 8 || value == 16 || value ==32 || value == 64 || value == 128) {
           ergebnisMayBe = Type.UNKNOWN;
        }
        else {
          ergebnis = Type.WORD;
          ergebnisMayBe = Type.UNKNOWN;
        }
      }
    }
    else {
      // if (value < -128 || value > 255) {
      ergebnis = Type.WORD;
      // }
    }
    return source.nextElement();
  }

  public Symbol numberFromString(Symbol symbol) {
    p_code.add(PCode.ZAHL.getValue());
    int value = Integer.valueOf(symbol.get().charAt(1));
    p_code.add(value);
//    if (value > 255) {
//      ergebnis = Type.WORD;
//    }

    return source.nextElement();
  }

  public static int convertNumberValue(String strvalue) {
    int value = 0;
    if (strvalue.startsWith("$")) {
      value = Integer.parseInt(strvalue.substring(1), 16);
    }
    else {
      value = Integer.parseInt(strvalue, 10);
    }
    return value;
  }

  /*
   * This variable is used to decide if we need to add some bytes to the heap_ptr
   * so we do not overwrite already set parameter in a function
   */
  private int parameterCount;

  private Symbol identifier(Symbol symbol) {

    String name = symbol.get();
    Symbol peekSymbol = source.peekSymbol();
    if (name.equals("@(")) {
      Symbol namePtr = source.nextElement();
      Symbol parentness = source.nextElement();
      source.match(parentness, ")");

      peekSymbol = source.peekSymbol();
      functionCallAccess(namePtr.get(), Type.FUNCTION_POINTER);
    }
    else if (peekSymbol.get().equals("(")) {
      functionCallAccess(name, Type.FUNCTION);
    }
    else if (peekSymbol.get().equals(":")) {
      // TODO: name sollte hier reserviertes Word sein!
      // source.throwIfVariableNotReservedWord(name);
      switch (name) {
      case "ADR":
        addressAccess();
        break;
      case "ABS":
        absoluteValue();
        break;
      case "B2W":
        toWord();
        break;
      case "HI":
        hibyte();
        break;
      default:
        source.error(symbol, "Expect only ADR or B2W before ':' was: " + name);
      }
    }
    else if (peekSymbol.get().equals("[")) {
      arrayAccess(name);
    }
    else {
      Type variableType = source.getVariableType(name);

      if (variableType.equals(Type.CONST)) {
        String strValue = source.getVariableAddress(name);
        source.throwIfConstVariableHasNoValue(name);
        if (strValue.charAt(0) >= '@' && strValue.charAt(0) <= 'Z') {
          // strValue is a variable
          p_code.add(PCode.NOP.getValue());
          p_code.add(PCode.ADDRESS.getValue());
          int variablePosition = source.getVariablePosition(strValue);
          if (variablePosition == -1 ) {
            if (strValue.startsWith("@")) {
              // soll sich hier der Assembler drum kümmern, das die Variable aufgelöst wird
              source.addVariable(strValue, Type.FUNCTION);
              variablePosition = source.getVariablePosition(strValue);
            }
            else {
              source.throwIfVariableUndefined(name);
            }
          }
          p_code.add(variablePosition);
        }
        else if (source.hasVariable(name)) {
          p_code.add(PCode.ZAHL.getValue());
          p_code.add(convertNumberValue(strValue));
        }
        else {
          source.throwIsValueNotANumber(symbol);
        }
      }
      else {
        // Wir nehmen an, es kommt eine Variable vom Type
        source.throwIfArrayType(variableType);

        p_code.add(PCode.WORD.getValue());
        int variablePosition = source.getVariablePosition(name);
        if (variablePosition == -1) {
          source.throwIfVariableUndefined(name);
        }
        p_code.add(variablePosition);
        source.getVariable(name).setRead();
      }
      if (source.getVariableSize(name) == 2 && ergebnis == Type.BYTE) {
        ergebnis = Type.WORD;
      }
    }
    return source.nextElement();
  }

  protected void functionCallAccess(String name, Type type) {
    // we are a function call
    Symbol nextSymbol = source.nextElement(); // "("
    source.match(nextSymbol, "(");
    ergebnis = Type.WORD; // Funktionen geben IMMER ein Word zurück

    // make unknown function start with '@' possible
    if (name.startsWith("@")) {
      source.addVariable(name, Type.FUNCTION);
    }
    source.throwIfFunctionUndefined(name);

    nextSymbol = source.nextElement();
    int localParameterCount = 0;

    p_code.add(PCode.PARAMETER_START_ADD_TO_HEAP_PTR.getValue());
    p_code.add(parameterCount);

    boolean isMoreParameter = true;
    while (isMoreParameter) {
      String mnemonic = nextSymbol.get();
      if (!mnemonic.equals(")")) {
        // Wir sind ein Parameter
        // Wir setzen eine Parameter-Start-Marke, wenn dessen Wert != 0 ist,
        // addieren wir etwas auf den HeapPointer, so überschreiben wir keine schon
        // gesetzten Parameter

        int oldParameterCount = parameterCount;
        parameterCount = localParameterCount;

        // Für den eigentlichen Parameter durchlaufen wir die expression rekursiv
        // nochmal
        nextSymbol = expression(nextSymbol).getLastSymbol();

        parameterCount = oldParameterCount;

        // Push Marke, damit schreiben wir (y,x) in den HeapPointer
        p_code.add(PCode.PARAMETER_PUSH.getValue());
        p_code.add(localParameterCount);
        localParameterCount += 1; // next word!
      }

      mnemonic = nextSymbol.get();
      if (mnemonic.equals(",")) {
        nextSymbol = source.nextElement();
      }
      else {
        source.match(nextSymbol, ")");
        isMoreParameter = false;
      }
    }
    if (type.equals(Type.FUNCTION)) {
      // Endlich der Funktionsaufruf
      p_code.add(PCode.FUNCTION.getValue());
      p_code.add(source.getVariablePosition(name));
      p_code.add(localParameterCount);
      source.getVariable(name).setRead();
    }
    else if (type.equals(Type.FUNCTION_POINTER)) {
      p_code.add(PCode.FUNCTION_POINTER.getValue());
      p_code.add(source.getVariablePosition(name));
      p_code.add(localParameterCount);
      source.getVariable(name).setRead();
    }
    else {
      source.error(nextSymbol, "Unknown problem occur, not function nor function pointer call.");
    }
    if (parameterCount > 0) {
      // sollte es Parameter geben und wir haben den HeapPointer manipuliert, hier
      // wieder zurücksetzen
      // das passiert über ein Macro, das die y,x Register nicht manipuliert, da drin
      // steht der alte Wert.
      p_code.add(PCode.PARAMETER_END_SUB_FROM_HEAP_PTR.getValue());
      p_code.add(parameterCount);
    }
  }

  protected void toWord() {
    Symbol peekSymbol = source.nextElement(); // ":"
    source.match(peekSymbol, ":");

    Symbol nameSymbol = source.nextElement(); // address of name
    p_code.add(PCode.NOP.getValue());
    p_code.add(PCode.TOWORD.getValue());
    ergebnis = Type.WORD; // Egal was es vorher war, wir sind jetzt word breit!
    String name = nameSymbol.get();
    source.throwIfVariableUndefined(name);
    if (Type.BYTE != source.getVariableType(name)) {
      source.error(nameSymbol, "Only Variables of type BYTE allowed after B2W:<var>");
    }

    int variablePosition = source.getVariablePosition(name);
    p_code.add(variablePosition);
    source.getVariable(nameSymbol.get()).setRead();
  }

  protected void hibyte() {
    Symbol peekSymbol = source.nextElement(); // ":"
    source.match(peekSymbol, ":");

    Symbol nameSymbol = source.nextElement(); // name
    p_code.add(PCode.NOP.getValue());
    p_code.add(PCode.HI_BYTE.getValue());
    ergebnis = Type.BYTE; // Egal was es vorher war, wir sind jetzt byte breit!
    String name = nameSymbol.get();
    source.throwIfVariableUndefined(name);
    if (source.getVariableSize(name) == 2 || source.getVariableType(name) == Type.CONST) {
      int variablePosition = source.getVariablePosition(name);
      p_code.add(variablePosition);
      source.getVariable(nameSymbol.get()).setRead();
    }
    else {
      source.error(nameSymbol, "Only Variables of type WORD/UINT16 or type CONST allowed after HI:<var>");
    }
  }

  protected void addressAccess() {
    Symbol peekSymbol = source.nextElement(); // ":"
    source.match(peekSymbol, ":");

    Symbol nameSymbol = source.nextElement(); // address of name
    p_code.add(PCode.NOP.getValue());
    p_code.add(PCode.ADDRESS.getValue());
    ergebnis = Type.WORD; // Egal was es vorher war, wir sind jetzt word breit!
    String name = nameSymbol.get();

    int variablePosition = source.getVariablePosition(name);
    if (variablePosition == -1) {
      if (name.startsWith("@")) {
        source.addVariable(name, Type.FUNCTION);
        variablePosition = source.getVariablePosition(name);
      }
      else {
        source.throwIfVariableUndefined(name);
      }
    }
    p_code.add(variablePosition);
    source.getVariable(nameSymbol.get()).setRead();
  }
  
  protected void absoluteValue() {
    Symbol peekSymbol = source.nextElement(); // ":"
    source.match(peekSymbol, ":");

    Symbol nameSymbol = source.nextElement(); // address of name
    p_code.add(PCode.NOP.getValue());
    if (source.getVariableType(nameSymbol.get()) == Type.WORD) {
      p_code.add(PCode.ABSOLUTE_WORD.getValue());
      ergebnis = Type.WORD; // Egal was es vorher war, wir sind jetzt word breit!      
    }
    else if (source.getVariableType(nameSymbol.get()) == Type.INT8) {
      p_code.add(PCode.ABSOLUTE_INT8.getValue());
      ergebnis = Type.INT8;      
    }
    else {
      source.error(Symbol.noSymbol(), "Error: abs:VARIABLE must of type WORD or INT8 other are not supported yet.");      
    }
    String name = nameSymbol.get();

    int variablePosition = source.getVariablePosition(name);
    if (variablePosition == -1) {
      if (name.startsWith("@")) {
        source.error(Symbol.noSymbol(), "Error: abs:@VAR is not supported.");
      }
      else {
        source.throwIfVariableUndefined(name);
      }
    }
    p_code.add(variablePosition);
    source.getVariable(nameSymbol.get()).setRead();
  }

  protected void arrayAccess(String name) {
    // Array Access
    Symbol squareBracketOpen = source.nextElement();
    // "["
    source.match(squareBracketOpen, "[");
    if (source.getVariableType(name)==Type.UNKNOWN && name.equals("@MEM")) {
      source.addVariable("@MEM", Type.FAT_BYTE_ARRAY);
    }
    source.throwIfVariableUndefined(name);

    /* Symbol squareBrackedClose = */ factor(squareBracketOpen);

    VariableDefinition variable = source.getVariable(name);
    variable.setRead();
    if (variable.getType() == Type.BYTE_ARRAY || variable.getType() == Type.STRING) {
      p_code.add(PCode.BYTE_ARRAY.getValue());
      p_code.add(source.getVariablePosition(name));
    }
    else if (variable.getType() == Type.FAT_BYTE_ARRAY) {
      p_code.add(PCode.FAT_BYTE_ARRAY.getValue());
      p_code.add(source.getVariablePosition(name));
    }
    else if (variable.getType() == Type.WORD_ARRAY || variable.getType() == Type.FAT_WORD_ARRAY) {
      p_code.add(PCode.WORD_ARRAY.getValue());
      p_code.add(source.getVariablePosition(name));
      ergebnis = Type.WORD;
    }
    else if (variable.getType() == Type.WORD_SPLIT_ARRAY) {
      p_code.add(PCode.WORD_SPLIT_ARRAY.getValue());
      p_code.add(source.getVariablePosition(name));
      ergebnis = Type.WORD;
    }
    else {
      source.error(new Symbol(name, null), "Type: " + variable.getType() + "is unhandled yet.");
    }
  }

  private void match(Symbol symbol, String expectedSymbol) {
    if (!symbol.get().equals(expectedSymbol)) {
      source.error(symbol, expectedSymbol + " expected");
    }
  }

  public Symbol add(Symbol symbol) {
    match(symbol, "+");
    Symbol nextSymbol = term(source.nextElement());
    p_code.add(PCode.PULL.getValue());
    p_code.add(PCode.UPN_ADD.getValue());
    return nextSymbol;
  }

  public Symbol and(Symbol symbol) {
    match(symbol, "&");
    Symbol nextSymbol = term(source.nextElement());
    p_code.add(PCode.PULL.getValue());
    p_code.add(PCode.UPN_AND.getValue());
    return nextSymbol;
  }

  public Symbol or(Symbol symbol) {
    match(symbol, "!");
    Symbol nextSymbol = term(source.nextElement());
    p_code.add(PCode.PULL.getValue());
    p_code.add(PCode.UPN_OR.getValue());
    return nextSymbol;
  }

  public Symbol xor(Symbol symbol) {
    match(symbol, "XOR");
    Symbol nextSymbol = term(source.nextElement());
    p_code.add(PCode.PULL.getValue());
    p_code.add(PCode.UPN_XOR.getValue());
    return nextSymbol;
  }

  public Symbol substract(Symbol symbol) {
    match(symbol, "-");
    Symbol nextSymbol = term(source.nextElement());
    p_code.add(PCode.PULL.getValue());
    p_code.add(PCode.UPN_SUB.getValue());

    return nextSymbol;
  }

  public Symbol mul(Symbol symbol) {
    match(symbol, "*");
    Symbol nextSymbol = term(source.nextElement());
    p_code.add(PCode.PULL.getValue());
    p_code.add(PCode.UPN_MUL.getValue());

    return nextSymbol;
  }

  public Symbol divide(Symbol symbol) {
    match(symbol, "/");
    Symbol nextSymbol = term(source.nextElement());
    p_code.add(PCode.PULL.getValue());
    p_code.add(PCode.UPN_DIV.getValue());

    return nextSymbol;
  }

  public Symbol modulo(Symbol symbol) {
    match(symbol, "MOD");
    Symbol nextSymbol = term(source.nextElement());
    p_code.add(PCode.PULL.getValue());
    p_code.add(PCode.UPN_MODULO.getValue());

    return nextSymbol;
  }

  public void optimisation() {
    // TODO: versuchen sich zu erinnern, warum wir ZAHL zu INT_ZAHL wandeln
    // mussten...
    for (int i = 0; i < p_code.size(); i++) {
      if (p_code.get(i) == PCode.WORD.getValue() || p_code.get(i) == PCode.BYTE_ARRAY.getValue()
          || p_code.get(i) == PCode.FAT_BYTE_ARRAY.getValue() || p_code.get(i) == PCode.WORD_ARRAY.getValue()
          || p_code.get(i) == PCode.WORD_SPLIT_ARRAY.getValue() || p_code.get(i) == PCode.ADDRESS.getValue()
          || p_code.get(i) == PCode.FUNCTION.getValue() || p_code.get(i) == PCode.STRING.getValue()) {
        ++i;
      }
      else if (p_code.get(i) == PCode.ZAHL.getValue()) {
        p_code.set(i, PCode.INT_ZAHL.getValue());
        ++i;
      }
    }

    p_code.add(PCode.END.getValue());

    // 168 0 162 168 1   163 8
    //       a   a+1 a+2 a+3 a+4
    // =>
    // 168 0 16  168 1
    for (int a = 0; a < p_code.size(); a++) {
      if (p_code.get(a) == PCode.PUSH.getValue()) {
        int p1 = p_code.get(a + 1);
        if (p1 == PCode.ZAHL.getValue() || p1 == 161 || p1 == PCode.INT_ZAHL.getValue()
            || p1 == PCode.WORD.getValue()) {
          if (p_code.get(a + 3) == PCode.PULL.getValue()) {
            if ((p_code.get(a + 4) & 0xf8) == 8) { // arithmetik
              // P_CODE(A)=P_CODE(A+K4)&K7!k16
              p_code.set(a, p_code.get(a + 4) & 0x7 | 16);

              // Anpassungen, Code zusammenziehen.
              for (int c = a + 3; c < p_code.size() - 2; c++) {
                p_code.set(c, p_code.get(c + 2));
              }
            }
          }
        }
      }
    }

    // 168   0 162  254 171  1   163  8      =>
    //         a    a+1 a+2  a+3 a+4  a+5
    // pzahl   push nop addr     pull arith
    // 168   0 16   254 171  1
    //                           c    c+1 c+2
//    for (int a = 0; a < p_code.size(); a++) {
//      if (p_code.get(a) == PCode.PUSH.getValue()) {
//        if (p_code.get(a+1) == PCode.NOP.getValue()) {
//          int p1 = p_code.get(a + 2);
//
//          if (p1 == PCode.ADDRESS.getValue()) {
//            if (p_code.get(a + 4) == PCode.PULL.getValue()) {
//              if ((p_code.get(a + 5) & 0xf8) == 8) { // arithmetik
//                // P_CODE(A)=P_CODE(A+K4)&K7!k16
//                p_code.set(a, p_code.get(a + 5) & 0x7 | 16);
//
//                // Anpassungen, Code zusammenziehen.
//                for (int c = a + 4; c < p_code.size() - 2; c++) {
//                  p_code.set(c, p_code.get(c + 2));
//                }
//              }
//            }
//          }
//        }
//      }
//    }

    // Sonderfall für einfache Zahlen
    if (p_code.size() == 3) {

      // Das gilt nur fuer Zahlen zwischen 0 und 256!!!
//      if (p_code.get(0) == PCode.INT_ZAHL.getValue() && p_code.get(2) == PCode.END.getValue()) {
//        if (ergebnis.getBytes() == 1 ) {
//          if (p_code.get(1) >= 0 && p_code.get(1) < 256) {
//
//            // wir sind eine Zahl, diese Zahl ist >= 0 und < 256
//            ergebnis = Type.BYTE;
//          }
//          if (p_code.get(1) >= -128 && p_code.get(1) < 0) {
//
//            // wir sind eine Zahl, diese Zahl ist >= -128 und < 0
//            ergebnis = Type.INT8;
//          }
//        }
//      }
      if (p_code.get(0) == PCode.WORD.getValue() && p_code.get(2) == PCode.END.getValue()) {
        // wir sind eine einfache Variable, das Ergebnis auf dessen Type einstellen
        String name = source.getVariableAt(p_code.get(1));
        final Type type = source.getVariableType(name);
        if (type.equals(Type.CONST)) {
          ergebnis = Type.WORD;
        }
        else {
          ergebnis = type;
        }
      }
    }
    else if (p_code.size() == 5) {
      if ((p_code.get(0) == PCode.INT_ZAHL.getValue() || p_code.get(0) == PCode.WORD.getValue())
          && (p_code.get(2) == PCode.BYTE_ARRAY.getValue() || p_code.get(2) == PCode.FAT_BYTE_ARRAY.getValue())
          && p_code.get(4) == PCode.END.getValue()) {

        // wir sind ein (FAT_)BYTE_ARRAY
        ergebnis = Type.BYTE;
      }
    }

    ArrayList<Integer> optimizedPCode = new ArrayList<>();
    int i = 0;
    while (p_code.get(i) != PCode.END.getValue()) {
      optimizedPCode.add(p_code.get(i));
      i++;
    }
    optimizedPCode.add(PCode.END.getValue());

    // TODO: kotz!
    optimizedPCode.add(0);
    optimizedPCode.add(0);
    optimizedPCode.add(0);
    optimizedPCode.add(0);
    optimizedPCode.add(0);

    p_code = optimizedPCode;

    source.setTypeOfLastExpression(ergebnis);
  }

  /**
   * Hilfsfunktion um die erstellten PCodes in einem String zurückzugeben
   *
   * @param currentExpression
   * @return String of PCodes
   */
  public String joinedPCode() {
    return joinedPCode(getPCode());
  }

  public static String joinedPCode(List<Integer> pCodeList) {

    StringBuilder joinedPCode = new StringBuilder();
    int length = pCodeList.size();
    for (int i = 0; i < length; i++) {
      int pCode = pCodeList.get(i);
      joinedPCode.append(String.valueOf(pCode));

      if (pCode == PCode.END.getValue()) {
        break;
      }

      if (i < length - 1) {
        joinedPCode.append(" ");
      }
    }
    return joinedPCode.toString();
  }

}
