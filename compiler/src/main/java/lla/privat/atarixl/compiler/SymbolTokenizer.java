// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class interprets the input stream (program) and split it by tokens.
 * At first, it overread all white spaces (space, return, tab, windows linefeed, atari carriage return
 * then
 * <li>Variable name must start with [a-zA-Z@] then also digits and underscores are possible
 * <li>Numbers [0-9%$#] $ sign for hex values, %sign for 8-bit binary values # for quads possible is also a dot (not used yet)
 * <li>Different symbols like + - * / braces [ ] ( ) also 2 ( := ) or 3 symbol signs are possible
 * <li>Conditional symbols < > <= >= <> != = ==
 * <li>Strings starts with single quote and end with single quote
 * <li>If variable name is a single A it will replaced by A__ due to assembler menemonic 'ASL A' or equal
 * <li>If variable starts with @ it will never prefixed if we are INCLUDE instead of PROGRAM type
 * <li>Some variable names are reserved words, they will also never prefixed
 * <li>Due to a program feature, ADR is a variable name, not a reserved word, but will not prefixed
 *
 *
 * @author develop
 *
 */
public class SymbolTokenizer implements Enumeration<Symbol> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SymbolTokenizer.class);

  private final String program;

  private int zeiger;

  private int oldZeiger;

  private String sourceCodeLine;

  private String prefix;

  private boolean peekSymbol = false;

  public SymbolTokenizer(String program) {
    this.program = program;
    this.prefix = "";
  }

  @Override
  public boolean hasMoreElements() {
    Symbol nextElement = peekSymbol();
    return nextElement.getId() != SymbolEnum.noSymbol;
  }

  /**
   * Main function, returns the next Symbol and set the intern pointer after this symbol
   * <li>variable name
   * <li>number
   * <li>symbol
   * <li>conditional symbol
   * <li>string
   * <li>reserved word
   */
  @Override
  public Symbol nextElement() {
    try {
      return getSymbol_impl();
    } catch (StringIndexOutOfBoundsException e) {
      return new Symbol("", SymbolEnum.noSymbol);
    }
  }

  private boolean showCode = false;

  public SymbolTokenizer handleShowCode(boolean showCode) {
    this.showCode = showCode;
    return this;
  }

  public String getSourceCodeLine() {
    return sourceCodeLine;
  }

  private Symbol getSymbol_impl() throws StringIndexOutOfBoundsException {
    oldZeiger = zeiger;

    Symbol symbol = new Symbol("", SymbolEnum.noSymbol);
    boolean markable = true;

    // check for white spaces
    char ch = program.charAt(zeiger);
    while (markable) {
      while (ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r' || ch == 0x9b) {
        ch = program.charAt(++zeiger);
      }

      char ch1 = program.charAt(zeiger + 1);

      if (ch == '/' && ch1 == '/') {
        while (ch != '\n' && ch != '\r' && ch != 0x9b) {
          ch = program.charAt(++zeiger);
        }
      }
      else {
        markable = false;
      }
    }

    if (showCode && peekSymbol == false) {
      sourceCodeLine = getCodeLine();
      showCode = false;
    }

    oldZeiger = zeiger;
    if (ch == '@' && program.charAt(zeiger+1) == '(') {
      String value = "@(";
      symbol = new Symbol(value, SymbolEnum.symbol);
      zeiger+=2;
    }
    // Variable names start with a-z, A-Z or @
    // then a-z, A-Z, 0-9 _ @
    else if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || ch == '@') {
      int p1 = zeiger;
      while ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_'
          || ch == '@') {
        ch = program.charAt(++zeiger);
      }
      String value = program.subSequence(p1, zeiger).toString();
      symbol = new Symbol(value, SymbolEnum.variable_name);

      String valueUpper = value.toUpperCase();

      // check for reserved names
      if (ReservedNames.isReservedWord(valueUpper)) {
        symbol.changeId(SymbolEnum.reserved_word);
      }

    }
    // check for Numbers start with 0-9, $ % #

    else if ((ch >= '0' && ch <= '9') || ch == '$' || ch == '%' || ch == '#') {
      int p1 = zeiger;
      ch = program.charAt(++zeiger);
      while ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f') || ch == '.') {
        ch = program.charAt(++zeiger);
      }
      String number = program.substring(p1, zeiger);
      if (number.charAt(0) == '#') {
        number = convertQuadToBin(number);
      }
      if (number.charAt(0) == '%') {
        number = convertBinToHex(number);
      }
      symbol = new Symbol(number, SymbolEnum.number);

    }
    // special chars
    else if ("+-*/=^<>()[].,:;&!".contains(String.valueOf(ch))) {
      int p1 = zeiger;
      if (";.+-*/()[],".contains(String.valueOf(ch))) {
        ++zeiger;
      }
      else {
        do {
          ch = program.charAt(++zeiger);
        } while ("<>=".contains(String.valueOf(ch)));
      }
      String sym = program.substring(p1, zeiger);
      SymbolEnum id = SymbolEnum.symbol;

      // conditional symbols
      if ("= <> < <= > >= != ==".contains(sym)) {
        id = SymbolEnum.condition_symbol;
      }
      symbol = new Symbol(sym, id);
      // TODO: throw away illegal symbols
    }

    // Strings, starts with ' and ends with '
    else if (ch == '\'') {
      int p1 = zeiger;
      char chold = ' ';
      ch = program.charAt(zeiger);
      boolean endQuote = false;
      // TODO: Erkennung '' oder \'
      while (!endQuote) {
        chold = ch;
        ch = program.charAt(++zeiger);
        if (ch == '\'' && chold != '\\') {
          endQuote = true;
        }
      }
      String str = program.substring(p1, zeiger + 1);
      ++zeiger;
      symbol = new Symbol(str, SymbolEnum.string);
    }
    if (symbol.getId() == SymbolEnum.variable_name || symbol.getId() == SymbolEnum.reserved_word
        || symbol.getId() == SymbolEnum.number) {
      symbol.changeValue(symbol.get().toUpperCase());
    }
    if (symbol.get().equals("A")) {
      symbol.changeValue("A__");
    }
    if (symbol.get().equals("W")) {
      symbol.changeValue("W__");
    }
    if (symbol.getId() == SymbolEnum.variable_name && prefix.length() > 0) {
      String variable = symbol.get();
      if (!variable.startsWith("@")) {
        symbol.changeValue(prefix + "_" + variable);
      }
    }
    if (symbol.getId() == SymbolEnum.reserved_word) {
      if (symbol.get().equals("ADR") ||
          symbol.get().equals("B2W")) { // TODO: prüfen ob ADR eine variable sein muss
        symbol.changeId(SymbolEnum.variable_name);
      }
    }
    return symbol;
  }

  private String convertQuadToBin(String number) {
    StringBuilder newNumber = new StringBuilder();
    newNumber.append('%');
    if (number.length() != 5) {
      throw new IllegalStateException("Kein Quad");
    }
    for (int i = 1; i < 5;i++) {
      char charAt = number.charAt(i);
      if (charAt == '1') {
        newNumber.append("01");
      }
      else if (charAt == '2') {
        newNumber.append("10");
      }
      else if (charAt == '3') {
        newNumber.append("11");
      }
      else {
        newNumber.append("00");
      }
    }
    return newNumber.toString();
  }

  private String convertBinToHex(String number) {
    int value = 0;
    int bit = 1;
    if (number.length() != 9) {
      throw new IllegalStateException("Kein Binary");
    }
    int i = number.length() - 1;
    while (i >= 1) {
      if (number.charAt(i) == '1') {
        value += bit;
      }
      bit *= 2;
      --i;
    }
    number = "$" + Integer.toHexString(value);
    return number;
  }

  /**
   *
   * @return next symbol in source
   * @deprecated use nextElement() instead!
   */
  @Deprecated
  public Symbol getSymbol() {
    peekSymbol = false;
    return nextElement();
  }

  /**
   *
   * @return next Symbol without increment the index
   */
  public Symbol peekSymbol() {
    peekSymbol = true;
    Symbol nextSymbol = getSymbol();
    zeiger = oldZeiger;
    return nextSymbol;
  }

  public int getZeiger() {
    return zeiger;
  }

  /**
   * Läuft rückwärts ab zeiger durch den Source und zählt das vorkommen von \n
   *
   * @return lineNumber
   */
  public int getLineNumber() {
    int lineNumber = 1;
    int zeigerPos = zeiger;
    boolean hasReturn = true;
    while (hasReturn) {
      zeigerPos = program.lastIndexOf("\n", zeigerPos-1);
      if (zeigerPos != -1) {
        ++lineNumber;
      }
      else {
        hasReturn = false;
      }
    }
    return lineNumber;
  }

  /**
   * Sucht rückwärts ab der aktuellen Position den Anfang der Zeile,
   * dann das Ende der Zeile und gibt das als String zurück
   * @return
   */
  public String getCodeLine() {
    int startline = program.lastIndexOf("\n", zeiger);
    startline = (startline == -1) ? 0 : startline;

    int endline = program.indexOf("\n", zeiger);
    endline = (endline == -1) ? program.length() : endline;

    return program.substring(startline, endline).trim();
  }

  /**
   * Gibt die Zeilennummer aus, dann den Content und die Fehlermeldung zu einem Fehler.
   * @param symbol
   * @param message
   */
  public void error(Symbol symbol, String message) {

    final String errorMessage =createErrorMessage(symbol, message);
    LOGGER.error(errorMessage);
    throw new IllegalStateException(errorMessage);
  }

  public void warning(Symbol symbol, String message) {
    LOGGER.warn(createErrorMessage(symbol, message));
  }

  private String createErrorMessage(Symbol symbol, String message) {
    final String errorMessage = String.format("Occured in Line number: %d%nCode-content: '%s'%nSymbol => '%s'%nMessage: %s",
        getLineNumber(), getCodeLine(),symbol.get(), message);
    return errorMessage;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix.toUpperCase();
  }

}
